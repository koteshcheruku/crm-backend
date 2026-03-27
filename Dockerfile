# Use Java 21 (recommended)
FROM eclipse-temurin:21-jdk

# Copy jar
COPY target/*.jar app.jar

# Run app
ENTRYPOINT ["java","-jar","/app.jar"]