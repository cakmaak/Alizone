# 1. Temel image (Java 17)
FROM eclipse-temurin:17-jdk-jammy

# 2. Çalışma dizini
WORKDIR /app

# 3. Maven wrapper ve pom.xml kopyala
COPY mvnw .
COPY .mvn/ .mvn
COPY pom.xml .

# 4. Dependencies'i indir
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B

# 5. Tüm kaynak kodunu kopyala
COPY src ./src

# 6. Build
RUN ./mvnw clean package -DskipTests

# 7. JAR dosyasını çalıştır
CMD ["java", "-jar", "target/Alizone-0.0.1-SNAPSHOT.jar"]
