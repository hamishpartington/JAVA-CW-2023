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
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("Use " + randomName + ";");
        String response = sendCommandToServer("CREATE TABLE census (name, age, weight);");
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
    }

    @Test
    public void testCreateTableNoUse() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        String response = sendCommandToServer("CREATE TABLE census (name, age, weight);");
        assertFalse(response.contains("[OK]"), "An invalid query was made, however an [OK] tag was returned");
        assertTrue(response.contains("[ERROR]"), "An invalid query was made, however an [ERROR] tag was not returned");
        assertTrue(response.contains("Database must be in use"), "An invalid query was made, however the [ERROR] message was not correct");
    }

    @Test
    public void testUse() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        String response = sendCommandToServer("uSe " + randomName + ";");
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
    }

    @Test
    public void testDropDatabase() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        String response = sendCommandToServer("drop database " + randomName + ";");
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        boolean folderExists = Files.exists(Paths.get("databases" + File.separator + "Cen1us"));
        assertFalse(folderExists, "Database folder has not been deleted by drop");
        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
    }

    @Test
    public void testDropTable() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("Use " + randomName + ";");
        sendCommandToServer("CREATE TABLE census (name, age, weight);");
        String response = sendCommandToServer("drop table census;");
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        boolean fileExists = Files.exists(Paths.get("databases" + File.separator + "Cen1us" + File.separator + "census"));
        assertFalse(fileExists, "Database folder has not been deleted by drop");
        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
    }

    @Test
    public void testDropTableNoInUseDatabase() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        String response = sendCommandToServer("drop table census;");
        assertFalse(response.contains("[OK]"), "An invalid query was made, however an [OK] tag was returned");
        assertTrue(response.contains("[ERROR]"), "An invalid query was made, however an [ERROR] tag was not returned");
        assertTrue(response.contains("Database must be in use"), "An invalid query was made, however the [ERROR] message was not correct");
    }

    @Test
    public void testAlter() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("Use " + randomName + ";");
        sendCommandToServer("CREATE TABLE census (name, age, weight);");
        String response = sendCommandToServer("ALTER TABLE census ADD height;");
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
    }

    @Test
    public void testAlterTableNoInUseDatabase() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        String response = sendCommandToServer("alter table census add age;");
        assertFalse(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertTrue(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
        assertTrue(response.contains("Database must be in use"), "An invalid query was made, however the [ERROR] message was not correct");
    }

    @Test
    public void testInsert() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("Use " + randomName + ";");
        sendCommandToServer("CREATE TABLE census (name, age, weight);");
        String response = sendCommandToServer("  INSERT  INTO  census   VALUES(  'Simon Lock'  ,35, 1.8  ) ;   ");
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
    }

    @Test
    public void testInsertTableNoInUseDatabase() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        String response = sendCommandToServer("  INSERT  INTO  census   VALUES(  'Simon Lock'  ,35, 1.8  ) ;   ");
        assertFalse(response.contains("[OK]"), "An invalid query was made, however an [OK] tag was returned");
        assertTrue(response.contains("[ERROR]"), "An invalid query was made, however an [ERROR] tag was not returned");
        assertTrue(response.contains("Database must be in use"), "An invalid query was made, however the [ERROR] message was not correct");
    }

    @Test
    public void testInsertNotEnoughValues() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("Use " + randomName + ";");
        sendCommandToServer("CREATE TABLE census (name, age, weight);");
        String response = sendCommandToServer("  INSERT  INTO  census   VALUES(  'Simon Lock'  , 1.8  ) ;   ");
        assertFalse(response.contains("[OK]"), "An invalid query was made, however an [OK] tag was returned");
        assertTrue(response.contains("[ERROR] Incorrect number of values"), "An invalid query was made, however an [ERROR] tag was not returned with the correct message");
    }

    @Test
    public void testSimpleSelect1() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("Use " + randomName + ";");
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
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("Use " + randomName + ";");
        sendCommandToServer("CREATE TABLE census (name, age, weight);");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Simon Lock',  18, 1.8  ) ;   ");
        String response = sendCommandToServer("SELECT name, id, age FROM census;");
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
        assertTrue(response.contains("name"), "name not returned by SELECT name");
        assertTrue(response.contains("age"), "age not returned by SELECT age");
        assertTrue(response.contains("id"), "id not returned by SELECT id");
        assertFalse(response.contains("weight"), "weight returned when not selected");
        assertTrue(response.contains("name\tid\tage\t"), "fields not returned in order of selection");
    }

   @Test
    public void testConditionalSelect1() {
       String randomName = generateRandomName();
       sendCommandToServer("CREATE DATABASE " + randomName + ";");
       sendCommandToServer("Use " + randomName + ";");
       sendCommandToServer("CREATE TABLE census (name, age, weight);");
       sendCommandToServer("  INSERT  INTO  census   VALUES(  'Simon Lock',  18, 80 ) ;   ");
       sendCommandToServer("  INSERT  INTO  census   VALUES(  'Sam Lock',  18, 90 ) ;   ");
       sendCommandToServer("  INSERT  INTO  census   VALUES(  'Simon Simpson',  23, 80 ) ;   ");
       String response = sendCommandToServer("SELECT nAme, id, aGe FROM census WHERE age == 18;");
       assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
       assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
       assertTrue(response.contains("Simon Lock"), "entry with valid age not returned");
       assertTrue(response.contains("Sam Lock"), "entry with valid age not returned");
    }

    @Test
    public void testConditionalSelect2() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("Use " + randomName + ";");
        sendCommandToServer("CREATE TABLE census (name, age, weight);");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Simon Lock',  18, 80 ) ;   ");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Sam Lock',  18, 90 ) ;   ");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Simon Simpson',  23, 80 ) ;   ");
        String response = sendCommandToServer("SELECT name, id, age FROM census WHERE age > 18;");
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
        assertTrue(response.contains("Simon Simpson"), "entry with valid age not returned");
    }

    @Test
    public void testConditionalSelect3() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("Use " + randomName + ";");
        sendCommandToServer("CREATE TABLE census (name, age, weight);");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Simon Lock',  18, 80 ) ;   ");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Sam Lock',  18, 90 ) ;   ");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Simon Simpson',  23, 80 ) ;   ");
        String response = sendCommandToServer("SELECT name, id, age FROM census WHERE name LIKE 'Lock';");
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
        assertFalse(response.contains("Simon Simpson"), "entry with invalid name returned");
        assertTrue(response.contains("Simon Lock"), "entry with valid name not returned");
        assertTrue(response.contains("Sam Lock"), "entry with valid name not returned");
    }

    @Test
    public void testConditionalSelect4() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("Use " + randomName + ";");
        sendCommandToServer("CREATE TABLE census (name, age, weight);");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Simon Lock',  18, 80 ) ;   ");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Sam Lock',  18, 90 ) ;   ");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Simon Simpson',  23, 80 ) ;   ");
        String response = sendCommandToServer("SELECT name, id, age FROM census WHERE name LIKE 'Lock' OR age >= 23;");
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
        assertTrue(response.contains("Simon Simpson"), "entry with valid name returned");
        assertTrue(response.contains("Simon Lock"), "entry with valid name not returned");
        assertTrue(response.contains("Sam Lock"), "entry with valid name not returned");
    }

    @Test
    public void testConditionalSelect5() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("Use " + randomName + ";");
        sendCommandToServer("CREATE TABLE census (name, age, weight);");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Simon Lock',  18, 80 ) ;   ");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Sam Lock',  18, 90 ) ;   ");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Simon Simpson',  23, 80 ) ;   ");
        String response = sendCommandToServer("SELECT name, id, age FROM census WHERE name LIKE 'Lock' AND weight <= 80;");
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
        assertFalse(response.contains("Simon Simpson"), "entry with invalid name returned");
        assertTrue(response.contains("Simon Lock"), "entry with valid name not returned");
        assertFalse(response.contains("Sam Lock"), "entry with invalid name returned");
    }

    @Test
    public void testUpdate1(){
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("Use " + randomName + ";");
        sendCommandToServer("CREATE TABLE census (name, age, weight);");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Simon Lock',  18, 80 ) ;   ");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Sam Lock',  18, 90 ) ;   ");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Simon Simpson',  23, 80 ) ;   ");
        String response = sendCommandToServer("UPDATE census SET Age = 24 WHERE name LIKE 'Sam';");
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
        server = new DBServer();
        sendCommandToServer("Use " + randomName + ";");
        String updatedTableString = server.getDatabaseInUse().getTables().get("census").toString();
        String expectedSubString = "'Sam Lock'\t24\t";
        assertTrue(updatedTableString.contains(expectedSubString), "The table has not updated correctly");
    }

    @Test
    public void testUpdate2(){
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("Use " + randomName + ";");
        sendCommandToServer("CREATE TABLE census (name, age, weight);");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Simon Lock',  18, 80 ) ;   ");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Sam Lock',  18, 90 ) ;   ");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Simon Simpson',  23, 80 ) ;   ");
        String response = sendCommandToServer("UPDATE census SET Age = 24 WHERE name LIKE 'Simon';");
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
        server = new DBServer();
        sendCommandToServer("Use " + randomName + ";");
        String updatedTableString = server.getDatabaseInUse().getTables().get("census").toString();
        String expectedSubString1 = "'Simon Lock'\t24\t";
        String expectedSubString2 = "'Simon Simpson'\t24\t";
        assertTrue(updatedTableString.contains(expectedSubString1), "The table has not updated correctly");
        assertTrue(updatedTableString.contains(expectedSubString2), "The table has not updated correctly");
    }

    @Test
    public void testUpdate3(){
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("Use " + randomName + ";");
        sendCommandToServer("CREATE TABLE census (name, age, weight);");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Simon Lock',  18, 80 ) ;   ");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Sam Lock',  18, 90 ) ;   ");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Simon Simpson',  23, 80 ) ;   ");
        String response = sendCommandToServer("UPDATE census SET id = 24 WHERE name LIKE 'Simon';");
        assertFalse(response.contains("[OK]"), "An invalid query was made, however an [OK] tag was not returned");
        assertTrue(response.contains("[ERROR]"), "An invalid query was made, however an [ERROR] tag was returned");
        assertTrue(response.contains("The id field cannot be updated"), "Expected exception not thrown");
    }

    @Test
    public void testUpdate4(){
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("Use " + randomName + ";");
        sendCommandToServer("CREATE TABLE census (name, age, weight);");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Simon Lock',  18, 80 ) ;   ");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Sam Lock',  18, 90 ) ;   ");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Simon Simpson',  23, 80 ) ;   ");
        String response = sendCommandToServer("UPDATE census SET height = 24 WHERE name LIKE 'Simon';");
        assertFalse(response.contains("[OK]"), "An invalid query was made, however an [OK] tag was not returned");
        assertTrue(response.contains("[ERROR]"), "An invalid query was made, however an [ERROR] tag was returned");
        assertTrue(response.contains("Cannot UPDATE height as it does not exist"), "Expected exception not thrown");
    }

    @Test
    public void testDelete1(){
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("Use " + randomName + ";");
        sendCommandToServer("CREATE TABLE census (name, age, weight);");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Simon Lock',  18, 80 ) ;   ");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Sam Lock',  18, 90 ) ;   ");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Simon Simpson',  23, 80 ) ;   ");
        String response = sendCommandToServer("DELETE FROM census WHERE name LIKE 'Simon';");
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
        server = new DBServer();
        sendCommandToServer("Use " + randomName + ";");
        String updatedTableString = server.getDatabaseInUse().getTables().get("census").toString();
        String unexpectedSubString = "'Simon'";
        assertFalse(updatedTableString.contains(unexpectedSubString), "The table has not deleted correctly");
    }

    @Test
    public void testDelete2(){
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("Use " + randomName + ";");
        sendCommandToServer("CREATE TABLE census (name, age, weight);");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Simon Lock',  18, 80 ) ;   ");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Sam Lock',  18, 90 ) ;   ");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Simon Simpson',  23, 80 ) ;   ");
        String response = sendCommandToServer("DELETE FROM census WHERE name LIKE 'Simon' AND age == 18;");
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
        server = new DBServer();
        sendCommandToServer("Use " + randomName + ";");
        String updatedTableString = server.getDatabaseInUse().getTables().get("census").toString();
        String unexpectedSubString = "'Simon Lock'";
        assertFalse(updatedTableString.contains(unexpectedSubString), "The table has not deleted correctly");
    }

    @Test
    public void testDelete3(){
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("Use " + randomName + ";");
        sendCommandToServer("CREATE TABLE census (name, age, weight);");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Simon Lock',  18, 80 ) ;   ");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Sam Lock',  18, 90 ) ;   ");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Simon Simpson',  23, 80 ) ;   ");
        String response = sendCommandToServer("DELETE FROM census WHERE name LIKE 'Simon' AND height == 18;");
        assertFalse(response.contains("[OK]"), "An invalid query was made, however an [OK] tag was not returned");
        assertTrue(response.contains("[ERROR]"), "An invalid query was made, however an [ERROR] tag was returned");
        assertTrue(response.contains("Cannot DELETE height as it does not exist"), "Expected exception not thrown");
    }

    @Test
    public void testDelete4(){
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("Use " + randomName + ";");
        sendCommandToServer("CREATE TABLE census (name, age, weight);");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Simon Lock',  18, 80 ) ;   ");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Sam Lock',  18, 90 ) ;   ");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Simon Simpson',  23, 80 ) ;   ");
        String response = sendCommandToServer("DELETE FROM censs WHERE name LIKE 'Simon' AND height == 18;");
        assertFalse(response.contains("[OK]"), "An invalid query was made, however an [OK] tag was not returned");
        assertTrue(response.contains("[ERROR]"), "An invalid query was made, however an [ERROR] tag was returned");
        assertTrue(response.contains("The table: censs does not exist in the " + randomName + " database"), "Expected exception not thrown");
    }

    @Test
    public void testJoin1(){
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("Use " + randomName + ";");
        sendCommandToServer("CREATE TABLE census (name, age, weight, cityId);");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Simon Lock',  18, 80, 1 ) ;   ");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Sam Lock',  18, 90, 2) ;   ");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Simon Simpson',  23, 80, 3 ) ;   ");
        sendCommandToServer("CREATE TABLE city (cityName);");
        sendCommandToServer("INSERT INTO city VALUES ('London');");
        sendCommandToServer("INSERT INTO city VALUES ('Bristol');");
        sendCommandToServer("INSERT INTO city VALUES ('Manchester');");
        String response = sendCommandToServer("JOIN census AND city ON cityId AND id;");
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
        String expectedCols = "id\tcensus.name\tcensus.age\tcensus.weight\tcity.cityName\t";
        assertTrue(response.contains(expectedCols), "Column names not as expected after join");
        String expectedData = "1\t'Simon Lock'\t18\t80\t'London'\t\n2\t'Sam Lock'\t18\t90\t'Bristol'\t\n3\t'Simon Simpson'\t23\t80\t'Manchester'";
        assertTrue(response.contains(expectedData), "Data not as expected after join");
    }
    @Test
    public void testJoin2(){
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("Use " + randomName + ";");
        sendCommandToServer("CREATE TABLE census (name, age, weight, cityId);");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Simon Lock',  18, 80, 1 ) ;   ");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Sam Lock',  18, 90, 2) ;   ");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Simon Simpson',  23, 80, 3 ) ;   ");
        sendCommandToServer("CREATE TABLE city (cityName);");
        sendCommandToServer("INSERT INTO city VALUES ('London');");
        sendCommandToServer("INSERT INTO city VALUES ('Bristol');");
        String response = sendCommandToServer("JOIN census AND city ON cityId AND id;");
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
        String expectedCols = "id\tcensus.name\tcensus.age\tcensus.weight\tcity.cityName\t";
        assertTrue(response.contains(expectedCols), "Column names not as expected after join");
        String expectedData = "1\t'Simon Lock'\t18\t80\t'London'\t\n2\t'Sam Lock'\t18\t90\t'Bristol'";
        String notExpectedData = "'Simon Simpson'";
        assertTrue(response.contains(expectedData), "Data not as expected after join");
        assertFalse(response.contains(notExpectedData), "Data not as expected after join");
    }

    @Test
    public void testJoin3(){
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("Use " + randomName + ";");
        sendCommandToServer("CREATE TABLE census (name, age, weight, cityId);");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Simon Lock',  18, 80, 1 ) ;   ");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Sam Lock',  18, 90, 3) ;   ");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Simon Simpson',  23, 80, 3 ) ;   ");
        sendCommandToServer("CREATE TABLE city (cityName);");
        sendCommandToServer("INSERT INTO city VALUES ('London');");
        sendCommandToServer("INSERT INTO city VALUES ('Bristol');");
        String response = sendCommandToServer("JOIN census AND city ON cityId AND id;");
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
        String expectedCols = "id\tcensus.name\tcensus.age\tcensus.weight\tcity.cityName\t";
        assertTrue(response.contains(expectedCols), "Column names not as expected after join");
        String expectedData = "1\t'Simon Lock'\t18\t80\t'London'";
        String notExpectedData1 = "'Simon Simpson'";
        String notExpectedData2 = "'Sam Lock'";
        assertTrue(response.contains(expectedData), "Data not as expected after join");
        assertFalse(response.contains(notExpectedData1), "Data not as expected after join");
        assertFalse(response.contains(notExpectedData2), "Data not as expected after join");
    }

    @Test
    public void testJoin4(){
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("Use " + randomName + ";");
        sendCommandToServer("CREATE TABLE census (name, age, weight, cityId);");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Simon Lock',  18, 80, 3 ) ;   ");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Sam Lock',  18, 90, 3) ;   ");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Simon Simpson',  23, 80, 2 ) ;   ");
        sendCommandToServer("CREATE TABLE city (cityName);");
        sendCommandToServer("INSERT INTO city VALUES ('London');");
        sendCommandToServer("INSERT INTO city VALUES ('Bristol');");
        String response = sendCommandToServer("JOIN cenSus AND ciTy ON cityid AND Id;");
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
        String expectedCols = "id\tcensus.name\tcensus.age\tcensus.weight\tcity.cityName\t";
        assertTrue(response.contains(expectedCols), "Column names not as expected after join");
        String expectedData = "1\t'Simon Simpson'\t23\t80\t'Bristol'";
        String notExpectedData1 = "'Simon Lock'";
        String notExpectedData2 = "'Sam Lock'";
        assertTrue(response.contains(expectedData), "Data not as expected after join");
        assertFalse(response.contains(notExpectedData1), "Data not as expected after join");
        assertFalse(response.contains(notExpectedData2), "Data not as expected after join");
    }

    @Test
    public void testJoin5(){
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("Use " + randomName + ";");
        sendCommandToServer("CREATE TABLE census (name, age, weight, cityId);");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Simon Lock',  18, 80, 3 ) ;   ");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Sam Lock',  18, 90, 3) ;   ");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Simon Simpson',  23, 80, 2 ) ;   ");
        sendCommandToServer("CREATE TABLE city (cityName);");
        sendCommandToServer("INSERT INTO city VALUES ('London');");
        sendCommandToServer("INSERT INTO city VALUES ('Bristol');");
        String response = sendCommandToServer("JOIN censs AND city ON cityId AND id;");
        assertFalse(response.contains("[OK]"), "An invalid query was made, however an [OK] tag was not returned");
        assertTrue(response.contains("[ERROR]"), "An invalid query was made, however an [ERROR] tag was returned");
        assertTrue(response.contains("The table: censs does not exist in the " + randomName + " database"), "Expected exception not thrown");
    }

    @Test
    public void testJoin6(){
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("Use " + randomName + ";");
        sendCommandToServer("CREATE TABLE census (name, age, weight, cityId);");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Simon Lock',  18, 80, 3 ) ;   ");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Sam Lock',  18, 90, 3) ;   ");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Simon Simpson',  23, 80, 2 ) ;   ");
        sendCommandToServer("CREATE TABLE city (cityName);");
        sendCommandToServer("INSERT INTO city VALUES ('London');");
        sendCommandToServer("INSERT INTO city VALUES ('Bristol');");
        String response = sendCommandToServer("JOIN census AND ciy ON cityId AND id;");
        assertFalse(response.contains("[OK]"), "An invalid query was made, however an [OK] tag was not returned");
        assertTrue(response.contains("[ERROR]"), "An invalid query was made, however an [ERROR] tag was returned");
        assertTrue(response.contains("The table: ciy does not exist in the " + randomName + " database"), "Expected exception not thrown");
    }

    @Test
    public void testJoin7(){
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("Use " + randomName + ";");
        sendCommandToServer("CREATE TABLE census (name, age, weight, cityId);");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Simon Lock',  18, 80, 3 ) ;   ");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Sam Lock',  18, 90, 3) ;   ");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Simon Simpson',  23, 80, 2 ) ;   ");
        sendCommandToServer("CREATE TABLE city (cityName);");
        sendCommandToServer("INSERT INTO city VALUES ('London');");
        sendCommandToServer("INSERT INTO city VALUES ('Bristol');");
        String response = sendCommandToServer("JOIN census AND city ON cityd AND id;");
        assertFalse(response.contains("[OK]"), "An invalid query was made, however an [OK] tag was not returned");
        assertTrue(response.contains("[ERROR]"), "An invalid query was made, however an [ERROR] tag was returned");
        assertTrue(response.contains("Cannot JOIN ON cityd as it does not exist"), "Expected exception not thrown");
    }

    @Test
    public void testJoin8(){
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("Use " + randomName + ";");
        sendCommandToServer("CREATE TABLE census (name, age, weight, cityId);");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Simon Lock',  18, 80, 3 ) ;   ");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Sam Lock',  18, 90, 3) ;   ");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Simon Simpson',  23, 80, 2 ) ;   ");
        sendCommandToServer("CREATE TABLE city (cityName);");
        sendCommandToServer("INSERT INTO city VALUES ('London');");
        sendCommandToServer("INSERT INTO city VALUES ('Bristol');");
        String response = sendCommandToServer("JOIN census AND city ON cityId AND idd;");
        assertFalse(response.contains("[OK]"), "An invalid query was made, however an [OK] tag was not returned");
        assertTrue(response.contains("[ERROR]"), "An invalid query was made, however an [ERROR] tag was returned");
        assertTrue(response.contains("Cannot JOIN ON idd as it does not exist"), "Expected exception not thrown");
    }

    @Test
    public void testPersistenceOfCurrentId(){
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("Use " + randomName + ";");
        sendCommandToServer("CREATE TABLE census (name, age, weight);");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Simon Lock',  18, 80 ) ;   ");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Sam Lock',  18, 90 ) ;   ");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Simon Simpson',  23, 80 ) ;   ");
        sendCommandToServer("DELETE FROM census WHERE name LIKE 'Simon ';");
        server = new DBServer();
        sendCommandToServer("Use " + randomName + ";");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Simon Lock',  18, 80 ) ;   ");
        String updatedTableString = server.getDatabaseInUse().getTables().get("census").toString();
        String expectedSubString = "4\t'Simon Lock'";
        assertTrue(updatedTableString.contains(expectedSubString), "The table has not deleted correctly");
    }

    @Test
    public void testNumericConditions() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("Use " + randomName + ";");
        sendCommandToServer("CREATE TABLE census (name, age, weight);");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Simon Lock',  18, 80 ) ;   ");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Sam Lock',  18, 90 ) ;   ");
        sendCommandToServer("  INSERT  INTO  census   VALUES(  'Simon Simpson',  23, 80 ) ;   ");
        String response = sendCommandToServer("SELECT name, id, age FROM census WHERE name LIKE 'Lock' AND weight < = 80.0;");
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
        assertFalse(response.contains("Simon Simpson"), "entry with invalid name returned");
        assertTrue(response.contains("Simon Lock"), "entry with valid name not returned");
        assertFalse(response.contains("Sam Lock"), "entry with invalid name returned");
    }
}
