FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml ./
COPY common/pom.xml common/pom.xml
COPY order-service/pom.xml order-service/pom.xml
RUN mvn -pl order-service -am dependency:go-offline
COPY common/src common/src
COPY order-service/src order-service/src
RUN mvn -pl order-service -am package -DskipTests

FROM eclipse-temurin:25-jre AS run
WORKDIR /app
COPY --from=build /app/order-service/target/order-service.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]