FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY usage-query-service/build/libs/usage-query-service.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
