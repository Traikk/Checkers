package core;

import java.util.Arrays;

import core.Piece;

public class Board
{
    private Piece[][] state;

    public Board(int size) {
        // initialize board with a default state
        state = new Piece[size][size];

        for(int i = 0; i < state.length; i++) {
            for(int j = 0; j < state[i].length; j++) {
                state[i][j] = new Piece(0);
            }
        }
    }

    // set state to default position player 1 will be represented as '1' and
    // player to with '2'
    public void setToStartingPosition() {
        // TODO: move to a more general place
        int totalPieces = 24;
        int counter = 0;

        for(int i = 0; i < getSize(); i++) {
            for(int j = 0; j < getSize(); j++) {
                if ((i + j) % 2 == 0 && counter < totalPieces) {
                    setPieceAt(j, i, new Piece(1));
                } else if ((i + j) % 2 != 0 && counter < totalPieces){
                    setPieceAt(j , getSize() - (i + 1), new Piece(2));
                }
                counter++;
            }
        }

    }

    // returns state char at x and y position
    public Piece getPieceAt(int x, int y) {
        Piece a;
        try {
            a = state[x][y];
        } catch(ArrayIndexOutOfBoundsException e) {
            return new Piece(0);
        }
        return a;
    }

    // sets piece in state at coorinates
    public void setPieceAt(int x, int y, Piece p) {
        state[x][y] = p;
    }

    // because our board a square we only need to return the number of rows
    public int getSize() {
        return state.length;
    }

    // sets the selection status of a given piece at coordinates in state
    public void setSelectionStatusAt(int x, int y, boolean to) {
        Piece a = getPieceAt(x, y);
        if(a.getSelectionStaus() == !to) {
            a.changeSelectionStatus();
        }
    }

    // returns selection status at coordinates in state
    public boolean getSelectionStatus(int x, int y) {
        boolean a;
        try {
            a = getPieceAt(x, y).getSelectionStaus();
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
        return a;
    }
}
