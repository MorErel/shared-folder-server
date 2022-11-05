FROM amazoncorretto:11.0.16-al2
RUN yum install shadow-utils -y && groupadd -g 13372 apps && useradd -M -u 13372 app -G apps && yum clean all
ENV JAR_NAME=shared-folder-server.jar
RUN mkdir -p /app
COPY target/$JAR_NAME /app/$JAR_NAME
WORKDIR /app
USER app
CMD java -Djaav.net.preferIpv4Stack=true -jar $JAR_NAME
