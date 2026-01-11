#  Projet MSA - Système de Gestion d'Emprunts de Livres

**Étudiant :** Amal Elbahari  
**Date :** Janvier 2026  
**Lien GitHub :** https://github.com/Amal-Elbahari/microservice_project

---

## 📌 Description du Projet

Application de gestion d'emprunts de livres construite avec une **architecture microservices**, respectant les principes de séparation des responsabilités et de communication asynchrone. Le système permet de gérer des utilisateurs, des livres et des emprunts avec notifications en temps réel via Kafka.

---

## 🎯 Objectifs Réalisés

✅ Architecture microservices avec **6 services** indépendants  
✅ Base de données MySQL séparée par service (**Database per Service**)  
✅ Communication asynchrone via **Apache Kafka**  
✅ Service de découverte avec **Eureka**  
✅ API Gateway pour routage centralisé  
✅ Déploiement complet avec **Docker Compose**  
✅ Notifications en temps réel lors de la création d'emprunts  

---

## 🏗️ Architecture du Système

### Vue d'ensemble
```
┌──────────────┐
│   Client     │
└──────┬───────┘
       │
       ↓
┌──────────────────────────────────────────────────────────┐
│                    API Gateway (9999)                     │
│           Point d'entrée unique + Load Balancing          │
└────────┬──────────────────┬──────────────────┬───────────┘
         │                  │                  │
         ↓                  ↓                  ↓
┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐
│  Book Service   │ │  User Service   │ │ Emprunt Service │
│   (Port 8081)   │ │   (Port 8082)   │ │   (Port 8085)   │
│   MySQL: db_book│ │ MySQL: db_user  │ │MySQL:db_emprunter│
└─────────────────┘ └─────────────────┘ └────────┬────────┘
                                                  │
                                                  │ Kafka Event
                                                  ↓
                                          ┌───────────────────┐
                                          │ Notification Svc  │
                                          │   (Port 8084)     │
                                          │  Kafka Consumer   │
                                          └───────────────────┘

        ┌────────────────────────────────────────┐
        │     Eureka Server (Port 8761)          │
        │      Service Discovery & Registry       │
        └────────────────────────────────────────┘
```

### Microservices

| Service | Port | Rôle | Base de données | Technologies |
|---------|------|------|-----------------|--------------|
| **Eureka Server** | 8761 | Service de découverte et registre | - | Spring Cloud Eureka |
| **API Gateway** | 9999 | Point d'entrée unique, routage dynamique | - | Spring Cloud Gateway |
| **User Service** | 8082 | Gestion des utilisateurs | `db_user` | Spring Boot, JPA, MySQL |
| **Book Service** | 8081 | Gestion des livres | `db_book` | Spring Boot, JPA, MySQL |
| **Emprunt Service** | 8085 | Gestion des emprunts + Kafka Producer | `db_emprunter` | Spring Boot, JPA, MySQL, Kafka, Feign |
| **Notification Service** | 8084 | Notifications asynchrones | - | Spring Boot, Kafka Consumer |

### Infrastructure

- **MySQL 8.0** : 3 bases de données séparées (principe Database per Service)
- **Apache Kafka + Zookeeper** : Communication asynchrone event-driven
- **Docker & Docker Compose** : Conteneurisation et orchestration

---

## 🔄 Flux de Fonctionnement

### Création d'un Emprunt
```
1. Client envoie requête POST → API Gateway (9999)
2. Gateway route vers → Emprunt Service (8085)
3. Emprunt Service vérifie via Feign:
   - User existe ? (appel à User Service)
   - Book existe ? (appel à Book Service)
4. Emprunt créé dans MySQL (db_emprunter)
5. Event Kafka publié → Topic "emprunt-created"
6. Notification Service consomme l'event
7. Notification affichée dans les logs
```

### Communication Inter-Services

- **Synchrone** : Feign Client (Emprunt → User/Book)
- **Asynchrone** : Kafka (Emprunt → Notification)
- **Service Discovery** : Eureka (tous les services s'enregistrent automatiquement)

---

## 📊 Modèle de Données

### User (db_user)
```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255)
);
```

### Book (db_book)
```sql
CREATE TABLE books (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255),
    author VARCHAR(255),
    available BOOLEAN DEFAULT TRUE
);
```

### Emprunt (db_emprunter)
```sql
CREATE TABLE emprunts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    emprunt_date DATETIME,
    return_date DATETIME
);
```

### Event Kafka
```json
{
  "empruntId": 1,
  "userId": 1,
  "bookId": 1,
  "eventType": "EMPRUNT_CREATED",
  "timestamp": "2026-01-11T12:00:00"
}
```

---

## 🚀 Installation et Démarrage

### Prérequis

- **Docker Desktop** (installé et démarré)
- **Java 17+**
- **Maven 3.6+**
- **Git**

### Étapes de Démarrage

#### 1. Cloner le Projet
```bash
git clone https://github.com/Amal-Elbahari/microservice_project.git
cd microservice_project
```

#### 2. Compiler Tous les Services
```bash
# Option A : Utiliser le script (Windows)
build-all.bat

# Option B : Compilation manuelle
cd eureka-server && mvn clean package -DskipTests && cd ..
cd gateway && mvn clean package -DskipTests && cd ..
cd user-service && mvn clean package -DskipTests && cd ..
cd book-service && mvn clean package -DskipTests && cd ..
cd emprunter && mvn clean package -DskipTests && cd ..
cd notification-service && mvn clean package -DskipTests && cd ..
```

#### 3. Démarrer l'Infrastructure
```bash
# Lancer tous les services
docker-compose up -d

# Attendre 2-3 minutes que tous les services démarrent
```

#### 4. Vérifier le Démarrage
```bash
# Vérifier que tous les conteneurs sont UP
docker ps

# Accéder au Dashboard Eureka
# Ouvrir dans le navigateur: http://localhost:8761
# Vous devriez voir les 5 services enregistrés
```

### Arrêt de l'Application
```bash
# Arrêter tous les services
docker-compose down

# Supprimer aussi les volumes (données MySQL)
docker-compose down -v
```

---

## 🧪 Tests et Utilisation

### 1. Créer un Utilisateur
```bash
curl -X POST http://localhost:9999/api/users \
  -H "Content-Type: application/json" \
  -d '{"name": "Alice Martin", "email": "alice@example.com"}'
```

**Réponse attendue :**
```json
{
  "id": 1,
  "name": "Alice Martin",
  "email": "alice@example.com"
}
```

### 2. Créer un Livre
```bash
curl -X POST http://localhost:9999/api/books \
  -H "Content-Type: application/json" \
  -d '{"title": "Clean Code", "author": "Robert Martin", "available": true}'
```

**Réponse attendue :**
```json
{
  "id": 1,
  "title": "Clean Code",
  "author": "Robert Martin",
  "available": true
}
```

### 3. Créer un Emprunt (Déclenche une Notification)
```bash
curl -X POST http://localhost:9999/api/emprunts \
  -H "Content-Type: application/json" \
  -d '{"userId": 1, "bookId": 1}'
```

**Réponse attendue :**
```json
{
  "id": 1,
  "userId": 1,
  "bookId": 1,
  "empruntDate": "2026-01-11T12:00:00",
  "returnDate": null
}
```

### 4. Vérifier la Notification Kafka
```bash
docker logs -f notification-service
```

**Logs attendus :**
```
📨 Événement Kafka reçu: EmpruntEvent(empruntId=1, userId=1, bookId=1, ...)
============================================================
🔔 NOTIFICATION - NOUVEL EMPRUNT CRÉÉ
============================================================
📌 ID Emprunt    : 1
👤 ID Utilisateur: 1
📚 ID Livre      : 1
📅 Date          : 2026-01-11T12:00:00
🏷️  Type          : EMPRUNT_CREATED
============================================================
✅ Notification envoyée avec succès!
```

### 5. Lister Tous les Emprunts
```bash
curl http://localhost:9999/api/emprunts
```

### 6. Accéder Directement aux Services (Bypass Gateway)
```bash
# User Service
curl http://localhost:8082/api/users

# Book Service
curl http://localhost:8081/api/books

# Emprunt Service
curl http://localhost:8085/api/emprunts
```

---

## 🔗 URLs Importantes

| Service | URL | Description |
|---------|-----|-------------|
| **Eureka Dashboard** | http://localhost:8761 | Voir tous les services enregistrés |
| **API Gateway** | http://localhost:9999 | Point d'entrée unique |
| **User Service** | http://localhost:8082/api/users | Gestion des utilisateurs |
| **Book Service** | http://localhost:8081/api/books | Gestion des livres |
| **Emprunt Service** | http://localhost:8085/api/emprunts | Gestion des emprunts |
| **Notification Service** | Port 8084 | Pas d'API REST (Kafka Consumer uniquement) |

---

## 🗄️ Accès aux Bases de Données

### Se Connecter à MySQL
```bash
# Accéder au conteneur MySQL
docker exec -it mysql-db mysql -uroot -proot

# Dans MySQL
SHOW DATABASES;

# Utiliser une base
USE db_user;
SELECT * FROM users;

USE db_book;
SELECT * FROM books;

USE db_emprunter;
SELECT * FROM emprunts;
```

### Schéma des Bases de Données

- **db_user** : Table `users` (id, name, email)
- **db_book** : Table `books` (id, title, author, available)
- **db_emprunter** : Table `emprunts` (id, user_id, book_id, emprunt_date, return_date)

---

## 📁 Structure du Projet
```
microservicesapp/
├── eureka-server/              # Service de découverte Eureka
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
├── gateway/                    # API Gateway
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
├── user-service/               # Microservice des utilisateurs
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
├── book-service/               # Microservice des livres
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
├── emprunter/                  # Microservice des emprunts
│   ├── src/
│   │   └── main/java/com/org/emprunt/
│   │       ├── EmpruntServiceApplication.java
│   │       ├── controller/
│   │       ├── service/
│   │       ├── repository/
│   │       ├── entity/
│   │       ├── dto/
│   │       ├── kafka/          # Kafka Producer
│   │       └── feign/          # Feign Clients
│   ├── pom.xml
│   └── Dockerfile
├── notification-service/       # Service de notifications
│   ├── src/
│   │   └── main/java/com/example/notificationservice/
│   │       ├── NotificationServiceApplication.java
│   │       ├── kafka/          # Kafka Consumer
│   │       ├── service/
│   │       └── dto/
│   ├── pom.xml
│   └── Dockerfile
├── docker-compose.yml          # Orchestration Docker
├── init-db.sql                 # Script d'initialisation MySQL
└── README.md                   # Ce fichier
```

---

## 🛠️ Technologies Utilisées

### Backend

- **Spring Boot** 2.7.14 / 3.3.0 - Framework principal
- **Spring Cloud** 2021.0.8 - Microservices patterns
- **Spring Cloud Eureka** - Service Discovery
- **Spring Cloud Gateway** - API Gateway
- **Spring Data JPA** - Persistence
- **Spring Cloud OpenFeign** - Communication synchrone
- **Spring Kafka** - Communication asynchrone

### Infrastructure

- **MySQL 8.0** - Base de données relationnelle
- **Apache Kafka** - Message broker
- **Zookeeper** - Coordination Kafka
- **Docker** - Conteneurisation
- **Docker Compose** - Orchestration

### Build & Développement

- **Maven** - Gestion des dépendances
- **Lombok** - Réduction du code boilerplate
- **Java 17** - Langage de programmation

---

## 🎯 Principes Architecturaux Appliqués

### 1. Database per Service
Chaque microservice possède sa propre base de données, garantissant l'indépendance et l'isolation des données.

### 2. Service Discovery
Eureka permet aux services de se découvrir dynamiquement sans configuration statique.

### 3. API Gateway Pattern
Point d'entrée unique pour tous les clients, simplifiant le routage et la sécurité.

### 4. Event-Driven Architecture
Utilisation de Kafka pour la communication asynchrone et le découplage des services.

### 5. Circuit Breaker Ready
Architecture préparée pour l'ajout de patterns de résilience (Hystrix, Resilience4j).

---

## 🐛 Dépannage

### Les Services ne Démarrent Pas
```bash
# Vérifier les logs d'un service
docker logs [nom_du_service]

# Exemple
docker logs gateway
docker logs emprunter
```

### Eureka ne Voit Pas les Services

- Vérifiez que tous les services pointent vers `http://eurika:8761/eureka/`
- Attendez 30-60 secondes après le démarrage
- Redémarrez le service : `docker-compose restart [service]`

### Kafka ne Fonctionne Pas
```bash
# Vérifier Kafka et Zookeeper
docker ps | grep kafka
docker ps | grep zookeeper

# Redémarrer Kafka
docker-compose restart kafka zookeeper
```

### MySQL Erreurs de Connexion
```bash
# Vérifier MySQL
docker logs mysql-db

# Redémarrer MySQL
docker-compose restart mysql-db
```

### Gateway Renvoie "Empty Reply"

- Vérifiez que le Gateway écoute sur le port 9999
- Vérifiez les routes dans `gateway/application.yml`
- Vérifiez que les services sont enregistrés dans Eureka

---

## 📈 Améliorations Futures

- [ ] Ajouter Spring Security pour l'authentification
- [ ] Implémenter Circuit Breaker (Resilience4j)
- [ ] Ajouter des tests unitaires et d'intégration
- [ ] Centraliser les logs (ELK Stack)
- [ ] Ajouter monitoring (Prometheus + Grafana)
- [ ] Implémenter API versioning
- [ ] Ajouter Swagger/OpenAPI documentation
- [ ] Implémenter gestion des retours de livres
- [ ] Notifications par email/SMS réelles
- [ ] Interface utilisateur (React/Angular)

---

## 📄 Licence

Ce projet est réalisé dans un cadre académique pour le cours MSA 2026.

---

## 👤 Auteur

**Amal Elbahari**  
Étudiant en data & software    
📧 Contact: amal.elbahari@gmail.com 
🔗 GitHub: https://github.com/Amal-Elbahari

---

## 📝 Notes de Rendu

- **Lien GitLab/GitHub :** https://github.com/Amal-Elbahari/microservice_project
- **Status :** ✅ Projet complet et fonctionnel

---

##  Remerciements

Merci au professeur pour les enseignements sur l'architecture microservices et les bonnes pratiques de développement distribué.

---

** Projet réalisé avec succès !**
