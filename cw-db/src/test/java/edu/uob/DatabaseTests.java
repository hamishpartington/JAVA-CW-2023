package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseTests {

    private Database database;

    @BeforeEach
    public void setup() {
        database = new Database("test");
    }

    @Test
    public void testCreate() {
        assertDoesNotThrow(database::create, "io exception thrown when creating database");
        boolean folderExists = Files.exists(Paths.get(database.getFolderPath()));
        assertTrue(folderExists, "Failed to create database");
    }

    @Test
    public void testDrop() {
        assertDoesNotThrow(database::create, "io exception thrown when creating database");
        assertDoesNotThrow(database::drop, "io exception thrown when dropping database");
        boolean folderExists = Files.exists(Paths.get(database.getFolderPath()));
        assertFalse(folderExists, "Failed to drop database");
    }
}
