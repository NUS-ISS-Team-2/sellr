# Build stage: use Maven to build the Spring Boot jar
FROM maven:3.9.2-eclipse-temurin-20 AS build

WORKDIR /build

# Copy pom.xml first to leverage Docker caching
COPY pom.xml .
COPY checkstyle.xml .


# Download dependencies only
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the Spring Boot jar (skip tests for faster build)
RUN mvn clean package -DskipTests

# Run stage: lightweight Java runtime
FROM eclipse-temurin:20-jdk-alpine

WORKDIR /app

# Copy the jar from build stage
COPY --from=build /build/target/*.jar app.jar

# Expose default Spring Boot port
ENV PORT=8080
EXPOSE $PORT

# Run the jar
ENTRYPOINT java -jar app.jar --server.port=$PORT