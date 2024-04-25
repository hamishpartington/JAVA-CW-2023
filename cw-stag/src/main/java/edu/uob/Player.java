package edu.uob;

import java.util.ArrayList;
import java.util.HashMap;

public class Player {
    private String name;
    private String currentLocation;
    private HashMap<String, Artefact> inventory;

    public Player(String name, String startLocation) {
        this.name = name;
        this.currentLocation = startLocation;
        this.inventory = new HashMap<>();
    }

    public HashMap<String, Artefact> getInventory() {
        return inventory;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public void addToInventory(Artefact artefact){
        inventory.put(artefact.getName(), artefact);
    }
}
