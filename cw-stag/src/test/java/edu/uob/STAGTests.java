package edu.uob;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.nio.file.Paths;
import java.io.IOException;
import java.time.Duration;
public class STAGTests {
    private GameServer server;

    // Create a new server _before_ every @Test
    @BeforeEach
    void setup() throws ParserConfigurationException, IOException, SAXException {
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
}
