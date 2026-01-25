# syntax=docker/dockerfile:1

FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY BoardGameManagement-Backend/gradlew ./gradlew
COPY BoardGameManagement-Backend/gradle ./gradle
COPY BoardGameManagement-Backend/build.gradle* ./
COPY BoardGameManagement-Backend/settings.gradle* ./
COPY BoardGameManagement-Backend/src ./src

RUN chmod +x ./gradlew
RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/build/libs/*.jar ./app.jar

EXPOSE 8080
ENV JAVA_OPTS=""

CMD ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]