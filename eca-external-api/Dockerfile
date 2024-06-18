FROM openjdk:21-jdk-slim
COPY target/eca-external-api.jar eca-external-api.jar
ENTRYPOINT exec java $JAVA_OPTS -jar eca-external-api.jar
