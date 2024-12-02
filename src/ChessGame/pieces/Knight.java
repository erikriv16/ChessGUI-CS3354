package ChessGame.pieces;

import ChessGame.Board;
import java.util.ArrayList;
import java.util.List;

/**
 * The Knight class represents a knight in the game of chess.
 * It extends the abstract Piece class and defines the movement rules for knights.
 */
public class Knight extends Piece {

    /**
     * Constructor for the Knight class.
     *
     * @param color the color of the knight, either "white" or "black"
     * @param position the initial position of the knight on the board in chess notation
     *        (e.g., "B1" or "G1" for white and "B8" or "G8" for black)
     */
    public Knight(String color, String position) {
        super(color, position);
    }

    /**
     * Returns an array of possible moves for the knight.
     * Knights move in an "L" shape: two squares in one direction and one square perpendicular.
     *
     * @return an array of possible moves in chess notation
     */
    @Override
    public List<String> possibleMoves(Piece[][] board) {
        List<String> moves = new ArrayList<>();
        int[] pos = Board.parsePosition(position); // Convert chess notation to array indices
        int row = pos[0];
        int col = pos[1];

        // All possible relative moves for a knight
        int[][] moveOffsets = {
                {-2, -1}, {-2, +1}, // Up two, left one or right one
                {-1, -2}, {-1, +2}, // Up one, left two or right two
                {+1, -2}, {+1, +2}, // Down one, left two or right two
                {+2, -1}, {+2, +1}  // Down two, left one or right one
        };

        // Check each possible move
        for (int[] offset : moveOffsets) {
            int newRow = row + offset[0];
            int newCol = col + offset[1];

            // Ensure the move is within bounds
            if (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
                Piece targetPiece = board[newRow][newCol];

                // Ensure the square is empty or contains an opponent's piece
                if (targetPiece == null || !targetPiece.getColor().equals(this.color)) {
                    moves.add(Board.convertToChessNotation(newRow, newCol));
                }
            }
        }

        return moves;
    }

    /**
     * Returns the Unicode symbol for the Knight.
     * The symbol corresponds to its color: "♘" for white and "♞" for black.
     *
     * @return the Unicode character representing the Knight.
     */
    @Override
    public String getUnicode() {
        return getColor().equalsIgnoreCase("White") ? "♘": "♞";
    }
}
