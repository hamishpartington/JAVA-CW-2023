package edu.uob;

import java.util.ArrayList;

public class Condition {
    private String attributeName;
    private String comparator;
    private String value;

    private String relationShipToNextCondition;

    private ArrayList<String> trueIds;

    public Condition(String attributeName, String comparator, String value) {
        this.attributeName = attributeName;
        this.comparator = comparator;
        this.value = value;
        this.relationShipToNextCondition = null;
    }

    public Condition(String attributeName, String comparator, String value, String relationShipToNextCondition) {
        this.attributeName = attributeName;
        this.comparator = comparator;
        this.value = value;
        this.relationShipToNextCondition = relationShipToNextCondition;
    }

    public void findTrueIds(Table table, String commandType) throws DBException {
        int dataIndex = table.getFields().indexOf(this.attributeName);
        this.trueIds = new ArrayList<>();
        if(dataIndex == -1) {
            throw new DBException.FieldDoesNotExist(this.attributeName, commandType);
        }
        int i = 0;
        for(String d : table.getData().get(dataIndex)) {
            if(evaluateCondition(d)) {
                this.trueIds.add(table.getData().get(0).get(i));
            }
            i++;
        }
    }

    private boolean evaluateCondition(String dataEntry) {
        double doubleDataEntry = 0;
        double doubleVal = 0;
        boolean isDataNumeric = true;
        boolean isDataBoolean = false;

        if(dataEntry.equalsIgnoreCase("TRUE") || dataEntry.equalsIgnoreCase("FALSE")) {
            isDataBoolean = true;
        }
        
        try {
            doubleDataEntry = Double.parseDouble(dataEntry);
            doubleVal = Double.parseDouble(this.value);
        } catch (NumberFormatException nfe) {
            isDataNumeric = false;
        }
        switch (this.comparator) {
            case "==" -> {
                if(!isDataNumeric && !isDataBoolean) {
                    return dataEntry.equals(this.value);
                }
                if(isDataBoolean) {
                    return dataEntry.equalsIgnoreCase(this.value);
                }
                return doNumericComparison(doubleDataEntry, doubleVal);}
            case "!=" -> {
                if(!isDataNumeric) {
                    return !dataEntry.equals(this.value);
                }
                if(isDataBoolean) {
                    return !dataEntry.equalsIgnoreCase(this.value);
                }
                return doNumericComparison(doubleDataEntry, doubleVal); }
            case "LIKE" -> {return dataEntry.contains(this.value.replaceAll("'", ""));}
            case ">", "<", ">=", "<=" -> {
                if(isDataNumeric) {
                    return this.doNumericComparison(doubleDataEntry, doubleVal);
                }
                return false;
            }
            default -> {
                return false;
            }
        }
    }

    private boolean doNumericComparison(double dataEntry, double value) {
        switch (this.comparator) {
            case ">" -> {return dataEntry > value;}
            case ">=" -> {return dataEntry >= value;}
            case "<" -> {return dataEntry < value;}
            case "<=" -> {return dataEntry <= value;}
            case "==" -> {return dataEntry == value;}
            case "!=" -> {return dataEntry != value;}
            default -> { return false;}
        }
    }

    public String getRelationShipToNextCondition() {
        return relationShipToNextCondition;
    }

    public ArrayList<String> getTrueIds() {
        return trueIds;
    }
}
