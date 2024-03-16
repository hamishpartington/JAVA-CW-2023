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

    public static class incorrectNumberOfValues extends DBException {
        @Serial private static final long serialVersionUID = 1;
        public incorrectNumberOfValues(String tableName, int size) {
            super("Incorrect number of values! The table: " + tableName + " has " + size + " non-id fields");
        }
    }

    public static class duplicateFields extends DBException {
        @Serial private static final long serialVersionUID = 1;
        public duplicateFields() {
            super("All fields must be unique");
        }
    }

    public static class fieldDoesNotExist extends DBException {
        @Serial private static final long serialVersionUID = 1;
        public fieldDoesNotExist(String field) {
            super("Cannot SELECT " + field + " as it does not exist");
        }
    }

    public static class cannotRemoveID extends DBException {
        @Serial private static final long serialVersionUID = 1;
        public cannotRemoveID() {
            super("The id field cannot be removed");
        }
    }
}
