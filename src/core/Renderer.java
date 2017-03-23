package core;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Color;

import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

import core.Board;
import core.Constants;
import core.Game;
import core.Piece;

// Renderer and its subclasses handle all gui actions including event listening
// coordinate conversion, and drawing
public class Renderer
{
    private final JFrame mainFrame;
    public final GameCanvas gameCanvas;

    public Renderer(Board board, int totalWidth, int totalHeight, int currentPlayer) {
        // TODO: move these declarations to Checkers.java
        int xMargin = 25;
        int yMargin = 25;

        Dimension screenSize = new Dimension(totalWidth, totalHeight);

        mainFrame = new JFrame();
        mainFrame.setSize(screenSize);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setResizable(false);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);

        Dimension frameSize = mainFrame.getContentPane().getSize();

        gameCanvas = new GameCanvas(frameSize, xMargin, yMargin, board, currentPlayer);
        // tileListener = new SelectTileListener(gameCanvas, board);

        mainFrame.getContentPane().add(gameCanvas);
    }

    public void addMouseListenerToCanvas(MouseListener m) {
        gameCanvas.addMouseListener(m);
    }

    public Dimension getCanvasSize() {
        return mainFrame.getContentPane().getSize();
    }
}



// swing Component where the game will be drawn upon
class GameCanvas extends JPanel
{
    private final int xMargin;
    private final int yMargin;

    private final double canvasWidth;
    private final double canvasHeight;

    private final Board board;
    private final int boardSize;

    private final int colSize;
    private final int rowSize;

    private int currentPlayer;

    public GameCanvas(Dimension frameSize, int xMar, int yMar, Board b, int currentPlayer) {
        xMargin = xMar;
        yMargin = yMar;

        canvasWidth = frameSize.getWidth() - (2 * xMargin);
        canvasHeight = frameSize.getHeight() - (2 * yMargin);

        board = b;
        boardSize = board.getSize();
        board.setToStartingPosition();

        colSize = (int) canvasWidth / boardSize;
        rowSize = (int) canvasHeight / boardSize;

        this.currentPlayer = currentPlayer;
    }


    // here are the central drawing routines
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        int intWidth = (int) canvasWidth;
        int intHeight = (int) canvasHeight;

        g.drawRect(xMargin, yMargin, intWidth, intHeight);
        drawBackground(g);

        drawBoard(g, board);

    }


    // drawing the grid pattern in the background
    public void drawBackground(Graphics g) {

        g.setColor(Constants.tileColorEven);

        for(int i = 0; i < boardSize; i++) {
            g.drawLine(xMargin + (i * colSize), yMargin ,xMargin + (i * colSize), (int) this.canvasHeight + yMargin);
            g.drawLine(xMargin, yMargin + (i * rowSize), (int) this.canvasWidth + xMargin, yMargin + (i * rowSize));

            for(int j = 0; j < boardSize; j++) {
                if ((i + j) % 2 == 0) {
                    g.fillRect(xMargin + (j * colSize), yMargin + (i * rowSize), colSize, rowSize);

                }
            }
        }
    }


    // draws a single piece at boardx and y coordinates and ajusts color according to
    // the owner. Also draws selected pieces darker than not selected ones
    public void drawPiece(Graphics g, Piece p, int boardX, int boardY) {
        // TODO: move the 2 sizeFactor variables to a more general place
        boolean selectionStatus = p.getSelectionStaus();

        if (p.getOwner() == 1) {
            g.setColor(Constants.player1Color);
        } else if(p.getOwner() == 2) {
            g.setColor(Constants.player2Color);
        } else {
            if((boardX + boardY) % 2 == 2) {
                g.setColor(Constants.tileColorEven);
            } else {
                g.setColor(Constants.tileColorOdd);
            }
        }

        double sizeFactor = 0.75;
        int width = (int) (colSize * sizeFactor);
        int height = (int) (rowSize * sizeFactor);

        int positionalXMargin = (colSize / 2) - (width / 2);
        int positionalYmargin = (rowSize / 2) - (height / 2);


        g.fillOval(xMargin + (boardX * colSize) + positionalXMargin, yMargin + (boardY * rowSize) + positionalYmargin, width, height);

        if(selectionStatus == true) {
            if(p.getOwner() == 1) {
                g.setColor(Constants.player1Color.darker());
            } else if(p.getOwner() == 2) {
                g.setColor(Constants.player2Color.darker());
            }
            g.fillOval(xMargin + (boardX * colSize) + positionalXMargin, yMargin + (boardY * rowSize) + positionalYmargin, width, height);
        }

        // queens have an extra circle in the middle
        if(p.getQueenStatus() == true) {
            double sizeFactorQueen = 0.3;

            int widthQueen = (int) (colSize * sizeFactorQueen);
            int heightQueen = (int) (rowSize * sizeFactorQueen);

            int positionalXMarginQueen = (colSize / 2) - (widthQueen / 2);
            int positionalYMarginQueen = (rowSize / 2) - (heightQueen / 2);

            g.setColor(Constants.tileColorEven);
            g.fillOval(xMargin + (boardX * colSize) + positionalXMarginQueen, yMargin + (boardY * rowSize) + positionalYMarginQueen, widthQueen, heightQueen);
        }
    }


    // calls the drawPiece() method on every piece in board if it has an owner
    public void drawBoard(Graphics g, Board b) {
        for(int i = 0; i < b.getSize(); i++) {
            for(int j = 0; j < b.getSize(); j++) {
                if(b.getPieceAt(i, j).getOwner() != 0) {
                    drawPiece(g, b.getPieceAt(i, j), i, j);
                }
            }
        }
    }

}
