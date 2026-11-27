FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/three-tier-backend-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-XX:+UseSerialGC", "-Xms128m", "-Xmx384m", "-jar", "app.jar"]