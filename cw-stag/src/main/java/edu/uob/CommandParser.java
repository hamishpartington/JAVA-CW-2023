package edu.uob;

public class CommandParser {
    private String playerName;
    private String originalCommand;
    private String processedCommand;

    public CommandParser(String originalCommand) {
        this.originalCommand = originalCommand;
        this.extractPlayerName();
        this.processCommand();

    }

    private void extractPlayerName() {
        this.playerName = this.originalCommand.split(":")[0].toLowerCase();
    }

    private void processCommand(){
        this.processedCommand = this.originalCommand.replaceAll(":", "").toLowerCase();
        this.processedCommand = this.processedCommand.replaceFirst(this.playerName, "");
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getProcessedCommand() {
        return processedCommand;
    }
}
