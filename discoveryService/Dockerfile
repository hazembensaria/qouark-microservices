FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 5002

ENTRYPOINT ["java", "-jar", "app.jar"]