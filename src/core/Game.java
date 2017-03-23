package core;

import core.Renderer;
import core.Board;
import core.Piece;

import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

public class Game
{
    private final Renderer renderer;
    private final Board board;
    private int currentPlayer;
    private boolean done;

    public DrawingVariables drawVar;

    private class DrawingVariables
    {
        public final int xMargin = 25;
        public final int yMargin = 25;

        public final int boardSize;

        public final int width;
        public final int height;

        public final int colSize;
        public final int rowSize;

        public DrawingVariables(Board b) {
            boardSize = b.getSize();

            width = (int) renderer.getCanvasSize().getWidth();
            height = (int) renderer.getCanvasSize().getHeight();

            colSize = width / boardSize;
            rowSize = height / boardSize;

        }
    }


    // central management class for all procedures
    public Game() {
        currentPlayer = 1;
        done = false;

        board = new Board(8);
        renderer = new Renderer(board, 800, 800, currentPlayer);

        renderer.addMouseListenerToCanvas(new SelectTileListener(this, board));
        drawVar = new DrawingVariables(board);

    }


    public int getCurrentPlayer() {
        return currentPlayer;
    }


    public void updateCurrentPlayer() {
        if(currentPlayer == 1) {
            currentPlayer = 2;
        } else if(currentPlayer == 2){
            currentPlayer = 1;
        }
    }


    public void updateCanvas() {
        renderer.gameCanvas.repaint();
    }


    // converts mouse coordinates to tiles on the board. If the tile does not exist
    // the coordinate value is -1 as decalred in core.Constants.NOT_A_TILE
    public int[] convertMouseCoordsToBoardCoords(int x, int y) {
        int boardX = (x - drawVar.xMargin) / drawVar.colSize;
        int boardXRest = (x - drawVar.xMargin) % drawVar.colSize;

        int boardY = (y - drawVar.yMargin) / drawVar.rowSize;
        int boardYRest = (y - drawVar.yMargin) % drawVar.colSize;

        if(boardX == drawVar.boardSize) {
            boardX = Constants.NOT_A_TILE;
        } else if(boardX == 0 && boardXRest < 0) {
            boardX = Constants.NOT_A_TILE;
        }

        if(boardY == drawVar.boardSize) {
            boardY = Constants.NOT_A_TILE;
        } else if (boardY == 0 && boardYRest < 0) {
            boardY = Constants.NOT_A_TILE;
        }

        int[] res = {boardX, boardY};
        return res;
    }

    public boolean checkIfSomeoneWon(Board b) {
        int player1Pieces = 0;
        int player2Pieces = 0;
        for(int i = 0; i < b.getSize(); i++) {
            for(int j = 0; j < b.getSize(); j++) {
                if(b.getPieceAt(i, j).getOwner() == 1) {
                    player1Pieces++;
                } else if(b.getPieceAt(i, j).getOwner() == 2) {
                    player2Pieces++;
                }
            }
        }

        if(player1Pieces == 0 || player2Pieces == 0) {
            return true;
        }

        return false;
    }

}


// listens for mouse clicks and calls repaint when there is a mouseClicked event
class SelectTileListener implements MouseListener
{
    Board board;
    Game game;

    int[] currentSelectedPiece;

    public SelectTileListener(Game g, Board b) {
        board = b;
        game = g;

        // stores currently selected Piece
        this.currentSelectedPiece = new int[] {-1, -1};
    }


    public void mousePressed(MouseEvent e) {

    }


    public void mouseReleased(MouseEvent e) {

    }


    public void mouseEntered(MouseEvent e) {

    }


    public void mouseExited(MouseEvent e) {

    }


    // caputres mouse Click events and then directs calls handler methods
    // and the update methods
    public void mouseClicked(MouseEvent e) {
        int[] boardCoords = game.convertMouseCoordsToBoardCoords(e.getX(), e.getY());

        if(boardCoords[0] != Constants.NOT_A_TILE && boardCoords[1] != Constants.NOT_A_TILE) {

            handleSelection(boardCoords);
            handleMovement(boardCoords);
        }

        checkForQueenPromotions(board);

        game.updateCanvas();
    }


    // changeSelectionStatus of a piece if all criteria for Selection are met
    public void handleSelection(int[] boardCoords) {
        if(currentSelectedPiece[0] == Constants.NOT_A_TILE || currentSelectedPiece[1] == Constants.NOT_A_TILE) {
            if(checkIfPieceHasCurrentOwner(boardCoords[0], boardCoords[1])) {
                if(board.getSelectionStatus(boardCoords[0], boardCoords[1]) != true) {

                    board.setSelectionStatusAt(boardCoords[0],boardCoords[1], true);
                    currentSelectedPiece = boardCoords;
                }
            }
        } else {
            if(currentSelectedPiece[0] == boardCoords[0] && currentSelectedPiece[1] == boardCoords[1]) {

                board.setSelectionStatusAt(boardCoords[0], boardCoords[1], false);
                currentSelectedPiece = new int[] {-1, -1};

            } else if(checkIfPieceHasCurrentOwner(boardCoords[0], boardCoords[1])) {

                board.setSelectionStatusAt(boardCoords[0], boardCoords[1], true);
                board.setSelectionStatusAt(currentSelectedPiece[0], currentSelectedPiece[1], false);
                currentSelectedPiece = new int[] {boardCoords[0], boardCoords[1]};
            }
        }
    }


    // moves pieces and removes old once some move happens also checks if game has ended
    // after the move and switches the player if it hasn't
    public void handleMovement(int[] boardCoords) {

        if(currentSelectedPiece[0] != Constants.NOT_A_TILE && currentSelectedPiece[1] != Constants.NOT_A_TILE &&
            board.getPieceAt(boardCoords[0], boardCoords[1]).getOwner() == 0){

            if(checkIfMoveIsLegal(currentSelectedPiece, boardCoords) && checkIfPieceHasCurrentOwner(boardCoords[0], boardCoords[1]) == false) {

                board.setPieceAt(boardCoords[0], boardCoords[1], board.getPieceAt(currentSelectedPiece[0], currentSelectedPiece[1]));
                board.setSelectionStatusAt(boardCoords[0], boardCoords[1], false);
                board.setPieceAt(currentSelectedPiece[0], currentSelectedPiece[1], new Piece(0));
                // clearSelectionStatus(board);

                if(game.checkIfSomeoneWon(board)) {
                    System.out.println("Player " + game.getCurrentPlayer() + " Won");
                    game.updateCanvas();
                }

                game.updateCurrentPlayer();
            } else if(checkIfJumpIsLegal(currentSelectedPiece, boardCoords) && checkIfPieceHasCurrentOwner(boardCoords[0], boardCoords[1]) == false) {

                board.setPieceAt(boardCoords[0], boardCoords[1], board.getPieceAt(currentSelectedPiece[0], currentSelectedPiece[1]));
                board.setPieceAt(currentSelectedPiece[0] + ((boardCoords[0] - currentSelectedPiece[0]) / 2), currentSelectedPiece[1] + ((boardCoords[1] - currentSelectedPiece[1]) / 2), new Piece(0));
                board.setPieceAt(currentSelectedPiece[0], currentSelectedPiece[1], new Piece(0));

                if(game.checkIfSomeoneWon(board)) {
                    System.out.println("Player " + game.getCurrentPlayer() + " Won");
                    game.updateCanvas();
                }

                game.updateCurrentPlayer();

            }


        }
    }


    // checks if the currentPlayer property has the same owner as the piece at boardx and y coords
    public boolean checkIfPieceHasCurrentOwner(int boardX, int boardY) {
        if (game.getCurrentPlayer() == board.getPieceAt(boardX, boardY).getOwner()) {
            return true;
        } else {
            return false;
        }
    }


    // returns ture if the move is legal and false if it is not
    public boolean checkIfMoveIsLegal(int[] pieceToMove, int[] destination) {
        int xMovement = destination[0] - pieceToMove[0];
        int yMovement = destination[1] - pieceToMove[1];

        int currentPlayer = board.getPieceAt(pieceToMove[0], pieceToMove[1]).getOwner();
        int opponent = 0;

        int yDirection = 0;

        if(currentPlayer == 1) {
            opponent = 2;
            yDirection = 1;
        } else if(currentPlayer == 2) {
            opponent = 1;
            yDirection = -1;
        }

        if(destination[0] != Constants.NOT_A_TILE && destination[1] != Constants.NOT_A_TILE) {
            if(board.getPieceAt(pieceToMove[0], pieceToMove[1]).getQueenStatus() == true) {
                if(Math.abs(xMovement) == 1 && Math.abs(yMovement) == 1) {
                    return true;
                }
            } else {
                if(Math.abs(xMovement) == 1 && (yMovement) == yDirection) {
                    return true;
                }
            }
        }
        return false;
    }


    // returns true if the jump from pieceToMove to destination is a legal move
    public boolean checkIfJumpIsLegal(int[] pieceToMove, int[] destination) {
        int xMovement = destination[0] - pieceToMove[0];
        int yMovement = destination[1] - pieceToMove[1];

        int currentPlayer = board.getPieceAt(pieceToMove[0], pieceToMove[1]).getOwner();

        int opponent = 0;
        int yDirection = 0;

        if(currentPlayer == 1) {
            opponent = 2;
            yDirection = 2;
        } else if(currentPlayer == 2) {
            opponent = 1;
            yDirection = -2;
        }

        if(destination[0] != Constants.NOT_A_TILE && destination[1] != Constants.NOT_A_TILE) {
            if(xMovement == 2 && yMovement == yDirection && board.getPieceAt(pieceToMove[0] + 1, pieceToMove[1] + (yDirection / 2)).getOwner() == opponent) {
                return true;
            } else if(xMovement == -2 && yMovement == yDirection && board.getPieceAt(pieceToMove[0] - 1, pieceToMove[1]  + (yDirection / 2)).getOwner() == opponent) {
                return true;
            }

            if(board.getPieceAt(pieceToMove[0], pieceToMove[1]).getQueenStatus() == true) {
                if(xMovement == 2 && yMovement == -yDirection && board.getPieceAt(pieceToMove[0] + 1, pieceToMove[1] + (-yDirection / 2)).getOwner() == opponent) {
                    return true;
                } else if(xMovement == -2 && yMovement == -yDirection && board.getPieceAt(pieceToMove[0] - 1, pieceToMove[1]  + (-yDirection / 2)).getOwner() == opponent) {
                    return true;
                }
            }
        }
        return false;

    }


    // sets the Selection status of every piece on the board to false
    // TODO: Check if this method is necesary; currently commented out in handleMovement
    public void clearSelectionStatus(Board b) {
        currentSelectedPiece[0] = Constants.NOT_A_TILE;
        currentSelectedPiece[1] = Constants.NOT_A_TILE;
        for(int i = 0; i < board.getSize(); i++) {
            for(int j = 0; j < board.getSize(); j++) {
                if(board.getPieceAt(i, j).getSelectionStaus() == true) {
                    board.setSelectionStatusAt(i, j, false);
                }
            }
        }
    }


    // checks if a piece has reached the other end of the board and should be
    // promoted to a queen, which can move backwards aswell
    public void checkForQueenPromotions(Board b) {
        for (int i = 0; i < board.getSize(); i++ ) {
            for(int j = 0; j < board.getSize(); j++) {
                if(board.getPieceAt(i, j).getOwner() == 1 && j == board.getSize() - 1) {
                    board.getPieceAt(i, j).promoteToQueen();
                } else if(board.getPieceAt(i, j). getOwner() == 2 && j == 0) {
                    board.getPieceAt(i, j).promoteToQueen();
                }
            }
        }
    }
}
