# Transaction

> Ce fichier a pour projet de décrire l'entierté des interactions CLIENT - SERVEUR

## connect

### client -> server
Player  
Demande une nouvelle connexion au serveur

### server -> client
List< Player >  
Préviens l'arrivée d'un nouveau joueur

## disconnect

### client -> server
Player  
Previens de la deconnexion d'un client

### server -> client

Player  
Avertie la deconnexion d'un joueur de la partie

## chat

### bidirectionnel

String  
Nouveau message

## ready

### client -> server

Player  
player is ready

### server -> client

List < Player >  
list of ready player

## start_game

### server -> client

null
tell the clients to start the game

## game

### server -> client

GameState
tell the current state of the party

#### client -> server

String
new input for the answer

## vote

### server -> client

VoteState
State of the game with the votes

### client -> server

Player
For whom the client vote
