# Use a minimal base image with Java installed
FROM openjdk:19-jdk-alpine

# Set a working directory in the container
WORKDIR /app

# Copy only necessary files
COPY start-files/start-complete.jar /app/

# Specify the entry point
ENTRYPOINT ["java", "-jar", "start-complete.jar"]
