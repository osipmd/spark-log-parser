# spark-log-parser
It's fiveth lab of university course Multithreading programming. Parsing Nasa log file using Spark and Hadoop with Docker containers.

Описание:   

Использует HDFS контейнер для хранения логов и записи результатов парсинга.    
Результаты, скаченные с HDFS, также представлены в директории /results.   
Запуск spark application происходит через spark-submit.    
Использование docker-compose не обязательно.    

Проект состоит из 4-х основных частей
  1) Dockerfile с конфигурацией для HDFS, spark-master и spark-worker контейнеров.   
  docker-hdfs - dockerfile для HDFS и настройки    
  docker-spark-base - базовый image для spark контейнеров    
  docker-spark-master - image для мастера    
  docker-spark-worker - image для spark workers    
  2) docker-compose.yaml - docker-compose файл для запуска в одну команду
  3) src - содержит код парсинга логов на scala    
    LogParser объект с 3 методами под каждую задачу
  4) copy-log-to-hdfs - скрипт для добавления логов в HDFS
    run-spark-parser - скрпит запуска программы через spark-submit
    
    ***
    
Подготовка:
  1) Скачать файл логов, например, через     
  wget file_link    
  gunzip file_name    
  
  2) Собрать jar файл исходного кода     
    sbt package 
  
  3) Собрать images    
  3.1) cd docker-hdfs           
       docker build -t osipmd/hdfs .       
  3.2) cd docker-spark-base                   
       docker build -t osipmd/spark-base .    
  3.3) cd docker-spark-master     
       docker build -t osipmd/spark-master .       
  3.4) cd docker-spark-worker     
       docker build -t osipmd/spark-worker .
  
  4) Запустить контейнеры через docker-compose          
      docker-compose up
       
  5) Собрать jar файл исходного кода     
      sbt package 
  
  5) Доавить логи в HDFS ./copy-log-to-hdfs.sh
  
  6) Запустить приложение на spark ./run-spark-parser.sh
       
