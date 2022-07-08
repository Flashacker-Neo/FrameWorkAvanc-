# Initialisation du projet
## Jhipster cli
Pour cet exercice j'ai commencé par lancer la commande :
```bash
jhipster
```
Celle-ci m'a permis de configurer mon projet jhipster et de choisir les technologies souhaités.
Une fois l'installation terminée je lance la commande suivante pour tester si la nouvelle installation fonctionne correctement :
```bash
./mvnw
```

De cette commande ressort l'erreur suivante :
```bash
java.sql.SQLSyntaxErrorException: Could not connect to address=(host=localhost)(port=3306)(type=master) : (conn=30) Unknown database 'exoFinal'
```

## Lancement de docker
Je me rend compte que j'ai oublier de lancer la base de donnée à l'aide de docker et lance donc les deux commandes suivantes :
```bash
sudo apt install docker-compose
docker-compose -f src/main/docker/mariadb.yml up
```

Suite à cette commande, second problème, il semblerait que la version de docker-compose dans le fichier `mariadb.yml` ne soit pas compatible avec ma version...
Je décide donc de changer la version dans le fichier `mariadb.yml` et de la passer de la `3.8` à la `3.1`, et je relance la commande :
```bash
docker-compose -f src/main/docker/mariadb.yml up
```

Docker ce lance correctement, je relance donc la commande suivante :
```bash
./mvnw
```
La commande se déroule correctement aussi et je peux accèder à l'interface web à l'addresse `localhost:8080`.

# Ajout du fichier jdl
Ensuite je suis aller chercher un fichier jdl pour démarrer le projet. J'ai choisi le fichier `space.jdl` que j'ai rajouté à la racine du dossier.
Une fois le fichier dans le projet je lance la commande pour l'utiliser :
```bash
jhipster jdl space.jdl
```
Puis encore une fois je lance la commande `./mvnw` pour vérifier que tout ce passe bien et qu'il n'y ai pas d'erreur lors du démarrage du serveur. Enfin je me connecte via l'interface web et je verifie que mes entités ont bien été créer.

# Création de nouvelles entités
A l'aide de la commande proposé par jhipster je décide de créer une nouvelle entité `Planet`.
```bash
jhipster entity Planet

The entity Planet is being created.


Generating field #1

? Do you want to add a field to your entity? Yes
? What is the name of your field? name
? What is the type of your field? String
? Do you want to add validation rules to your field? Yes
? Which validation rules do you want to add? Required, Minimum length
? What is the minimum length of your field? 3

================= Planet =================
Fields
name (String) required minlength='3'


Generating field #2

? Do you want to add a field to your entity? Yes
? What is the name of your field? distance
? What is the type of your field? Long
? Do you want to add validation rules to your field? Yes
? Which validation rules do you want to add? Required, Minimum
? What is the minimum of your field? 1

================= Planet =================
Fields
name (String) required minlength='3'
distance (Long) required min='1'


Generating field #3

? Do you want to add a field to your entity? Yes
? What is the name of your field? type
? What is the type of your field? Enumeration (Java enum type)
? What is the class name of your enumeration? PlanetTypes
? What are the values of your enumeration (separated by comma, no spaces)? Gaz,Stone,Ice
? Do you want to add validation rules to your field? Yes
? Which validation rules do you want to add? Required

================= Planet =================
Fields
name (String) required minlength='3'
distance (Long) required min='1'
type (PlanetTypes) required


Generating field #4

? Do you want to add a field to your entity? Yes
? What is the name of your field? satellite
? What is the type of your field? Instant
? Do you want to add validation rules to your field? No

================= Planet =================
Fields
name (String) required minlength='3'
distance (Long) required min='1'
type (PlanetTypes) required
satellite (Instant) 


Generating field #5

? Do you want to add a field to your entity? No

================= Planet =================
Fields
name (String) required minlength='3'
distance (Long) required min='1'
type (PlanetTypes) required
satellite (Instant) 


Generating relationships to other entities

? Do you want to add a relationship to another entity? No

================= Planet =================
Fields
name (String) required minlength='3'
distance (Long) required min='1'
type (PlanetTypes) required
satellite (Instant) 



? Do you want to use separate service class for your business logic? No, the REST controller should use the repository directly
? Is this entity read-only? No
? Do you want pagination and sorting on your entity? Yes, with infinite scroll and sorting headers

Everything is configured, generating the entity...
```

Une fois la commande terminée je verifie en lancent le projet et en allant voir sur l'interface web.

# Création d'une page Angular

Pour la création de la page j'ai du créer un nouveau dossier qui contient 3 fichiers :
- `myroute.component.ts`
- `myroute.component.html`
- `myroute.component.scss`

    
Une fois c'est trois fichier créer j'ai importer le fichier dans le `app.module.ts` et mis la référence à la class créer dans le fichier `myroute.component.ts` en tant que `Déclaration`.
    
Enfin j'ai ajouter un chemin dans le fichier `app-routing.module.ts` dans lequel j`ai aussi importer mon component.