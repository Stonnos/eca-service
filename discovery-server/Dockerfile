FROM openjdk:11-jdk-slim
COPY target/discovery-server.jar discovery-server.jar
ENTRYPOINT exec java $JAVA_OPTS -jar discovery-server.jar
