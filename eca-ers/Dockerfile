FROM openjdk:11-jdk-slim
COPY target/eca-ers.jar eca-ers.jar
ENTRYPOINT exec java $JAVA_OPTS -jar eca-ers.jar
