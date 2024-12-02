package ChessGame.pieces;

import ChessGame.Board;
import java.util.ArrayList;
import java.util.List;


/**
 * The Pawn class represents a pawn in the game of chess.
 * It extends the abstract Piece class and defines the movement rules for pawns.
 */
public class Pawn extends Piece {
    /**
     * Constructor for the Pawn class.
     *
     * @param color    the color of the pawn, either "white" or "black"
     * @param position the initial position of the pawn on the board in chess notation.
     *                 White pawns start on row 2 (e.g., "A2" to "H2") and black pawns start on row 7 (e.g., "A7" to "H7").
     */
    public Pawn(String color, String position) {
        super(color, position);
    }

    /**
     * Returns an array of possible moves for the pawn.
     * Pawns can move one square forward, or two squares forward from their starting position.
     * They capture diagonally.
     *
     * @return an array of possible moves in chess notation
     */
    @Override
    public List<String> possibleMoves(Piece[][] board) {
        List<String> moves = new ArrayList<>();
        int[] position = Board.parsePosition(this.position);
        int row = position[0];
        int col = position[1];

        int direction = this.color.equals("white") ? -1 : 1; // White moves up (-1), Black moves down (+1)

        // Forward movement (one square)
        if (Board.isInBounds(row + direction, col) && board[row + direction][col] == null) {
            moves.add(Board.convertToChessNotation(row + direction, col));
        }

        // Forward movement (two squares) - only if the pawn is on its starting row
        if ((this.color.equals("white") && row == 6) || (this.color.equals("black") && row == 1)) {
            if (board[row + direction][col] == null && board[row + 2 * direction][col] == null) {
                moves.add(Board.convertToChessNotation(row + 2 * direction, col));
            }
        }

        // Diagonal capture (forward-left)
        if (Board.isInBounds(row + direction, col - 1)) {
            Piece targetPiece = board[row + direction][col - 1];
            if (targetPiece != null && !targetPiece.getColor().equals(this.color)) {
                moves.add(Board.convertToChessNotation(row + direction, col - 1));
            }
        }

        // Diagonal capture (forward-right)
        if (Board.isInBounds(row + direction, col + 1)) {
            Piece targetPiece = board[row + direction][col + 1];
            if (targetPiece != null && !targetPiece.getColor().equals(this.color)) {
                moves.add(Board.convertToChessNotation(row + direction, col + 1));
            }
        }

        return moves;
    }

    /**
     * Returns the Unicode symbol for the Pawn.
     * The symbol corresponds to its color: "♙" for white and "♟" for black.
     *
     * @return the Unicode character representing the Pawn.
     */
    @Override
    public String getUnicode() {
        return getColor().equalsIgnoreCase("White") ? "♙" : "♟"; // White pawn is "♙", Black pawn is "♟"
    }
}