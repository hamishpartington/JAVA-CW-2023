package edu.uob;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class CommandParserTests {

    @Test
    void testExtractPlayerName() throws STAGException.InvalidName {
        String command = "hamish: look in cellar";
        Set<String> triggers = new HashSet<>();
        triggers.add("cut");
        CommandParser cp = new CommandParser(command, triggers);
        assertEquals("hamish", cp.getPlayerName(), "Name not as expected");
    }

    @Test
    void testProcessCommand () throws STAGException.InvalidName {
        String command = "hamish: look in: cellar";
        Set<String> triggers = new HashSet<>();
        triggers.add("cut");
        CommandParser cp = new CommandParser(command, triggers);
        assertEquals("  look in  cellar", cp.getProcessedCommand(), "processed command not as expected");
    }

    @Test
    void testInvalidName1() {
        String command = "hamish_: look in: cellar";
        Set<String> triggers = new HashSet<>();
        triggers.add("cut");
        AtomicReference<CommandParser> cp = null;
        assertThrows(STAGException.InvalidName.class, ()-> cp.set(new CommandParser(command, triggers)));
    }

    @Test
    void testInvalidName2() {
        String command = "?hamish: look in: cellar";
        Set<String> triggers = new HashSet<>();
        triggers.add("cut");
        AtomicReference<CommandParser> cp = null;
        assertThrows(STAGException.InvalidName.class, ()-> cp.set(new CommandParser(command, triggers)));
    }

    @Test
    void testInvalidName3() {
        String command = "Hamish_ p: look in: cellar";
        Set<String> triggers = new HashSet<>();
        triggers.add("cut");
        AtomicReference<CommandParser> cp = null;
        assertThrows(STAGException.InvalidName.class, ()-> cp.set(new CommandParser(command, triggers)));
    }
}
