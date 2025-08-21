# Build stage
FROM maven:3.8-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# Production stage
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/sigr-backend-1.0.0.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]