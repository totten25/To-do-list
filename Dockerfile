# Use official base image of Java Runtime
From openjdk:8-jdk-alpine

# Set spring boot jar file
ARG JAR_FILE=target/task-0.0.1-SNAPSHOT.jar

# Add jar file to the container
ADD ${JAR_FILE} /usr/task/to-do-list.jar

# COPY application.properties
ADD src/main/resources/application.properties /etc/dsci/task/config/

WORKDIR /usr/task/

# Launch jar file
ENTRYPOINT ["java", "-jar", "/usr/task/to-do-list.jar", "--spring.config.location=classpath:/application.properties,/etc/dsci/task/application.properties"]