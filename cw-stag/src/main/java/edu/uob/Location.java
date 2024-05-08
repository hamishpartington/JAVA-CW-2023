package edu.uob;

import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Location extends GameEntity{

    private final HashMap<String, Furniture> furniture;
    private final HashMap<String, Character> characters;
    private final HashMap<String, Artefact> artefacts;

    private final HashSet<String> players;

    private final HashSet<String> accessibleLocations;

    public Location(Node details, Graph location){
        super(details.getId().getId(), details.getAttribute("description"));
        this.characters = new HashMap<>();
        this.furniture = new HashMap<>();
        this.artefacts = new HashMap<>();
        this.accessibleLocations = new HashSet<>();
        this.addAssociatedEntities(location);
        this.players = new HashSet<>();
    }

    private void addAssociatedEntities(Graph location) {
        ArrayList<Graph> entities = location.getSubgraphs();

        for(Graph e : entities) {
            String entityType = e.getId().getId();
            ArrayList<Node> nodes = e.getNodes(false);
            for(Node n : nodes) {
                String name = n.getId().getId().toLowerCase();
                String description = n.getAttribute("description");

                if(entityType.equalsIgnoreCase("furniture")) {
                    this.furniture.put(name, new Furniture(name, description));
                } else if (entityType.equalsIgnoreCase("artefacts")) {
                    this.artefacts.put(name, new Artefact(name, description));
                } else if (entityType.equalsIgnoreCase("characters")) {
                    this.characters.put(name, new Character(name, description));
                }
            }
        }
    }

    public void addAccessibleLocation(String location) {
        this.accessibleLocations.add(location);
    }

    public void removeAccessibleLocation(String location) {
        this.accessibleLocations.remove(location);
    }

    public String toString(String currPlayer) {
        StringBuilder builder = new StringBuilder();
        builder.append("You are in a ").append(super.toString()).append("You can see:\n");
        this.furniture.forEach((key, entry) -> builder.append(entry.toString()));
        this.artefacts.forEach((key, entry) -> builder.append(entry.toString()));
        this.characters.forEach((key, entry) -> builder.append(entry.toString()));
        builder.append("Other players at this location:\n");
        this.players.forEach(player -> {
                    if (!player.equals(currPlayer)) {
                        builder.append(player).append("\n");
                    }
                }
        );
        builder.append("You can access from here:\n");
        this.accessibleLocations.forEach(loc -> builder.append(loc).append("\n"));

        return builder.toString();
    }

    public HashSet<String> getAccessibleLocations() {
        return accessibleLocations;
    }

    public HashMap<String, Artefact> getArtefacts() {
        return artefacts;
    }

    public HashSet<String> getPlayers() {
        return players;
    }

    public ArrayList<String> getAvailableEntities(){
        ArrayList<String> availableEntities = new ArrayList<>();
        availableEntities.add(this.getName());
        availableEntities.addAll(this.artefacts.keySet());
        availableEntities.addAll(this.characters.keySet());
        availableEntities.addAll(this.furniture.keySet());

        return availableEntities;
    }

    public GameEntity consumeEntity(String consumed) {
        if(this.artefacts.containsKey(consumed)){
            return this.artefacts.remove(consumed);
        } else if(this.characters.containsKey(consumed)) {
            return this.characters.remove(consumed);
        } else {
            return this.furniture.remove(consumed);
        }
    }

    public HashMap<String, Furniture> getFurniture() {
        return furniture;
    }

    public HashMap<String, Character> getCharacters() {
        return characters;
    }
}
