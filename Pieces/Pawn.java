package Pieces;

import main.GamePanel;
import main.Type;

public class Pawn extends Piece {

    public Pawn(int color, int col,int row) {

        super(color, col, row);
        type = Type.Pawn;
        if (color == GamePanel.WHITE){
            image = getImage("white_Pawn.png");

        }
        else {
            image = getImage("black_Pawn.png");
        }
    }

    public boolean canMove(int targetCol, int targetRow){

        if(isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol, targetRow) == false){

            int moveValue;
            if(color == GamePanel.WHITE){
                moveValue = -1; // White moves up
            }
            else {
                moveValue = 1; // Black moves down
            }

            //check the hitting piece;
            hittingP = getHittingP(targetCol, targetRow);

            // 1 square movement
            if(targetCol == preCol && targetRow == preRow + moveValue && hittingP == null){
                return true;
            }

            // 2 square movement
            if(targetCol == preCol && targetRow == preRow + moveValue * 2 && hittingP == null && moved == false && 
                    pieceIsOnStraightLine(targetCol, targetRow) == false){
                return true;
            }

            //diagonal movement & capture
            if(Math.abs(targetCol - preCol) == 1 && targetRow == preRow + moveValue && hittingP != null &&
                    hittingP.color != this.color){
                return true;
            }

            //en passant
            if(Math.abs(targetCol - preCol) == 1 && targetRow == preRow + moveValue){
                for(Piece piece: GamePanel.simPieces){
                    if(piece.col == targetCol && piece.row == preRow && piece.twoStepped == true){
                        hittingP = piece;
                        return true;
                    }
                }
            }
        }
        return false;
    }
}