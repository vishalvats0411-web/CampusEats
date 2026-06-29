# Stage 1: Build the application
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
# Copy the maven wrapper and pom.xml first to cache dependencies
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
# Ensure the wrapper is executable
RUN chmod +x ./mvnw
# Download dependencies
RUN ./mvnw dependency:go-offline

# Copy the rest of the source code
COPY src ./src
# Build the application, skipping tests to speed up deployment
RUN ./mvnw clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:21-jre
WORKDIR /app
# Copy the built jar from the build stage
COPY --from=build /app/target/CampusEats-0.0.1-SNAPSHOT.jar app.jar
# Expose the standard Spring Boot port
EXPOSE 8080
# Execute the application
ENTRYPOINT ["java", "-jar", "app.jar"]