# Use official base image of Java Runtime
From openjdk:8-jdk-alpine

# Set spring boot jar file
ARG JAR_FILE=target/task-0.0.1-SNAPSHOT.jar

# Add jar file to the container
ADD ${JAR_FILE} /usr/task/to-do-list.jar

# COPY application.properties
ADD src/main/resources/application.properties /etc/task/config/

# Expose 8080 as restful webservice port

WORKDIR /usr/task/

# Launch jar file
ENTRYPOINT ["java", "-jar", "/to-do-list.jar", "--spring.config.location=classpath:/application.properties,/etc/dsci/task/application.properties"]