package edu.uob;
import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;
import com.alexmerz.graphviz.objects.Edge;

import java.util.ArrayList;
import java.util.HashSet;

public class Location extends GameEntity{

    private HashSet<GameEntity> furniture;
    private HashSet<GameEntity> characters;
    private HashSet<GameEntity> artefacts;

    public Location(Node details, Graph location){
        super(details.getId().getId(), details.getAttribute("description"));
        this.characters = new HashSet<>();
        this.furniture = new HashSet<>();
        this.artefacts = new HashSet<>();
        this.addAssociatedEntities(location);
    }

    private void addAssociatedEntities(Graph location) {
        ArrayList<Graph> entities = location.getSubgraphs();

        for(Graph e : entities) {
            String entityType = e.getId().getId();
            ArrayList<Node> nodes = e.getNodes(false);
            for(Node n : nodes) {
                String name = n.getId().getId();
                String description = n.getAttribute("description");

                if(entityType.equalsIgnoreCase("furniture")) {
                    this.furniture.add(new Furniture(name, description));
                } else if (entityType.equalsIgnoreCase("artefacts")) {
                    this.artefacts.add(new Artefact(name, description));
                } else if (entityType.equalsIgnoreCase("characters")) {
                    this.characters.add(new Character(name, description));
                }
            }
        }
    }

}
