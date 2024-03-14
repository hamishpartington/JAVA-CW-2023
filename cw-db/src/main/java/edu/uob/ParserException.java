package edu.uob;

public class ParserException extends Exception {
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

    public static class InvalidDrop extends ParserException {
        public InvalidDrop (String token) {
            super("Can only drop a table or database, not " + token);
        }
    }

    public static class InvalidAlter extends ParserException {
        public InvalidAlter () {
            super("Can only alter a table");
        }
    }

    public static class NoInto extends ParserException {
        public NoInto () {
            super("Need INTO after INSERT");
        }
    }

    public static class NoValues extends ParserException {
        public NoValues () {
            super("Need VALUES after table name in INSERT statement");
        }
    }

    public static class NoValueList extends ParserException {
        public NoValueList () {
            super("Need list of values in parentheses after VALUES in INSERT statement");
        }
    }
    public static class InvalidAlterationType extends ParserException {
        public InvalidAlterationType (String token) {
            super("Can only ADD or DROP in ALTER command. Not:" + token);
        }
    }
    public static class ListNotTerminated extends ParserException {
        public ListNotTerminated(String terminator, String type) {
            super(type + " list must be terminated with " + terminator);
        }
    }

    public static class InvalidValue extends ParserException {
        public InvalidValue(String token) {
            super(token + " is not a valid value.");
        }
    }

    public static class InvalidSelect extends ParserException {
        public InvalidSelect(String token) {
            super("Invalid SELECT statement! Next word in SELECT statement should be ; or WHERE not " + token);
        }
    }

    public static class UnmatchedParentheses extends ParserException {
        public UnmatchedParentheses() {
            super("WHERE clause contains conditions with unmatched parentheses");
        }
    }

    public static class InvalidComparator extends ParserException {
        public InvalidComparator(String token) {
            super(token + " is not a valid comparator");
        }
    }

    public static class InvalidStatementSyntax extends ParserException {
        public InvalidStatementSyntax(String token, String statement) {
            super(token + " is not a valid syntax for " + statement + " statement");
        }
    }

    public static class NoSetInUpdate extends ParserException {
        public NoSetInUpdate(String token) {
            super("Expected SET not " + token);
        }
    }

    public static class InvalidNameValuePair extends ParserException {
        public InvalidNameValuePair() {
            super("Name value pair should be in Attribute = Value format");
        }
    }
}
