FROM gradle:8.5-jdk17 AS builder

WORKDIR /app

COPY build.gradle settings.gradle gradle.properties ./
COPY gradle ./gradle
COPY buildSrc ./buildSrc
COPY HealthyEverything-api ./HealthyEverything-api
COPY HealthyEverything-Batch ./HealthyEverything-Batch





RUN gradle :HealthyEverything-api:bootJar --no-daemon -x test


FROM eclipse-temurin:17-jre

WORKDIR /app

ENV TZ=Asia/Seoul

COPY --from=builder /app/HealthyEverything-api/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
