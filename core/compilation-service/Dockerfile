FROM openjdk:21-jdk-slim
WORKDIR /app

COPY target/*.jar app.jar
COPY wait-for.sh /wait-for.sh
RUN chmod +x /wait-for.sh

ENTRYPOINT ["java", "-jar", "app.jar"]