package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ParserTests {

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

    @Test
    public void testParseCommand1() {
        String query = "  INSERT  INTO  people   VALUES(  'Simon Lock'  ,35, 'simon@bristol.ac.uk' , 1.8  ) ";
        Parser parser = new Parser(query);
        assertThrows(ParserException.QueryNotTerminated.class, parser::parseCommand,
                "Exception not thrown when query not terminated with ;");
    }

    @Test
    public void testParseCommand3() {
        String query = "  INERT  INTO  people   VALUES(  'Simon Lock'  ,35, 'simon@bristol.ac.uk' , 1.8  ) ;   ";
        Parser parser = new Parser(query);
        assertThrows(ParserException.NotACommandType.class, parser::parseCommand,
                "Exception not thrown for invalid command type");
    }

    @Test
    public void testParseUse() {
        String query = "uSe Cen$sus;";
        Parser parser = new Parser(query);
        assertThrows(ParserException.InvalidDatabaseName.class, parser::parseCommand,
                "Exception not thrown for invalid database name");
    }

    @Test
    public void testParseCreate1() {
        String query = "CREATE DATABAS census;";
        Parser parser = new Parser(query);
        assertThrows(ParserException.InvalidCreate.class, parser::parseCommand,
                "Exception not thrown for invalid create");
    }

    @Test
    public void testParseCreate2() {
        String query = "CREATE DATABASE census;";
        Parser parser = new Parser(query);
        assertDoesNotThrow(parser::parseCommand,
                "Exception thrown for valid create statement");
    }

    @Test
    public void testParseCreate3() {
        String query = "CREATE TABLE cen/sus;";
        Parser parser = new Parser(query);
        assertThrows(ParserException.InvalidTableName.class, parser::parseCommand,
                "Exception not thrown for invalid table name");
    }

    @Test
    public void testInvalidAttribute() {
        String query = "CREATE TABLE census (name, a*ge, weight);";
        Parser parser = new Parser(query);
        assertThrows(ParserException.InvalidAttributeName.class, parser::parseCommand,
                "Exception not thrown for invalid attribute name");
    }

    @Test
    public void testDrop1() {
        String query = "DROP TBLE census;";
        Parser parser = new Parser(query);
        assertThrows(ParserException.InvalidDrop.class, parser::parseCommand,
                "Exception not thrown for invalid drop");
    }

    @Test
    public void testDrop2() {
        String query = "DROP TABLE ce<sus;";
        Parser parser = new Parser(query);
        assertThrows(ParserException.InvalidTableName.class, parser::parseCommand,
                "Exception not thrown for invalid table name in drop");
    }
    @Test
    public void testAlter() {
        String query = "ALTER TBLE census ADD age;";
        Parser parser = new Parser(query);
        assertThrows(ParserException.InvalidAlter.class, parser::parseCommand,
                "Exception not thrown for invalid alter");
    }
    @Test
    public void testInvalidAlterationType() {
        String query = "ALTER TABLE census AD age;";
        Parser parser = new Parser(query);
        assertThrows(ParserException.InvalidAlterationType.class, parser::parseCommand,
                "Exception not thrown for invalid alteration type");
    }

    @Test
    public void testInsert1() {
        String query = "INSERT INT marks VALUES ('Simon', 65, TRUE);";
        Parser parser = new Parser(query);
        assertThrows(ParserException.NoInto.class, parser::parseCommand,
                "Exception not thrown for insert with no INTO");
    }

    @Test
    public void testInsert2() {
        String query = "INSERT INTO marks ('Simon', 65, TRUE);";
        Parser parser = new Parser(query);
        assertThrows(ParserException.NoValues.class, parser::parseCommand,
                "Exception not thrown for insert with no VALUES");
    }

    @Test
    public void testInsert3() {
        String query = "INSERT INTO marks VALUES 'Sim#on', 65, TRUE);";
        Parser parser = new Parser(query);
        assertThrows(ParserException.NoValueList.class, parser::parseCommand,
                "Exception not thrown for insert with no ValueList");
    }

    @Test
    public void testInsert4() {
        String query = "INSERT INTO marks VALUES ('Sim)on', 65, TRUE;";
        Parser parser = new Parser(query);
        assertThrows(ParserException.ListNotTerminated.class, parser::parseCommand,
                "Exception not thrown for insert with no ValueList terminator");
    }

    @Test
    public void testInsert5() {
        String query = "INSERT INTO marks VALUES ('Sim£on', +65, TRUE);";
        Parser parser = new Parser(query);
        assertThrows(ParserException.InvalidValue.class, parser::parseCommand,
                "Exception not thrown for insert with no invalid value");
    }

    @Test
    public void testInsert6() {
        String query = "INSERT INTO marks VALUES ('Sim)on', +6.5.2, TRUE);";
        Parser parser = new Parser(query);
        assertThrows(ParserException.InvalidValue.class, parser::parseCommand,
                "Exception not thrown for insert with invalid value");
    }

    @Test
    public void testInsert7() {
        String query = "INSERT INTO marks VALUES ('Sim)on', +6., TRUE);";
        Parser parser = new Parser(query);
        assertThrows(ParserException.InvalidValue.class, parser::parseCommand,
                "Exception not thrown for insert with invalid value");
    }

    @Test
    public void testInsert8() {
        String query = "INSERT INTO marks VALUES ('Sim)on', +6, TRUUE);";
        Parser parser = new Parser(query);
        assertThrows(ParserException.InvalidValue.class, parser::parseCommand,
                "Exception not thrown for insert with invalid value");
    }

    @Test
    public void testInsert9() {
        String query = "INSERT INTO marks VALUES ('Sim)on, +6, TRUUE);";
        Parser parser = new Parser(query);
        assertThrows(ParserException.QueryNotTerminated.class, parser::parseCommand,
                "Exception not thrown for insert with invalid value");
    }

    @Test
    public void testConditionalSelect1() {
        String query = "SELECT name, id, age FROM census WHERE name = 'Simon';";
        Parser parser = new Parser(query);
        assertThrows(ParserException.InvalidComparator.class, parser::parseCommand,
                "Exception thrown for valid select");
    }

    @Test
    public void testConditionalSelect2() {
        String query = "SELECT name, id, age FROM census WHERE (name == 'Simon';";
        Parser parser = new Parser(query);
        assertThrows(ParserException.UnmatchedParentheses.class, parser::parseCommand,
                "Exception thrown for valid select");
    }
    @Test
    public void testConditionalSelect3() {
        String query = "SELECT name, id, age FROM census WHERE (name == 'Simon') && (age == 30);";
        Parser parser = new Parser(query);
        assertThrows(ParserException.InvalidStatementSyntax.class, parser::parseCommand,
                "Exception thrown for valid select");
    }

    @Test
    public void testNoSet() {
        String query = "UPDATE census SEY age = 10 WHERE name = Chris;";
        Parser parser = new Parser(query);
        assertThrows(ParserException.NoSetInUpdate.class, parser::parseCommand,
                "Exception not thrown for update with no SET");
    }

    @Test
    public void testNoWhere() {
        String query = "UPDATE census SET age = 10 WHERRE name == 'Chris';";
        Parser parser = new Parser(query);
        assertThrows(ParserException.ListNotTerminated.class, parser::parseCommand,
                "Exception not thrown for update with no WHERE");
    }

    @Test
    public void testUpdate1() {
        String query = "UPDATE census SET age == 10 WHERE name == 'Chris';";
        Parser parser = new Parser(query);
        assertThrows(ParserException.InvalidNameValuePair.class, parser::parseCommand,
                "Exception not thrown for invalid name value pair");
    }

    @Test
    public void testUpdate2() {
        String query = "UPDATE census SET age = 10 WHERE name == Chris;";
        Parser parser = new Parser(query);
        assertThrows(ParserException.InvalidValue.class, parser::parseCommand,
                "Exception not thrown for invalid name value pair");
    }

    @Test
    public void testUpdate3() {
        String query = "UPDATE census SET age = 10, WHERE name == 'Chris';";
        Parser parser = new Parser(query);
        assertThrows(ParserException.InvalidNameValuePair.class, parser::parseCommand,
                "Exception not thrown for invalid name value pair");
    }

    @Test
    public void testUpdate4() {
        String query = "UPDATE census SET age = 10, nam*e =  WHERE name == 'Chris';";
        Parser parser = new Parser(query);
        assertThrows(ParserException.InvalidAttributeName.class, parser::parseCommand,
                "Exception not thrown for invalid attribute name");
    }

    @Test
    public void testDelete1() {
        String query = "DELETE FROM Census WHERE (id != 1 AND name LIKE 'I') OR ((age >= 18) AND height <= 180);";
        Parser parser = new Parser(query);
        assertDoesNotThrow(parser::parseCommand,
                "Exception thrown for valid DELETE statement");
    }

    @Test
    public void testDelete2() {
        String query = "DELETE FRM Census WHERE (id != 1 AND name LIKE 'I') OR ((age >= 18) AND height <= 180);";
        Parser parser = new Parser(query);
        assertThrows(ParserException.NoFromDelete.class, parser::parseCommand,
                "Exception not thrown for DELETE with no FROM");
    }

    @Test
    public void testDelete3() {
        String query = "DELETE FROM Census WHER (id != 1 AND name LIKE 'I') OR ((age >= 18) AND height <= 180);";
        Parser parser = new Parser(query);
        assertThrows(ParserException.NoWhere.class, parser::parseCommand,
                "Exception thrown for DELETE with no WHERE");
    }

    @Test
    public void testDelete4() {
        String query = "DELETE FROM Census WHERE ((id != 1) AND (name LIKE 'I')) OR ((age >= 18) AND height <= 180) invalid;";
        Parser parser = new Parser(query);
        assertThrows(ParserException.InvalidStatementSyntax.class, parser::parseCommand,
                "Exception not thrown for delete with invalid end");
    }

    @Test
    public void testJoin1() {
        String query = "JOIN table1 AnD table2 oN id ANd id invalid;";
        Parser parser = new Parser(query);
        assertThrows(ParserException.InvalidStatementSyntax.class, parser::parseCommand,
                "Exception not thrown for join with invalid end");
    }

    @Test
    public void testJoin2() {
        String query = "JOIN table1 AD table2 ON id AND id;";
        Parser parser = new Parser(query);
        assertThrows(ParserException.InvalidJoin.class, parser::parseCommand,
                "Exception not thrown for invalid join");
    }

    @Test
    public void testJoin3() {
        String query = "JOIN table1 AND table2 Orn id AND id;";
        Parser parser = new Parser(query);
        assertThrows(ParserException.InvalidJoin.class, parser::parseCommand,
                "Exception not thrown for invalid join");
    }

    @Test
    public void testJoin4() {
        String query = "JOIN table1 AND table2 On id && id;";
        Parser parser = new Parser(query);
        assertThrows(ParserException.InvalidJoin.class, parser::parseCommand,
                "Exception not thrown for invalid join");
    }
}
