FROM  eclipse-temurin:17-jdk
# Créer un répertoire de travail
WORKDIR /app
# Copier le jar et le script
COPY target/*.jar app.jar

# Exposer le port
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]

