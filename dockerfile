FROM tomcat:8-jre8
COPY target/eca-service.war /usr/local/tomcat/webapps/
CMD ["catalina.sh", "run"]