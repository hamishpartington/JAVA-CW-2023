package edu.uob;

import java.util.*;

public class CommandParser {
    private String playerName;
    private final String originalCommand;
    private String processedCommand;
    private ArrayList<String> tokenisedCommand;
    private String triggerWord;
    private String destination;
    private String artefact;
    private HashSet<String> triggers;

    public CommandParser(String originalCommand, Set<String> triggers) throws STAGException.InvalidName {
        this.originalCommand = originalCommand;
        this.extractPlayerName();
        this.processCommand();
        this.tokenise(triggers);
    }

    private void extractPlayerName() throws STAGException.InvalidName {
        this.playerName = this.originalCommand.split(":")[0].toLowerCase();
        if(this.playerName.matches(".*[^a-z\\s-'].*")) {
            throw new STAGException.InvalidName();
        }
    }

    private void processCommand(){
        this.processedCommand = this.originalCommand.replaceFirst(this.playerName, "").toLowerCase();
        this.processedCommand = this.processedCommand.replaceAll("[^a-z\\s]", " ");
    }

    private void tokenise(Set<String> triggers) {
        this.tokenisedCommand = new ArrayList<>(Arrays.asList(this.processedCommand.trim().split("\\s+")));

        for(int i = 0; i < tokenisedCommand.size() - 1; i++) {
            String token = tokenisedCommand.get(i);
            String nextToken = tokenisedCommand.get(i+1);

            if(triggers.stream().anyMatch(trigger -> trigger.contains(token + " " + nextToken))){
                tokenisedCommand.set(i, token + " " + nextToken);
                tokenisedCommand.remove(i+1);
                i--;
            }
        }
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getProcessedCommand() {
        return processedCommand;
    }

    public List<String> getTokenisedCommand() {
        return tokenisedCommand;
    }

    public void checkTokensForMultipleTriggers(Set<String> definedTriggers) throws STAGException {

        this.triggers = new HashSet<>();
        tokenisedCommand.forEach(token -> {
            if(isBasicTrigger(token) | isDefined(token, definedTriggers)) {
                triggers.add(token);
            }
        });
        if(triggers.size() > 1) {
            this.whenMultipleTriggers(triggers, definedTriggers);
        }
        if(triggers.isEmpty()) {
            throw new STAGException.NoTrigger();
        }
        this.triggerWord = (String) triggers.toArray()[0];
    }

    public void checkLocation(Set<String> locationKeys, HashSet<String> accessibleLocations) throws STAGException {
        this.checkEntity(locationKeys, "location");

        if(!accessibleLocations.contains(this.destination)){
            throw new STAGException.Inaccessible(this.destination);
        }
    }

    public void whenMultipleTriggers(HashSet<String> triggers, Set<String> definedTriggers) throws STAGException {
        int nBasicTriggers = 0;
        int nDefinedTriggers = 0;
        for(String trigger: triggers) {
            if(isBasicTrigger(trigger)) {
                nBasicTriggers++;
            }
            if(isDefined(trigger, definedTriggers)) {
                nDefinedTriggers++;
            }
        }
        if(nBasicTriggers > 1 || (nBasicTriggers == 1 && nDefinedTriggers > 0)) {
            throw new STAGException.MultipleTriggers();
        }
    }

    public void checkArtefacts(Set<String> allGameArtefacts, Set<String> availableItems) throws STAGException {
        this.checkEntity(allGameArtefacts, "artefact");
        if(!availableItems.contains(this.artefact)){
            if(this.triggerWord.equals("get")) {
                throw new STAGException.NotAvailable(this.artefact);
            } else if (this.triggerWord.equals("drop")) {
                throw new STAGException.NotAvailable(this.artefact, true);
            }
        }
    }

    public void checkEntity(Set<String> keys, String entityType) throws STAGException {
        HashSet<String> entities = new HashSet<>();
        tokenisedCommand.forEach(token -> {
            if(isDefined(token, keys)) {
                entities.add(token);
            }
        });
        if(entities.size() > 1) {
            if(entityType.equals("location")) {
                throw new STAGException.MultipleLocations();
            } else {
                throw new STAGException.MultipleArtefacts(this.triggerWord);
            }
        }

        if(entities.isEmpty()) {
            if(entityType.equals("location")) {
                throw new STAGException.NoLocation();
            } else {
                throw new STAGException.NoArtefact(this.triggerWord);
            }
        }

        if(entityType.equals("location")) {
            this.destination = (String) entities.toArray()[0];
        } else {
            this.artefact = (String) entities.toArray()[0];
        }
    }

    private boolean isBasicTrigger(String token) {
        switch(token) {
            case "inv", "inventory", "get", "drop", "goto", "look", "health" -> {
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    private boolean isDefined(String token, Set<String> defined) {
        return defined.contains(token);
    }

    public String getTriggerWord() {
        return triggerWord;
    }

    public String getDestination() {
        return destination;
    }

    public String getArtefact() {
        return artefact;
    }

    public HashSet<String> getTriggers() {
        return triggers;
    }
}
