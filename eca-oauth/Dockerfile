FROM openjdk:11-jdk-slim
ADD wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh
COPY target/eca-oauth.war eca-oauth.war
ENTRYPOINT exec /wait-for-it.sh $DB_CONTAINER_WAIT --timeout=$DB_CONTAINER_WAIT_TIMEOUT -- java $JAVA_OPTS -jar eca-oauth.war
