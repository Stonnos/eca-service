FROM openjdk:11-jdk-slim
COPY target/eca-mail.war eca-mail.war
ENTRYPOINT exec java $JAVA_OPTS -jar eca-mail.war
