FROM openjdk:11-jdk-slim
COPY target/eca-ers.war eca-ers.war
ENTRYPOINT exec java $JAVA_OPTS -jar eca-ers.war
