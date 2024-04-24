package edu.uob;

public abstract class GameEntity
{
    private String name;
    private String description;

    public GameEntity(String name, String description)
    {
        this.name = name.toLowerCase();
        this.description = description.toLowerCase();
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    @Override
    public String toString() {
        return this.name + ": " + this.description + "\n";
    }

}
