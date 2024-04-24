package edu.uob;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class CommandParser {
    private String playerName;
    private String originalCommand;
    private String processedCommand;
    private String[] tokenisedCommand;
    private String triggerWord;
    private String destination;
    private String artefact;

    public CommandParser(String originalCommand) {
        this.originalCommand = originalCommand;
        this.extractPlayerName();
        this.processCommand();
        this.tokenise();
    }

    private void extractPlayerName() {
        this.playerName = this.originalCommand.split(":")[0].toLowerCase();
    }

    private void processCommand(){
        this.processedCommand = this.originalCommand.replaceAll(":", "").toLowerCase();
        this.processedCommand = this.processedCommand.replaceFirst(this.playerName, "");
    }

    private void tokenise() {
        this.tokenisedCommand = this.processedCommand.split("\\s+");
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getProcessedCommand() {
        return processedCommand;
    }

    public String[] getTokenisedCommand() {
        return tokenisedCommand;
    }

    public void checkTokensForMultipleTriggers(Set<String> definedTriggers) throws STAGException {
        AtomicInteger triggerCount = new AtomicInteger();
        Arrays.stream(tokenisedCommand).forEach(token -> {
            if(isBasicTrigger(token) | isDefined(token, definedTriggers)) {
                this.triggerWord = token;
                triggerCount.getAndIncrement();
            }
        });
        if(triggerCount.get() > 1) {
            throw new STAGException.MultipleTriggers();
        }
    }

    public void checkLocation(Set<String> locationKeys, ArrayList<String> accessibleLocations) throws STAGException {
        AtomicInteger locationCount = new AtomicInteger();
        Arrays.stream(tokenisedCommand).forEach(token -> {
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
        Arrays.stream(tokenisedCommand).forEach(token -> {
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
            case "inv", "inventory", "get", "drop", "goto", "look" -> {
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
