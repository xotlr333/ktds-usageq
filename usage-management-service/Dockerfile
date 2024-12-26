FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY usage-management-service/build/libs/usage-management-service.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
