FROM openjdk:21-jdk-slim
COPY target/eca-audit-log.jar eca-audit-log.jar
ENTRYPOINT exec java $JAVA_OPTS -jar eca-audit-log.jar
