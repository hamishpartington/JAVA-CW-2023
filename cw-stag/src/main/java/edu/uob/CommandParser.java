package edu.uob;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class CommandParser {
    private String playerName;
    private final String originalCommand;
    private String processedCommand;
    private ArrayList<String> tokenisedCommand;
    private String triggerWord;
    private String destination;
    private String artefact;

    public CommandParser(String originalCommand, Set<String> triggers) {
        this.originalCommand = originalCommand;
        this.extractPlayerName();
        this.processCommand();
        this.tokenise(triggers);
    }

    private void extractPlayerName() {
        this.playerName = this.originalCommand.split(":")[0].toLowerCase();
    }

    private void processCommand(){
        this.processedCommand = this.originalCommand.replaceFirst(this.playerName, "").toLowerCase();
        this.processedCommand = this.processedCommand.replaceAll("[^a-z\\s]", " ");
    }

    private void tokenise(Set<String> triggers) {
        String[] initialPass = this.processedCommand.trim().split("\\s+");
        this.tokenisedCommand = new ArrayList<>();
        int i;
        for(i = 0; i < initialPass.length - 1; i++) {
            String token = initialPass[i];
            String nextToken = initialPass[i+1];

            if(triggers.stream().anyMatch(trigger -> trigger.matches(token + " " + nextToken))){
                tokenisedCommand.add(token + " " + nextToken);
                i++;
            } else {
                tokenisedCommand.add(token);
            }
        }
        // add final token
        tokenisedCommand.add(initialPass[i]);
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getProcessedCommand() {
        return processedCommand;
    }

    public ArrayList<String> getTokenisedCommand() {
        return tokenisedCommand;
    }

    public void checkTokensForMultipleTriggers(Set<String> definedTriggers) throws STAGException {
        AtomicInteger triggerCount = new AtomicInteger();
        tokenisedCommand.forEach(token -> {
            if(isBasicTrigger(token) | isDefined(token, definedTriggers)) {
                this.triggerWord = token;
                triggerCount.getAndIncrement();
            }
        });
        if(triggerCount.get() > 1) {
            throw new STAGException.MultipleTriggers();
        }
        if(triggerCount.get() == 0) {
            throw new STAGException.NoTrigger();
        }
    }

    public void checkLocation(Set<String> locationKeys, HashSet<String> accessibleLocations) throws STAGException {
        AtomicInteger locationCount = new AtomicInteger();
        tokenisedCommand.forEach(token -> {
            if(isDefined(token, locationKeys)) {
                this.destination = token;
                locationCount.getAndIncrement();
            }
        });
        if(locationCount.get() > 1) {
            throw new STAGException.MultipleLocations();
        }

        if(locationCount.get() == 0) {
            throw new STAGException.NoLocation();
        }

        if(!accessibleLocations.contains(this.destination)){
            throw new STAGException.Inaccessible(this.destination);
        }
    }

    public void checkArtefacts(Set<String> allGameArtefacts, Set<String> availableItems) throws STAGException {
        AtomicInteger artefactCount = new AtomicInteger();
        tokenisedCommand.forEach(token -> {
            if(isDefined(token, allGameArtefacts)) {
                this.artefact = token;
                artefactCount.getAndIncrement();
            }
        });
        if(artefactCount.get() > 1) {
            throw new STAGException.MultipleArtefacts(this.triggerWord);
        }

        if(artefactCount.get() == 0) {
            throw new STAGException.NoArtefact(this.triggerWord);
        }

        if(!availableItems.contains(this.artefact)){
            if(this.triggerWord.equals("get")) {
                throw new STAGException.NotAvailable(this.artefact);
            } else if (this.triggerWord.equals("drop")) {
                throw new STAGException.NotAvailable(this.artefact, true);
            }
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
}
