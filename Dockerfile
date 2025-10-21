# Use a supported JDK
FROM openjdk:24-jdk-slim

# Set a working directory
WORKDIR /app

# Copy your fat JAR into the image
COPY build/libs/app.jar app.jar

# Expose the port your app runs on
EXPOSE 8080

# Set JVM options (memory, headroom, etc.)
ENV JAVA_TOOL_OPTIONS="-Xmx1024m -Xms512m"

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

