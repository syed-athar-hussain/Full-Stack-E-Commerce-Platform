FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml ./
COPY product-service/pom.xml product-service/pom.xml
RUN mvn -pl product-service -am dependency:go-offline
COPY product-service/src product-service/src
RUN mvn -pl product-service -am package -DskipTests

FROM eclipse-temurin:25-jre AS run
WORKDIR /app
COPY --from=build /app/product-service/target/product-service.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
