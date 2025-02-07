# Stage 1: Build
FROM maven:3.8.5-openjdk-21 AS builder

# Install Chrome and required dependencies
RUN apt-get update && apt-get install -y \
    chromium \
    chromium-driver \
    libgconf-2-4 \
    xvfb \
    && rm -rf /var/lib/apt/lists/*

# Set display port to avoid crash
ENV DISPLAY=:99

WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8001
ENTRYPOINT ["java","-jar","app.jar"]
