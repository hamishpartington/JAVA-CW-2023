package edu.uob;
import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;
import com.alexmerz.graphviz.objects.Edge;

import java.util.ArrayList;
import java.util.HashMap;

public class Location extends GameEntity{

    private HashMap<String, GameEntity> furniture;
    private HashMap<String, GameEntity> characters;
    private HashMap<String, GameEntity> artefacts;

    private ArrayList<String> accessibleLocations;

    public Location(Node details, Graph location){
        super(details.getId().getId().toLowerCase(), details.getAttribute("description"));
        this.characters = new HashMap<>();
        this.furniture = new HashMap<>();
        this.artefacts = new HashMap<>();
        this.accessibleLocations = new ArrayList<>();
        this.addAssociatedEntities(location);
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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("You are in a " + super.toString() + "You can see:\n");
        this.furniture.forEach((key, entry) -> builder.append(entry.toString()));
        this.artefacts.forEach((key, entry) -> builder.append(entry.toString()));
        this.characters.forEach((key, entry) -> builder.append(entry.toString()));
        builder.append("You can access from here:\n");
        this.accessibleLocations.forEach(loc -> builder.append(loc + "\n"));

        return builder.toString();
    }
}
