package edu.uob;

import java.io.Serial;

public class ParserException extends Exception {
    @Serial
    private static final long serialVersionUID = 1;
    public ParserException(String message) {
        super(message);
    }

    public static class QueryNotTerminated extends ParserException {
        @Serial private static final long serialVersionUID = 1;
        public QueryNotTerminated() {
            super("Query must be terminated with semicolon");
        }
    }

    public static class NotACommandType extends ParserException {
        @Serial private static final long serialVersionUID = 1;
        public NotACommandType(String token) {
            super(token + " is not a valid command type");
        }
    }

    public static class InvalidDatabaseName extends ParserException {
        @Serial private static final long serialVersionUID = 1;
        public InvalidDatabaseName(String token) {
            super(token + " is not a valid database name. Must use alphanumeric characters with no spaces.");
        }
    }
    public static class InvalidTableName extends ParserException {
        @Serial private static final long serialVersionUID = 1;
        public InvalidTableName (String token) {
            super(token + " is not a valid table name. Must use alphanumeric characters with no spaces.");
        }
    }

    public static class InvalidAttributeName extends ParserException {
        @Serial private static final long serialVersionUID = 1;
        public InvalidAttributeName  (String token) {
            super(token + " is not a valid attribute name. Must use alphanumeric characters with no spaces.");
        }
    }

    public static class InvalidCreate extends ParserException {
        @Serial private static final long serialVersionUID = 1;
        public InvalidCreate (String token) {
            super("Can only create a table or database, not " + token);
        }
    }

    public static class InvalidDrop extends ParserException {
        @Serial private static final long serialVersionUID = 1;
        public InvalidDrop (String token) {
            super("Can only drop a table or database, not " + token);
        }
    }

    public static class InvalidAlter extends ParserException {
        @Serial private static final long serialVersionUID = 1;
        public InvalidAlter () {
            super("Can only alter a table");
        }
    }

    public static class NoInto extends ParserException {
        @Serial private static final long serialVersionUID = 1;
        public NoInto () {
            super("Need INTO after INSERT");
        }
    }

    public static class NoValues extends ParserException {
        @Serial private static final long serialVersionUID = 1;
        public NoValues () {
            super("Need VALUES after table name in INSERT statement");
        }
    }

    public static class NoValueList extends ParserException {
        @Serial private static final long serialVersionUID = 1;
        public NoValueList () {
            super("Need list of values in parentheses after VALUES in INSERT statement");
        }
    }
    public static class InvalidAlterationType extends ParserException {
        @Serial private static final long serialVersionUID = 1;
        public InvalidAlterationType (String token) {
            super("Can only ADD or DROP in ALTER command. Not:" + token);
        }
    }
    public static class ListNotTerminated extends ParserException {
        @Serial private static final long serialVersionUID = 1;
        public ListNotTerminated(String terminator, String type) {
            super(type + " list must be terminated with " + terminator);
        }
    }

    public static class InvalidValue extends ParserException {
        @Serial private static final long serialVersionUID = 1;
        public InvalidValue(String token) {
            super(token + " is not a valid value.");
        }
    }

    public static class InvalidSelect extends ParserException {
        @Serial private static final long serialVersionUID = 1;
        public InvalidSelect(String token) {
            super("Invalid SELECT statement! Next word in SELECT statement should be ; or WHERE not " + token);
        }
    }

    public static class UnmatchedParentheses extends ParserException {
        @Serial private static final long serialVersionUID = 1;
        public UnmatchedParentheses() {
            super("WHERE clause contains conditions with unmatched parentheses");
        }
    }

    public static class InvalidComparator extends ParserException {
        @Serial private static final long serialVersionUID = 1;
        public InvalidComparator(String token) {
            super(token + " is not a valid comparator");
        }
    }

    public static class InvalidStatementSyntax extends ParserException {
        @Serial private static final long serialVersionUID = 1;
        public InvalidStatementSyntax(String token, String statement) {
            super(token + " is not a valid syntax for " + statement + " statement");
        }
    }

    public static class NoSetInUpdate extends ParserException {
        @Serial private static final long serialVersionUID = 1;
        public NoSetInUpdate(String token) {
            super("Expected SET not " + token);
        }
    }

    public static class InvalidNameValuePair extends ParserException {
        @Serial private static final long serialVersionUID = 1;
        public InvalidNameValuePair() {
            super("Name value pair should be in Attribute = Value format");
        }
    }

    public static class NoFromDelete extends ParserException {
        @Serial private static final long serialVersionUID = 1;
        public NoFromDelete(String token) {
            super("Expected DELETE FROM not DELETE " + token);
        }
    }

    public static class NoWhere extends ParserException {
        @Serial private static final long serialVersionUID = 1;
        public NoWhere(String token) {
            super("Expected WHERE not " + token);
        }
    }

    public static class InvalidJoin extends ParserException {
        @Serial private static final long serialVersionUID = 1;
        public InvalidJoin(String token, String expected) {
            super("Invalid JOIN! Expected " + expected + " not " + token);
        }
    }

    public static class NoDatabaseInUse extends ParserException {
        @Serial private static final long serialVersionUID = 1;
        public NoDatabaseInUse(String statementType) {
            super("Database must be in use to " + statementType + " tables");
        }
    }

    public static class CannotEnterMultipleQueries extends ParserException {
        @Serial private static final long serialVersionUID = 1;
        public CannotEnterMultipleQueries() {
            super("The server can only handle one query at a time. It is forbidden to chain multiple queries togeter with ;");
        }
    }

    public static class InvalidWildAttributeList extends ParserException {
        @Serial private static final long serialVersionUID = 1;
        public InvalidWildAttributeList() {
            super("SELECT * cannot include extra attributes");
        }
    }
}
