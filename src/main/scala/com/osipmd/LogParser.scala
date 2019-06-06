package com.osipmd

import java.text.SimpleDateFormat

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}
import org.apache.spark.sql.functions._

object LogParser {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("LogParser Application")
      .master("spark://spark-master:7077")
      .getOrCreate()
    val logFile = spark.sparkContext.textFile("hdfs://hdfs:9000/nasa/access_log_Jul95")

    firstTask(logFile)
    secondTask(logFile)
    thirdTask(logFile, spark)
  }

  def firstTask(rdd: RDD[String]): Unit = {
    val regexp = raw"""^(\S+) (\S+) (\S+) \[([\w:/]+\s[+\-]\d{4})\] "(.+)" (\d{3}) (\S+)""".r
    rdd.filter {
      case regexp(_, _, _, _, _, code, _) => code(0) == '5'
      case _ => false
    }.map {
      case regexp(_, _, _, _, request, _, _) => (request, 1)
    }.reduceByKey((a, b) => a + b)
      .coalesce(1)
      .saveAsTextFile("hdfs://hdfs:9000/firstTask.txt")
  }

  def secondTask(rdd: RDD[String]): Unit = {
    val regexp = raw"""^(\S+) (\S+) (\S+) \[([\w/]+):([\w:]+\s[+\-]\d{4})\] "(\S+) (.*)" (\d{3}) (\S+)""".r
    rdd.filter {
      case regexp(_*) => true
      case _ => false
    }.map {
      case regexp(_, _, _, date, _, method, _, code, _) => ((date, method, code), 1)
      case _ => (("01/Jan/1960", "UNKNOWN", -1), 1)
    }.reduceByKey((a, b) => a + b)
      .filter(a => a._2 >= 10)
      .sortBy(a => a._1._1)
      .coalesce(1)
      .saveAsTextFile("hdfs://hdfs:9000/secondTask.txt")
  }

  def thirdTask(rdd: RDD[String], spark: SparkSession): Unit = {
    import spark.implicits._
    val regexp = raw"""^(\S+) (\S+) (\S+) \[([\w/]+):([\w:]+\s[+\-]\d{4})\] "(.*)" (\d{3}) (\S+)""".r
    val oldFormat = new SimpleDateFormat("dd/MMM/yyyy")
    val newFormat = new SimpleDateFormat("yyyy-MM-dd")

    def dfSchema(columnNames: List[String]): StructType =
      StructType(
        Seq(
          StructField(name = "date", dataType = StringType, nullable = false),
          StructField(name = "count", dataType = IntegerType, nullable = false)
        )
      )

    val schema = dfSchema(List("date", "count"))
    val dateRdd = rdd.filter {
      case regexp(_, _, _, _, _, _, code, _) => code(0) == '5' || code(0) == '4'
      case _ => false
    }.map {
      case regexp(_, _, _, date, _, _, _, _) =>
        Row(newFormat.format(oldFormat.parse(date)), 1)
    }
    spark.createDataFrame(dateRdd, schema)
      .withColumn("date", col("date").cast("date"))
      .groupBy(window($"date", "1 week", "1 day"))
      .agg(sum("count"))
      .orderBy("window")
      .rdd
      .coalesce(1)
      .saveAsTextFile("hdfs://hdfs:9000/thirdTask.txt")
  }
}
