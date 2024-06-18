FROM openjdk:21-jdk-slim
COPY target/eca-mail.jar eca-mail.jar
ENTRYPOINT exec java $JAVA_OPTS -jar eca-mail.jar
