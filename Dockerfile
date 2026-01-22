# Build stage
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=build /app/target/Alizone-0.0.1-SNAPSHOT.jar app.jar

# Expose port for Render
EXPOSE 8080

# Bind Spring Boot to all interfaces
ENV PORT 8080
CMD ["sh", "-c", "java -Dserver.address=0.0.0.0 -Dserver.port=$PORT -jar app.jar"]