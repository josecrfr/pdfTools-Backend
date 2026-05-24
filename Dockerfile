FROM eclipse-temurin:21-jdk

WORKDIR /app

# Instalar Python
RUN apt-get update && apt-get install -y \
    python3 \
    python3-pip \
    python3-venv

# Copiar proyecto
COPY . .

# Crear entorno virtual
RUN python3 -m venv venv

# Instalar dependencias Python
RUN ./venv/bin/pip install PyMuPDF Pillow

# Dar permisos
RUN chmod +x mvnw

# Compilar Spring Boot
RUN ./mvnw clean package -DskipTests

EXPOSE 8080

CMD ["java", "-jar", "target/tools-0.0.1-SNAPSHOT.jar"]