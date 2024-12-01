package ChessGame.pieces;

import java.util.List;


/**
 * Abstract class representing a chess piece.
 * Each piece has a color and a position on the board.
 */
public abstract class Piece {
    protected String color;
    protected String position;

    /**
     * Constructor for the Piece class.
     *
     * @param color the color of the piece, either "white" or "black"
     * @param position the initial position of the piece in chess notation (e.g., "E2")
     */
    public Piece(String color, String position) {
        this.color = color;
        this.position = position;
    }

    /**
     * Gets the color of the piece.
     *
     * @return the color of the piece, either "white" or "black"
     */
    public String getColor() {
        return color;
    }

    /**
     * Gets the current position of the piece on the board.
     *
     * @return the current position in chess notation (e.g., "E2")
     */
    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    // Abstract method to be implemented by each specific piece type
    public abstract List<String> possibleMoves(Piece[][] board);

    /**
     * Method to return the Unicode representation of the piece.
     * This should be overridden by each subclass.
     */
    public abstract String getUnicode();

}
