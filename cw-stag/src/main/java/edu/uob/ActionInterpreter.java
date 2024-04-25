package edu.uob;

import java.util.Arrays;
import java.util.HashSet;

public class ActionInterpreter {
    private HashSet<GameAction> potentialActions;
    private String[] tokens;
    private HashSet<GameAction> viableActions;

    private String trigger;

    public ActionInterpreter(HashSet<GameAction> potentialActions, String[] tokens, String trigger) {
        this.potentialActions = potentialActions;
        this.tokens = tokens;
        this.viableActions = new HashSet<>();
        this.trigger = trigger;
    }

    public GameAction determineViableAction() throws STAGException {
        for(GameAction action: potentialActions) {
            Arrays.stream(tokens).forEach(token -> {
                if(action.getSubjects().contains(token)){
                    this.viableActions.add(action);
                }
            });
        }
        if(this.viableActions.isEmpty()) {
            throw new STAGException.NoSubject();
        }

        if(this.viableActions.size() > 1) {
            throw new STAGException.Ambiguous(this.trigger);
        }
        return (GameAction)this.viableActions.toArray()[0];
    }
}
