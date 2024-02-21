package edu.uob;

import edu.uob.OXOMoveException.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class ExampleControllerTests {
  private OXOModel model;
  private OXOController controller;

  // Make a new "standard" (3x3) board before running each test case (i.e. this method runs before every `@Test` method)
  // In order to test boards of different sizes, winning thresholds or number of players, create a separate test file (without this method in it !)
  @BeforeEach
  void setup() {
    model = new OXOModel(3, 3, 3);
    model.addPlayer(new OXOPlayer('X'));
    model.addPlayer(new OXOPlayer('O'));
    controller = new OXOController(model);
  }

  // This next method is a utility function that can be used by any of the test methods to _safely_ send a command to the controller
  void sendCommandToController(String command) {
      // Try to send a command to the server - call will timeout if it takes too long (in case the server enters an infinite loop)
      // Note: this is ugly code and includes syntax that you haven't encountered yet
      String timeoutComment = "Controller took too long to respond (probably stuck in an infinite loop)";
      assertTimeoutPreemptively(Duration.ofMillis(1000), ()-> controller.handleIncomingCommand(command), timeoutComment);
  }

  // Test simple move taking and cell claiming functionality
  @Test
  void testBasicMoveTaking() throws OXOMoveException {
    // Find out which player is going to make the first move
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    // Make a move
    sendCommandToController("a1");
    // Check that A1 (cell [0,0] on the board) is now "owned" by the first player
    String failedTestComment = "Cell a1 wasn't claimed by the first player";
    assertEquals(firstMovingPlayer, controller.gameModel.getCellOwner(0, 0), failedTestComment);
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
  void testVerticalWin() throws OXOMoveException {
    // Find out which player is going to make the first move (they should be the eventual winner)
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    // Make a bunch of moves for the two players
    sendCommandToController("a1"); // First player
    sendCommandToController("a2"); // Second player
    sendCommandToController("b1"); // First player
    sendCommandToController("a3"); // Second player
    sendCommandToController("c1"); // First player

    // a1, a2, a3 should be a win for the first player (since players alternate between moves)
    // Let's check to see whether the first moving player is indeed the winner
    String failedTestComment = "Winner was expected to be " + firstMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(firstMovingPlayer, model.getWinner(), failedTestComment);
  }

  @Test
  void testDiagWin1() throws OXOMoveException {
    // Find out which player is going to make the first move (they should be the eventual winner)
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    // Make a bunch of moves for the two players
    sendCommandToController("a1"); // First player
    sendCommandToController("b1"); // Second player
    sendCommandToController("b2"); // First player
    sendCommandToController("c1"); // Second player
    sendCommandToController("c3"); // First player

    // a1, a2, a3 should be a win for the first player (since players alternate between moves)
    // Let's check to see whether the first moving player is indeed the winner
    String failedTestComment = "Winner was expected to be " + firstMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(firstMovingPlayer, model.getWinner(), failedTestComment);
  }
  @Test
  void testDiagWin2() throws OXOMoveException {
    // Find out which player is going to make the first move (they should be the eventual winner)
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    // Make a bunch of moves for the two players
    sendCommandToController("a3"); // First player
    sendCommandToController("b1"); // Second player
    sendCommandToController("b2"); // First player
    sendCommandToController("c3"); // Second player
    sendCommandToController("c1"); // First player

    // a1, a2, a3 should be a win for the first player (since players alternate between moves)
    // Let's check to see whether the first moving player is indeed the winner
    String failedTestComment = "Winner was expected to be " + firstMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(firstMovingPlayer, model.getWinner(), failedTestComment);
  }
  @Test
  void testDraw() throws OXOMoveException {
    // Make a bunch of moves for the two players
    sendCommandToController("a1"); // First player
    sendCommandToController("a2"); // Second player
    sendCommandToController("a3"); // First player
    sendCommandToController("b1"); // Second player
    sendCommandToController("b3"); // First player
    sendCommandToController("b2"); // Second player
    sendCommandToController("c1"); // First player
    sendCommandToController("c3"); // Second player
    sendCommandToController("c2"); // First player
    // a1, a2, a3 should be a win for the first player (since players alternate between moves)
    // Let's check to see whether the first moving player is indeed the winner
    String failedTestComment = "Draw was expected to be but wasn't";
    assertTrue(model.isGameDrawn(), failedTestComment);

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
  void testInvalidIdentifierCharacterException1() throws OXOMoveException {
    String failedTestComment = "Controller failed to throw an InvalidIdentifierCharacterException for command `?1`";
    assertThrows(InvalidIdentifierCharacterException.class, ()-> sendCommandToController("?1"), failedTestComment);
  }
  @Test
  void testInvalidIdentifierCharacterException2() throws OXOMoveException {
    String failedTestComment = "Controller failed to throw an InvalidIdentifierCharacterException for command `aa`";
    assertThrows(InvalidIdentifierCharacterException.class, ()-> sendCommandToController("aa"), failedTestComment);
  }
  @Test
  void testOutsideCellRangeException1() throws OXOMoveException {
    String failedTestComment = "Controller failed to throw an OutsideCellRangeException for command `D3`";
    assertThrows(OutsideCellRangeException.class, ()-> sendCommandToController("D3"), failedTestComment);
  }
  @Test
  void testOutsideCellRangeException2() throws OXOMoveException {
    String failedTestComment = "Controller failed to throw an OutsideCellRangeException for command `D3`";
    assertThrows(OutsideCellRangeException.class, ()-> sendCommandToController("A4"), failedTestComment);
  }
  @Test
  void testCellAlreadyTakenException() throws OXOMoveException {
    String failedTestComment = "Controller failed to throw an OutsideCellRangeException for command `D3`";
    sendCommandToController("A1");
    assertThrows(CellAlreadyTakenException.class, ()-> sendCommandToController("A1"), failedTestComment);
  }
  @Test
  void testIncreaseWinThreshold1() {
    String failedTestComment = "Controller failed to increase Win Threshold";
    model.addRow();
    model.addColumn();
    controller.increaseWinThreshold();
    assertEquals(4, model.getWinThreshold(), failedTestComment);
  }
  @Test
  void testIncreaseWinThreshold2() {
    String failedTestComment = "Controller increased win threshold beyond possible level";
    model.addRow();
    model.addColumn();
    controller.increaseWinThreshold();
    controller.increaseWinThreshold();
    assertEquals(4, model.getWinThreshold(), failedTestComment);
  }
  @Test
  void testIncreaseWinThreshold3() {
    String failedTestComment = "Controller increased win threshold after draw";
    model.addRow();
    model.addColumn();
    model.setGameDrawn(true);
    controller.increaseWinThreshold();
    assertEquals(3, model.getWinThreshold(), failedTestComment);
  }
  @Test
  void testIncreaseWinThreshold4() {
    String failedTestComment = "Controller increased win threshold after win";
    model.addRow();
    model.addColumn();
    model.setWinner(model.getPlayerByNumber(1));
    controller.increaseWinThreshold();
    assertEquals(3, model.getWinThreshold(), failedTestComment);
  }
  @Test
  void testIncreaseWinThreshold5() {
    String failedTestComment = "Controller changed win threshold after reset";
    model.addRow();
    model.addColumn();
    controller.increaseWinThreshold();
    model.addRow();
    model.addColumn();
    controller.increaseWinThreshold();
    controller.reset();
    assertEquals(5, model.getWinThreshold(), failedTestComment);
  }
  @Test
  void testDecreaseWinThreshold1() {
    String failedTestComment = "Controller decreased win threshold beyond minimum";
    controller.decreaseWinThreshold();
    assertEquals(3, model.getWinThreshold(), failedTestComment);
  }
  @Test
  void testDecreaseWinThreshold2() {
    String failedTestComment = "Controller did not decrease win threshold";
    model.addRow();
    model.addColumn();
    controller.increaseWinThreshold();
    model.addRow();
    model.addColumn();
    controller.increaseWinThreshold();
    controller.decreaseWinThreshold();
    assertEquals(4, model.getWinThreshold(), failedTestComment);
  }
  @Test
  void testDecreaseWinThreshold3() {
    String failedTestComment = "Controller decreased win threshold after draw";
    model.addRow();
    model.addColumn();
    controller.increaseWinThreshold();
    model.setGameDrawn(true);
    controller.decreaseWinThreshold();;
    assertEquals(4, model.getWinThreshold(), failedTestComment);
  }
  @Test
  void testDecreaseWinThreshold4() {
    String failedTestComment = "Controller decreased win threshold after win";
    model.addRow();
    model.addColumn();
    controller.increaseWinThreshold();
    model.setWinner(model.getPlayerByNumber(1));
    controller.decreaseWinThreshold();;
    assertEquals(4, model.getWinThreshold(), failedTestComment);
  }
  @Test
  void testAddRow1(){
    String failedTestComment = "Controller did not add row";
    controller.addRow();
    assertEquals(4, model.getNumberOfRows(), failedTestComment);
  }
  @Test
  void testAddRow2(){
    String failedTestComment = "Controller added row beyond max";
    controller.addRow();
    controller.addRow();
    controller.addRow();
    controller.addRow();
    controller.addRow();
    controller.addRow();
    controller.addRow();
    controller.addRow();
    controller.addRow();
    assertEquals(9, model.getNumberOfRows(), failedTestComment);
  }
  @Test
  void testAddRow3(){
    String failedTestComment = "Controller added row after win";
    controller.addRow();
    controller.addRow();
    controller.addRow();
    model.setWinner(model.getPlayerByNumber(1));
    controller.addRow();
    controller.addRow();
    assertEquals(6, model.getNumberOfRows(), failedTestComment);
  }

  @Test
  void testRemoveRow1(){
    String failedTestComment = "Controller removed row beyond min";
    controller.removeRow();
    assertEquals(3, model.getNumberOfRows(), failedTestComment);
  }
  @Test
  void testRemoveRow2(){
    String failedTestComment = "Controller did not remove row";
    controller.addRow();
    controller.addRow();
    controller.removeRow();
    assertEquals(4, model.getNumberOfRows(), failedTestComment);
  }
  @Test
  void testRemoveRow3(){
    String failedTestComment = "Controller removed row after win";
    controller.addRow();
    controller.addRow();
    model.setWinner(model.getPlayerByNumber(1));
    controller.removeRow();
    assertEquals(5, model.getNumberOfRows(), failedTestComment);
  }
  @Test
  void testRemoveRow4(){
    String failedTestComment = "Controller removed row when not empty";
    controller.addRow();
    controller.addRow();
    sendCommandToController("E1");
    controller.removeRow();
    assertEquals(5, model.getNumberOfRows(), failedTestComment);
  }
  @Test
  void testAddColumn1(){
    String failedTestComment = "Controller did not add column";
    controller.addColumn();
    assertEquals(4, model.getNumberOfColumns(), failedTestComment);
  }
  @Test
  void testAddColumn2(){
    String failedTestComment = "Controller added column beyond max";
    controller.addColumn();
    controller.addColumn();
    controller.addColumn();
    controller.addColumn();
    controller.addColumn();
    controller.addColumn();
    controller.addColumn();
    controller.addColumn();
    controller.addColumn();
    controller.addColumn();
    assertEquals(9, model.getNumberOfColumns(), failedTestComment);
  }
  @Test
  void testAddColumn3(){
    String failedTestComment = "Controller added column after win";
    controller.addColumn();
    controller.addColumn();
    controller.addColumn();
    model.setWinner(model.getPlayerByNumber(1));
    controller.addColumn();
    controller.addColumn();
    assertEquals(6, model.getNumberOfColumns(), failedTestComment);
  }

  @Test
  void testRemoveColumn1(){
    String failedTestComment = "Controller removed column beyond min";
    controller.removeColumn();
    assertEquals(3, model.getNumberOfColumns(), failedTestComment);
  }
  @Test
  void testRemoveColumn2(){
    String failedTestComment = "Controller did not remove row";
    controller.addColumn();
    controller.addColumn();
    controller.removeColumn();
    assertEquals(4, model.getNumberOfColumns(), failedTestComment);
  }
  @Test
  void testRemoveColumn3(){
    String failedTestComment = "Controller removed column after win";
    controller.addColumn();
    controller.addColumn();
    model.setWinner(model.getPlayerByNumber(1));
    controller.removeColumn();
    assertEquals(5, model.getNumberOfColumns(), failedTestComment);
  }
  @Test
  void testRemoveColumn4(){
    String failedTestComment = "Controller removed column when not empty";
    controller.addColumn();
    controller.addColumn();
    sendCommandToController("A5");
    controller.removeColumn();
    assertEquals(5, model.getNumberOfColumns(), failedTestComment);
  }
}
