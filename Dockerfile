# Multi-stage Dockerfile for Spring Boot application

# Stage 1: Build
FROM gradle:8.5-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle build -x test --no-daemon

# Stage 2: Runtime
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy JAR from build stage
COPY --from=build /app/build/libs/converson-web-1.0.0.jar app.jar

# Create temp directories
RUN mkdir -p /tmp/uploads /tmp/converted

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", "-Xmx512m", "-XX:+UseG1GC", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
