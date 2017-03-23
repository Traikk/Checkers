package core;

public class Piece
{
    private int owner;
    private boolean isSelected;
    private boolean isQueen;

    public Piece(int owner) {
        this.owner = owner;
        isSelected = false;
        isQueen = false;
    }


    public boolean getSelectionStaus() {
        return isSelected;
    }


    public boolean getQueenStatus() {
        return isQueen;
    }


    public void changeSelectionStatus() {
        isSelected = !isSelected;
    }


    public int getOwner() {
        return owner;
    }

    public void promoteToQueen() {
        isQueen = true;
    }

}
