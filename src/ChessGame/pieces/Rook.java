package ChessGame.pieces;

import ChessGame.Board;
import java.util.ArrayList;
import java.util.List;

/**
 * The Rook class represents a rook in the game of chess.
 * It extends the abstract Piece class and defines the movement rules for rooks.
 */
public class Rook extends Piece {

    /**
     * Constructor for the Rook class.
     *
     * @param color the color of the rook, either "white" or "black"
     * @param position the initial position of the rook on the board in chess notation
     *        (e.g., "A1" or "H1" for white and "A8" or "H8" for black).
     */
    public Rook(String color, String position) {
        super(color, position);
    }

    /**
     * Returns an array of possible moves for the rook.
     * Rooks can move any number of squares horizontally or vertically,
     * and can capture opponent pieces as long as there are no pieces of the same color blocking their path.
     *
     * @return an array of possible moves in chess notation
     */
    @Override
    public List<String> possibleMoves(Piece[][] board) {
        List<String> moves = new ArrayList<>();
        int[] pos = Board.parsePosition(position);
        int row = pos[0];
        int col = pos[1];

        // Directions: up, down, left, right
        int[][] directions = {
                {-1, 0}, // Up
                {1, 0},  // Down
                {0, -1}, // Left
                {0, 1}   // Right
        };

        // Iterate through each direction
        for (int[] direction : directions) {
            int newRow = row + direction[0];
            int newCol = col + direction[1];

            // Continue moving in the current direction until blocked
            while (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
                Piece targetPiece = board[newRow][newCol];

                if (targetPiece == null) {
                    // Square is empty, add to possible moves
                    moves.add(Board.convertToChessNotation(newRow, newCol));
                } else {
                    // Square is occupied, check if it's an opponent's piece
                    if (!targetPiece.getColor().equals(this.color)) {
                        moves.add(Board.convertToChessNotation(newRow, newCol));
                    }
                    // Stop exploring this direction after encountering any piece
                    break;
                }

                // Move further in the current direction
                newRow += direction[0];
                newCol += direction[1];
            }
        }

        return moves;
    }

    /**
     * Returns the Unicode symbol for the Rook.
     * The symbol corresponds to its color: "♖" for white and "♜" for black.
     *
     * @return the Unicode character representing the Rook.
     */
    @Override
    public String getUnicode() {
        return getColor().equalsIgnoreCase("White") ? "♖": "♜" ;
    }

}
