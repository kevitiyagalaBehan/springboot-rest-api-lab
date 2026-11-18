# syntax=docker/dockerfile:1

# ---------- Stage 1: build ----------
# Official Eclipse Temurin JDK image, used only to compile the app.
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /workspace

# Copy the build files first so the dependency layer is cached between builds.
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw && ./mvnw -B -q dependency:go-offline

COPY src src
RUN ./mvnw -B -q package -DskipTests

# ---------- Stage 2: runtime ----------
# Minimal official JRE image: no JDK, no Maven, no source code in the final image.
FROM eclipse-temurin:17-jre-alpine

# Patch OS packages to reduce CVEs found by Trivy.
RUN apk upgrade --no-cache

# Unprivileged user with a fixed UID (no root inside the container).
RUN addgroup -S -g 10001 app && adduser -S -u 10001 -G app app

WORKDIR /app
COPY --from=build --chown=10001:10001 /workspace/target/*.jar app.jar

USER 10001:10001
EXPOSE 8080

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0"

HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
