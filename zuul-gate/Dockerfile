FROM openjdk:11-jdk-slim
COPY target/zuul-gate.war zuul-gate.war
ENTRYPOINT exec java -jar zuul-gate.war