# Multi-stage Dockerfile for Spring Boot application

# Stage 1: Build
FROM gradle:8.5-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle build -x test --no-daemon

# Stage 2: Runtime
FROM openjdk:17-jdk-slim
WORKDIR /app

COPY --from=build /app/build/libs/converson-web-1.0.0.jar app.jar

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*
RUN mkdir -p /tmp/uploads /tmp/converted

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-Xmx512m", "-XX:+UseG1GC", "-Dspring.profiles.active=prod", "-jar", "app.jar"]

