package edu.uob;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class CommandParserTests {

    @Test
    void testExtractPlayerName() {
        String command = "hamish: look in cellar";
        Set<String> triggers = new HashSet<>();
        triggers.add("cut");
        CommandParser cp = new CommandParser(command, triggers);
        assertEquals("hamish", cp.getPlayerName(), "Name not as expected");
    }

    @Test
    void testProcessCommand () {
        String command = "hamish: look in: cellar";
        Set<String> triggers = new HashSet<>();
        triggers.add("cut");
        CommandParser cp = new CommandParser(command, triggers);
        assertEquals(" look in cellar", cp.getProcessedCommand(), "processed command not as expected");
    }
}
