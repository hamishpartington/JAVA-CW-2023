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
    public void testInvalidAttributeList() {
        String query = "CREATE TABLE census (name, age, weight;";
        Parser parser = new Parser(query);
        assertThrows(ParserException.AttributeListNotTerminated.class, parser::parseCommand,
                "Exception not thrown for non-terminated attribute list");
    }
}
