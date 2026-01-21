# =========================
# Build stage
# =========================
FROM eclipse-temurin:25-jdk AS builder

WORKDIR /app

# Copy Maven wrapper & pom first (for caching)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN chmod +x mvnw
RUN ./mvnw -B -DskipTests dependency:go-offline

# Copy source
COPY src src

# Build jar
RUN ./mvnw -B -DskipTests clean package

# =========================
# Runtime stage
# =========================
FROM eclipse-temurin:25-jre

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
