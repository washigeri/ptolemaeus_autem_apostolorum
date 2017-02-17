# Hackathon Ares : Le client Java

## Partie 1: Installation

### Installation sur Windows

Pour télécharger Java, rendez-vous ici : [https://www.java.com/fr/download/](https://www.java.com/fr/download/)

### Installation sur Linux

Pour installer Java, il suffit de lancer la commande suivante :
```
sudo apt install openjdk-8-jdk
```
Ensuite, il faut configuer la variable JAVA_HOME :
```
sudo nano ~/.bashrc
```
Ajoutez la ligne suivante à la fin du fichier :
```
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-i386
```
ou 
```
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
```
en fonction de la version téléchargée (32 ou 64 bits)

## Partie 2: Utilisation

### Utilisation du fichier JAR

#### IntelliJ

Allez dans `File` puis `Project Structure`. Dans la partie `Modules`, cliquez sur l'onglet `Dependencies` puis cliquez sur le `+` à droite et choisissez `1 JARs or directories...` et enfin choisissez le fichier JAR que vous venez de télécharger.   
Voilà vous pouvez utiliser la librairie !

#### Eclipse

Créez un nouveau projet puis faites un clic droit sur le projet. Choisissez `Build path` puis `Configure build path`. Allez dans l'onglet `Libraries` et cliquez sur `Add External JARs` et enfin choisissez le fichier JAR que vous venez de télécharger.   
Voilà vous pouvez utiliser la librairie !

### Clonage de la librairie

Si vous voulez avoir tout le code, vous pouvez cloner le repository suivant :
```
git clone https://git.ares-ensiie.eu/hackathon/hackathon-java-client
```

## Partie 3: Exemple

Le fichier `Exemple.java` donne un exemple commenté du client Java. Ce client va jouer des coups aléatoires jusqu'à ce qu'il gagne ou qu'il perde.   
Pour lancer l'exemple, vous pouvez le lancer depuis un IDE ou exécuter les commandes suivantes depuis le répertoire de ce fichier :
```
javac -cp ".:influence-client.jar" -d . Exemple.java
java -cp ".:influence-client.jar" Exemple
```

## Partie 4: Code source et documentation

### Code source

Le code source du client Java peut être trouvé sur le Gitlab dans le projet [hackathon/hackathon-java-client](https://git.ares-ensiie.eu/hackathon/hackathon-java-client)

### Documentation

La documentation est disponible au format HTML dans le dossier doc, vous avez juste à lancer le fichier `index.html`.  
Vous pouvez aussi la retrouver ici : [doc-java](https://perso.ares-ensiie.eu/miclo2018/doc_java)
