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

    /**
     * Checks if a move to the specified row and column is valid.
     * A valid move must be within the bounds of the board and the destination square must be empty.
     *
     * @param row   the row index of the target square.
     * @param col   the column index of the target square.
     * @param board a 2D array representing the chessboard.
     * @return true if the move is valid; false otherwise.
     */
    protected boolean isValidMove(int row, int col, Piece[][] board) {
        return row >= 0 && row < 8 && col >= 0 && col < 8 &&
                (board[row][col] == null);
    }

    /**
     * Checks if a square is occupied by an opponent's piece.
     *
     * @param row   the row index of the square.
     * @param col   the column index of the square.
     * @param board a 2D array representing the chessboard.
     * @return true if the square is occupied by an opponent's piece; false otherwise.
     */
    protected boolean isOpponentPiece(int row, int col, Piece[][] board) {
        if (board[row][col] == null) {
            return false; // No piece at the specified location
        }

        boolean isOpponent = !board[row][col].getColor().equals(this.color);
        System.out.println("DEBUG: Checking opponent piece at (" + row + ", " + col + "): " +
                board[row][col].getColor() + " -> Opponent: " + isOpponent);
        return isOpponent;
    }

    /**
     * Checks if a square is empty.
     *
     * @param row   the row index of the square.
     * @param col   the column index of the square.
     * @param board a 2D array representing the chessboard.
     * @return true if the square is empty; false otherwise.
     */
    protected boolean isEmptySquare(int row, int col, Piece[][] board) {
        boolean isEmpty = (board[row][col] == null);
        System.out.println("DEBUG: Checking if square at (row: " + row + ", col: " + col + ") is empty: " + isEmpty);
        return isEmpty;
    }

    /**
     * Converts a row and column index into chess notation (e.g., 0, 0 -> "A1").
     *
     * @param row the row index (0-based).
     * @param col the column index (0-based).
     * @return the corresponding chess notation string.
     */
    protected String convertToChessNotation(int row, int col) {
        return (char) ('A' + col) + "" + (row + 1);
    }

    /**
     * Parses a chess notation string into row and column indices (e.g., "A1" -> {0, 0}).
     *
     * @param position the chess notation string.
     * @return an array where the first element is the row index and the second is the column index.
     */
    protected int[] parsePosition(String position) {
        char col = position.charAt(0);
        char row = position.charAt(1);
        return new int[]{row - '1', col - 'A'};
    }

    /**
     * Checks if a square is within the bounds of the chessboard.
     *
     * @param row the row index of the square.
     * @param col the column index of the square.
     * @return true if the square is within bounds; false otherwise.
     */
    protected boolean isWithinBounds(int row, int col) {
        boolean inBounds = row >= 0 && row < 8 && col >= 0 && col < 8;
        System.out.println("DEBUG: Checking bounds for (row: " + row + ", col: " + col + "): " + inBounds);
        return inBounds;
    }

}
