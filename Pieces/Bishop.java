package Pieces;

import main.GamePanel;
import main.Type;

public class Bishop extends Piece{

    public Bishop(int color, int col,int row) {
        super(color, col, row);
        type = Type.Bishop;
        if (color == GamePanel.WHITE){
            image = getImage("white_Bishop.png");
        }
        else {
            image = getImage("/black_Bishop.png");
        }
    }

    public boolean canMove(int targetCol, int targetRow) {
        if (isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol, targetRow) == false) {
            if (Math.abs(preCol - targetCol) == Math.abs(preRow - targetRow)) {
                if (isValidSquare(targetCol, targetRow) && pieceIsOnDiagonalLine(targetCol, targetRow) == false) {
                    return true;
                }
            }
        }
        return false;
    }
}