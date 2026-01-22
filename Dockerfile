# Base image
FROM eclipse-temurin:17-jdk-jammy

# Çalışma dizini
WORKDIR /app

# Maven yükle
RUN apt-get update && apt-get install -y maven

# Kaynak kod ve pom.xml
COPY pom.xml . 
COPY src ./src

# Build et
RUN mvn clean package -DskipTests

# Render kendi $PORT environment variable'ını veriyor
EXPOSE 8080

# CMD array formatında, Render bunu daha güvenli algılıyor
CMD ["java", "-Dserver.port=$PORT", "-jar", "target/Alizone-0.0.1-SNAPSHOT.jar"]