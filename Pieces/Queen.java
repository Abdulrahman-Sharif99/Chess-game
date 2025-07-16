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
}