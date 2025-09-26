FROM openjdk:21-jdk-slim

WORKDIR /app

EXPOSE 8888

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
  CMD curl --fail http://localhost:8888/actuator/health || exit 1


COPY target/*.jar app.jar
COPY wait-for.sh /wait-for.sh
RUN chmod +x /wait-for.sh

ENTRYPOINT ["java", "-jar", "app.jar"]

