FROM openjdk:11-jdk-slim
COPY target/eca-oauth.war eca-oauth.war
ENTRYPOINT exec java $JAVA_OPTS -jar eca-oauth.war
