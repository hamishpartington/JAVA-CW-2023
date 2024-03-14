package edu.uob;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ParserTests {

    @Test
    public void testParseCommand1() {
        String query = "  INSERT  INTO  people   VALUES(  'Simon Lock'  ,35, 'simon@bristol.ac.uk' , 1.8  ) ";
        Parser parser = new Parser(query);
        assertThrows(ParserException.QueryNotTerminated.class, parser::parseCommand,
                "Exception not thrown when query not terminated with ;");
    }

    @Test
    public void testParseCommand2() {
        String query = "  INSERT  INTO  people   VALUES(  'Simon Lock'  ,35, 'simon@bristol.ac.uk' , 1.8  ) ;   ";
        Parser parser = new Parser(query);
        assertDoesNotThrow(parser::parseCommand,
                "Exception thrown when query terminated with ;");
    }

    @Test
    public void testParseCommand3() {
        String query = "  INERT  INTO  people   VALUES(  'Simon Lock'  ,35, 'simon@bristol.ac.uk' , 1.8  ) ;   ";
        Parser parser = new Parser(query);
        assertThrows(ParserException.NotACommandType.class, parser::parseCommand,
                "Exception not thrown for invalid command type");
    }

    @Test
    public void testParseUse1() {
        String query = "uSe Cen1us;";
        Parser parser = new Parser(query);
        assertDoesNotThrow(parser::parseCommand, "Problem with parsing use command");
    }

    @Test
    public void testParseUse2() {
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
    public void testParseCreate4() {
        String query = "CREATE TABLE census (name, age, weight);";
        Parser parser = new Parser(query);
        assertDoesNotThrow(parser::parseCommand,
                "Exception thrown for valid create statement");
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
    public void testAlter1() {
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
    public void testAlter2() {
        String query = "ALTER TABLE census ADD age;";
        Parser parser = new Parser(query);
        assertDoesNotThrow(parser::parseCommand,
                "Exception thrown for valid alter");
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
    public void testSimpleSelect1() {
        String query = "SELECT * FROM census;";
        Parser parser = new Parser(query);
        assertDoesNotThrow(parser::parseCommand,
                "Exception thrown for valid select");
    }

    @Test
    public void testSimpleSelect2() {
        String query = "SELECT name, id, age FROM census;";
        Parser parser = new Parser(query);
        assertDoesNotThrow(parser::parseCommand,
                "Exception thrown for valid select");
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
    public void testConditionalSelect4() {
        String query = "SELECT name, id, age FROM census WHERE (name == 'Simon' AND age == 30) OR weight >= 50 AND name LIKE 'ham';";
        Parser parser = new Parser(query);
        assertDoesNotThrow(parser::parseCommand,
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
    public void testUpdate5() {
        String query = "UPDATE census SET age = 10, name = 'Isaac', weight = 50 WHERE name == 'Chris';";
        Parser parser = new Parser(query);
        assertDoesNotThrow(parser::parseCommand,
                "Exception thrown for invalid name value pair");
    }
}
