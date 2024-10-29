# Define base docker image
FROM openjdk:17

# Label for the maintainer
LABEL maintainer="sadetechnosolution.net"

# Set the working directory
WORKDIR /app

# Copy the JAR file to the container
COPY target/jwt-module-0.0.1-SNAPSHOT.jar jwt-module.jar

# Expose the port your application runs on
EXPOSE 8081

# Command to run the application
ENTRYPOINT ["java", "-jar", "jwt-module.jar"]
