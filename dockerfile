FROM openjdk:8-jdk-alpine
RUN apk add --no-cache openjdk8
MAINTAINER SravanTatikonda
EXPOSE 9901
ARG JAR_FILE=target/kafka-connect-poc-1.0.0.jar
ADD ${JAR_FILE} kafka-connect-poc-1.0.0.jar

ENTRYPOINT ["java", "-jar","/kafka-connect-poc-1.0.0.jar"]