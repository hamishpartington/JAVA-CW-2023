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
        assertFalse(response.contains("[OK]"), "An invalid query was made, however an [OK] tag was returned");
        assertTrue(response.contains("[ERROR]"), "An invalid query was made, however an [ERROR] tag was not returned");
        assertTrue(response.contains("Database must be in use"), "An invalid query was made, however the [ERROR] message was not correct");
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
        assertFalse(response.contains("[OK]"), "An invalid query was made, however an [OK] tag was returned");
        assertTrue(response.contains("[ERROR]"), "An invalid query was made, however an [ERROR] tag was not returned");
        assertTrue(response.contains("Database must be in use"), "An invalid query was made, however the [ERROR] message was not correct");
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
        assertTrue(response.contains("Database must be in use"), "An invalid query was made, however the [ERROR] message was not correct");
    }

    @Test
    public void testInsert() {
        sendCommandToServer("CREATE DATABASE Cen1us;");
        sendCommandToServer("Use Cen1us;");
        sendCommandToServer("CREATE TABLE census (name, age, weight);");
        String response = sendCommandToServer("  INSERT  INTO  census   VALUES(  'Simon Lock'  ,35, 1.8  ) ;   ");
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
    }

    @Test
    public void testInsertTableNoInUseDatabase() {
        sendCommandToServer("CREATE DATABASE Cen1us;");
        String response = sendCommandToServer("  INSERT  INTO  census   VALUES(  'Simon Lock'  ,35, 1.8  ) ;   ");
        assertFalse(response.contains("[OK]"), "An invalid query was made, however an [OK] tag was returned");
        assertTrue(response.contains("[ERROR]"), "An invalid query was made, however an [ERROR] tag was not returned");
        assertTrue(response.contains("Database must be in use"), "An invalid query was made, however the [ERROR] message was not correct");
    }

    @Test
    public void testInsertNotEnoughValues() {
        sendCommandToServer("CREATE DATABASE Cen1us;");
        sendCommandToServer("Use Cen1us;");
        sendCommandToServer("CREATE TABLE census (name, age, weight);");
        String response = sendCommandToServer("  INSERT  INTO  census   VALUES(  'Simon Lock'  , 1.8  ) ;   ");
        System.out.println(response);
        assertFalse(response.contains("[OK]"), "An invalid query was made, however an [OK] tag was returned");
        assertTrue(response.contains("[ERROR] Incorrect number of values"), "An invalid query was made, however an [ERROR] tag was not returned with the correct message");
    }

    @Test
    public void testSimpleSelect1() {
        sendCommandToServer("CREATE DATABASE Cen1us;");
        sendCommandToServer("Use Cen1us;");
        sendCommandToServer("CREATE TABLE census (name, age, weight);");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Simon Lock',  18, 1.8  ) ;   ");
        String response = sendCommandToServer("SELECT * FROM census;");
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
        assertTrue(response.contains("name"), "name not returned by SELECT *");
        assertTrue(response.contains("age"), "age not returned by SELECT *");
        assertTrue(response.contains("id"), "id not returned by SELECT *");
        assertTrue(response.contains("weight"), "weight not returned by SELECT *");
    }

    @Test
    public void testSimpleSelect2() {
        sendCommandToServer("CREATE DATABASE Cen1us;");
        sendCommandToServer("Use Cen1us;");
        sendCommandToServer("CREATE TABLE census (name, age, weight);");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Simon Lock',  18, 1.8  ) ;   ");
        String response = sendCommandToServer("SELECT name, id, age FROM census;");
        System.out.println(response);
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
        assertTrue(response.contains("name"), "name not returned by SELECT name");
        assertTrue(response.contains("age"), "age not returned by SELECT age");
        assertTrue(response.contains("id"), "id not returned by SELECT id");
        assertFalse(response.contains("weight"), "weight returned when not selected");
        assertTrue(response.contains("name\tid\tage\t"), "fields not returned in order of selection");
    }

   /* @Test
    public void testConditionalSelect4() {
        String query = "SELECT name, id, age FROM census WHERE (name == 'Simon' AND age == 30) OR weight >= 50 AND name LIKE 'ham';";
        Parser parser = new Parser(query);
        assertDoesNotThrow(parser::parseCommand,
                "Exception thrown for valid select");
    }*/
}
