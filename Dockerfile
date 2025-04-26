# Используем официальный образ JDK 17
FROM eclipse-temurin:23-jdk

# Устанавливаем рабочую директорию внутри контейнера сабыр
WORKDIR /app

# Копируем всё в контейнер
COPY . .

# Собираем проект с помощью встроенного Maven Wrapper
RUN ./mvnw clean package -DskipTests

# Открываем порт 8080
EXPOSE 8080

# Указываем команду для запуска приложения
CMD ["java", "-jar", "target/lab2-0.0.1-SNAPSHOT.jar"]


