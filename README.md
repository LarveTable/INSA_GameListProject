# Projet de développement d'une application Android!

Bonjour ! Ce projet avait pour but de nous introduire au développement **Android** sous **Kotlin**, avec l'aide du framework **Jetpack Compose**. Le projet s'est décomposé en 6 travaux pratiques pour qu'on puisse progresser graduellement. Ce README a pour objectif de présenter mon travail durant ces séances, et d'expliquer ce que j'ai pu (ou non) réaliser.


## TP 1

Les premières séances ont été consacrées à la découverte du langage Kotlin donc il n'y a pas grand chose à ajouter !


## TP2

Le deuxième TP consistait à développer une première ébauche de l'interface de notre liste de jeux. J'ai décidé de séparer la couverture d'un jeu de sa description (clickable) purement arbitrairement, pour mieux comprendre comment fonctionnait le layout sur Jetpack Compose. La totalité des fonctionnalités demandées a été implémentée.
Petit bonus : j'ai fait en sorte que si la couverture n'est pas trouvée, on affiche une couverture "Missing" (libre de droit !).

## TP3

Dès le début du projet, j'ai fait l'erreur classique de mettre l'ensemble de mon code dans la même fonction (onCreate) car j'en étais encore au début de la découverte (ça a duré longtemps quand même...). J'ai donc commencé par vouloir implémenter une **AppBar** dynamique qui change d'état en fonction de ce qui est affiché, mais je me suis heurté à pas mal de problèmes que j'ai pu partiellement régler à l'aide de l'API **remember** de Jetpack Compose. J'ai donc pu "bidouiller" avec des variables utilisant remember pour avoir la fonction attendue.

## TP4

Le TP 4 était relativement court car il était surtout question de reproduire le layout demandé en récupérant les couvertures appropriées pour le jeu et les plateformes.

## TP5

Pas de commentaires particuliers pour le TP5, l'ensembles des fonctionnalités demandées fonctionne.

## TP6

Malgré mon avance durant les séances précédentes, le fait de tout écrire dans une seule fonction a vraiment atteint ses limites lors du TP6 ! En effet, je ne trouvais pas de solution viable pour implémenter le système de favoris car mon code était trop brouillon et devenait illisible...
Sous les conseils du prof. j'ai donc pris mon courage à deux mains et réorganisé mon code. J'ai pris beaucoup de temps pour le faire mais j'ai grandement amélioré la qualité de mon code, notamment en ajoutant des "**ViewModels**", un "**Repository**", et séparés les fonctionnalités de mon code en plusieurs fichiers/fonctions. 

Avec un code bien plus clair, j'ai pu implémenter la fonctionnalité de favoris sans difficulté.


## TP facultatif

Le TP facultatif avait pour but de communiquer avec l'API de **Twitch** afin de récupérer une liste de jeux un peu plus fournie. Son implémentation n'était pas la plus compliquée avec la nouvelle organisation du code, et j'ai donc pu ajouter cette fonctionnalité.
Petit bonus : un rond de chargement apparaît le temps que l'appli. récupère les informations sur les jeux auprès de l'API. Si jamais l'application n'arrive pas à récupérer les données (pour n'importe quelle raison), la liste utilisée précédemment en local est chargée à la place !
