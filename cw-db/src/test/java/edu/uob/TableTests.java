package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
public class TableTests {
    private Database database;

    @BeforeEach
    public void setup() {
        database = new Database("test");
        if(!Files.exists(Paths.get(database.getFolderPath()))){
            assertDoesNotThrow(database::create);
        }

        File databaseDirectory = new File(database.getFolderPath());
        File[] files = databaseDirectory.listFiles();
        if(files != null) {
            for(File f : files){
                f.delete();
            }
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
    public void testToString() {
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
        assertDoesNotThrow(()->database.insertIntoTable("test", values),
                "Exception thrown attempting to insert values");
        String expectedString = "id\tage\tDoB\tgender\t\n1\t25\t23/08/1997\tMale\t\n2\t25\t23/08/1997\tMale\t\n";
        String actualString = database.getTables().get("test").toString();
        assertEquals(expectedString, actualString, "Table.ToString() not working as expected");
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

    @Test
    public void testSelectAll() {
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
        assertDoesNotThrow(()->database.insertIntoTable("test", values),
                "Exception thrown attempting to insert values");
        ArrayList<String> fields = new ArrayList<>();
        fields.add("*");
        Table procTable = assertDoesNotThrow(()->database.selectFromTable(fields, "test", false, null),
                "Exception thrown when selecting *");
        String expectedString = "id\tage\tDoB\tgender\t\n1\t25\t23/08/1997\tMale\t\n2\t25\t23/08/1997\tMale\t\n";
        String actualString = procTable.toString();
        assertEquals(expectedString, actualString, "Table.ToString() not working as expected");
    }

    @Test
    public void testSelectSomeFields() {
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
        assertDoesNotThrow(()->database.insertIntoTable("test", values),
                "Exception thrown attempting to insert values");
        attributes.remove("DoB");
        Table procTable = assertDoesNotThrow(()->database.selectFromTable(attributes, "test", false, null),
                "Exception thrown when selecting *");
        String expectedString = "age\tgender\t\n25\tMale\t\n25\tMale\t\n";
        String actualString = procTable.toString();
        assertEquals(expectedString, actualString, "Table.ToString() not working as expected");
    }

    @Test
    public void testFieldDoesNotExist() {
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
        assertDoesNotThrow(()->database.insertIntoTable("test", values),
                "Exception thrown attempting to insert values");
        ArrayList<String> fields = new ArrayList<>();
        fields.add("NotAField");
        assertThrows(DBException.fieldDoesNotExist.class, ()->database.selectFromTable(fields, "test", false, null),
                "Exception thrown when selecting *");
    }

    @Test
    public void testAlterAdd() {
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
        assertDoesNotThrow(()->database.alterTable("test", "name", "ADD"));
        String expectedString = "id\tage\tDoB\tgender\tname\t\n1\t25\t23/08/1997\tMale\t\t\n";
        assertEquals(database.getTables().get("test").toString(), expectedString, "Add not working");
        assertThrows(DBException.duplicateFields.class,
                ()->database.alterTable("test", "name", "ADD"),
                "Didn't throw exception when attempting to add field which already exists");
        assertThrows(DBException.TableDoesNotExist.class,
                ()->database.alterTable("notATable", "name", "ADD"),
                "Didn't throw exception when attempting to alter table which doesn't exist");
    }

    @Test
    public void testAlterDrop() {
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
        assertDoesNotThrow(()->database.alterTable("test", "age", "DROP"));
        String expectedString = "id\tDoB\tgender\t\n1\t23/08/1997\tMale\t\n";
        assertEquals(database.getTables().get("test").toString(), expectedString, "Drop not working");
        assertThrows(DBException.fieldDoesNotExist.class,
                ()->database.alterTable("test", "age", "DROP"),
                "Didn't throw exception when attempting to drop field which does not exist");
        assertThrows(DBException.cannotRemoveID.class,
                ()->database.alterTable("test", "id", "DROP"),
                "Didn't throw exception when attempting to drop id field");
    }
}
