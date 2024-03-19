package edu.uob;

import java.io.Serial;

public class DBException extends Exception {
    public DBException(String message) {
        super(message);
    }
    @Serial private static final long serialVersionUID = 1;
    public static class TableAlreadyExists extends DBException {
        @Serial private static final long serialVersionUID = 1;
        public TableAlreadyExists(String tableName, String databaseName) {
            super(tableName + " already exists in the " + databaseName + " database");
        }
    }
    public static class TableDoesNotExist extends DBException {
        @Serial private static final long serialVersionUID = 1;
        public TableDoesNotExist (String tableName, String databaseName) {
            super("The table: " + tableName + " does not exist in the " + databaseName + " database");
        }
    }

    public static class DBAlreadyExists extends DBException {
        @Serial private static final long serialVersionUID = 1;
        public DBAlreadyExists(String DBName) {
            super("The database: " + DBName + " already exists");
        }
    }

    public static class DBDoesNotExist extends DBException {
        @Serial private static final long serialVersionUID = 1;
        public DBDoesNotExist(String DBName) {
            super("The database: " + DBName + " does not exist");
        }
    }

    public static class IncorrectNumberOfValues extends DBException {
        @Serial private static final long serialVersionUID = 1;
        public IncorrectNumberOfValues(String tableName, int size) {
            super("Incorrect number of values! The table: " + tableName + " has " + size + " non-id fields");
        }
    }

    public static class DuplicateFields extends DBException {
        @Serial private static final long serialVersionUID = 1;
        public DuplicateFields() {
            super("All fields must be unique");
        }
    }

    public static class FieldDoesNotExist extends DBException {
        @Serial private static final long serialVersionUID = 1;
        public FieldDoesNotExist(String field, String commandType) {
            super("Cannot " + commandType + " " + field + " as it does not exist");
        }
    }

    public static class CannotRemoveID extends DBException {
        @Serial private static final long serialVersionUID = 1;
        public CannotRemoveID() {
            super("The id field cannot be removed");
        }
    }

    public static class CannotUpdateID extends DBException {
        @Serial private static final long serialVersionUID = 1;
        public CannotUpdateID() {
            super("The id field cannot be updated");
        }
    }

    public static class ReservedKeyWord extends DBException {
        @Serial private static final long serialVersionUID = 1;
        public ReservedKeyWord(String name) {
            super(name + " is a reserved keyword so cannot be used as a name");
        }
    }
}
