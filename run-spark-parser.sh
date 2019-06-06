#!/usr/bin/env bash

sudo docker cp ./target/scala-2.11/spark-parser_2.11-0.1.jar spark-master:/
sudo docker exec -it spark-master /spark/bin/spark-submit --master spark://spark-master:7077 --class com.osipmd.LogParser spark-parser_2.11-0.1.jar
