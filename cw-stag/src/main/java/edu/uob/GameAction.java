package edu.uob;

import java.util.HashSet;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class GameAction
{
    private HashSet<String> triggers;
    private HashSet<String> subjects;
    private HashSet<String> consumed;
    private HashSet<String> produced;

    private String narration;

    public GameAction(Element action) {
        this.triggers = new HashSet<>();
        this.buildSet(action, "triggers", "keyphrase", this.triggers);
        this.subjects = new HashSet<>();
        this.buildSet(action, "subjects", "entity", this.subjects);
        this.consumed = new HashSet<>();
        this.buildSet(action, "consumed", "entity", this.consumed);
        this.produced = new HashSet<>();
        this.buildSet(action, "produced", "entity", this.produced);
        this.narration = action.getElementsByTagName("narration").item(0).getTextContent();

    }

    private void buildSet(Element action, String firstTag, String secondTag, HashSet<String> set) {
        Element firstTags = (Element)action.getElementsByTagName(firstTag).item(0);
        NodeList secondTags = firstTags.getElementsByTagName(secondTag);
        int length = secondTags.getLength();
        for(int i = 0; i < length; i++) {
            set.add(secondTags.item(i).getTextContent().toLowerCase());
        }
    }

    public HashSet<String> getTriggers() {
        return triggers;
    }

    public HashSet<String> getSubjects() {
        return subjects;
    }

    public HashSet<String> getConsumed() {
        return consumed;
    }

    public HashSet<String> getProduced() {
        return produced;
    }

    public String getNarration() {
        return narration;
    }
}
