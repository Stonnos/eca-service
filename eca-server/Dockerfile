FROM openjdk:11-jdk-slim
RUN mkdir -p /home/experiment
COPY target/eca-server.war eca-server.war
ENTRYPOINT exec java $JAVA_OPTS -jar eca-server.war
