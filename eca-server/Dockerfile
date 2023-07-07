FROM openjdk:11-jdk-slim
RUN mkdir -p /home/experiment
COPY target/eca-server.jar eca-server.jar
ENTRYPOINT exec java $JAVA_OPTS -jar eca-server.jar
