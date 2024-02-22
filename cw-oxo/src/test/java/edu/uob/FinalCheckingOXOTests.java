package edu.uob;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import edu.uob.OXOMoveException.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Duration;

class FinalCheckingOXOTests {

    private OXOModel model;
    private OXOController controller;

    // This next method is a utility function that can be used by any of the test methods to _safely_ send a command to the controller
    void sendCommandToController(String command) {
        // Try to send a command to the server - call will timeout if it takes too long (in case the server enters an infinite loop)
        // Note: this is ugly code and includes syntax that you haven't encountered yet
        String timeoutComment = "Controller took too long to respond (probably stuck in an infinite loop)";
        assertTimeoutPreemptively(Duration.ofMillis(1000), ()-> controller.handleIncomingCommand(command), timeoutComment);
    }

    // Make a new "standard" (3x3) board before running each test case (i.e. this method runs before every `@Test` method)
    // In order to test boards of different sizes, winning thresholds or number of players, create a separate test file (without this method in it !)
    @BeforeEach
    void createStandardModel() {
        model = new OXOModel(3, 3, 3);
        model.addPlayer(new OXOPlayer('X'));
        model.addPlayer(new OXOPlayer('O'));
        controller = new OXOController(model);
    }

    // Test out basic win detection
    @Test
    void testBasicWin() throws OXOMoveException {
        // Find out which player is going to make the first move (they should be the eventual winner)
        OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        // Make a bunch of moves for the two players
        sendCommandToController("a1"); // First player
        sendCommandToController("b1"); // Second player
        sendCommandToController("a2"); // First player
        sendCommandToController("b2"); // Second player
        sendCommandToController("a3"); // First player

        // a1, a2, a3 should be a win for the first player (since players alternate between moves)
        // Let's check to see whether the first moving player is indeed the winner
        String failedTestComment = "Winner was expected to be " + firstMovingPlayer.getPlayingLetter() + " but wasn't";
        assertEquals(firstMovingPlayer, model.getWinner(), failedTestComment);
    }

    @Test
    void testCaseInsensitivity() throws OXOMoveException {
        // Find out which player is going to make the first move (they should be the eventual winner)
        OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        // Make a bunch of moves for the two players
        sendCommandToController("a1"); // First player
        sendCommandToController("B1"); // Second player
        sendCommandToController("A2"); // First player
        sendCommandToController("b2"); // Second player
        sendCommandToController("a3"); // First player

        // a1, a2, a3 should be a win for the first player (since players alternate between moves)
        // Let's check to see whether the first moving player is indeed the winner
        String failedTestComment = "Winner was expected to be " + firstMovingPlayer.getPlayingLetter() + " but wasn't";
        assertEquals(firstMovingPlayer, model.getWinner(), failedTestComment);
    }

    // Example of how to test for the throwing of exceptions
    @Test
    void testInvalidIdentifierException() throws OXOMoveException {
        // Check that the controller throws a suitable exception when it gets an invalid command
        String failedTestComment = "Controller failed to throw an InvalidIdentifierLengthException for command `abc123`";
        // The next lins is a bit ugly, but it is the easiest way to test exceptions (soz)
        assertThrows(InvalidIdentifierLengthException.class, ()-> sendCommandToController("abc123"), failedTestComment);
    }

    @Test
    void testCellAlreadyTakenExceptions()
    {
        final String cellID = "b2";
        sendCommandToController(cellID);
        String failedTestComment = "Controller failed to throw a CellAlreadyTakenException for " + cellID;
        assertThrows(CellAlreadyTakenException.class, ()-> sendCommandToController(cellID), failedTestComment);
    }

    @Test
    void testGameReset()
    {
        OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        // Make a move
        sendCommandToController("a1");
        controller.reset();
        // Check that A1 (cell [0,0] on the board) is not "owned" by the first player
        assertNotEquals(firstMovingPlayer, controller.gameModel.getCellOwner(0, 0), "Cell a1 wasn't cleared on reset");
        assertEquals(firstMovingPlayer, model.getPlayerByNumber(model.getCurrentPlayerNumber()), "Player number not re-initialised on game reset");
    }

    @Test
    void testOutsideCellRangeExceptions()
    {
        final String firstCellID = "z1";
        String failedTestComment = "Controller failed to throw a CellDoesNotExistException for " + firstCellID;
        assertThrows(OutsideCellRangeException.class, ()-> sendCommandToController(firstCellID), failedTestComment);

        final String secondCellID = "a4";
        failedTestComment = "Controller failed to throw a CellDoesNotExistException for " + secondCellID;
        assertThrows(OutsideCellRangeException.class, ()-> sendCommandToController(secondCellID), failedTestComment);

        final String thirdCellID = "b0";
        failedTestComment = "Controller failed to throw a CellDoesNotExistException for " + thirdCellID;
        assertThrows(OutsideCellRangeException.class, ()-> sendCommandToController(thirdCellID), failedTestComment);
    }

    @Test
    void testInvalidCellIdentifierExceptions()
    {
        final String firstCellID = "11";
        String failedTestComment = "Controller failed to throw an InvalidIdentifierCharacterException for " + firstCellID;
        assertThrows(InvalidIdentifierCharacterException.class, ()-> sendCommandToController(firstCellID), failedTestComment);

        final String secondCellID = "aa";
        failedTestComment = "Controller failed to throw an InvalidIdentifierCharacterException for " + secondCellID;
        assertThrows(InvalidIdentifierCharacterException.class, ()-> sendCommandToController(secondCellID), failedTestComment);

        final String thirdCellID = "0a";
        failedTestComment = "Controller failed to throw an InvalidIdentifierCharacterException for " + thirdCellID;
        assertThrows(InvalidIdentifierCharacterException.class, ()-> sendCommandToController(thirdCellID), failedTestComment);

        final String forthCellID = "";
        failedTestComment = "Controller failed to throw an InvalidIdentifierLengthException for " + forthCellID;
        assertThrows(InvalidIdentifierLengthException.class, ()-> sendCommandToController(forthCellID), failedTestComment);

        final String fifthCellID = "b";
        failedTestComment = "Controller failed to throw an InvalidIdentifierLengthException for " + fifthCellID;
        assertThrows(InvalidIdentifierLengthException.class, ()-> sendCommandToController(fifthCellID), failedTestComment);
    }

    @Test
    void testNoPlayerChangeAfterFailedMove()
    {
        final String cellID = "a1";
        int firstPlayerNumber = model.getCurrentPlayerNumber();
        sendCommandToController(cellID);
        assertTrue(firstPlayerNumber!=model.getCurrentPlayerNumber(), "Player number did not change after valid move");
        String failedTestComment = "Controller failed to throw an CellAlreadyTakenException for " + cellID;
        assertThrows(CellAlreadyTakenException.class, ()-> sendCommandToController(cellID), failedTestComment);
        assertTrue(firstPlayerNumber!=model.getCurrentPlayerNumber(), "Player changed even though invalid move taken");
    }

    @Test
    void testWinningStates()
    {
        // Horizontals
        checkWinnerFor(new String[] {"a1","b2","a2","c2","a3"},3,3,3,0);
        checkWinnerFor(new String[] {"b1","a2","b2","c2","b3"},3,3,3,0);
        // Verticals
        checkWinnerFor(new String[] {"b1","b2","a1","c3","c1"},3,3,3,0);
        checkWinnerFor(new String[] {"a3","b1","c3","b2","b3"},3,3,3,0);
        // Diagonals !
        checkWinnerFor(new String[] {"a1","a3","b2","b3","c3"},3,3,3,0);
        checkWinnerFor(new String[] {"a3","a1","c1","b3","b2"},3,3,3,0);
    }

    @Test
    void testWinLengthOfOne()
    {
        checkWinnerFor(new String[] {"a1"},3,3,1,0);
    }

    @Test
    void testTwoThresholdWinNotThroughCentre()
    {
        checkWinnerFor(new String[] {"b1","a1","c2"},3,3,2,0);
    }

    @Test
    void testGameEndsAfterWin()
    {
        // Set the board so that the top row is a winner
        sendCommandToController("a1");
        sendCommandToController("b1");
        sendCommandToController("a2");
        sendCommandToController("b2");
        sendCommandToController("a3");
        // Find out which player has won
        OXOPlayer originalWinner = model.getWinner();
        // Attempt an extra go
        sendCommandToController("b3");

        // Check that someone has actually won !
        assertTrue(originalWinner != null, "No winner detected");
        // Check that the extra go hasn't altered the board
        assertTrue(model.getCellOwner(1,2) == null, "Extra turn taken after win");
        // Check also that the winner hasn't changed
        assertTrue(originalWinner.getPlayingLetter() == model.getWinner().getPlayingLetter(), "Players still alternate after win");
        // Check that the board size can't be changed
        controller.addRow();
        controller.addColumn();
        assertTrue(model.getNumberOfRows() == 3, "Rows can be added after a win");
        assertTrue(model.getNumberOfColumns() == 3, "Columns can be added after a win");
    }

    @Test
    void testNonWinningStates()
    {
        // Make sure that the controller can actually detect a win (or they get points for just saying "no winner" every time !)
        checkWinnerFor(new String[] {"a1","b2","a2","c2","a3"},3,3,3,0);
        checkWinnerFor(new String[] {},3,3,3,-1);
        checkWinnerFor(new String[] {"a2","a1","b1","b3","b2","c2","c1"},3,3,3,-1);
        checkWinnerFor(new String[] {"a3","a2","b1","a1","b2","b3","c2","c1","c3"},3,3,3,-1);
    }

    @Test
    void testDrawnStates()
    {
        checkForDraw(new String[] {"a1","a2","a3","b1","b3","b2","c1","c3","c2"});
        checkForDraw(new String[] {"a1","a3","a2","b1","b3","b2","c1","c2","c3"});
    }

    @Test
    void testBoardExtensionAfterDraw()
    {
        checkForDraw(new String[] {"a1","a2","a3","b1","b3","b2","c1","c3","c2"});
        model.addColumn();
        sendCommandToController("a4");
        String failedTestComment = "Game can't be continued after draw and extension";
        assertEquals(model.getWinner(), null, failedTestComment);

    }

    @Test
    void testPlayerCharMutation()
    {
        // We are going to set the board so that the top row is a winner
        sendCommandToController("a1");
        sendCommandToController("b1");
        // However, mutate the first players character
        model.getPlayerByNumber(0).setPlayingLetter('Y');
        sendCommandToController("a2");
        sendCommandToController("b2");
        sendCommandToController("a3");
        String errorMessage = "Failed to cope with player changing playing letter during the game";
        assertTrue(model.getWinner().getPlayingLetter() == model.getPlayerByNumber(0).getPlayingLetter(), errorMessage);
    }

    @Test
    void testBigGame()
    {
        // Test for an actual 5-in-a-row horizontal win
        checkWinnerFor(new String[]{"b1","a1","b2","a2","b3","a3","b4","a4","b5"},5,5,5,0);
        // Make sure that the controller can actually detect a win (or they get points for just saying "no winner" every time !)
        checkWinnerFor(new String[]{"a1","a2","b2","a3","c3","a4","d4","a5"},5,5,5,-1);
        // Test for an actual 5-in-a-row diagonal win
        checkWinnerFor(new String[]{"a1","a2","b2","a3","c3","a4","d4","a5","e5"},5,5,5,0);
    }

    @Test
    void testMinimumWinThreshold()
    {
        controller.decreaseWinThreshold();
        assertTrue(model.getWinThreshold() == 3, "Win threshold can be reduced below the minimim of 3");
    }

    @Test
    void testWinThresholdNotReducedDuringGame()
    {
        model = new OXOModel(4, 4, 4);
        assertTrue(model.getWinThreshold() == 4, "Win threshold not set correctly at start of game");
        model.addPlayer(new OXOPlayer('X'));
        model.addPlayer(new OXOPlayer('O'));
        controller = new OXOController(model);
        sendCommandToController("a3");
        controller.decreaseWinThreshold();
        assertTrue(model.getWinThreshold() == 4, "Win threshold can be reduced during the game");
    }

    @Test
    void testWinThresholdIncreasedDuringGame()
    {
        model = new OXOModel(4, 4, 3);
        assertTrue(model.getWinThreshold() == 3, "Win threshold not set correctly at start of game");
        model.addPlayer(new OXOPlayer('X'));
        model.addPlayer(new OXOPlayer('O'));
        controller = new OXOController(model);
        sendCommandToController("a3");
        controller.increaseWinThreshold();
        assertTrue(model.getWinThreshold() == 4, "Win threshold cannot be increased during the game");
    }

    @Test
    void testWinThresholdNotIncreasedAfterWin()
    {
        checkWinnerFor(new String[]{"a1","b1","a2","b2","a3"},4,4,3,0);
        controller.increaseWinThreshold();
        assertTrue(model.getWinThreshold() == 3, "Win threshold can be increased after a game has been won");
    }

    @Test
    void testMaximumWinThreshold()
    {
        controller.increaseWinThreshold();
        assertTrue(model.getWinThreshold() == 3, "Win threshold can be increased beyond the size of the board");
    }

    @Test
    void testMinimumBoardSize()
    {
        controller.removeRow();
        controller.removeColumn();
        assertTrue(model.getNumberOfRows() == 3, "Number of rows can be reduced below the minimim of 3");
        assertTrue(model.getNumberOfColumns() == 3, "Number of columns can be reduced below the minimim of 3");
    }

    @Test
    void testFilledRowsNotRemovable()
    {
        model = new OXOModel(4, 4, 3);
        model.addPlayer(new OXOPlayer('X'));
        model.addPlayer(new OXOPlayer('O'));
        controller = new OXOController(model);
        sendCommandToController("d1");
        controller.removeRow();
        assertTrue(model.getNumberOfRows() == 4, "Rows containing claimed cells can be removed");
    }

    @Test
    void testFilledColumnsNotRemovable()
    {
        model = new OXOModel(4, 4, 3);
        model.addPlayer(new OXOPlayer('X'));
        model.addPlayer(new OXOPlayer('O'));
        controller = new OXOController(model);
        sendCommandToController("a4");
        controller.removeColumn();
        assertTrue(model.getNumberOfColumns() == 4, "Rows containing claimed cells can be removed");
    }

    @Test
    void testMaximumBoardSize()
    {
        for(int i=0; i<20 ;i++) controller.addRow();
        assertTrue(model.getNumberOfRows() == 9, "Number of rows can be increased above the maximim of 9");
    }

    @Test
    void testThreePlayers()
    {
        model = new OXOModel(3,3,3);
        model.addPlayer(new OXOPlayer('X'));
        model.addPlayer(new OXOPlayer('O'));
        model.addPlayer(new OXOPlayer('Y'));
        controller = new OXOController(model);

        OXOPlayer firstPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        sendCommandToController("a3");
        OXOPlayer secondPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        sendCommandToController("a2");
        OXOPlayer thirdPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        sendCommandToController("a1");
        // Check that everyone took a turn
        assertTrue((firstPlayer != secondPlayer) && (firstPlayer != thirdPlayer) && (thirdPlayer != secondPlayer), "Didn't see all three players in action !");
        // Everyone's second move
        sendCommandToController("b2");
        sendCommandToController("b3");
        sendCommandToController("b1");
        // The winner's third move
        sendCommandToController("c1");
        assertTrue((model.getWinner() != null) && (model.getWinner().getPlayingLetter() == model.getPlayerByNumber(0).getPlayingLetter()), "Win not detected in 3 player mode !");
    }

    // Utility methods - not @Tests in their own right, but called by the real test methods

    void checkForDraw(String[] commands)
    {
        createStandardModel();
        for(int i=0; i<commands.length ;i++) sendCommandToController(commands[i]);
        assertTrue(model.getWinner()==null,"Draw not identified - a winner was identified");
        assertTrue(model.isGameDrawn(),"Draw not identified - the drawn boolean was not set");
    }

    void checkWinnerFor(String[] commands, int numberOfRows, int numberOfColumns, int winThreshold, int expectedWinnerNumber)
    {
        // Overwrite the standard model - in case we have been asked for a bigger board size
        model = new OXOModel(numberOfRows, numberOfColumns, winThreshold);
        OXOPlayer X = new OXOPlayer('X');
        OXOPlayer O = new OXOPlayer('O');
        model.addPlayer(X);
        model.addPlayer(O);
        controller = new OXOController(model);
        for(int i=0; i<commands.length ;i++) sendCommandToController(commands[i]);
        OXOPlayer actualWinner = model.getWinner();
        char expectedWinnerLetter = ' ';
        if(expectedWinnerNumber != -1) expectedWinnerLetter = model.getPlayerByNumber(expectedWinnerNumber).getPlayingLetter();

        assertFalse((expectedWinnerNumber == -1) && (actualWinner != null), "Winner set when there was none");
        assertFalse((expectedWinnerNumber != -1) && (actualWinner == null), "No winner detected for winning board");
        // Check the letter of the player, rather than the player object because some
        // people create duplicate players when setting the winner (for which they have already been penalised)
        if(actualWinner != null) {
            String errorMessage = "Expected winner to be " + expectedWinnerLetter + " but was " + actualWinner.getPlayingLetter();
            assertTrue(actualWinner.getPlayingLetter() == expectedWinnerLetter, errorMessage);
        }
    }

}
