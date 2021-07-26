FROM openjdk:8-jdk-alpine

COPY target/demo-0.0.1-SNAPSHOT.jar  /opt/demo.jar
COPY startup.sh /opt

RUN chmod +x /opt/startup.sh

ENV MEM_MIN="lg"
ENV MEM_MAX="lg"

EXPOSE 23306/tcp

CMD "/opt/startup.sh"