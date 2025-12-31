
# VertDrop — Delivery Management System v0.1.0

## Présentation
VertDrop est une application back-end Spring Boot destinée à la gestion des livraisons. Elle permet de suivre, planifier et optimiser l’envoi de colis, d’assurer la traçabilité via un historique des statuts et de fournir des APIs paginées, filtrables et recherchables pour une utilisation côté front ou mobile.

## Contexte
La société cliente souhaite remplacer une gestion manuelle (Excel / papier) par une solution web pour réduire les erreurs, améliorer la traçabilité et optimiser les tournées par zones et priorités. VertDrop sert de base évolutive pour un futur Système d’Information Logistique.

## Objectifs principaux
- Centraliser la gestion : clients expéditeurs, destinataires, livreurs, zones, produits, colis.
- Suivre le cycle complet d’un colis (création → collecte → transit → livraison).
- Fournir historique complet pour chaque colis (traçabilité).
- Recherche, filtrage et pagination performants pour de larges jeux de données.
- Validation côté serveur, gestion centralisée des erreurs et journalisation       .

## Utilisateurs
- Gestionnaire logistique
- Livreur
- Client expéditeur
- Destinataire

## Fonctionnalités implémentées (v0.1.0)
- CRUD : ClientExpéditeur, Destinataire, Livreur, Zone, Produit, Colis.
- Pagination & tri (Pageable) pour les listes.
- Filtrage (statut, zone, priorité, date) et recherche par mot-clé.
- Historique des statuts (HistoriqueLivraison) et endpoint pour consulter l’historique d’un colis.
- Validation DTO via Jakarta Validation (@Valid, @NotBlank, @Email, @Size, @Positive, ...).
- Gestion centralisée des exceptions (@ControllerAdvice) avec format d’erreur unifié.
- NotFoundException pour centraliser les 404.
- Journalisation SLF4J dans services et controllers.
- Migrations via Liquibase.
- Documentation OpenAPI / Swagger (si activé).

## Modèle métier (tables principales)
- ClientExpediteur : id, nom, prenom, email, telephone, adresse
- Destinataire : id, nom, prenom, email, telephone, adresse
- Livreur : id, nom, prenom, telephone, véhicule, zoneAssignée, disponible
- Colis : id, description, poids, statut, priorite, livreur_id, client_id, destinataire_id, zone_id, villeDestination
- Zone : id, nom, codePostal
- HistoriqueLivraison : id, colis_id, statut, dateChangement, commentaire
- Produit, Colis_Produit (multi-produits)

## Architecture & bonnes pratiques
- Layers : Controller → Service → Repository (Spring Data JPA)
- DTOs + MapStruct pour la sérialisation / mapping
- Validation des entrées côté contrôleur (@Valid sur @RequestBody)
- Exceptions métiers (NotFoundException) et GlobalExceptionHandler
- SLF4J pour logs (LoggerFactory.getLogger(...))
- Pagination & filtre via Pageable et requêtes JPQL flexibles

## Dépendances essentielles
- Spring Boot (Web, Data JPA)
- spring-boot-starter-validation (Jakarta Validation)
- MapStruct
- Liquibase
- PostgreSQL (ou H2 pour dev)
- SLF4J (via Spring Boot Starter)
- Swagger / springdoc-openapi (optionnel)

> Exemple de dépendance à ajouter (pom.xml)
```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

## Installation & exécution (local)
1. Cloner le dépôt :
```bash
git clone <repo-url>
cd vertdrop
```

2. Configurer la base de données (Postgres) et variables d’environnement (ou application.yml) :
- SPRING_DATASOURCE_URL
- SPRING_DATASOURCE_USERNAME
- SPRING_DATASOURCE_PASSWORD

Exemple minimal application.yml :
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/vertdrop
    username: vertdrop_user
    password: secret
  jpa:
    hibernate:
      ddl-auto: validate
liquibase:
  enabled: true
```

3. Démarrer Postgres (docker optionnel) :
```bash
docker run --name vertdrop-db -e POSTGRES_USER=vertdrop_user -e POSTGRES_PASSWORD=secret -e POSTGRES_DB=vertdrop -p 5432:5432 -d postgres:15
```

4. Lancer l’application :
```bash
mvn clean spring-boot:run
# ou
mvn clean package -DskipTests
java -jar target/vertdrop-0.1.0.jar
```

## Endpoints clés (exemples)
- POST /api/clients         — créer ClientExpediteur (Corps JSON valide, @Valid)
- GET  /api/clients         — lister (pageable)
- POST /api/colis           — créer Colis (ColisDTO validé)
- GET  /api/colis           — lister/paginer/filtrer/rechercher
  - params: page, size, sort, statut, zoneId, keyword
- PUT  /api/colis/{id}/status — mettre à jour statut (UpdateStatusRequestDTO, @Valid)
- GET  /api/colis/{id}/history — récupérer historique du colis

## Validation & erreur unifiée
- DTOs d’entrée annotés (ex. @NotBlank, @Email, @Size, @Positive)
- Contrôleurs : ajouter @Valid sur tous les @RequestBody de création/mise à jour
- GlobalExceptionHandler intercepte :
  - MethodArgumentNotValidException → 400 avec ErrorResponseDTO { timestamp, status, error, message, path }
  - NotFoundException → 404 formaté de la même façon
  - autres exceptions métier → 500/forme standardisée

## Logging (SLF4J)
- Ajoutez dans chaque controller/service :
```java
private static final Logger log = LoggerFactory.getLogger(NomDeLaClasse.class);
```
- Loggez entrées importantes, succès et erreurs :
  - log.info("Créer colis: {}", dto.getDescription());
  - log.error("Erreur lors de ...", exception);

## Remplacement Optional → NotFoundException
- Avant (pattern à remplacer) :
```java
return service.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
```
- Après (centralisé) :
```java
ColisDTO dto = service.findByIdOrThrow(id); // service lance NotFoundException si absent
return ResponseEntity.ok(dto);
```
- Le GlobalExceptionHandler transformera NotFoundException en réponse 404 uniforme.

## Tests & vérifications (Postman)
- Importez la collection Postman fournie (si disponible).
- Scénarios recommandés :
  1. Créer un client valide → 201
  2. Créer un colis valide → 201 et vérifier création d’une entrée historique initiale
  3. Mettre à jour statut plusieurs fois → vérifier historique (GET /api/colis/{id}/history)
  4. Tester pagination & recherche : GET /api/colis?page=0&size=5&keyword=nom
  5. Tester validation : envoyer payload incomplet → 400 ErrorResponseDTO
  6. Appeler GET sur id inexistant → 404 ErrorResponseDTO

## Migrations & BDD
- Liquibase : changelogs dans `src/main/resources/db/changelog`
- Les tables principales : clients, destinataires, livreurs, zones, produits, colis, historique_livraison, colis_produit

## Roadmap (epics)
- EPIC-6 : Pagination, filtrage & recherche — implémenté
- EPIC-7 : Historique & traçabilité — implémenté
- EPIC-8 : Validation, exceptions & logging — en cours (pattern @Valid, NotFoundException, GlobalExceptionHandler, SLF4J)
- Futurs : Auth / RBAC, notifications email, optimisation planification tournées, dashboard

## Contribution
- Fork → feature branch → PR → revue
- Règles de commit et format de code : respecter conventions Java/Spring et mapper DTOs via MapStruct
- Ouvrir issues pour bugs/feature requests

## Auteurs & contact
- Auteur principal : Bensaltana Houssam  
- Référent projet : Nafia Akdi

## Licence
HoussamBensaltana

---
```
