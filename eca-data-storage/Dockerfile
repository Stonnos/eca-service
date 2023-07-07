FROM openjdk:11-jdk-slim
COPY target/eca-data-storage.jar eca-data-storage.jar
ENTRYPOINT exec java $JAVA_OPTS -jar eca-data-storage.jar
