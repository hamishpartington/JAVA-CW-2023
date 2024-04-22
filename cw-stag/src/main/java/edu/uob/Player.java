package edu.uob;

import java.util.ArrayList;

public class Player {
    private String name;
    private String currentLocation;
    private ArrayList<String> inventory;

    public Player(String name, String startLocation) {
        this.name = name;
        this.currentLocation = startLocation;
        this.inventory = new ArrayList<>();
    }
}
