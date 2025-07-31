package Pieces;

import main.GamePanel;
import main.Type;

public class Rook extends Piece{

    public Rook(int color, int col,int row) {
        super(color, col, row);
        type = Type.Rook;
        if (color == GamePanel.WHITE){
            image = getImage("white_Rook.png");
        }
        else {
            image = getImage("black_Rook.png");
        }
    }

    public boolean canMove(int targetCol, int targetRow){

        if(isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol, targetRow) == false){
            if(Math.abs(targetCol-preCol) * Math.abs(targetRow - preRow) ==0){
                if(isValidSquare(targetCol, targetRow) && pieceIsOnStraightLine(targetCol, targetRow) == false){
                    return true;
                }
            }
        }
        return false;
    }
}
