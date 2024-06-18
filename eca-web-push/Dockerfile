FROM openjdk:21-jdk-slim
COPY target/eca-web-push.jar eca-web-push.jar
ENTRYPOINT exec java $JAVA_OPTS -jar eca-web-push.jar
