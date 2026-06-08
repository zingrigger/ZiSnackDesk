# -------------------------
# Build stage: build the application with Maven
# -------------------------
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B -DskipTests package

# -------------------------
# Runtime stage: minimal, secure image
# - GC logs -> stdout so `docker logs` captures them
# - Heap dumps -> written to /app/dumps (mount this directory on the host)
# -------------------------
FROM eclipse-temurin:21-jdk AS runtime
LABEL org.opencontainers.image.source="https://example.com/your/repo"

ENV APP_HOME=/app
ENV DUMP_DIR=/app/dumps
# Default JVM options. Override at runtime with `-e JAVA_OPTS="..."` if needed.
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -Xlog:gc*:stdout:time,uptime -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=${DUMP_DIR}"

WORKDIR ${APP_HOME}
RUN mkdir -p ${APP_HOME} ${DUMP_DIR} \
    && chown -R 1000:1000 ${APP_HOME} ${DUMP_DIR} || true

COPY --from=build /workspace/target/*.jar app.jar

# Run as unprivileged numeric user (1000) — avoids running as root.
USER 1000

# Application port (matches `server.port` in application.yml)
EXPOSE 5000

# Persist heap dumps: recommend mounting a host directory here when running.
VOLUME ["${DUMP_DIR}"]

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]