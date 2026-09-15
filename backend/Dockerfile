# Use a supported JDK
FROM eclipse-temurin:24

# Set a working directory
WORKDIR /app

# Copy your fat JAR into the image
COPY build/libs/app.jar app.jar

# Expose the port your app runs on
EXPOSE 8080

# Set JVM options (memory, headroom, etc.)
ENV JAVA_TOOL_OPTIONS="-Xmx8192m -Xms512m"

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

