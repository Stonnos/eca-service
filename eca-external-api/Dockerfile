FROM openjdk:11-jdk-slim
COPY target/eca-external-api.war eca-external-api.war
ENTRYPOINT exec java $JAVA_OPTS -jar eca-external-api.war
