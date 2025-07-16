package Pieces;

import main.GamePanel;

public class Queen extends Piece{

    public Queen(int color, int col,int row) {
        super(color, col, row);
        if (color == GamePanel.WHITE){
            image = getImage("white_Queen.png");
        }
        else {
            image = getImage("black_Queen.png");
        }
    }
    public boolean canMove(int targetCol, int targetRow){
        if (isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol, targetRow) == false) {
            //vertical and horizontal movement
            if (preCol == targetCol || preRow == targetRow) {
                if (isValidSquare(targetCol, targetRow) && pieceIsOnStraightLine(targetCol, targetRow) == false) {
                    return true;
                }
            }

            //diagonal movement
            if (Math.abs(preCol - targetCol) == Math.abs(preRow - targetRow)){
                if (isValidSquare(targetCol, targetRow) && pieceIsOnDiagonalLine(targetCol, targetRow) == false) {
                    return true;
                }
            }
        }
        return false;
    }
}