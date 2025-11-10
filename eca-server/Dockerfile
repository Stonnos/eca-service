FROM eclipse-temurin:21
RUN mkdir -p /home/experiment
COPY target/eca-server.jar eca-server.jar
ENTRYPOINT exec java $JAVA_OPTS -jar eca-server.jar
