FROM openjdk:21-jdk-slim
COPY target/discovery-server.jar discovery-server.jar
ENTRYPOINT exec java $JAVA_OPTS -jar discovery-server.jar
