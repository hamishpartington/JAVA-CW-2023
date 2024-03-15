package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
public class QueryHandlingTests {

    private DBServer server;

    // Create a new server _before_ every @Test
    @BeforeEach
    public void setup() {
        server = new DBServer();
        // delete all existing databases
        File databaseDirectory = new File(server.getStorageFolderPath());
        File[] directories = databaseDirectory.listFiles();
        if(directories != null) {
            for(File d : directories){
                File[] files = d.listFiles();
                if(files != null){
                    for(File f : files) {
                        f.delete();
                    }
                }
                d.delete();
            }
        }
    }

    // Random name generator - useful for testing "bare earth" queries (i.e. where tables don't previously exist)
    private String generateRandomName() {
        String randomName = "";
        for(int i=0; i<10 ;i++) randomName += (char)( 97 + (Math.random() * 25.0));
        return randomName;
    }

    private String sendCommandToServer(String command) {
        // Try to send a command to the server - this call will timeout if it takes too long (in case the server enters an infinite loop)
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> { return server.handleCommand(command);},
                "Server took too long to respond (probably stuck in an infinite loop)");
    }

    // A basic test that creates a database, creates a table, inserts some test data, then queries it.
    // It then checks the response to see that a couple of the entries in the table are returned as expected
    @Test
    public void testCreateDatabase() {
        String randomName = generateRandomName();
        String response = sendCommandToServer("CREATE DATABASE " + randomName + ";");
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");

    }

    @Test
    public void testCreateTable() {
        sendCommandToServer("CREATE DATABASE Cen1us;");
        sendCommandToServer("Use Cen1us;");
        String response = sendCommandToServer("CREATE TABLE census (name, age, weight);");
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
    }

    @Test
    public void testCreateTableNoUse() {
        sendCommandToServer("CREATE DATABASE Cen1us;");
        String response = sendCommandToServer("CREATE TABLE census (name, age, weight);");
        assertFalse(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertTrue(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
        assertTrue(response.contains("Database must be in use"), "A valid query was made, however an [ERROR] tag was returned");
    }

    @Test
    public void testUse() {
        sendCommandToServer("CREATE DATABASE Cen1us;");
        String response = sendCommandToServer("uSe Cen1us;");
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
    }

    @Test
    public void testDropDatabase() {
        sendCommandToServer("CREATE DATABASE Cen1us;");
        String response = sendCommandToServer("drop database Cen1us;");
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        boolean folderExists = Files.exists(Paths.get("databases" + File.separator + "Cen1us"));
        assertFalse(folderExists, "Database folder has not been deleted by drop");
        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
    }

    @Test
    public void testDropTable() {
        sendCommandToServer("CREATE DATABASE Cen1us;");
        sendCommandToServer("Use Cen1us;");
        sendCommandToServer("CREATE TABLE census (name, age, weight);");
        String response = sendCommandToServer("drop table census;");
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        boolean fileExists = Files.exists(Paths.get("databases" + File.separator + "Cen1us" + File.separator + "census"));
        assertFalse(fileExists, "Database folder has not been deleted by drop");
        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
    }

    @Test
    public void testDropTableNoInUseDatabase() {
        sendCommandToServer("CREATE DATABASE Cen1us;");
        String response = sendCommandToServer("drop table census;");
        assertFalse(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertTrue(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
        assertTrue(response.contains("Database must be in use"), "A valid query was made, however an [ERROR] tag was returned");
    }

    @Test
    public void testAlter() {
        sendCommandToServer("CREATE DATABASE Cen1us;");
        sendCommandToServer("Use Cen1us;");
        sendCommandToServer("CREATE TABLE census (name, age, weight);");
        String response = sendCommandToServer("ALTER TABLE census ADD height;");
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
    }

    @Test
    public void testAlterTableNoInUseDatabase() {
        sendCommandToServer("CREATE DATABASE Cen1us;");
        String response = sendCommandToServer("alter table census add age;");
        assertFalse(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertTrue(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
        assertTrue(response.contains("Database must be in use"), "A valid query was made, however an [ERROR] tag was returned");
    }
}
