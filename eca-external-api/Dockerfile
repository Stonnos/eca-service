FROM openjdk:8-jdk-slim
RUN mkdir -p /home/classifiers/data
ADD wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh
COPY target/eca-external-api.war eca-external-api.war
ENTRYPOINT exec /wait-for-it.sh $DB_CONTAINER_WAIT --timeout=$DB_CONTAINER_WAIT_TIMEOUT -- java $JAVA_OPTS -jar eca-external-api.war