FROM openjdk:11-jdk-slim
COPY target/eca-web.war eca-web.war
ENTRYPOINT exec java $JAVA_OPTS -jar eca-web.war
