FROM openjdk:21-jdk-slim
COPY target/eca-web.jar eca-web.jar
ENTRYPOINT exec java $JAVA_OPTS -jar eca-web.jar
