FROM openjdk:11-jdk-slim
COPY target/zuul-gate.jar zuul-gate.jar
ENTRYPOINT exec java $JAVA_OPTS -jar zuul-gate.jar
