package edu.uob;

import java.util.HashMap;

public class Player {
    private final String name;
    private String currentLocation;
    private HashMap<String, Artefact> inventory;
    private int health;

    public Player(String name, String startLocation) {
        this.name = name;
        this.currentLocation = startLocation;
        this.inventory = new HashMap<>();
        this.health = 3;
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

    public void adjustHealth(boolean isConsumption) {
        if(isConsumption) {
            this.health--;
        } else if(this.health < 3) {
            this.health++;
        }
    }

    public int getHealth() {
        return health;
    }

    public void die(String startLocationKey, Location deathLocation) {
        this.inventory.forEach((key, entry) -> deathLocation.getArtefacts().put(key, entry));
        this.inventory.clear();
        deathLocation.getPlayers().remove(this.name);
        this.currentLocation = startLocationKey;
        this.health = 3;
    }
}
