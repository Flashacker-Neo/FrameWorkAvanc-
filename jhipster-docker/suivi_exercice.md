Pour l'ajout du nouveau champ `businessId` à l'objet `employee` je suis d'abord aller trouvé le fichier java qui défini l'entité à savoir `demo3/domain/Employee.java`.    

J'ai ensuite ajouté le champ `businessId` et mis l'anotation `@Column(name = "business_id")` puis j'ai créer le getter et setter associé.    
Ensuite j'ai recherché toutes les références à la classe `Employee` dans le code et j'ai modifier chaque fichier ou il fallait rajouté le champ nouvellement créer.   

Une fois la partie java faite, j'ai fait de même avec la partie angular, j'ai cherché le fichier qui définissait l'objet `employee` à savoir `entities/employee/employee.model.ts` et j'ai rajouté le champ `businessId`.
Puis j'ai modifié les pages de l'entité pour rajouter le nouveau champ. Et enfin j'ai modifier le fichier `i18n/employee.json` pour avoir la traduction des inputs que j'ai rajoutés.   

Après avoir modifier tous les fichiers nécessaires pour rajouter un nouveau champ, j'ai ouvert un terminal à la racine du projet et j'ai lancé la commande suivante :
```bash
./mvnw
```    

Or après que le projet est builder et démarrer correctement lorsque je me suis rendu sur la page de l'entité j'ai reçu une erreur. Je suis donc aller voir dans le terminal pour avoir plus de détail et je suis tombé sur cette erreur :
```bash
Column "EMPLOYEE0_.BUSINESS_ID" not found; SQL statement:
select employee0_.id as id1_2_, employee0_.business_id as business2_2_, employee0_.commission_pct as commissi3_2_, employee0_.department_id as departm10_2_, employee0_.email as email4_2_, employee0_.first_name as first_na5_2_, employee0_.hire_date as hire_dat6_2_, employee0_.last_name as last_nam7_2_, employee0_.manager_id as manager11_2_, employee0_.phone_number as phone_nu8_2_, employee0_.salary as salary9_2_ from employee employee0_ order by employee0_.id asc limit ? [42122-200]
```   

J'ai donc essayer d'effacer la base de donnée pour forcer JHipster à la recréer avec le nouveau model d'entité, pour cela j'ai lancé la commande :
```bash
rm -rf target
```
Malheureusement pour moi après avoir relancer l'application la base de donnée à été recréer de le même façon que la première fois et donc l'erreur n'a pas pu être résolu.