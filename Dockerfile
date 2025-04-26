FROM eclipse-temurin:23-jdk

WORKDIR /app

COPY . .

RUN ./mvnw clean package -DskipTests

EXPOSE 8080

CMD ["java", "-jar", "target/lab2-0.0.1-SNAPSHOT.jar"]


