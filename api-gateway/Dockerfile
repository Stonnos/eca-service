FROM openjdk:17-jdk-slim
COPY target/api-gateway.jar api-gateway.jar
ENTRYPOINT exec java $JAVA_OPTS -jar api-gateway.jar
