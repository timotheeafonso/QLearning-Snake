# QLearning-Snake

Développement d'un jeu snake avec 0, 1 ou 2 joueurs. L'apprentissage par renforcement a été utilisé pour améliorer les agents à chaque partie jouée. Les algorithmes utilisés incluent le Q-learning tabulaire et le Q-learning approximatif. L'algorithme A* a été implémenté pour calculer les distances.

# Lancement du projet

Executer l'un des fichiers suivant:
- src/main_duel/main_batchMode_duel.java
- src/main_duel/main_debugMode_duel.java
- src/main_solo/main_batchMode_solo.java
- src/main_solo/main_debugMode_solo.java

batch mode: lance plusieurs partie en parallele pour calculer des statistiques
debug mode: lance une seule partie
duel: deux agents sur la carte 
solo: un seul agent sur la carte

---

## Paramètres modifiables avant l'exécution :

Avant de lancer l'un des quatre fichiers, il est possible de modifier les paramètres suivants pour ajuster le comportement du programme :

- **gamma** : Le facteur de discount utilisé dans l'algorithme de Q-learning. Il détermine l'importance des récompenses futures par rapport aux récompenses immédiates. (Valeur typique : 0.9)

- **epsilon** : Le taux d'exploration dans l'algorithme d'apprentissage par renforcement. Il contrôle le degré de randomisation dans les actions de l'agent. Une valeur plus élevée signifie plus d'exploration, tandis qu'une valeur plus faible privilégie l'exploitation de la politique actuelle. (Valeur typique : 0.1)

- **alpha** : Le taux d'apprentissage utilisé dans le Q-learning. Il détermine dans quelle mesure les nouvelles informations modifient les valeurs existantes. (Valeur typique : 0.1)

- **randomFirstApple** : Si activé, la première pomme sera placée de manière aléatoire sur la carte à chaque exécution du jeu, créant ainsi une variabilité dans l'environnement pour chaque jeu. (Valeur typique : `true` ou `false`)

- **layoutName** : Le nom de la carte de jeu utilisée. Ce paramètre fait référence aux fichiers présents dans le dossier `layouts/`, qui contiennent les différentes configurations de terrain sur lesquelles le serpent évolue. (Exemple : `classic_layout.txt`, `hard_layout.txt`)

- **strategy** : La stratégie utilisée par les agents pour prendre leurs décisions :
  - **random** : L'agent choisit sa direction de manière aléatoire, sans logique d'apprentissage.
  - **advanced** : L'agent choisit sa direction de manière aléatoire, mais évite les mouvements qui seraient fatals (comme se mordre soi-même ou se heurter aux murs).
  - **tabularQLearning** : Utilisation de la méthode classique du Q-learning avec une table de Q-valeurs pour représenter l'état-action.
  - **approximateQLearning** : Utilisation du Q-learning approximé, où une fonction d'approximation (comme une régression linéaire) est utilisée pour estimer les valeurs d'état-action, adaptée aux environnements avec de grands espaces d'état.
  - **human** : L'utilisateur contrôle directement l'agent en choisissant sa direction via les flèches directionnelles du clavier ou les touches `Z`, `Q`, `S`, `D` si deux joueurs sont impliqués.

---

Les résultats et les observations sont disponibles dans le fichier **"rapport.pdf"**. Ce rapport contient une analyse détaillée des performances des agents, des observations sur les stratégies utilisées et des graphiques illustrant les résultats des différentes expérimentations.
