package ChessGame.pieces;

import ChessGame.Board;
import java.util.ArrayList;
import java.util.List;

/**
 * The King class represents a king in the game of chess.
 * It extends the abstract Piece class and defines the movement rules for the king.
 */
public class King extends Piece {

    /**
     * Constructor for the King class.
     *
     * @param color the color of the king, either "white" or "black"
     * @param position the initial position of the king on the board in chess notation (e.g., "E1" for white or "E8" for black)
     */
    public King(String color, String position) {
        super(color, position);
    }

    /**
     * Returns an array of possible moves for the king.
     * Kings can move one square in any direction (horizontally, vertically, or diagonally)
     * and capture opponent pieces, as long as they do not move into a square that is under attack
     * (i.e., a square that would put the king in check).
     *
     * @return an array of possible moves in chess notation
     */
    @Override
    public List<String> possibleMoves(Piece[][] board) {
        List<String> moves = new ArrayList<>();
        int[] pos = Board.parsePosition(position);
        int row = pos[0];
        int col = pos[1];

        // Directions: one square in each direction
        int[][] directions = {
                {-1, -1}, {-1, 0}, {-1, 1}, // Up-left, up, up-right
                {0, -1},         {0, 1},   // Left, right
                {1, -1}, {1, 0}, {1, 1}    // Down-left, down, down-right
        };

        for (int[] direction : directions) {
            int newRow = row + direction[0];
            int newCol = col + direction[1];

            // Ensure the move is within bounds
            if (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
                Piece targetPiece = board[newRow][newCol];

                // Ensure the square is empty or contains an opponent's piece
                if (targetPiece == null || !targetPiece.getColor().equals(this.color)) {
                    String move = Board.convertToChessNotation(newRow, newCol);

                    // Simulate the move and check if it puts the king in check
                    Piece[][] boardCopy = Board.copyBoard(board);
                    boardCopy[newRow][newCol] = this;
                    boardCopy[row][col] = null;

                    if (!isKingInCheckAfterMove(boardCopy)) {
                        moves.add(move);
                    }
                }
            }
        }

        return moves;
    }

    // Helper to check if the king is in check after a move
    private boolean isKingInCheckAfterMove(Piece[][] boardCopy) {
        Board tempBoard = new Board(boardCopy); // Create a temporary board with the modified state
        return tempBoard.isKingInCheck(this.color);
    }

    /**
     * Returns the Unicode symbol for the king.
     * The symbol corresponds to its color: "♔" for white and "♚" for black.
     *
     * @return the Unicode character representing the King.
     */
    @Override
    public String getUnicode() {
        return getColor().equalsIgnoreCase("White") ?  "♔": "♚";
    }
}