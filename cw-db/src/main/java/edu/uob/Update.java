package edu.uob;

public class Update {
    private String attributeName;
    private String newValue;

    public Update(String attributeName, String newValue) {
        this.attributeName = attributeName;
        this.newValue = newValue;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public String getNewValue() {
        return newValue;
    }
}
