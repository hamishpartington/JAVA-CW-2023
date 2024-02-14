package edu.uob;

public class OXOController {
    OXOModel gameModel;

    public OXOController(OXOModel model) {
        gameModel = model;
    }

    public void handleIncomingCommand(String command) throws OXOMoveException {
        int row, column, playerNum;
        if(command.charAt(0) <= 'Z'){
            row = command.charAt(0) - 'A';
        }else{
            row = command.charAt(0) - 'a';
        }
        column = command.charAt(1) - '1';

        playerNum = gameModel.getCurrentPlayerNumber();

        gameModel.setCellOwner(row, column, gameModel.getPlayerByNumber(playerNum));

        if(gameModel.getNumberOfPlayers() == playerNum + 1){
            gameModel.setCurrentPlayerNumber(0);
        }else{
            gameModel.setCurrentPlayerNumber(playerNum + 1);
        }
    }
    public void addRow() {}
    public void removeRow() {}
    public void addColumn() {}
    public void removeColumn() {}
    public void increaseWinThreshold() {}
    public void decreaseWinThreshold() {}
    public void reset() {
        OXOPlayer clearer = new OXOPlayer('\0');
        int nRows = gameModel.getNumberOfRows();
        int nCols = gameModel.getNumberOfColumns();

        for(int i = 0; i < nRows; i++){
            for( int j = 0; j < nCols; j++){
                gameModel.setCellOwner(i, j, clearer);
            }
        }
        gameModel.setCurrentPlayerNumber(0);
    }
}
