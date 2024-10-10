# Text Adventure Game

This was the second summative coursework for my JAVA module on my MSc. The task was to write a simple text adventure game. I received a distinction for this coursework with a mark of 73.

To play:
* Make sure you have a Java SDK installed ([instructions](https://www.golinuxcloud.com/install-java-linux-windows-mac/))
* Clone the repo
* Open your terminal and navigate to the `cw-stag` directory
* Run the client with `./mvnw exec:java@client -D exec.args="Your name"`
* Open a new terminal window and run the server with `./mvnw exec:java@server`
* Play the game entering instructions in the client window. The server will interpret your commands and return the outcomes (narrations or prompts if error have been made) to the client.

Core commands:
- "inventory" (or "inv" for short): lists all of the artefacts currently being carried by the player
- "get": picks up a specified artefact from the current location and adds it into player's inventory
- "drop": puts down an artefact from player's inventory and places it into the current location
- "goto": moves the player to the specified location (if there is a path to that location)
- "look": prints names and descriptions of entities in the current location and lists paths to other locations

Customising your game:
- an entities file in a `.dot` file in the `config` directory contains all game objects, characters, locations, and paths. The default game is loaded `extended-entities.dot` but if you would like to create your own game, create your own entities file following the same structure and update `entitiesFile` in the `main` class of `GameServer.java` accordingly.
- Custom actions are defined in a `.xml` file in the `config` directory. Each action has triggers, subjects, consumed entities, produced entities, and a narraction. The default game is loaded `extended-actions.xml` but if you would like to create your own game, create your own actions file following the same structure and update `actionsFile` in the `main` class of `GameServer.java` accordingly.

Hope you enjoy playing!
