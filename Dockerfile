# Builder stage
FROM maven:3.9.6-eclipse-temurin-17 as MAVEN_BUILD
WORKDIR /build

# Copy the project files
COPY src /build/src
COPY pom.xml /build

# Build the application
RUN mvn package -DskipTests

# Final stage
FROM eclipse-temurin:17-jre-jammy

# Creating app directory
WORKDIR /app

# Security best practices
# Create a user group and user to run our app instead of root
RUN addgroup --system app && adduser --system --group app
USER app:app

# We copy the built JAR from builder stage to the final stage
COPY --from=MAVEN_BUILD /build/target/*.jar app.jar

# Expose port 8080 for outside world
EXPOSE 8080

# Run the JAR
ENTRYPOINT ["java", "-jar", "app.jar"]