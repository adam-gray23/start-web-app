# Use a base image with Java installed
FROM openjdk:19

# Set a working directory in the container
WORKDIR /app

COPY start-files/start-complete.jar /app/start-complete.jar

COPY input/input.txt /app/input.txt

WORKDIR /app

ENTRYPOINT [ "java", "-jar", "start-complete.jar", "input.txt" ]