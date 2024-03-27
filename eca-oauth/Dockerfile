FROM openjdk:17-jdk-slim
COPY target/eca-oauth.jar eca-oauth.jar
ENTRYPOINT exec java $JAVA_OPTS -jar eca-oauth.jar
