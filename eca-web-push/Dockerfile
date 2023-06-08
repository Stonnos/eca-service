FROM openjdk:11-jdk-slim
COPY target/eca-web-push.war eca-web-push.war
ENTRYPOINT exec java $JAVA_OPTS -jar eca-web-push.war
