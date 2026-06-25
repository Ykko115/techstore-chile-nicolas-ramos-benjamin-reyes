# ---- Etapa 1: Build (Maven + JDK) ----
FROM maven:3.8.8-eclipse-temurin-17 AS build
WORKDIR /app

# Copia solo el pom.xml primero para cachear las dependencias
COPY pom.xml ./
RUN mvn -B dependency:go-offline

# Copia el código fuente y empaqueta el .jar
COPY src ./src
RUN mvn -B -DskipTests package

# ---- Etapa 2: Runtime (JRE liviano, usuario no-root) ----
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# Ejecuta el contenedor con el usuario sin privilegios "nobody" (ya existe en alpine)
RUN chown nobody:nobody /app/app.jar
USER nobody

# Puerto en el que escucha el microservicio Spring Boot dentro del contenedor
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]