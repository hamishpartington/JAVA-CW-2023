package edu.uob;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CommandParserTests {

    @Test
    void testExtractPlayerName() {
        String command = "hamish: look in cellar";
        CommandParser cp = new CommandParser(command);
        assertEquals("hamish", cp.getPlayerName(), "Name not as expected");
    }

    @Test
    void testProcessCommand () {
        String command = "hamish: look in: cellar";
        CommandParser cp = new CommandParser(command);
        assertEquals(" look in cellar", cp.getProcessedCommand(), "processed command not as expected");
    }
}
