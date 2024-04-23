package edu.uob;

import java.util.ArrayList;

public class Player {
    private String name;
    private String currentLocation;
    private ArrayList<Artefact> inventory;

    public Player(String name, String startLocation) {
        this.name = name;
        this.currentLocation = startLocation;
        this.inventory = new ArrayList<>();
    }

    public ArrayList<Artefact> getInventory() {
        return inventory;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }
}
