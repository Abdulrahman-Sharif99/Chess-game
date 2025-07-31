package Pieces;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.Board;
import main.GamePanel;
import main.Type;

public class Piece {

    public Type type;
    public BufferedImage image;
    public int x, y;
    public int col, row, preCol, preRow;
    public int color;
    public Piece hittingP;
    public boolean moved = false;
    public boolean twoStepped = false;

    public Piece(int color, int col, int row){
        this.color = color;
        this.col = col;
        this.row = row;

        x = getX(col);
        y = getY(row);
        preCol = col;
        preRow = row;
    }

    public BufferedImage getImage(String imagePath) {
        BufferedImage image = null;
        try {
            var is = getClass().getResourceAsStream("/ChessPieces/" + imagePath);
            if (is == null) {
                System.out.println("Image not found: /ChessPieces/" + imagePath);
                return null;
            }
            image = ImageIO.read(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }


    public int getX(int col) {
        return col * Board.SQUARE_SIZE;
    }
    public int getY(int row) {
        return row * Board.SQUARE_SIZE;
    }
    public int getCol(int X) {
        return (x + Board.HALF_SQUARE_SIZE) / Board.SQUARE_SIZE;
    }
    public int getRow(int Y) {
        return (y + Board.HALF_SQUARE_SIZE) / Board.SQUARE_SIZE;
    }
    public int getIndex() {
        for(int index =0; index < GamePanel.pieces.size(); index++){
            if(GamePanel.pieces.get(index) == this){
                return index;
            }
        }
        return 0;
    }

    public void updatePosition() {

        if(type == Type.Pawn){
            if(Math.abs(preRow - row) == 2){
                twoStepped = true;
            }
        }
        x = getX(col);
        y = getY(row);
        preCol = getCol(x);
        preRow = getRow(y);
        moved = true;
    }

    public void resetPosition() {
        col = preCol;
        row = preRow;
        x = getX(col);
        y = getY(row);
    }
    public boolean canMove(int targetCol, int targetRow) {
        return false;
    }
    public boolean isWithinBoard(int targetCol, int targetRow) {
        return targetCol >= 0 && targetCol < 8 && targetRow >= 0 && targetRow < 8;
    }
    public boolean isSameSquare(int targetCol, int targetRow){
        if(targetCol == preCol && targetRow == preRow){
            return true;
        }
        return false;
    }
    public Piece getHittingP(int targetCol, int targetRow){
        for(Piece piece : GamePanel.simPieces){
            if(piece.col == targetCol && piece.row == targetRow && piece != this){
                return piece;
            }
        }
        return null;
    }

    public boolean isValidSquare(int targetCol, int targetRow){
        
        hittingP = getHittingP(targetCol, targetRow);

        if(hittingP == null){ // Check if Square is vacant
            return true;
        }
        else {
            if(hittingP.color != this.color){ // Check if the piece is an enemy
                return true;
            }
            else {
                hittingP = null;
            }
        }
        return false;
    }
    public boolean pieceIsOnStraightLine(int targetCol, int targetRow) {

        //When piece is moving to the left
        for(int c = preCol -1; c > targetCol; c--){
            for(Piece piece : GamePanel.simPieces){
                if(piece.col == c && piece.row == targetRow){
                    return true;
                }
            }
        }
        //when piece is moving to the right
        for(int c = preCol + 1; c < targetCol; c++){
            for(Piece piece : GamePanel.simPieces){
                if(piece.col == c && piece.row == targetRow){
                    return true;
                }
            }
        }
        //when piece is moving up
        for(int r = preRow -1; r > targetRow; r--){
            for(Piece piece : GamePanel.simPieces){
                if(piece.col ==targetCol && piece.row == r){
                    return true;
                }
            }
        }
        //when piece is moving down
        for(int r = preRow + 1; r < targetRow; r++){
            for(Piece piece : GamePanel.simPieces){
                if(piece.col ==targetCol && piece.row == r){
                    return true;
                }
            }
        }

        return false;
    }

    public boolean pieceIsOnDiagonalLine(int targetCol, int targetRow) {

        if(targetRow < preRow){
            //Up Left
            for(int c = preCol - 1; c > targetCol; c--){
                int diff = Math.abs(c-preCol);
                for(Piece piece : GamePanel.simPieces){
                    if(piece.col == c && piece.row == preRow - diff){
                        hittingP = piece;
                        return true;
                    }
                }
            }

            //Up right
            for(int c = preCol + 1; c < targetCol; c++){
                int diff = Math.abs(c-preCol);
                for(Piece piece : GamePanel.simPieces){
                    if(piece.col == c && piece.row == preRow - diff){
                        hittingP = piece;
                        return true;
                    }
                }
            }
        }

        if(targetRow > preRow){
            //Down Left
            for(int c = preCol - 1; c > targetCol; c--){
                int diff = Math.abs(c-preCol);
                for(Piece piece : GamePanel.simPieces){
                    if(piece.col == c && piece.row == preRow + diff){
                        hittingP = piece;
                        return true;
                    }
                }
            }

            //Down Right
            for(int c = preCol + 1; c < targetCol; c++){
                int diff = Math.abs(c-preCol);
                for(Piece piece : GamePanel.simPieces){
                    if(piece.col == c && piece.row == preRow + diff){
                        hittingP = piece;
                        return true;
                    }
                }
            }
        }

        return false;
    }
    public void draw(Graphics2D g2) {
        if (image != null) {
            g2.drawImage(image, x, y, Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
        }
    }
}