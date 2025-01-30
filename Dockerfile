FROM openjdk:17
      WORKDIR /app
      COPY .mvn/ .mvn
      COPY mvnw pom.xml ./
      RUN ./mvnw dependency:go-offline
      COPY src ./src
      RUN ./mvnw package -DskipTests
      CMD ["java", "-jar", "target/my-app.jar"]