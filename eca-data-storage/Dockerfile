FROM openjdk:11-jdk-slim
COPY target/eca-data-storage.war eca-data-storage.war
ENTRYPOINT exec java $JAVA_OPTS -jar eca-data-storage.war
