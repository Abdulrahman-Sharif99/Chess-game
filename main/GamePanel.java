package main;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.awt.Font;

import javax.swing.JPanel;

import Pieces.Bishop;
import Pieces.Knight;
import Pieces.Piece;
import Pieces.Queen;
import Pieces.Rook;

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
    ArrayList<Piece> promoPieces = new ArrayList<>();
    Piece activeP, checkingP;
    public static Piece castlingP ;

    //Starting order 
    public static final int WHITE = 0;
    public static final int BLACK = 1;
    int currentColor = WHITE;

    boolean canMove;
    boolean validSquare;
    boolean promotion;
    boolean gameOver;

    public GamePanel(){
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(51, 0, 0));
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

        if(promotion){
            promoting();
        }

        else if(gameOver == false){
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
            if (mouse.pressed == false) {
                if (activeP != null) {
                    if (validSquare) {
                        //confirms the move 

                        // Actually remove only now
                        if (activeP.hittingP != null) {
                            pieces.remove(activeP.hittingP);
                        }

                        // Update the position of the active piece and piece that has been captured
                        activeP.updatePosition();
                        copyPieces(pieces, simPieces);

                        if(castlingP != null){
                            castlingP.updatePosition();
                        }

                        if(isKinginCheck() && isCheckMate()){
                            gameOver = true;

                        }
                        else{
                            //continues the game
                            if(canPromote()){
                                promotion = true;
                            } else {
                                switchTurns();
                                activeP = null;
                            }
                        }

                    } else {
                        activeP.resetPosition();
                        copyPieces(pieces, simPieces);
                        activeP = null;
                    }
                }
            }
        }
    }
    private void simulate() {
        canMove = false;
        validSquare = false;

        // Reset simulation pieces each loop
        copyPieces(pieces, simPieces);

        // Reset the castling piece's position
        if(castlingP != null) {
            castlingP.col = castlingP.preCol;
            castlingP.x = castlingP.getX(castlingP.col);
            castlingP = null;
        }

        activeP.x = mouse.x - Board.HALF_SQUARE_SIZE;
        activeP.y = mouse.y - Board.HALF_SQUARE_SIZE;
        activeP.col = activeP.getCol(mouse.x);
        activeP.row = activeP.getRow(mouse.y);

        if (activeP.canMove(activeP.col, activeP.row)) {
            canMove = true;

            checkCastling();

            if(isillegalMove(activeP) == false && opponentcanCapturedKing() == false){
                validSquare = true;
            }

        }
    }

    private boolean isillegalMove(Piece king){
        if(king.type == Type.King){
            for(Piece piece : simPieces){
                if(piece.color != king.color && piece.canMove(king.col, king.row)){
                    return true;
                }
            }
        }
        return false;
    }

    private boolean opponentcanCapturedKing(){

        Piece king = getKing(false);

        for(Piece piece : simPieces){
            if(piece.color != king.color && piece.canMove(king.col, king.row)){
                return true;
            }
        }
        return false;
    }

    private boolean isKinginCheck(){

        //gets opponent King
        Piece king = getKing(true);

        //Checks if activeP can move into spot where King piece currently is
        if(activeP.canMove(king.col,king.row)){
            checkingP = activeP;
            return true;
        }
        else{
            checkingP = null;
        }

        return false;
    }
    private Piece getKing(boolean opponent){

        Piece king = null;
        for(Piece piece : simPieces){
            if(opponent){
                if(piece.type == Type.King && piece.color != currentColor){
                    king = piece;
                }
            }
            else {
                if(piece.type == Type.King && piece.color == currentColor){
                    king = piece;
                }
            }
        }
            return king;
    }

    private boolean isCheckMate(){

        Piece king = getKing(true);

        if(KingCanMove(king)){
            return false;
        }
        else{
            //But still has a chance
            // Check if another piece can block the check

            int colDiff = Math.abs(checkingP.col - king.col);
            int rowDiff = Math.abs(checkingP.row - king.row);

            if(colDiff == 0){
                // The Checking Piece is attacking vertically
                if(checkingP.row < king.row){
                    //checking piece is above King piece
                    for(int row = checkingP.row; row < king.row ;row++){
                        for(Piece piece: simPieces){
                            if(piece != king && piece.color != currentColor && piece.canMove(checkingP.col, row)){
                                return false;
                            }
                        }
                    }
                }
                if(checkingP.row > king.row){
                    //checking piece is below King piece
                    for(int row = checkingP.row; row > king.row ;row--){
                        for(Piece piece: simPieces){
                            if(piece != king && piece.color != currentColor && piece.canMove(checkingP.col, row)){
                                return false;
                            }
                        }
                    }
                }
            }
            else if(rowDiff == 0){
                // The Checking Piece is attacking horizontally 
                if(checkingP.col < king.col){
                    //checking piece is to the left of the King piece
                    for(int col = checkingP.col; col < king.col; col++){
                        for(Piece piece: simPieces){
                            if(piece != king && piece.color != currentColor && piece.canMove(col, checkingP.row)){
                                return false;
                            }
                        }
                    }
                }
                if(checkingP.col> king.col){
                    //checking piece is to the right of the King piece
                    for(int col = checkingP.col; col > king.col; col--){
                        for(Piece piece: simPieces){
                            if(piece != king && piece.color != currentColor && piece.canMove(col, checkingP.row)){
                                return false;
                            }
                        }
                    }
                }
            }
            else if (rowDiff == colDiff){
                // The Checking Piece is attacking diagonally
                if(checkingP.row < king.row){
                    //checking piece is above King
                    if(checkingP.col < king.col){
                        //checking piece is to the upper left of the King piece
                        for(int col = checkingP.col, row = checkingP.row; col < king.col; col++, row++){
                            for(Piece piece : simPieces){
                                if(piece != king && piece.color != currentColor && piece.canMove(col, row)){
                                    return false;
                                }
                            }
                        }
                    }
                    if(checkingP.col > king.col){
                        //checking piece is to the upper right of the King piece
                        for(int col = checkingP.col, row = checkingP.row; col > king.col; col--, row++){
                            for(Piece piece : simPieces){
                                if(piece != king && piece.color != currentColor && piece.canMove(col, row)){
                                    return false;
                                }
                            }
                        }
                    }
                }
                if(checkingP.col> king.col){
                    //checking piece is below King
                    if(checkingP.col < king.col){
                        //checking piece is to the bottom left of the King piece
                        for(int col = checkingP.col, row = checkingP.row; col < king.col; col++, row--){
                            for(Piece piece : simPieces){
                                if(piece != king && piece.color != currentColor && piece.canMove(col, row)){
                                    return false;
                                }
                            }
                        }
                    }
                    if(checkingP.col > king.col){
                        //checking piece is to the bottom right of the King piece
                        for(int col = checkingP.col, row = checkingP.row; col > king.col; col--, row--){
                            for(Piece piece : simPieces){
                                if(piece != king && piece.color != currentColor && piece.canMove(col, row)){
                                    return false;
                                }
                            }
                        }
                    }
                }                    
            }
        }

        return true;
    }
    private boolean KingCanMove(Piece king){

        if(isValidMove(king, -1,-1)){return true;}
        if(isValidMove(king, 1,-1)){return true;}
        if(isValidMove(king, -1,1)){return true;}
        if(isValidMove(king, 1,1)){return true;}
        if(isValidMove(king, 0,-1)){return true;}
        if(isValidMove(king, 0,1)){return true;}
        if(isValidMove(king, -1,0)){return true;}
        if(isValidMove(king, 1,0)){return true;}

        return false;
    }
    private boolean isValidMove(Piece king, int colPlus, int rowPlus){

        boolean isValidMove = false;

        //temporarily update King's position
        king.col += colPlus;
        king.row += rowPlus;

        if(king.canMove(king.col, king.row)){
            if(king.hittingP != null){
                simPieces.remove(king.hittingP.getIndex());
            }
            if(isillegalMove(king) == false){
                isValidMove = true;
            }
        }

        //reset king pieces position and restore removed pieces
        king.resetPosition();
        copyPieces(pieces, simPieces);

        return isValidMove;
    }


    private void checkCastling(){
        if(castlingP != null){
            if(castlingP.col == 0){
                castlingP.col +=3;
            }
            else if(castlingP.col ==7){
                castlingP.col -=2;
            }
            castlingP.x = castlingP.getX(castlingP.col);
        }
    }

    private void switchTurns() {
        if(currentColor == WHITE) {
            currentColor = BLACK;
            //Reset twoStepped status for black pieces
            for(Piece piece : pieces) {
                if(piece.color == BLACK){
                    piece.twoStepped = false;
                }
            }
        } else {
            currentColor = WHITE;
            //Reset twoStepped status for white pieces
            for(Piece piece : pieces) {
                if(piece.color == WHITE){
                    piece.twoStepped = false;
                }
            }
        }
        activeP = null;
    }

    private boolean canPromote() {
        if(activeP.type == Type.Pawn) {
            if(activeP.color == WHITE && activeP.row == 0 || activeP.color == BLACK && activeP.row == 7) {
                promoPieces.clear();
                promoPieces.add(new Pieces.Queen(activeP.color, 9,5));
                promoPieces.add(new Pieces.Rook(activeP.color,9,2));
                promoPieces.add(new Pieces.Bishop(activeP.color, 9, 4));
                promoPieces.add(new Pieces.Knight(activeP.color, 9,3));
                return true;
            }
        }
        return false;
    }

    private void promoting(){

        if(activeP == null) return; // Prevent NullPointerException

        if(mouse.pressed){
            for(Piece piece : promoPieces){
                if(piece.col == mouse.x / Board.SQUARE_SIZE && piece.row == mouse.y / Board.SQUARE_SIZE){
                    switch(piece.type) {
                        case Queen:
                            simPieces.add(new Queen(activeP.color, activeP.col, activeP.row));
                            break;
                        case Rook:
                            simPieces.add(new Rook(activeP.color, activeP.col, activeP.row));
                            break;
                        case Bishop:
                            simPieces.add(new Bishop(activeP.color, activeP.col, activeP.row));
                            break;
                        case Knight:
                            simPieces.add(new Knight(activeP.color, activeP.col, activeP.row));
                            break;
                        default:
                            break;
                    }
                    
                    simPieces.remove(activeP.getIndex());
                    copyPieces(simPieces, pieces);
                    activeP = null;
                    promotion = false;
                    switchTurns();
                }
            }
        }
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
            if(canMove){
                if(isillegalMove(activeP) || opponentcanCapturedKing()){
                    g2.setColor(Color.red);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                    g2.fillRect(activeP.col * Board.SQUARE_SIZE, activeP.row * Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                }
                else{
                    g2.setColor(Color.green);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                    g2.fillRect(activeP.col * Board.SQUARE_SIZE, activeP.row * Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                }
            }
            activeP.draw(g2);
        }

        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(new Font("Arial", Font.PLAIN, 40));
        g2.setColor(Color.white);

        if(promotion){
            g2.drawString("Promote to:", 840, 150);
            for(Piece piece : promoPieces){
                g2.drawImage(piece.image, piece.getX(piece.col), piece.getY(piece.row),
                        Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
            }
        }
        else{
            if(currentColor == WHITE){
                g2.drawString("White's turn", 820, 550);
                if(checkingP != null && checkingP.color == BLACK){
                    g2.setColor(Color.red);
                    g2.drawString("The King", 840, 650);
                    g2.drawString("is in check!", 840, 700);
                }
            }
            else {
                g2.drawString("Black's turn", 820, 250);
                if(checkingP != null && checkingP.color == WHITE){
                    g2.setColor(Color.red);
                    g2.drawString("The King", 840, 650);
                    g2.drawString("is in check!", 840, 700);
                }
            }
        }
        if(gameOver){
            String s = "";
            if(currentColor == WHITE){
                s = "White Wins!";
            }
            else{
                s ="Black Wins!";
            }
            g2.setFont(new Font("Arial", Font.PLAIN,90));
            g2.setColor(Color.green);
            g2.drawString(s, 200, 420);
        }

    }
}