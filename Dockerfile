FROM gradle:8.5.0-jdk21 AS builder

WORKDIR /app

COPY . .

RUN ./gradlew clean bootJar --no-daemon -Dorg.gradle.java.home=$JAVA_HOME

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY --from=builder /app/applications/app-service/build/libs/*.jar app.jar

EXPOSE 8080 8081

ENTRYPOINT ["java", "-jar", "app.jar"]