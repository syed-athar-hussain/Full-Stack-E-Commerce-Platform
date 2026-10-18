FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app
COPY ../pom.xml ./
COPY pom.xml user-service/pom.xml
RUN mvn -pl user-service -am dependency:go-offline
COPY src user-service/src
RUN mvn -pl user-service -am package -DskipTests

FROM eclipse-temurin:25-jre AS run
WORKDIR /app
COPY --from=build /app/user-service/target/user-service.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]