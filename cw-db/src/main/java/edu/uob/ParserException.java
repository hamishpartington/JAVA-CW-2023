package edu.uob;

public class ParserException extends Exception{
    public ParserException(String message) {
        super(message);
    }

    public static class QueryNotTerminated extends ParserException {
        public QueryNotTerminated() {
            super("Query must be terminated with semicolon");
        }
    }

    public static class NotACommandType extends ParserException {
        public NotACommandType(String token) {
            super(token + " is not a valid command type");
        }
    }

    public static class InvalidDatabaseName extends ParserException {
        public InvalidDatabaseName(String token) {
            super(token + " is not a valid database name. Must use alphanumeric characters with no spaces.");
        }
    }
    public static class InvalidTableName extends ParserException {
        public InvalidTableName (String token) {
            super(token + " is not a valid table name. Must use alphanumeric characters with no spaces.");
        }
    }

    public static class InvalidAttributeName extends ParserException {
        public InvalidAttributeName  (String token) {
            super(token + " is not a valid attribute name. Must use alphanumeric characters with no spaces.");
        }
    }

    public static class InvalidCreate extends ParserException {
        public InvalidCreate (String token) {
            super("Can only create a table or database, not " + token);
        }
    }

    public static class AttributeListNotTerminated extends ParserException {
        public AttributeListNotTerminated (String terminator) {
            super("AttributeList must be terminated with " + terminator);
        }
    }
}
