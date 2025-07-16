package main;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JPanel;

import Pieces.Piece;

public class GamePanel extends JPanel implements Runnable{
   
    public static final int WIDTH = 1100;
    public static final int HEIGHT = 800;
    final int FPS = 60;
    Thread gameThread;
    Board board = new Board();
    Mouse mouse = new Mouse();

    //Pieces
    public static ArrayList<Piece> pieces = new ArrayList<>();
    public static ArrayList<Piece> simPieces = new ArrayList<>();
    Piece activeP;

    //Starting order 
    public static final int WHITE = 0;
    public static final int BLACK = 1;
    int currentColor = WHITE;

    public GamePanel(){
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.black);
        addMouseListener(mouse);
        addMouseMotionListener(mouse);

        setPieces();
        copyPieces(pieces, simPieces);
    }

    public void launchGame(){
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void setPieces(){
        //White pieces
        pieces.add(new Pieces.Pawn(WHITE, 0, 6));
        pieces.add(new Pieces.Pawn(WHITE, 1, 6));
        pieces.add(new Pieces.Pawn(WHITE, 2, 6));
        pieces.add(new Pieces.Pawn(WHITE, 3, 6));
        pieces.add(new Pieces.Pawn(WHITE, 4, 6));
        pieces.add(new Pieces.Pawn(WHITE, 5, 6));
        pieces.add(new Pieces.Pawn(WHITE, 6, 6));
        pieces.add(new Pieces.Pawn(WHITE, 7, 6));
        pieces.add(new Pieces.Rook(WHITE, 0, 7));
        pieces.add(new Pieces.Rook(WHITE, 7, 7));
        pieces.add(new Pieces.Knight(WHITE, 1, 7));
        pieces.add(new Pieces.Knight(WHITE, 6, 7));
        pieces.add(new Pieces.Bishop(WHITE, 2, 7));
        pieces.add(new Pieces.Bishop(WHITE, 5, 7));
        pieces.add(new Pieces.Queen(WHITE, 3, 7));
        pieces.add(new Pieces.King(WHITE, 4, 7));

        //Black pieces
        pieces.add(new Pieces.Pawn(BLACK, 0, 1));
        pieces.add(new Pieces.Pawn(BLACK, 1, 1));
        pieces.add(new Pieces.Pawn(BLACK, 2, 1));
        pieces.add(new Pieces.Pawn(BLACK, 3, 1));
        pieces.add(new Pieces.Pawn(BLACK, 4, 1));
        pieces.add(new Pieces.Pawn(BLACK, 5, 1));
        pieces.add(new Pieces.Pawn(BLACK, 6, 1));
        pieces.add(new Pieces.Pawn(BLACK, 7, 1));
        pieces.add(new Pieces.Rook(BLACK, 0, 0));
        pieces.add(new Pieces.Rook(BLACK, 7, 0));
        pieces.add(new Pieces.Knight(BLACK, 1, 0));
        pieces.add(new Pieces.Knight(BLACK, 6, 0));
        pieces.add(new Pieces.Bishop(BLACK, 2, 0));
        pieces.add(new Pieces.Bishop(BLACK, 5, 0));
        pieces.add(new Pieces.Queen(BLACK, 3, 0));
        pieces.add(new Pieces.King(BLACK, 4, 0));
    }
    private void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target){
        target.clear();
        for(int i = 0; i < source.size(); i++){
            target.add(source.get(i));
        }
    }

    @Override
    public void run(){

        //GAME LOOP
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while(gameThread != null){

            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if(delta >= 1){
                update();
                repaint();
                delta--;
            }
        }
    }

    private void update(){

        //Check if mouse is pressed
        if(mouse.pressed){
            //check if the aciveP is null
            if(activeP == null){
                //check you can pick up a piece
                for(Piece piece : pieces){
                    //check if the mouse is on an ally Piece, pick up as the activeP
                    if(piece.color == currentColor && 
                       piece.col == mouse.x/Board.SQUARE_SIZE &&
                       piece.row == mouse.y/Board.SQUARE_SIZE){

                        activeP = piece;
                    }
                }
            }
            else{
                simulate();
            }
        }
        if(mouse.pressed == false){
            if(activeP != null){
                activeP.updatePosition();
                activeP = null; // reset active piece after placing it
            }
        }
    }
    private void simulate(){

        activeP.x = mouse.x - Board.HALF_SQUARE_SIZE;
        activeP.y = mouse.y - Board.HALF_SQUARE_SIZE;
        activeP.col = activeP.getCol(mouse.x);
        activeP.row = activeP.getRow(mouse.y);
    }
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        
        Graphics2D g2 = (Graphics2D)g;

        //Draw the board
        board.draw(g2);

        //Draw the pieces
        for(Piece piece : pieces){
            piece.draw(g2);
        }

        if(activeP != null){
            g2.setColor(Color.red);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
            g2.fillRect(activeP.col * Board.SQUARE_SIZE, activeP.row * Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            activeP.draw(g2);
        }
    }
}