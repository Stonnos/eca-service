FROM tomcat:8-jre8
RUN mkdir -p /home/experiment
RUN mkdir -p /home/experiment/data
ADD scripts/wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh
ENTRYPOINT [ "/wait-for-it.sh", "eca-db:5432", "--" ]
COPY target/eca-service.war /usr/local/tomcat/webapps/
CMD ["catalina.sh", "run"]