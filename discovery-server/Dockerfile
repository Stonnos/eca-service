FROM openjdk:11-jdk-slim
COPY target/discovery-server.war discovery-server.war
ENTRYPOINT exec java $JAVA_OPTS -jar discovery-server.war
