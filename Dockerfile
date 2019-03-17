# Use official base image of Java Runtime
#From openjdk:8-jdk-alpine
From maven:3-jdk-8

# Add project into container path
ADD . /usr/task/

# COPY application.properties
ADD src/main/resources/application.properties /etc/dsci/task/config/

WORKDIR /usr/task/

RUN mvn clean install

# Set spring boot jar file
ARG JAR_FILE=target/task-0.0.1-SNAPSHOT.jar

# Move jar file to the container
RUN mv ${JAR_FILE} /usr/task/to-do-list.jar

# Launch jar file
ENTRYPOINT ["java", "-jar", "/usr/task/to-do-list.jar", "--spring.config.location=classpath:/application.properties,/etc/dsci/task/application.properties"]