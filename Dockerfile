# Use a base image with JDK and Alpine Linux
FROM openjdk:17-jdk-alpine

# Metadata and author label
LABEL author="Sarvadnyaa Barate"

# Set volume point to /tmp
VOLUME /tmp

# Specify the location of the Spring Boot executable JAR file
ARG JAR_FILE=target/*.jar

# Copy the JAR file into the container at /app.jar
COPY ${JAR_FILE} app.jar

# Entrypoint for the Docker container, executing the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app.jar"]
