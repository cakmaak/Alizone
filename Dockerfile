# Base image
FROM eclipse-temurin:17-jdk-jammy

# Çalışma dizini
WORKDIR /app

# Maven ve diğer paketleri yükle
RUN apt-get update && apt-get install -y maven

# Kaynak kodu ve pom.xml'i kopyala
COPY pom.xml .
COPY src ./src

# Build et
RUN mvn clean package -DskipTests

# Port
EXPOSE 8080

# Uygulamayı başlat
CMD CMD ["java", "-Dserver.port=$PORT", "-jar", "target/Alizone-0.0.1-SNAPSHOT.jar"]