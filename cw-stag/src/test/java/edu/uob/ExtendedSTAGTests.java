package edu.uob;

import static org.junit.jupiter.api.Assertions.*;

import com.alexmerz.graphviz.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.nio.file.Paths;
import java.io.IOException;
import java.time.Duration;
public class ExtendedSTAGTests {
    private GameServer server;

    // Create a new server _before_ every @Test
    @BeforeEach
    void setup() throws ParserConfigurationException, IOException, SAXException, ParseException {
        File entitiesFile = Paths.get("config" + File.separator + "extended-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "extended-actions.xml").toAbsolutePath().toFile();
        server = new GameServer(entitiesFile, actionsFile);
    }

    String sendCommandToServer(String command) {
        // Try to send a command to the server - this call will timeout if it takes too long (in case the server enters an infinite loop)
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> { return server.handleCommand(command);},
                "Server took too long to respond (probably stuck in an infinite loop)");
    }

    @Test
    void testTokeniser() {
        sendCommandToServer("hamish: get axe");
        sendCommandToServer("hamish: goto forest");
        String response = sendCommandToServer("hamish: cut down tree");
        assertEquals("You cut down the tree with the axe", response, "Spaced trigger not working");
    }

    @Test
    void testLocationSpecificAction1() {
        sendCommandToServer("hamish: goto forest");
        sendCommandToServer("hamish: goto riverbank");
        sendCommandToServer("hamish: blow horn");
        String response = sendCommandToServer("hamish: look");
        assertTrue(response.contains("lumberjack"), "Lumberjack did not appear");
    }

    @Test
    void testLocationSpecificAction2() {
        sendCommandToServer("hamish: goto forest");
        sendCommandToServer("hamish: goto riverbank");
        sendCommandToServer("hamish: get horn");
        sendCommandToServer("hamish: goto forest");
        sendCommandToServer("hamish: blow horn");
        String response = sendCommandToServer("hamish: look");
        assertFalse(response.contains("lumberjack"), "Lumberjack should not appear");
    }

    @Test
    void testThreeWordTrigger() {
        sendCommandToServer("hamish: get axe");
        sendCommandToServer("hamish: goto forest");
        String response = sendCommandToServer("hamish: vigorously chop down tree");
        assertEquals("You cut down the tree with the axe", response, "Three word trigger not working");
    }
}
