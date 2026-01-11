FROM openjdk:27-ea-oracle
# Créer un répertoire de travail
WORKDIR /app
# Copier le jar et le script
COPY target/*.jar app.jar

# Exposer le port
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "app.jar"]

