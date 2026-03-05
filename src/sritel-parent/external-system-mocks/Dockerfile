FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml ./pom.xml
COPY external-system-mocks ./external-system-mocks
COPY user-service ./user-service
COPY billing-service ./billing-service
COPY notification-service ./notification-service

RUN mvn -pl external-system-mocks -am -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/external-system-mocks/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
