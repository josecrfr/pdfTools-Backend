FROM eclipse-temurin:21

WORKDIR /app

# Copiar el JAR con el nombre exacto
COPY target/tools-0.0.1-SNAPSHOT.jar app.jar

# EXPOSE 4050

ENTRYPOINT ["java", "-jar", "app.jar"]
