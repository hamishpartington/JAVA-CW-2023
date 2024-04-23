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
public class BasicSTAGTests {
    private GameServer server;

    // Create a new server _before_ every @Test
    @BeforeEach
    void setup() throws ParserConfigurationException, IOException, SAXException, ParseException {
        File entitiesFile = Paths.get("config" + File.separator + "basic-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "basic-actions.xml").toAbsolutePath().toFile();
        server = new GameServer(entitiesFile, actionsFile);
    }

    String sendCommandToServer(String command) {
        // Try to send a command to the server - this call will timeout if it takes too long (in case the server enters an infinite loop)
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> { return server.handleCommand(command);},
                "Server took too long to respond (probably stuck in an infinite loop)");
    }

    @Test
    void testInvNoItems() {
        String response = sendCommandToServer("simon: inv");
        assertEquals("You have no artefacts in your inventory\n", response,
                "inv command not working as expected");
    }

    @Test
    void testInventoryNoItems() {
        String response = sendCommandToServer("simon: Inventory");
        assertEquals("You have no artefacts in your inventory\n", response,
                "inventory command not working as expected");
    }

    @Test
    void testLook1() {
        String response = sendCommandToServer("simon: look").toLowerCase();
        assertTrue(response.contains("cabin"), "Did not see the name of the current room in response to look");
        assertTrue(response.contains("log cabin"), "Did not see a description of the room in response to look");
        assertTrue(response.contains("magic potion"), "Did not see a description of artifacts in response to look");
        assertTrue(response.contains("wooden trapdoor"), "Did not see description of furniture in response to look");
        assertTrue(response.contains("forest"), "Did not see available paths in response to look");
    }

    @Test
    void testTooManyTriggers1() {
        String response = sendCommandToServer("hamish: inv goto cabin");
        assertEquals("There are too many trigger words in this command", response,
                "No error message for too many triggers");
    }

    @Test
    void testTooManyTriggers2() {
        String response = sendCommandToServer("hamish: unlock open door with key");
        assertEquals("There are too many trigger words in this command", response,
                "No error message for too many triggers");
    }

    @Test
    void testTooManyTriggers3() {
        String response = sendCommandToServer("hamish: look open door with key");
        assertEquals("There are too many trigger words in this command", response,
                "No error message for too many triggers");
    }
}
