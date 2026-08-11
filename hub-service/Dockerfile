FROM eclipse-temurin:17-jdk AS builder

WORKDIR /workspace

COPY gradlew .
COPY gradle gradle
COPY settings.gradle .
COPY build.gradle .

RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon

COPY src src

RUN ./gradlew bootJar --no-daemon


FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=builder /workspace/build/libs/*.jar app.jar

EXPOSE 19007

ENTRYPOINT ["java", "-jar", "app.jar"]
