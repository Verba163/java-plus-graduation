FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/main-service*.jar /app/main-service.jar
CMD ["java", "-jar", "/app/main-service.jar"]