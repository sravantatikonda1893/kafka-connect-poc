FROM openjdk:8-jdk-alpine
RUN apk add --no-cache openjdk8
MAINTAINER rbr
EXPOSE 9901
VOLUME /tmp
ADD maven/${fileName}.jar ${fileName}.jar
RUN sh -c 'touch /${fileName}.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/${fileName}.jar"]