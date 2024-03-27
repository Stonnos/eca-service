FROM openjdk:17-jdk-slim
COPY target/eca-data-loader.jar eca-data-loader.jar
ENTRYPOINT exec java $JAVA_OPTS -jar eca-data-loader.jar
