# DS1 – Développement Web : API REST de Vente en ligne (Spring Boot)

## 1. Contexte

Une boutique en ligne permet à des **clients** de consulter des **produits** classés par **catégories**,
de les ajouter à leurs **favoris**, de passer des **commandes** (composées de **lignes de commande**),
de les **payer**, de suivre leur **livraison**, puis de laisser un **avis** sur les produits reçus.

Cycle de vie d'une commande :

```
EN_ATTENTE ──payer──► PAYEE ──expédier──► EXPEDIEE ──livrer──► LIVREE
     │                  │
     └────annuler───────┴──► ANNULEE   (stock restauré, paiement REMBOURSE)
```

## 2. Technologies

- Java 17, Spring Boot 3.3.5
- Spring Web, Spring Data JPA (Hibernate), Bean Validation
- MySQL (XAMPP) — base `vente_en_ligne` créée automatiquement
- Lombok (`@Getter`, `@Setter`, `@NoArgsConstructor`, `@RequiredArgsConstructor`)
- Tests : JUnit 5 + H2 en mémoire ; Postman pour les tests fonctionnels

## 3. Structure du projet

```
src/
├─ main/java/com/khalil/projet/
│   ├─ model/        → 8 entités JPA + 4 enums
│   ├─ repository/   → interfaces JpaRepository + requêtes personnalisées
│   ├─ service/      → logique métier et règles de gestion
│   ├─ controller/   → API REST
│   ├─ dto/          → objets de requête / réponse (records Java)
│   ├─ exception/    → exceptions métier + gestionnaire global (@RestControllerAdvice)
│   ├─ config/       → données de démonstration au démarrage
│   └─ ProjetApplication.java
├─ main/resources/application.properties
└─ test/             → tests JUnit de la logique des commandes
postman/vente-en-ligne.postman_collection.json
```

## 4. Entités et relations

| Entité | Attributs principaux | Relations |
|---|---|---|
| **Categorie** | id, nom (unique), description | OneToMany → Produit |
| **Produit** | id, nom, description, prix, stock | ManyToOne → Categorie, OneToMany → Avis |
| **Client** | id, nom, email (unique), telephone, adresse, dateInscription | OneToMany → Commande, **ManyToMany → Produit (favoris)** |
| **Commande** | id, reference, dateCommande, statut, montantTotal | ManyToOne → Client, OneToMany → LigneCommande (cascade) |
| **LigneCommande** | id, quantite, prixUnitaire, sousTotal (calculé) | ManyToOne → Commande, ManyToOne → Produit |
| **Paiement** | id, montant, methode, statut, datePaiement | OneToOne → Commande |
| **Livraison** | id, adresse, ville, codePostal, transporteur, numeroSuivi, statut, dates | OneToOne → Commande |
| **Avis** | id, note (1–5), commentaire, dateAvis | ManyToOne → Client, ManyToOne → Produit (unique client+produit) |

Enums : `StatutCommande`, `StatutPaiement`, `MethodePaiement`, `StatutLivraison`.

```
Categorie 1───* Produit *───* Client (favoris)
                  │1            │1
                  │             │
                  *             *
            LigneCommande *───1 Commande 1───1 Paiement
                                   │1
                                   └───1 Livraison
Produit 1───* Avis *───1 Client
```

**Choix de conception :**
- `LigneCommande` est une entité à part (et non un simple ManyToMany Commande–Produit) car elle porte des données : quantité et prix au moment de l'achat.
- Le prix est copié dans la ligne (`prixUnitaire`) : si le prix du produit change, les anciennes commandes ne changent pas.
- Les côtés inverses des relations (`produits`, `commandes`, `favoris`...) sont annotés `@JsonIgnore` pour éviter les boucles infinies en JSON.
- `BigDecimal` pour les montants (pas d'erreurs d'arrondi comme avec `double`).

## 5. Règles métier

1. **Stock** : une commande est refusée si la quantité dépasse le stock ; le stock est décrémenté à la création, restauré à l'annulation ou la modification.
2. **Paiement** : le montant payé doit être exactement égal au total ; sinon le paiement est refusé et la commande reste `EN_ATTENTE`.
3. **Transitions de statut** : on ne peut payer qu'une commande `EN_ATTENTE`, expédier qu'une commande `PAYEE`, livrer qu'une commande `EXPEDIEE`.
4. **Annulation** : possible seulement avant l'expédition ; si la commande était payée, le paiement passe à `REMBOURSE`.
5. **Avis** : un client ne peut noter qu'un produit qu'il a reçu (commande `LIVREE`), et une seule fois.
6. **Suppressions protégées** : impossible de supprimer une catégorie qui contient des produits, un produit déjà commandé, un client qui a des commandes (→ 409).
7. **Unicité** : email client et nom de catégorie uniques (→ 409).

Toutes les méthodes de service sont `@Transactional` : si une erreur arrive au milieu (ex. stock insuffisant sur la 2e ligne), tout est annulé.

## 6. Requêtes personnalisées (repository)

| Besoin métier | Méthode | Type |
|---|---|---|
| Produits les plus vendus | `ProduitRepository.findMeilleuresVentes` | JPQL `GROUP BY` + `SUM` + DTO |
| Chiffre d'affaires sur une période | `CommandeRepository.calculerChiffreAffaires` | JPQL `SUM` + `BETWEEN` |
| Clients ayant le plus dépensé | `ClientRepository.findMeilleursClients` | JPQL `JOIN` + `GROUP BY` + DTO |
| Note moyenne d'un produit | `AvisRepository.calculerNoteMoyenne` | JPQL `AVG` |
| Alerte de stock faible | `ProduitRepository.findByStockLessThanEqualOrderByStockAsc` | Requête dérivée |
| Recherche / filtre prix | `findByNomContainingIgnoreCase`, `findByPrixBetweenOrderByPrixAsc` | Requêtes dérivées |
| Vérifier qu'un client a reçu un produit | `LigneCommandeRepository.existsByCommandeClientIdAndProduitIdAndCommandeStatut` | Requête dérivée sur relations |

## 7. Endpoints

Base : `http://localhost:8080`

### Catégories `/api/categories`
| Méthode | URL | Description | Code |
|---|---|---|---|
| GET | `/api/categories` | Lister | 200 |
| GET | `/api/categories/{id}` | Détail | 200 / 404 |
| GET | `/api/categories/{id}/produits` | Produits de la catégorie | 200 |
| POST | `/api/categories` | Créer | 201 / 400 / 409 |
| PUT | `/api/categories/{id}` | Modifier | 200 |
| DELETE | `/api/categories/{id}` | Supprimer | 204 / 409 |

### Produits `/api/produits`
| Méthode | URL | Description |
|---|---|---|
| GET / POST | `/api/produits` | Lister / créer |
| GET / PUT / DELETE | `/api/produits/{id}` | Détail / modifier / supprimer |
| GET | `/api/produits/recherche?nom=souris` | Recherche par nom |
| GET | `/api/produits/prix?min=50&max=200` | Filtre par prix |
| GET | `/api/produits/alerte-stock?seuil=5` | Produits à réapprovisionner |
| GET | `/api/produits/{id}/avis` | Avis du produit |
| GET | `/api/produits/{id}/note-moyenne` | Note moyenne |

Exemple de corps :
```json
{ "nom": "Tapis de yoga", "description": "6 mm", "prix": 59.90, "stock": 25, "categorie": { "id": 1 } }
```

### Clients `/api/clients`
| Méthode | URL | Description |
|---|---|---|
| GET / POST | `/api/clients` | Lister / créer |
| GET / PUT / DELETE | `/api/clients/{id}` | Détail / modifier / supprimer |
| GET | `/api/clients/{id}/commandes` | Historique des commandes |
| GET | `/api/clients/{id}/favoris` | Favoris |
| POST / DELETE | `/api/clients/{id}/favoris/{produitId}` | Ajouter / retirer un favori |

### Commandes `/api/commandes`
| Méthode | URL | Description | Code |
|---|---|---|---|
| GET | `/api/commandes` (option `?statut=PAYEE`) | Lister / filtrer | 200 |
| GET | `/api/commandes/{id}` | Détail avec lignes | 200 / 404 |
| POST | `/api/commandes` | Passer une commande | 201 / 400 |
| PUT | `/api/commandes/{id}` | Modifier (si EN_ATTENTE) | 200 / 400 |
| DELETE | `/api/commandes/{id}` | Supprimer (si EN_ATTENTE ou ANNULEE) | 204 / 400 |
| PUT | `/api/commandes/{id}/annuler` | Annuler | 200 / 400 |
| POST | `/api/commandes/{id}/paiement` | Payer | 201 / 400 |
| GET | `/api/commandes/{id}/paiement` | Voir le paiement | 200 / 404 |
| POST | `/api/commandes/{id}/expedier` | Expédier (crée la livraison) | 201 / 400 |
| PUT | `/api/commandes/{id}/livrer` | Confirmer la livraison | 200 / 400 |
| GET | `/api/commandes/{id}/livraison` | Voir la livraison | 200 / 404 |

```json
POST /api/commandes
{ "clientId": 1, "lignes": [ { "produitId": 2, "quantite": 2 }, { "produitId": 4, "quantite": 1 } ] }

POST /api/commandes/1/paiement
{ "montant": 1390.00, "methode": "CARTE" }

POST /api/commandes/1/expedier
{ "adresse": "12 rue de Carthage", "ville": "Tunis", "codePostal": "1000", "transporteur": "Aramex" }
```

### Paiements, livraisons, avis, statistiques
| Méthode | URL | Description |
|---|---|---|
| GET | `/api/paiements` (option `?statut=REMBOURSE`), `/api/paiements/{id}` | Consulter les paiements |
| GET | `/api/livraisons` (option `?statut=EN_COURS`), `/api/livraisons/{id}` | Consulter les livraisons |
| PUT | `/api/livraisons/{id}` | Corriger adresse / transporteur (si pas encore livrée) |
| GET / POST | `/api/avis` | Lister / créer un avis |
| GET / PUT / DELETE | `/api/avis/{id}` | Détail / modifier / supprimer |
| GET | `/api/statistiques/meilleures-ventes?limit=5` | Top produits |
| GET | `/api/statistiques/meilleurs-clients?limit=5` | Top clients |
| GET | `/api/statistiques/chiffre-affaires?debut=2026-01-01&fin=2026-12-31` | Chiffre d'affaires |

## 8. Gestion des erreurs

Toutes les erreurs renvoient le même format JSON (classe `GlobalExceptionHandler`) :

```json
{
  "timestamp": "2026-11-08T10:15:30",
  "status": 400,
  "error": "Bad Request",
  "message": "Données invalides",
  "path": "/api/produits",
  "erreursValidation": { "prix": "Le prix doit être supérieur à 0", "stock": "Le stock ne peut pas être négatif" }
}
```

| Code | Cas |
|---|---|
| 400 | Validation (`@Valid`), règle métier (stock, montant, statut), JSON ou paramètre invalide |
| 404 | Ressource ou URL introuvable |
| 409 | Doublon (email, catégorie, avis) ou suppression impossible (données liées) |
| 500 | Erreur inattendue |

## 9. Exécution

1. Démarrer **MySQL** dans XAMPP (port 3306, utilisateur `root` sans mot de passe).
   Si votre MySQL a un mot de passe, modifiez `spring.datasource.password` dans `application.properties`.
2. Ouvrir le projet dans IntelliJ / Spring Tools / VS Code (import Maven) et lancer `ProjetApplication`,
   ou en ligne de commande : `mvn spring-boot:run`
3. Au premier démarrage, la base `vente_en_ligne` et les tables sont créées, puis des données de
   démonstration sont insérées (3 catégories, 6 produits, 3 clients).
4. Tests unitaires : `mvn test` (utilise H2, MySQL non nécessaire).

## 10. Tests Postman

1. Importer `postman/vente-en-ligne.postman_collection.json` dans Postman.
2. L'application doit tourner sur `http://localhost:8080` (variable `baseUrl`).
3. Clic droit sur la collection → **Run collection** : les 87 requêtes s'exécutent dans l'ordre
   et les identifiants créés sont enregistrés automatiquement dans des variables.
4. Chaque requête contient des tests automatiques (code HTTP, contenu de la réponse) ;
   les requêtes `ERREUR - ...` vérifient les cas d'erreur (400, 404, 409).

Les captures d'écran des tests réussis se trouvent dans le dossier `captures/`.
