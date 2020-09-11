FROM openjdk:8-jdk-slim
COPY target/zuul-gate.war zuul-gate.war
ENTRYPOINT exec java $JAVA_OPTS -jar zuul-gate.war