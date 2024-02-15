package edu.uob;

public class OXOController {
    OXOModel gameModel;

    public OXOController(OXOModel model) {
        gameModel = model;
    }

    public void handleIncomingCommand(String command) throws OXOMoveException {
        int row, column, playerNum;
        if(gameModel.getWinner() != null || gameModel.isGameDrawn()){
            return;
        }

        if(command.charAt(0) <= 'Z'){
            row = command.charAt(0) - 'A';
        }else{
            row = command.charAt(0) - 'a';
        }
        column = command.charAt(1) - '1';

        playerNum = gameModel.getCurrentPlayerNumber();

        gameModel.setCellOwner(row, column, gameModel.getPlayerByNumber(playerNum));

        if(this.detectWin(playerNum)){
            gameModel.setWinner(gameModel.getPlayerByNumber(playerNum));
            return;
        }
        if(this.detectDraw()) {
            gameModel.setGameDrawn(true);
        }

        if(gameModel.getNumberOfPlayers() == playerNum + 1){
            gameModel.setCurrentPlayerNumber(0);
        }else{
            gameModel.setCurrentPlayerNumber(playerNum + 1);
        }
    }
    public void addRow() {
        if(gameModel.getWinner() == null){
            gameModel.addRow();
        }
    }
    public void removeRow() {
        if(gameModel.getWinner() == null){
            gameModel.removeRow();
        }
    }
    public void addColumn() {
        if(gameModel.getWinner() == null){
            gameModel.addColumn();
        }
    }
    public void removeColumn() {
        if(gameModel.getWinner() == null){
            gameModel.removeColumn();
        }
    }
    public void increaseWinThreshold() {}
    public void decreaseWinThreshold() {}
    public void reset() {
        int nRows = gameModel.getNumberOfRows();
        int nCols = gameModel.getNumberOfColumns();

        for(int i = 0; i < nRows; i++){
            for( int j = 0; j < nCols; j++){
                gameModel.setCellOwner(i, j, null);
            }
        }
        gameModel.setWinner(null);
        gameModel.setCurrentPlayerNumber(0);
    }

    public boolean detectWin(int playerNum){
        int nRows = gameModel.getNumberOfRows();
        int nCols = gameModel.getNumberOfColumns();

        OXOPlayer currPlayer = gameModel.getPlayerByNumber(playerNum);

        if(winRow(currPlayer, nRows, nCols) || winCol(currPlayer, nRows, nCols) || winDiag(currPlayer, nRows, nCols)){
            return true;
        }
        return false;
    }

    public boolean winRow(OXOPlayer currPlayer, int nRows, int nCols){
        return winLoop(currPlayer, nRows, nCols, true);
    }
    public boolean winCol(OXOPlayer currPlayer, int nRows, int nCols){
        return winLoop(currPlayer, nCols, nRows, false);
    }

    public boolean winDiag(OXOPlayer currPlayer, int nRows, int nCols){
        for(int i = 0; i < nRows; i++){
            for(int j = 0; j < nCols; j++){
                if(gameModel.getCellOwner(i, j) == currPlayer){
                    if(checkDiag(currPlayer, i, j, nRows, nCols)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean checkDiag(OXOPlayer currPlayer, int row, int col, int nRows, int nCols){
        int seqLen = 1;
        for(int i = row + 1, j = col + 1; i < nRows && j < nCols; i++, j++){
            if(gameModel.getCellOwner(i, j) == currPlayer){
                seqLen++;
            }else{
                break;
            }
            if(seqLen >= gameModel.getWinThreshold()){
                return true;
            }
        }
        seqLen = 1;
        for(int i = row + 1, j = col - 1; i < nRows && j >= 0; i++, j--){
            if(gameModel.getCellOwner(i, j) == currPlayer){
                seqLen++;
            }else{
                break;
            }
            if(seqLen >= gameModel.getWinThreshold()){
                return true;
            }
        }
        return false;
    }
    private boolean winLoop(OXOPlayer currPlayer, int iMax, int jMax, boolean row){
        int seqLen;
        for(int i = 0; i < iMax; i++){
            seqLen = 0;
            for(int j = 0; j < jMax; j++){
                if(!row){
                    if(gameModel.getCellOwner(j, i) == currPlayer){
                        seqLen++;
                    }
                }else if(gameModel.getCellOwner(i, j) == currPlayer){
                    seqLen++;
                }
                if(seqLen >= gameModel.getWinThreshold()){
                    return true;
                }
            }
        }
        return false;
    }
    public boolean detectDraw(){
        int nRows = gameModel.getNumberOfRows();
        int nCols = gameModel.getNumberOfColumns();

        for(int i = 0; i < nRows; i++){
            for(int j = 0; j < nCols; j++){
                if(gameModel.getCellOwner(i, j) == null){
                    return false;
                }
            }
        }
        return true;
    }
}
