FROM openjdk:11-jdk-slim
COPY target/eca-audit-log.war eca-audit-log.war
ENTRYPOINT exec java $JAVA_OPTS -jar eca-audit-log.war
