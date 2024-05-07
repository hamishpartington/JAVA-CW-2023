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
    void testInvExtraneous() {
        String response = sendCommandToServer("simon: inv tree");
        assertEquals("Your command contains extraneous entities", response, "Extraneous entities not detected");
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
    void testLookExtraneous() {
        String response = sendCommandToServer("simon: look at forest");
        assertEquals("Your command contains extraneous entities", response, "Extraneous entities not detected");
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

    @Test
    void testGoto1() {
        String response = sendCommandToServer("hamish: goto forest");
        assertTrue(response.contains("forest"), "Did not see the name of the current room in response to look");
        assertTrue(response.contains("dark forest"), "Did not see a description of the room in response to look");
        assertTrue(response.contains("brass key"), "Did not see a description of artefacts in response to look");
        assertTrue(response.contains("big tree"), "Did not see description of furniture in response to look");
        assertTrue(response.contains("cabin"), "Did not see available paths in response to look");
    }

    @Test
    void testGoto2() {
        String response = sendCommandToServer("hamish: goto cellar");
        assertEquals("The cellar cannot be accessed from your current location", response, "Correct error message not shown");
        response = sendCommandToServer("hamish: look");
        assertTrue(response.contains("cabin"), "Did not see the name of the current room in response to look");
        assertTrue(response.contains("log cabin"), "Did not see a description of the room in response to look");
        assertTrue(response.contains("magic potion"), "Did not see a description of artifacts in response to look");
        assertTrue(response.contains("wooden trapdoor"), "Did not see description of furniture in response to look");
        assertTrue(response.contains("forest"), "Did not see available paths in response to look");
    }

    @Test
    void testGoto3() {
        String response = sendCommandToServer("hamish: goto cellar forest");
        assertEquals("There are too many locations in this command. You can only goto one of them", response, "Correct error message not shown");
        response = sendCommandToServer("hamish: look");
        assertTrue(response.contains("cabin"), "Did not see the name of the current room in response to look");
        assertTrue(response.contains("log cabin"), "Did not see a description of the room in response to look");
        assertTrue(response.contains("magic potion"), "Did not see a description of artifacts in response to look");
        assertTrue(response.contains("wooden trapdoor"), "Did not see description of furniture in response to look");
        assertTrue(response.contains("forest"), "Did not see available paths in response to look");
    }

    @Test
    void testGoto4() {
        String response = sendCommandToServer("hamish: goto");
        assertEquals("There is no location in your goto command", response, "Correct error message not shown");
        response = sendCommandToServer("hamish: look");
        assertTrue(response.contains("cabin"), "Did not see the name of the current room in response to look");
        assertTrue(response.contains("log cabin"), "Did not see a description of the room in response to look");
        assertTrue(response.contains("magic potion"), "Did not see a description of artifacts in response to look");
        assertTrue(response.contains("wooden trapdoor"), "Did not see description of furniture in response to look");
        assertTrue(response.contains("forest"), "Did not see available paths in response to look");
    }

    @Test
    void testGoto5() {
        String response = sendCommandToServer("hamish: goto forest forest");
        assertTrue(response.contains("forest"), "Did not see the name of the current room in response to look");
        assertTrue(response.contains("dark forest"), "Did not see a description of the room in response to look");
        assertTrue(response.contains("brass key"), "Did not see a description of artefacts in response to look");
        assertTrue(response.contains("big tree"), "Did not see description of furniture in response to look");
        assertTrue(response.contains("cabin"), "Did not see available paths in response to look");
    }


    @Test
    void testGotoExtraneous() {
        String response = sendCommandToServer("simon: goto forest tree");
        assertEquals("Your command contains extraneous entities", response, "Extraneous entities not detected");
        response = sendCommandToServer("hamish: look");
        assertTrue(response.contains("cabin"), "Did not see the name of the current room in response to look");
        assertTrue(response.contains("log cabin"), "Did not see a description of the room in response to look");
        assertTrue(response.contains("magic potion"), "Did not see a description of artifacts in response to look");
        assertTrue(response.contains("wooden trapdoor"), "Did not see description of furniture in response to look");
        assertTrue(response.contains("forest"), "Did not see available paths in response to look");
    }

    @Test
    void testGet1() {
        String response = sendCommandToServer("hamish: get axe");
        assertEquals("You picked up a axe", response, "Get response not as expected");
        assertTrue(sendCommandToServer("hamish: inv").contains("axe"));
        assertFalse(sendCommandToServer("hamish: look").contains("axe"), "Axe not removed from room");
    }

    @Test
    void testGet2() {
        String response = sendCommandToServer("hamish: get poem");
        assertEquals("There is no artefact in your get command", response, "Get response not as expected");
        assertEquals("You have no artefacts in your inventory\n", sendCommandToServer("hamish: inv"),
                "inv command not working as expected");
    }

    @Test
    void testGet3() {
        String response = sendCommandToServer("hamish: get key");
        assertEquals("The key is not in your current location so cannot be picked up", response, "Get response not as expected");
    }

    @Test
    void testGet4() {
        String response = sendCommandToServer("hamish: get axe axe");
        assertEquals("You picked up a axe", response, "Get response not as expected");
        assertTrue(sendCommandToServer("hamish: inv").contains("axe"));
        assertFalse(sendCommandToServer("hamish: look").contains("axe"), "Axe not removed from room");
    }

    @Test
    void testGetExtraneous() {
        String response = sendCommandToServer("simon: get axe forest.");
        assertEquals("Your command contains extraneous entities", response, "Extraneous entities not detected");
        assertFalse(sendCommandToServer("simon: inv").contains("axe"), "Should not have picked up axe");
        assertTrue(sendCommandToServer("simon: look").contains("axe"), "Should not have picked up axe");
    }

    @Test
    void testDrop1() {
        String response = sendCommandToServer("hamish: drop axe");
        assertEquals("The axe is not in your inventory so cannot be dropped", response, "Drop response not as expected");
    }

    @Test
    void testDrop2() {
        sendCommandToServer("hamish: goto forest");
        sendCommandToServer("hamish: get key");
        sendCommandToServer("hamish: goto cabin");
        assertEquals("You dropped a key", sendCommandToServer("hamish: drop key"), "Drop response not as expected");
        String response = sendCommandToServer("hamish: look");
        assertTrue(response.contains("cabin"), "Did not see the name of the current room in response to look");
        assertTrue(response.contains("log cabin"), "Did not see a description of the room in response to look");
        assertTrue(response.contains("brass key"), "Did not see a description of dropped artefact in response to look");
        assertTrue(response.contains("wooden trapdoor"), "Did not see description of furniture in response to look");
        assertTrue(response.contains("forest"), "Did not see available paths in response to look");
    }

    @Test
    void testDrop3() {
        String response = sendCommandToServer("hamish: drop poem");
        assertEquals("There is no artefact in your drop command", response, "Drop response not as expected");
    }

    @Test
    void testDrop4() {
        sendCommandToServer("hamish: goto forest");
        sendCommandToServer("hamish: get key");
        sendCommandToServer("hamish: goto cabin");
        assertEquals("You dropped a key", sendCommandToServer("hamish: drop key brass key"), "Drop response not as expected");
        String response = sendCommandToServer("hamish: look");
        assertTrue(response.contains("cabin"), "Did not see the name of the current room in response to look");
        assertTrue(response.contains("log cabin"), "Did not see a description of the room in response to look");
        assertTrue(response.contains("brass key"), "Did not see a description of dropped artefact in response to look");
        assertTrue(response.contains("wooden trapdoor"), "Did not see description of furniture in response to look");
        assertTrue(response.contains("forest"), "Did not see available paths in response to look");
    }

    @Test
    void testDropExtraneous() {
        sendCommandToServer("hamish: goto forest");
        sendCommandToServer("hamish: get key");
        sendCommandToServer("hamish: goto cabin");
        String response = sendCommandToServer("hamish: drop key in forest.");
        assertEquals("Your command contains extraneous entities", response, "Extraneous entities not detected");
        assertTrue(sendCommandToServer("hamish: inv").contains("key"), "key should not have been dropped");
    }

    @Test
    void testLookWithOtherPlayers() {
        sendCommandToServer("mickey-mouse: look");
        sendCommandToServer("goofy: look");
        sendCommandToServer("donald: look");
        String response = sendCommandToServer("hamish: look");
        assertTrue(response.contains("mickey-mouse"), "Other player not displayed by look");
        assertTrue(response.contains("goofy"), "Other player not displayed by look");
        assertTrue(response.contains("donald"), "Other player not displayed by look");
        assertFalse(response.contains("hamish"), "Current player should not be displayed by look");
    }

    @Test
    void testGameAction1(){
        sendCommandToServer("hamish: get axe");
        sendCommandToServer("hamish: goto forest");
        assertEquals("You cut down the tree with the axe", sendCommandToServer("hamish: chop tree with axe"),
                "Action response not as expected");
        String response = sendCommandToServer("hamish: look");
        assertTrue(response.contains("log"));
        assertFalse(response.contains("tree"));
    }

    @Test
    void testGameAction2(){
        sendCommandToServer("hamish: goto forest");
        sendCommandToServer("hamish: get key");
        sendCommandToServer("hamish: goto cabin");
        assertEquals("You unlock the trapdoor and see steps leading down into a cellar", sendCommandToServer("hamish: open trapdoor"),
                "Action response not as expected");
        String response = sendCommandToServer("hamish: look");
        assertTrue(response.contains("cellar"), "Path to cellar not produced");
        assertFalse(sendCommandToServer("hamish: inv").contains("key"), "Key not consumed");
    }

    @Test
    void testAction3() {
        String response = sendCommandToServer("hamish: open trapdoor");
        assertEquals("All subjects must either be in your current location or inventory in order to perform an action",
                response, "Response to inviable action not as expected");
    }

    @Test
    void testAction4() {
        String response = sendCommandToServer("hamish: pen trapdoor");
        assertEquals("There are no trigger words in this command",
                response, "Response to inviable action not as expected");
    }

    @Test
    void testGameAction5(){
        sendCommandToServer("hamish: goto forest");
        sendCommandToServer("hamish: get key");
        sendCommandToServer("hamish: goto cabin");
        assertEquals("You unlock the trapdoor and see steps leading down into a cellar", sendCommandToServer("hamish: open trapdoor trapdoor"),
                "Action response not as expected");
        String response = sendCommandToServer("hamish: look");
        assertTrue(response.contains("cellar"), "Path to cellar not produced");
        assertFalse(sendCommandToServer("hamish: inv").contains("key"), "Key not consumed");
    }

    @Test
    void testActionExtraneous() {
        sendCommandToServer("hamish: goto forest");
        sendCommandToServer("hamish: get key");
        sendCommandToServer("hamish: goto cabin");
        String response = sendCommandToServer("hamish: open trapdoor with axe");
        assertEquals("Your command contains extraneous entities", response, "Extraneous entities not detected");
        assertTrue(sendCommandToServer("hamish: inv").contains("key"), "key not in inv");
        assertFalse(sendCommandToServer("hamish: look").contains("cellar"), "Trapdoor should not have been opened");
    }

    @Test
    void testHealth1() {
        sendCommandToServer("hamish: get axe");
        sendCommandToServer("hamish: get potion");
        sendCommandToServer("hamish: goto forest");
        sendCommandToServer("hamish: get key");
        sendCommandToServer("hamish: goto cabin");
        sendCommandToServer("hamish: open trapdoor");
        sendCommandToServer("hamish: goto cellar");
        sendCommandToServer("hamish: fight elf");
        sendCommandToServer("simon: goto cellar");
        sendCommandToServer("hamish: fight elf");
        assertEquals("You have 1 health point(s) remaining", sendCommandToServer("hamish: health"), "Health level not as expected");
        String response = sendCommandToServer("hamish: fight elf");
        assertTrue(response.contains("lose some health"), "action narration not as expected");
        assertTrue(response.contains("died"), "Player should have died");
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("axe"), "Dead player's dropped items should be here");
        assertTrue(response.contains("potion"), "Dead player's dropped items should be here");
        assertFalse(response.contains("hamish"), "Dead player should not be at this location");
        assertEquals("You have no artefacts in your inventory\n", sendCommandToServer("hamish: inv"),
                "Player who died should have no items");
    }

    @Test
    void testHealth2() {
        sendCommandToServer("hamish: get potion");
        sendCommandToServer("hamish: drink potion");
        String response = sendCommandToServer("hamish: health");
        assertEquals("You have 3 health point(s) remaining", response, "Health level not as expected");
    }

    @Test
    void testHealth3() {
        sendCommandToServer("hamish: get potion");
        sendCommandToServer("hamish: goto forest");
        sendCommandToServer("hamish: get key");
        sendCommandToServer("hamish: goto cabin");
        sendCommandToServer("hamish: open trapdoor");
        sendCommandToServer("hamish: goto cellar");
        sendCommandToServer("hamish: fight elf");
        sendCommandToServer("hamish: fight elf");
        sendCommandToServer("hamish: drink potion");
        String response = sendCommandToServer("hamish: health");
        assertEquals("You have 2 health point(s) remaining", response, "Health level not as expected");
    }

    @Test
    void testHealth4() {
        sendCommandToServer("hamish: get potion");
        sendCommandToServer("hamish: drink potion");
        String response = sendCommandToServer("hamish: health in me");
        assertEquals("You have 3 health point(s) remaining", response, "Health level not as expected");
    }

    @Test
    void testHealthExtraneous() {
        String response = sendCommandToServer("hamish: health forest");
        assertEquals("Your command contains extraneous entities", response, "Extraneous entities not detected");
    }

    @Test
    void testPunctuation1() {
        String response = sendCommandToServer("simon: inv!");
        assertEquals("You have no artefacts in your inventory\n", response,
                "inv command not working as expected");
    }

    @Test
    void testPunctuation2() {
        String response = sendCommandToServer("hamish: open the big, bad trapdoor.");
        assertEquals("All subjects must either be in your current location or inventory in order to perform an action",
                response, "Response to inviable action not as expected");
    }

    @Test
    void testPunctuation3(){
        sendCommandToServer("hamish: get that dangerous-looking axe.");
        sendCommandToServer("hamish: goto the deep; dark forest?");
        assertEquals("You cut down the tree with the axe", sendCommandToServer("hamish: chop tree with axe"),
                "Action response not as expected");
        String response = sendCommandToServer("hamish: look");
        assertTrue(response.contains("log"));
        assertFalse(response.contains("tree"));
    }

}
