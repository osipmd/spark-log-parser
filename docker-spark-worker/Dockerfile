FROM osipmd/spark-base

COPY worker.sh /

ENV SPARK_WORKER_WEBUI_PORT 8081
ENV SPARK_WORKER_LOG /spark/logs
ENV SPARK_MASTER "spark://spark-master:7077"

EXPOSE 8081

ENTRYPOINT  ["/bin/bash", "/worker.sh"]
