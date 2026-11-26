# syntax=docker/dockerfile:1
FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace
COPY gradlew settings.gradle.kts settings-gradle.lockfile build.gradle.kts gradle.lockfile ./
COPY gradle gradle
RUN chmod +x gradlew
RUN --mount=type=cache,target=/root/.gradle ./gradlew --no-daemon dependencies > /dev/null
COPY src src
RUN --mount=type=cache,target=/root/.gradle ./gradlew --no-daemon bootJar -x test
RUN java -Djarmode=tools -jar build/libs/podcuaderno.jar extract --destination extracted

FROM eclipse-temurin:25-jre
RUN groupadd --system app && useradd --system --gid app --no-create-home app
WORKDIR /app
COPY --from=build /workspace/extracted/ ./
USER app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/podcuaderno.jar"]
