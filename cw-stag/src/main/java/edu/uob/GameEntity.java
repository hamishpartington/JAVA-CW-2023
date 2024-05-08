package edu.uob;

public abstract class GameEntity
{
    private final String name;
    private final String description;

    public GameEntity(String name, String description)
    {
        this.name = name.toLowerCase();
        this.description = description.toLowerCase();
    }

    public String getName()
    {
        return name;
    }

    @Override
    public String toString() {
        return this.name + ": " + this.description + "\n";
    }

}
