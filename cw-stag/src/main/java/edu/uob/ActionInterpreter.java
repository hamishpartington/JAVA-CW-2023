package edu.uob;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class ActionInterpreter {
    private final HashSet<GameAction> potentialActions;
    private final List<String> tokens;
    private final HashSet<GameAction> viableActions;

    private final HashSet<GameAction> performableActions;

    private final String trigger;

    public ActionInterpreter(HashSet<GameAction> potentialActions, List<String> tokens, String trigger) {
        this.potentialActions = potentialActions;
        this.tokens = tokens;
        this.viableActions = new HashSet<>();
        this.performableActions = new HashSet<>();
        this.trigger = trigger;
    }

    public void determineViableActions() throws STAGException {
        for(GameAction action: potentialActions) {
            tokens.forEach(token -> {
                if(action.getSubjects().contains(token)){
                    this.viableActions.add(action);
                }
            });
        }
        if(this.viableActions.isEmpty()) {
            throw new STAGException.NoSubject();
        }
    }

    public GameAction determinePerformableActions(Location currLocation, HashMap<String, Artefact> playerInventory) throws STAGException {
        this.determineViableActions();
        for(GameAction action: viableActions) {
            HashSet<String> subjects = action.getSubjects();
            boolean performable = true;
            for(String subject : subjects){
                if(!(currLocation.getAvailableEntities().contains(subject) || playerInventory.containsKey(subject))){
                    performable = false;
                    break;
                }
            }
            if(performable) {
                this.performableActions.add(action);
            }
        }
        if(this.performableActions.isEmpty()) {
            throw new STAGException.NotAvailable();
        }

        if(this.performableActions.size() > 1) {
            throw new STAGException.Ambiguous(this.trigger);
        }
        return (GameAction)this.performableActions.toArray()[0];
    }
}
