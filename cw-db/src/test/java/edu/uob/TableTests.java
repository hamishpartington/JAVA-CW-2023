package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
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
        Set<String> attributes = new HashSet<>();
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
}
