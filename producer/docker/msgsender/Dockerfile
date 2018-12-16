FROM ubuntu:16.04

RUN apt update && apt install -y unzip default-jdk curl stress iputils-ping

RUN mkdir -p /tmp/install
ADD wlp-webProfile7-17.0.0.3.zip /tmp/install

RUN unzip /tmp/install/wlp-webProfile7-17.0.0.3.zip -d /root
RUN /root/wlp/bin/server create
RUN rm -f /tmp/install/wlp-webProfile7-17.0.0.3.zip

ADD msgsender.war /root/wlp/usr/servers/defaultServer/apps
ADD server.xml /root/wlp/usr/servers/defaultServer

#RUN mkdir -p /root/wlp/usr/servers/defaultServer/jdbc
#ADD mysql-connector-java-5.1.44-bin.jar /root/wlp/usr/servers/defaultServer/jdbc
#ADD send /root/
#RUN chmod +x /root/send

#RUN /root/wlp/bin/securityUtility createSSLCertificate --server=defaultServer --password=passw0rd

WORKDIR /root

CMD /root/wlp/bin/server run
