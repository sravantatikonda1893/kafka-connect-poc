FROM openjdk:8-jdk-alpine
RUN apk add --no-cache openjdk8
MAINTAINER rbr
EXPOSE 9999
VOLUME /tmp
ADD maven/${fileName}.jar ${fileName}.jar
RUN sh -c 'touch /${fileName}.jar'
RUN mkdir -p /data/orders
RUN mkdir -p /data/logs

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/${fileName}.jar"]