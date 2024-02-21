package edu.uob;

import java.util.ArrayList;
import java.util.Arrays;

public class OXOModel {

    private ArrayList<ArrayList<OXOPlayer>> cells;
    private OXOPlayer[] players;
    private int currentPlayerNumber;
    private OXOPlayer winner;
    private boolean gameDrawn;
    private int winThreshold;

    private boolean gameStarted;

    public OXOModel(int numberOfRows, int numberOfColumns, int winThresh) {
        winThreshold = winThresh;
        this.gameStarted = false;
        cells = new ArrayList<>() {{
            for(int i = 0; i < numberOfRows; i++) {
                add(new ArrayList<>() {{
                    for (int j = 0; j < numberOfColumns; j++){
                        add(null);
                    }
                }});
            }
        }};
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
        return cells.size();
    }

    public int getNumberOfColumns() {
        return cells.get(0).size();
    }

    public OXOPlayer getCellOwner(int rowNumber, int colNumber) {
        return cells.get(rowNumber).get(colNumber);
    }

    public void setCellOwner(int rowNumber, int colNumber, OXOPlayer player) {
        cells.get(rowNumber).set(colNumber, player);
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
            cells.add(new ArrayList<>() {{
                for (int j = 0; j < nCols; j++){
                    add(null);
                }
            }});
        }
        this.setGameDrawn(false);
    }
    public void addColumn() {
        int nRows = getNumberOfRows();
        int nCols = getNumberOfColumns();

        if (nCols < 9) {
            for(int i = 0; i < nRows; i++){
                cells.get(i).add(null);
            }
        }
        this.setGameDrawn(false);
    }

    public void removeRow(){
        int nRows = getNumberOfRows();
        int nCols = getNumberOfColumns();

        if(nRows > 3 && isRowEmpty(nRows - 1, nCols)){
            cells.remove(nRows-1);
        }
    }
    private boolean isRowEmpty(int maxRow, int nCols){
        for(int i = 0; i < nCols; i++){
            if(cells.get(maxRow).get(i) != null){
                return false;
            }
        }
        return true;
    }
    public void removeColumn() {
        int nRows = getNumberOfRows();
        int nCols = getNumberOfColumns();

        if (nCols > 3 && isColEmpty(nCols -1, nRows)) {
            for(int i = 0; i < nRows; i++){
                cells.get(i).remove(nCols-1);
            }
        }
    }
    private boolean isColEmpty(int maxCol, int nRows){
        for(int i = 0; i < nRows; i++){
            if(cells.get(i).get(maxCol) != null){
                return false;
            }
        }
        return true;
    }
    void setGameStarted(boolean gameStarted){
        this.gameStarted = gameStarted;
    }
    boolean getGameStarted(){
        return this.gameStarted;
    }
}
