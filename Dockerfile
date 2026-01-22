# Base image
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

# Maven yükle
RUN apt-get update && apt-get install -y maven

# Kaynak kod ve pom.xml
COPY pom.xml .
COPY src ./src

# Build
RUN mvn clean package -DskipTests

# Render portu kullanacak
EXPOSE 8080

# Spring Boot'u başlat
CMD java -Dserver.port=$PORT -jar target/Alizone-0.0.1-SNAPSHOT.jar