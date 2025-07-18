package main;

import java.awt.Graphics2D;
import java.awt.Color;

public class Board{
    final int MAX_COL = 8;
    final int MAX_ROW = 8;
    public static final int SQUARE_SIZE = 100;
    public static final int HALF_SQUARE_SIZE = SQUARE_SIZE / 2;

    public void draw(Graphics2D g2){

        int c = 0; // variable to alternate colors

        for(int row = 0; row < MAX_ROW; row++){
            for(int col = 0; col < MAX_COL; col++){
                
                if(c ==0){
                    g2.setColor(Color.white);
                    c = 1;
                }
                else{
                    g2.setColor(Color.GRAY);
                    c = 0;
                }
                g2.fillRect(col * SQUARE_SIZE, row * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
            }
            if(c == 0){
                c = 1; // reset for next row
            } else {
                c = 0; 
            }
        }
    }
}