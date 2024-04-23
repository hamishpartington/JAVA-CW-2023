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
}
