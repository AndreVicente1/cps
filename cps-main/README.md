# CPS

## Exécution synchrone

### Cas traité:

Pour toutes les AST

Sans continuation (ECont): OK
Avec continuation (FCont et DCont): OK
Deux noeuds: OK
Quatre noeuds: OK
Trois noeuds et un noeud isolé: OK
Ordre de création aléatoire des noeuds: OK
Ordre de création ordonnée des noeuds: OK
Un noeud mis entre deux noeuds: OK
FindByIdentifier: OK
FindByZone: OK
Propagation à partir de différents noeud: OK

Mise à jour des capteurs des noeuds: FIX (où mettre la fonction, avec intervalle de temps?)
