# Stage 1: Build
FROM gradle:8.5-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle build -x test --no-daemon

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copiar JAR del build
COPY --from=build /app/build/libs/converson-web-1.0.0.jar app.jar

# Curl para healthcheck
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Directorios temporales
RUN mkdir -p /tmp/uploads /tmp/converted

EXPOSE 8080

# Healthcheck: usa el PORT de Render (si existe), sino 8080
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD sh -c "curl -f http://localhost:${PORT:-8080}/actuator/health || exit 1"

# Render usa PORT, local usa 8080
ENTRYPOINT ["sh", "-c", "java -Xmx512m -XX:+UseG1GC -Dspring.profiles.active=prod -Dserver.port=${PORT:-8080} -jar app.jar"]
