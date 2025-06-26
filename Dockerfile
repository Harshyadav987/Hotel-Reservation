# Java 17 runtime image
FROM eclipse-temurin:17-jdk

# Jar file ko container me copy karo
COPY target/*.jar app.jar

# Container port
EXPOSE 8080

# App run karne ka command
ENTRYPOINT ["java", "-jar", "/app.jar"]
