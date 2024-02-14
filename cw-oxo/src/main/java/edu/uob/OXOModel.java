package edu.uob;

import java.util.ArrayList;
import java.util.Arrays;

public class OXOModel {

    private ArrayList<OXOPlayer[][]> cells;
    private OXOPlayer[] players;
    private int currentPlayerNumber;
    private int arrayListIndex;
    private OXOPlayer winner;
    private boolean gameDrawn;
    private int winThreshold;

    public OXOModel(int numberOfRows, int numberOfColumns, int winThresh) {
        winThreshold = winThresh;
        cells = new ArrayList<>() {{
            add(new OXOPlayer[numberOfRows][numberOfColumns]);
        }};
        arrayListIndex = 0;
        players = new OXOPlayer[2];
    }

    public int getNumberOfPlayers() {
        return players.length;
    }

    public void addPlayer(OXOPlayer player) {
        for (int i = 0; i < players.length; i++) {
            if (players[i] == null) {
                players[i] = player;
                return;
            }
        }
    }

    public OXOPlayer getPlayerByNumber(int number) {
        return players[number];
    }

    public OXOPlayer getWinner() {
        return winner;
    }

    public void setWinner(OXOPlayer player) {
        winner = player;
    }

    public int getCurrentPlayerNumber() {
        return currentPlayerNumber;
    }

    public void setCurrentPlayerNumber(int playerNumber) {
        currentPlayerNumber = playerNumber;
    }

    public int getNumberOfRows() {
        return cells.get(this.arrayListIndex).length;
    }

    public int getNumberOfColumns() {
        return cells.get(this.arrayListIndex)[0].length;
    }

    public OXOPlayer getCellOwner(int rowNumber, int colNumber) {
        return cells.get(this.arrayListIndex)[rowNumber][colNumber];
    }

    public void setCellOwner(int rowNumber, int colNumber, OXOPlayer player) {
        OXOPlayer board[][] = cells.get(this.arrayListIndex);
        board[rowNumber][colNumber] = player;
        cells.set(this.arrayListIndex, board);
    }

    public void setWinThreshold(int winThresh) {
        winThreshold = winThresh;
    }

    public int getWinThreshold() {
        return winThreshold;
    }

    public void setGameDrawn(boolean isDrawn) {
        gameDrawn = isDrawn;
    }

    public boolean isGameDrawn() {
        return gameDrawn;
    }

    public void addRow(){
        int nRows = getNumberOfRows();
        int nCols = getNumberOfColumns();

        if(nRows < 9){
            copyLoop(nRows, nCols, 0, 1);
        }
    }
    public void addColumn() {
        int nRows = getNumberOfRows();
        int nCols = getNumberOfColumns();

        if (nCols < 9) {
            copyLoop(nRows, nCols, 1, 0);
        }
    }

    public void removeRow(){
        int nRows = getNumberOfRows();
        int nCols = getNumberOfColumns();

        if(nRows > 3){
            copyLoop(nRows, nCols, 0, -1);
        }
    }
    public void removeColumn() {
        int nRows = getNumberOfRows();
        int nCols = getNumberOfColumns();

        if (nCols > 3) {
            copyLoop(nRows, nCols, -1, 0);
        }
    }
    public void copyLoop(int nRows, int nCols, int colAdj, int rowAdj){
        cells.add(new OXOPlayer[nRows + rowAdj][nCols + colAdj]);
        for (int i = 0; i < nRows && i < nRows + rowAdj; i++) {
            for (int j = 0; j < nCols && j < (nCols + colAdj); j++) {
                OXOPlayer currCell = getCellOwner(i, j);
                this.arrayListIndex++;
                setCellOwner(i, j, currCell);
                this.arrayListIndex--;
            }
        }
        this.arrayListIndex++;
    }
}
