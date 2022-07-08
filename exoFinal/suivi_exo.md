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
