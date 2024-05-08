package edu.uob;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.*;

import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;
import com.alexmerz.graphviz.objects.Edge;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public final class GameServer {

    private static final char END_OF_TRANSMISSION = 4;
    private final File entitiesFile, actionsFile;
    private final HashMap<String, Location> locations;

    private final HashMap<String, Player> players;

    private final HashMap<String, HashSet<GameAction>> gameActions;
    private String startLocationKey;
    private String currPlayer;

    private String returnString;

    private CommandParser commandParser;

    private ActionInterpreter actionInterpreter;

    private GameAction actionToPerform;

    private ArrayList<String> allEntityNames;

    public static void main(String[] args) {
        File entitiesFile = Paths.get("config" + File.separator + "extended-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "extended-actions.xml").toAbsolutePath().toFile();
        try {
            GameServer server = new GameServer(entitiesFile, actionsFile);
            server.blockingListenOn(8888);
        } catch (IOException ioe) {
            System.out.println("IOException was thrown when trying to construct server");
            System.exit(1);
        } catch (ParseException pe) {
            System.out.println("ParseException was thrown when trying to construct server");
            System.exit(1);
        } catch (ParserConfigurationException pce) {
            System.out.println("ParserConfigurationException was thrown when trying to construct server");
            System.exit(1);
        } catch (SAXException se) {
            System.out.println("SAXException was thrown when trying to construct server");
            System.exit(1);
        }
    }

    /**
    * Do not change the following method signature or we won't be able to mark your submission
    * Instanciates a new server instance, specifying a game with some configuration files
    *
    * @param entitiesFile The game configuration file containing all game entities to use in your game
    * @param actionsFile The game configuration file containing all game actions to use in your game
    */
    public GameServer(File entitiesFile, File actionsFile) throws IOException, ParseException, ParserConfigurationException, SAXException {
        this.entitiesFile = entitiesFile;
        this.actionsFile = actionsFile;
        this.locations = new HashMap<>();
        this.players = new HashMap<>();
        this.gameActions = new HashMap<>();
        this.returnString = "";
        this.parseEntitiesFile();
        this.parseActionsFile();
        this.getAllEntityNames();
    }

    public void parseEntitiesFile() throws FileNotFoundException, ParseException {
        Parser parser = new Parser();
        FileReader reader = new FileReader(this.entitiesFile);
        parser.parse(reader);
        Graph wholeDocument = parser.getGraphs().get(0);
        ArrayList<Graph> sections = wholeDocument.getSubgraphs();
        ArrayList<Graph> locations = sections.get(0).getSubgraphs();
        ArrayList<Edge> paths = sections.get(1).getEdges();
        this.startLocationKey = locations.get(0).getNodes(false).get(0).getId().getId().toLowerCase();

        for(Graph loc: locations) {
            Node details = loc.getNodes(false).get(0);
            this.locations.put(details.getId().getId().toLowerCase(), new Location(details, loc));
        }

        for(Edge path : paths) {
            Node fromLocation = path.getSource().getNode();
            String fromName = fromLocation.getId().getId().toLowerCase();
            Node toLocation = path.getTarget().getNode();
            String toName = toLocation.getId().getId().toLowerCase();
            this.locations.get(fromName).addAccessibleLocation(toName);
        }

    }

    public void parseActionsFile() throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(this.actionsFile);
        Element root = document.getDocumentElement();
        NodeList actions = root.getChildNodes();
        for(int i = 1; i < actions.getLength(); i+=2) {
            GameAction currentAction = new GameAction((Element)actions.item(i));
            //map action to trigger phrases
            for(String keyphrase: currentAction.getTriggers()) {
                if(!this.gameActions.containsKey(keyphrase)) {
                    this.gameActions.put(keyphrase, new HashSet<>());
                }
                this.gameActions.get(keyphrase).add(currentAction);
            }
        }
    }

    /**
    * Do not change the following method signature or we won't be able to mark your submission
    * This method handles all incoming game commands and carries out the corresponding actions.</p>
    *
    * @param command The incoming command to be processed
    */
    public String handleCommand(String command) {
        try {
            this.commandParser = new CommandParser(command, this.gameActions.keySet());
            this.currPlayer = commandParser.getPlayerName();
            if(!this.players.containsKey(currPlayer)) {
                this.players.put(currPlayer, new Player(currPlayer, this.startLocationKey));
                this.locations.get(startLocationKey).getPlayers().add(currPlayer);
            }
            commandParser.checkTokensForMultipleTriggers(gameActions.keySet());
            this.interpretCommand();
        } catch (STAGException e) {
            return e.getLocalizedMessage();
        }

        return this.returnString;
    }

    public void interpretCommand() throws STAGException {
        String trigger = commandParser.getTriggerWord();
        switch(trigger) {
            case "inv", "inventory" -> this.inventory();
            case "look" -> this.look();
            case "goto" -> this.goTo();
            case "get" -> this.get();
            case "drop" -> this.drop();
            case "health" -> {
                this.checkExtraneousEntities("health");
                this.returnString = "You have " + this.players.get(currPlayer).getHealth() + " health point(s) remaining";
            }
            default -> {
                HashSet<GameAction> potentialActions = new HashSet<>();
                // add actions for all triggers
                for(String trig : commandParser.getTriggers()) {
                    potentialActions.addAll(gameActions.get(trig));
                }
                this.actionInterpreter = new ActionInterpreter(potentialActions, commandParser.getTokenisedCommand(), trigger);
                String playerLocationKey = this.players.get(currPlayer).getCurrentLocation();
                this.actionToPerform = this.actionInterpreter.determinePerformableActions(this.locations.get(playerLocationKey), this.players.get(currPlayer).getInventory());
                this.checkExtraneousEntities(trigger);
                this.performAction();
            }
        }
    }

    public void inventory() throws STAGException {
        this.checkExtraneousEntities("inv");
        if(!this.players.get(this.currPlayer).getInventory().isEmpty()) {
            StringBuilder builder = new StringBuilder("You have:\n");
            this.players.get(this.currPlayer).getInventory().forEach((key, entry) -> builder.append(entry.toString()));
            this.returnString = builder.toString();
        } else {
            this.returnString = "You have no artefacts in your inventory\n";
        }
    }

    public void look() throws STAGException {
        this.checkExtraneousEntities("look");
        String playerLocation = this.players.get(this.currPlayer).getCurrentLocation();

        this.returnString = this.locations.get(playerLocation).toString(currPlayer);
    }

    public void goTo() throws STAGException {
        String playerLocationKey = this.players.get(currPlayer).getCurrentLocation();
        this.commandParser.checkLocation(this.locations.keySet(), this.locations.get(playerLocationKey).getAccessibleLocations());
        this.checkExtraneousEntities("goto");
        this.locations.get(playerLocationKey).getPlayers().remove(currPlayer);
        String destination = commandParser.getDestination();
        this.players.get(currPlayer).setCurrentLocation(destination);
        this.locations.get(destination).getPlayers().add(currPlayer);
        this.returnString = this.locations.get(destination).toString(currPlayer);
    }

    public void get() throws STAGException {
        Set<String> allGameArtefacts = this.getAllGameArtefacts();
        String playerLocationKey = this.players.get(currPlayer).getCurrentLocation();
        Set<String> availableArtefacts = this.locations.get(playerLocationKey).getArtefacts().keySet();
        commandParser.checkArtefacts(allGameArtefacts, availableArtefacts);
        this.checkExtraneousEntities("get");
        Artefact artefact = this.locations.get(playerLocationKey).getArtefacts().get(commandParser.getArtefact());
        this.locations.get(playerLocationKey).getArtefacts().remove(commandParser.getArtefact());
        this.players.get(currPlayer).addToInventory(artefact);
        this.returnString = "You picked up a " + artefact.getName();
    }

    public void drop() throws STAGException {
        Set<String> artefactsInInv = this.players.get(currPlayer).getInventory().keySet();
        Set<String> allGameArtefacts = this.getAllGameArtefacts();
        commandParser.checkArtefacts(allGameArtefacts, artefactsInInv);
        this.checkExtraneousEntities("drop");
        Artefact artefact = this.players.get(currPlayer).getInventory().get(commandParser.getArtefact());
        this.players.get(currPlayer).getInventory().remove(commandParser.getArtefact());
        String playerLocationKey = this.players.get(currPlayer).getCurrentLocation();
        this.locations.get(playerLocationKey).getArtefacts().put(artefact.getName(), artefact);
        this.returnString = "You dropped a " + artefact.getName();
    }

    public void performAction() {
        HashSet<String> consumed = this.actionToPerform.getConsumed();
        HashSet<String> produced = this.actionToPerform.getProduced();
        this.consumerProducer(true, consumed);
        this.consumerProducer(false, produced);
        returnString = this.actionToPerform.getNarration();
        if(this.players.get(currPlayer).getHealth() == 0) {
            returnString = returnString + "\nyou died and lost all of your items, you must return to the start of the game";
            String deathLocationKey = this.players.get(currPlayer).getCurrentLocation();
            this.players.get(currPlayer).die(this.startLocationKey, this.locations.get(deathLocationKey));
        }
    }

    public void addEntityToLocation(String locationKey, GameEntity consumedEntity) {
        Location location = this.locations.get(locationKey);
        if(consumedEntity != null) {
            if (consumedEntity instanceof Artefact) {
                location.getArtefacts().put(consumedEntity.getName(), (Artefact) consumedEntity);
            } else if (consumedEntity instanceof Furniture) {
                location.getFurniture().put(consumedEntity.getName(), (Furniture) consumedEntity);
            } else if (consumedEntity instanceof Character) {
                location.getCharacters().put(consumedEntity.getName(), (Character) consumedEntity);
            }
        }
    }

    void consumerProducer(boolean isConsumption, HashSet<String> entities) {

        String locationKey;

        if(isConsumption) {
            locationKey = "storeroom";
        } else {
            locationKey = this.players.get(currPlayer).getCurrentLocation();
        }

        for(String entity: entities) {
            if(this.players.get(currPlayer).getInventory().containsKey(entity) && isConsumption) {
                Artefact removedArtefact = this.players.get(currPlayer).getInventory().remove(entity);
                this.addEntityToLocation(locationKey, removedArtefact);
            } else if (entity.equals("health")) {
                this.players.get(currPlayer).adjustHealth(isConsumption);
            } else {
                this.consumeOrProduceEntity(entity, isConsumption, locationKey);
            }
        }
    }

    public void consumeOrProduceEntity(String entityKey, boolean isConsumption, String locationKey) {
        GameEntity consumedEntity = null;
        for (Map.Entry<String, Location> entry : this.locations.entrySet()) {
            String key = entry.getKey();
            Location value = entry.getValue();
            ArrayList<String> locationEntities = value.getAvailableEntities();
            locationEntities.remove(key);
            if (locationEntities.contains(entityKey)) {
                consumedEntity = value.consumeEntity(entityKey);
            }
        }
        // now if location then remove/create path
        String currLocation = this.players.get(currPlayer).getCurrentLocation();
        // cannot consume or produce current location
        if(this.locations.containsKey(entityKey) && !entityKey.equalsIgnoreCase(currLocation)) {
            if(!isConsumption) {
                this.locations.get(currLocation).addAccessibleLocation(entityKey);
            } else {
                this.locations.get(currLocation).removeAccessibleLocation(entityKey);
            }
        } else {
            this.addEntityToLocation(locationKey, consumedEntity);
        }
    }

    private Set<String> getAllGameArtefacts() {
        Set<String> artefacts = new HashSet<>();

        this.locations.forEach((key, entry) -> artefacts.addAll(entry.getArtefacts().keySet()));
        this.players.forEach((key, entry) -> artefacts.addAll(entry.getInventory().keySet()));

        return artefacts;
    }

    private void checkExtraneousEntities(String trigger) throws STAGException {
        switch (trigger) {
            case "inv", "inventory", "health", "look" -> this.hasExtraneousEntities(0);
            case "goto", "drop", "get" -> this.hasExtraneousEntities(1);
            default -> this.hasExtraneousEntities(this.actionToPerform);
        }
    }

    private void hasExtraneousEntities(int maxEntities) throws STAGException {

        HashSet<String> entities = new HashSet<>();
        for(String token : this.commandParser.getTokenisedCommand()) {
            if(this.allEntityNames.contains(token)) {
                entities.add(token);
            }
        }
        if(entities.size() > maxEntities) {
            throw new STAGException.ExtraneousEntities();
        }
    }

    private void hasExtraneousEntities(GameAction action) throws STAGException {
        for(String token : this.commandParser.getTokenisedCommand()) {
            HashSet<String> nonExtraneous = new HashSet<>();
            nonExtraneous.addAll(action.getSubjects());
            nonExtraneous.addAll(action.getConsumed());
            nonExtraneous.addAll(action.getProduced());
            if(!nonExtraneous.contains(token) && this.allEntityNames.contains(token)) {
                throw new STAGException.ExtraneousEntities();
            }
        }
    }

    private void getAllEntityNames() {
        this.allEntityNames = new ArrayList<>();
        this.locations.forEach((key, entry) -> {
            this.allEntityNames.addAll(entry.getAvailableEntities());
            this.allEntityNames.add(key);
        });
    }

    /**
    * Do not change the following method signature or we won't be able to mark your submission
    * Starts a *blocking* socket server listening for new connections.
    *
    * @param portNumber The port to listen on.
    * @throws IOException If any IO related operation fails.
    */
    public void blockingListenOn(int portNumber) throws IOException {
        try (ServerSocket s = new ServerSocket(portNumber)) {
            System.out.println("Server listening on port " + portNumber);
            while (!Thread.interrupted()) {
                try {
                    blockingHandleConnection(s);
                } catch (IOException e) {
                    System.out.println("Connection closed");
                }
            }
        }
    }

    /**
    * Do not change the following method signature or we won't be able to mark your submission
    * Handles an incoming connection from the socket server.
    *
    * @param serverSocket The client socket to read/write from.
    * @throws IOException If any IO related operation fails.
    */
    private void blockingHandleConnection(ServerSocket serverSocket) throws IOException {
        try (Socket s = serverSocket.accept();
        BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {
            System.out.println("Connection established");
            String incomingCommand = reader.readLine();
            if(incomingCommand != null) {
                System.out.println("Received message from " + incomingCommand);
                String result = handleCommand(incomingCommand);
                writer.write(result);
                writer.write("\n" + END_OF_TRANSMISSION + "\n");
                writer.flush();
            }
        }
    }
}
