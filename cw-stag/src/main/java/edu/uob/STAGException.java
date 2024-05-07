package edu.uob;

import java.io.Serial;
public class STAGException extends Exception {
    public STAGException(String message) {
        super(message);
    }
    @Serial private static final long serialVersionUID = 1;
    public static class MultipleTriggers extends STAGException {
        @Serial private static final long serialVersionUID = 1;
        public MultipleTriggers() {
            super("There are too many trigger words in this command");
        }
    }

    public static class NoTrigger extends STAGException {
        @Serial private static final long serialVersionUID = 1;
        public NoTrigger() {
            super("There are no trigger words in this command");
        }
    }

    public static class MultipleLocations extends STAGException {
        @Serial private static final long serialVersionUID = 1;
        public MultipleLocations() {
            super("There are too many locations in this command. You can only goto one of them");
        }
    }

    public static class Inaccessible extends STAGException {
        @Serial private static final long serialVersionUID = 1;
        public Inaccessible(String location) {
            super("The " + location + " cannot be accessed from your current location");
        }
    }

    public static class NoLocation extends STAGException {
        @Serial private static final long serialVersionUID = 1;
        public NoLocation() {
            super("There is no location in your goto command");
        }
    }

    public static class MultipleArtefacts extends STAGException {
        @Serial private static final long serialVersionUID = 1;
        public MultipleArtefacts(String trigger) {
            super("There are too many artefacts in this command. You can only " + trigger + " one of them");
        }
    }

    public static class NoArtefact extends STAGException {
        @Serial private static final long serialVersionUID = 1;
        public NoArtefact(String trigger) {
            super("There is no artefact in your " + trigger + " command");
        }
    }

    public static class NotAvailable extends STAGException {
        @Serial private static final long serialVersionUID = 1;
        public NotAvailable(String artefact) {
            super("The " + artefact + " is not in your current location so cannot be picked up");
        }
        public NotAvailable(String artefact, boolean isDrop){
            super("The " + artefact + " is not in your inventory so cannot be dropped");
        }

        public NotAvailable(){
            super("All subjects must either be in your current location or inventory in order to perform an action");
        }
    }

    public static class NoSubject extends STAGException {
        @Serial private static final long serialVersionUID = 1;
        public NoSubject() {
            super("You must specify at least one subject of an action");
        }
    }

    public static class Ambiguous extends STAGException {
        @Serial private static final long serialVersionUID = 1;
        public Ambiguous(String trigger) {
            super("There is more than one '" + trigger + "' action possible for the given command. You must be more specific.");
        }
    }

    public static class ExtraneousEntities extends STAGException {
        @Serial private static final long serialVersionUID = 1;
        public ExtraneousEntities() {
            super("Your command contains extraneous entities");
        }
    }
}
