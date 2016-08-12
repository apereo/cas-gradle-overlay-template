FROM infusionsoft/tomcat_java-base:7u65.7057.41

#enable compression
RUN sed -i '/<Connector port="8080" protocol="HTTP1"/a compression="on"' /opt/tomcat/conf/server.xml

ENV JAVA_HOME "/opt/java"

ADD bin/run /app/

ADD build/libs/ROOT.war /opt/tomcat/webapps/ROOT.war

CMD ["/app/run"]
