package ChessGame.pieces;

import ChessGame.Board;
import java.util.ArrayList;
import java.util.List;


/**
 * The Bishop class represents a bishop in the game of chess.
 * It extends the abstract Piece class and defines the movement rules for the bishop.
 */
public class Bishop extends Piece {

    /**
     * Constructor for the Bishop class.
     *
     * @param color the color of the bishop, either "white" or "black"
     * @param position the initial position of the bishop on the board in chess notation
     *        (e.g., "C1" or "F1" for white and "C8" or "F8" for black)
     */

    public Bishop(String color, String position) {
        super(color, position);
    }

    /**
     * Calculates and returns a list of all possible moves for the bishop based on its
     * current position and the state of the chessboard.
     *
     * Bishops can move diagonally in any direction without limit, provided there are no
     * pieces of the same color blocking the path. If an opponent's piece is encountered,
     * the bishop can capture it, but cannot move beyond it.
     *
     * @param board a 2D array representing the chessboard, where each element is a Piece
     *              or null if the square is empty.
     * @return a list of possible moves in chess notation (e.g., "D4", "F6").
     */
    @Override
    public List<String> possibleMoves(Piece[][] board) {
        List<String> moves = new ArrayList<>();
        int[] pos = Board.parsePosition(position);
        int row = pos[0];
        int col = pos[1];

        // Directions: top-left, top-right, bottom-left, bottom-right
        int[][] directions = {
                {-1, -1}, {-1, 1}, // Diagonal up-left, up-right
                {1, -1}, {1, 1}    // Diagonal down-left, down-right
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
     * Returns the Unicode symbol for the bishop.
     * The symbol corresponds to its color: "♗" for white and "♝" for black.
     *
     * @return the Unicode character representing the bishop.
     */
    @Override
    public String getUnicode() {
        return getColor().equalsIgnoreCase("White") ?  "♗": "♝";
    }
}
