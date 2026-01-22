FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

RUN apt-get update && apt-get install -y maven

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

EXPOSE 8080

CMD java -Dserver.port=$PORT -jar target/Alizone-0.0.1-SNAPSHOT.jar