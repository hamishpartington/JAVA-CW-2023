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
}
