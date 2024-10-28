#define base docker image
FROM openjdk:17
LABEL maintainer="sadetechnosolution.net"
ADD target/jwt-module-0.0.1-SNAPSHOT.jar jwt-module.jar
ENTRYPOINT["java","-jar","jwt-module.jar"]