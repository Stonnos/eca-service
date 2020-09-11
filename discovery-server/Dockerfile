FROM openjdk:8-jdk-slim
COPY target/discovery-server.war discovery-server.war
ENTRYPOINT exec java $JAVA_OPTS -jar discovery-server.war