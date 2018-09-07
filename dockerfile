FROM tomcat:8-jre8
RUN mkdir /home/experiment
RUN mkdir /home/experiment/data
COPY target/eca-service.war /usr/local/tomcat/webapps/
CMD ["catalina.sh", "run"]