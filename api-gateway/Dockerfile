FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml ./
COPY api-gateway/pom.xml api-gateway/pom.xml
RUN mvn -pl api-gateway -am dependency:go-offline
COPY api-gateway/src api-gateway/src
RUN mvn -pl api-gateway -am package -DskipTests

FROM eclipse-temurin:25-jre AS run
WORKDIR /app
COPY --from=build /app/api-gateway/target/api-gateway.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]