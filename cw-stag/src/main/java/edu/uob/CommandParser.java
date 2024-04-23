package edu.uob;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class CommandParser {
    private String playerName;
    private String originalCommand;
    private String processedCommand;
    private String[] tokenisedCommand;

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

    public void checkTokensForMultipleBasicTriggers() throws STAGException {
        AtomicInteger triggerCount = new AtomicInteger();
        Arrays.stream(tokenisedCommand).forEach(token -> {
            if(isBasicTrigger(token)) {
                triggerCount.getAndIncrement();
            }
        });
        if(triggerCount.get() > 1) {
            throw new STAGException.MultipleTriggers();
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
}
