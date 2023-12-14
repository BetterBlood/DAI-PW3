# Start from the Java 17 Temurin image
FROM eclipse-temurin:17 as builder

# Set the working directory
WORKDIR /app

# Copy the dependencies
COPY .mvn .mvn
COPY mvnw mvnw
COPY pom.xml pom.xml

# Download the dependencies and their transitive dependencies
RUN ./mvnw dependency:go-offline

# Copy the source code
COPY src src

# Build the application
RUN ./mvnw package

# Start from the Java 17 Temurin image
FROM eclipse-temurin:17

# Set the working directory
WORKDIR /app

# Copy the jar file from the builder stage
COPY --from=builder /app/target/dai-pw3-1.0-SNAPSHOT.jar /app/dai-pw3-1.0-SNAPSHOT.jar

# Set the entrypoint
ENTRYPOINT ["java", "-jar", "dai-pw3-1.0-SNAPSHOT.jar"]

# Set the default command
CMD ["--help"]