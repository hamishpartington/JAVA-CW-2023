package edu.uob;

public class DBException extends Exception {
    public DBException(String message) {
        super(message);
    }

    public static class TableAlreadyExists extends DBException {
        public TableAlreadyExists(String tableName, String databaseName) {
            super(tableName + " already exists in the " + databaseName + " database");
        }
    }
    public static class TableDoesNotExist extends DBException {
        public TableDoesNotExist (String tableName, String databaseName) {
            super("The table: " + tableName + " does not exist in the " + databaseName + " database");
        }
    }

    public static class DBAlreadyExists extends DBException {
        public DBAlreadyExists(String DBName) {
            super("The database: " + DBName + " already exists");
        }
    }

    public static class DBDoesNotExist extends DBException {
        public DBDoesNotExist(String DBName) {
            super("The database: " + DBName + " does not exist");
        }
    }
}
