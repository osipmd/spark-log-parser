#!/usr/bin/env bash

sudo docker cp access_log_Jul95 hdfs:/
sudo docker exec -it hdfs /opt/hadoop/bin/hadoop fs -mkdir /nasa
sudo docker exec -it hdfs /opt/hadoop/bin/hadoop fs -put access_log_Jul95 /nasa/access_log_Jul95