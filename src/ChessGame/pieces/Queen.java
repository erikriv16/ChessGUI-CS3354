package ChessGame.pieces;

import ChessGame.Board;
import java.util.ArrayList;
import java.util.List;

/**
 * The Queen class represents a queen in the game of chess.
 * It extends the abstract Piece class and defines the movement rules for the queen.
 * The queen is the most powerful piece on the board and can move any number of squares
 * horizontally, vertically, or diagonally.
 */
public class Queen extends Piece {

    /**
     * Constructor for the Queen class.
     *
     * @param color the color of the queen, either "white" or "black"
     * @param position the initial position of the queen on the board in chess notation
     *        (e.g., "D1" for white or "D8" for black).
     */
    public Queen(String color, String position) {
        super(color, position);
    }

    /**
     * Returns an array of possible moves for the queen.
     * The queen can move any number of squares in any direction (horizontally, vertically, or diagonally),
     * and can capture an opponent's piece as long as there are no pieces of the same color blocking its path.
     *
     * @return an array of possible moves in chess notation
     */
    @Override
    public List<String> possibleMoves(Piece[][] board) {
        List<String> moves = new ArrayList<>();
        int[] pos = Board.parsePosition(position);
        int row = pos[0];
        int col = pos[1];

        // Directions: combine rook (straight) and bishop (diagonal) directions
        int[][] directions = {
                {-1, 0}, {1, 0}, {0, -1}, {0, 1},   // Straight directions (up, down, left, right)
                {-1, -1}, {-1, 1}, {1, -1}, {1, 1}  // Diagonal directions (up-left, up-right, down-left, down-right)
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
     * Returns the Unicode symbol for the Queen.
     * The symbol corresponds to its color: "♕" for white and "♛" for black.
     *
     * @return the Unicode character representing the Queen.
     */
    @Override
    public String getUnicode() {
        return getColor().equalsIgnoreCase("White") ? "♕" : "♛";
    }
}