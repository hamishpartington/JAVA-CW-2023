package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
public class TableTests {
    private Database database;

    @BeforeEach
    public void setup() {
        database = new Database("test");
        if(!Files.exists(Paths.get(database.getFolderPath()))){
            assertDoesNotThrow(database::create);
        }
    }

    @Test
    public void testCreate() {
        ArrayList<String> attributes = new ArrayList<>();
        attributes.add("age");
        attributes.add("DoB");
        attributes.add("gender");
        assertDoesNotThrow(()->database.createTable("test", attributes),
                "Io exception thrown when creating table");
    }
    @Test
    public void testTableAlreadyExists() {
        assertDoesNotThrow(()->database.createTable("test", null),
                "Io exception thrown when creating table");
        assertThrows(DBException.TableAlreadyExists.class,
                ()->database.createTable("test", null),
                "Table already exists");
    }

    @Test
    public void testDrop() {
        assertDoesNotThrow(()->database.createTable("test", null),
                "Io exception thrown when creating table");
        File tableFile = database.getTables().get("test").getTableFile();
        assertDoesNotThrow(()->database.dropTable("test"),
                "Exception thrown when attempting to drop table");
        assertFalse(tableFile.exists(), "Failed to delete tableFile");
        assertNull(database.getTables().get("test"), "Failed to remove table object from database");
    }

    @Test
    public void testTableDoesNotExist() {
        assertDoesNotThrow(()->database.createTable("test", null),
                "Io exception thrown when creating table");
        assertDoesNotThrow(()->database.dropTable("test"),
                "Exception thrown when attempting to drop table");
        assertThrows(DBException.TableDoesNotExist.class, ()->database.dropTable("test"),
                "Table does not exist exception not thrown");
    }

    @Test
    public void testInsert() {
        ArrayList<String> attributes = new ArrayList<>();
        attributes.add("age");
        attributes.add("DoB");
        attributes.add("gender");
        assertDoesNotThrow(()->database.createTable("test", attributes),
                "Io exception thrown when creating table");
        ArrayList<String> values = new ArrayList<>();
        values.add("25");
        values.add("23/08/1997");
        values.add("Male");
        assertDoesNotThrow(()->database.insertIntoTable("test", values),
                "Exception thrown attempting to insert values");
    }

    @Test
    public void testIncorrectNumberOfValues() {
        ArrayList<String> attributes = new ArrayList<>();
        attributes.add("age");
        attributes.add("DoB");
        attributes.add("gender");
        assertDoesNotThrow(()->database.createTable("test", attributes),
                "Io exception thrown when creating table");
        ArrayList<String> values = new ArrayList<>();
        values.add("25");
        values.add("23/08/1997");
        assertThrows(DBException.incorrectNumberOfValues.class, ()->database.insertIntoTable("test", values),
                "incorrectNumberOfValues exception not thrown");
    }

    @Test
    public void testDuplicateFields() {
        ArrayList<String> attributes = new ArrayList<>();
        attributes.add("age");
        attributes.add("DoB");
        attributes.add("age");
        assertThrows(DBException.duplicateFields.class, ()->database.createTable("test", attributes),
                "duplicateFields exception not thrown");
    }
}
