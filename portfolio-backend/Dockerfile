# syntax=docker/dockerfile:1

# ---- build: compile the Spring Boot jar --------------------------------------------------------
FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace

# Build files first, so the dependency layer is reused until they change.
# gradle/ contains the wrapper and verification-metadata.xml, the lockfiles pin the versions.
COPY gradlew settings.gradle.kts build.gradle.kts gradle.lockfile settings-gradle.lockfile ./
COPY gradle gradle
RUN chmod +x gradlew && ./gradlew --no-daemon dependencies > /dev/null

COPY src src
RUN ./gradlew --no-daemon bootJar -x test

# ---- runtime: JRE only, non-root ---------------------------------------------------------------
FROM eclipse-temurin:17-jre-alpine AS runtime
RUN addgroup -S app && adduser -S -G app app
WORKDIR /app
COPY --from=build --chown=app:app /workspace/build/libs/app.jar app.jar
USER app

EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s --start-period=90s --retries=3 \
  CMD wget -qO- http://127.0.0.1:8080/actuator/health | grep -q '"status":"UP"' || exit 1

# Size the heap from the container memory limit; override with JAVA_TOOL_OPTIONS if needed
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
