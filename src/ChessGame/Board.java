package ChessGame;

import ChessGame.pieces.*;

import javax.swing.*;
import java.util.List;


/**
 * The Board class represents the chessboard and manages the state of the game.
 * It initializes the board, handles piece movement, and enforces game rules such as
 * check, checkmate, and stalemate.
 */
public class Board {
    private Piece[][] pieces = new Piece[8][8]; // The array representing the board
    private String currentPlayer = "white"; // "white" or "black"
    private final int SIZE = 8;
    private boolean gameEnded = false;
    private String gameEndReason = ""; // Reason for the game ending

    /**
     * Default constructor for the Board class.
     * Initializes the chessboard with the standard starting positions.
     */
    public Board() {
        this.pieces = new Piece[SIZE][SIZE];
        initializeBoard();
    }

    /**
     * Overloaded constructor for initializing the board with a given state.
     *
     * @param initialState a 2D array of Piece objects representing the initial state of the board.
     */
    public Board(Piece[][] initialState) {
        this.pieces = new Piece[8][8];
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (initialState[row][col] != null) {
                    this.pieces[row][col] = createPieceCopy(initialState[row][col]);
                }
            }
        }
    }

    /**
     * Initializes the board to the standard starting positions for all pieces.
     */
    public void initializeBoard() {
        // Initialize white pieces
        pieces[7][0] = new Rook("white", convertToChessNotation(7, 0));
        System.out.println("DEBUG: Added White Rook at A8");

        pieces[7][1] = new Knight("white", convertToChessNotation(7, 1));
        System.out.println("DEBUG: Added White Knight at B8");

        pieces[7][2] = new Bishop("white", convertToChessNotation(7, 2));
        System.out.println("DEBUG: Added White Bishop at C8");

        pieces[7][3] = new Queen("white", convertToChessNotation(7, 3));
        System.out.println("DEBUG: Added White Queen at D8");

        pieces[7][4] = new King("white", convertToChessNotation(7, 4));
        System.out.println("DEBUG: Added White King at E8");

        pieces[7][5] = new Bishop("white", convertToChessNotation(7, 5));
        System.out.println("DEBUG: Added White Bishop at F8");

        pieces[7][6] = new Knight("white", convertToChessNotation(7, 6));
        System.out.println("DEBUG: Added White Knight at G8");

        pieces[7][7] = new Rook("white", convertToChessNotation(7, 7));
        System.out.println("DEBUG: Added White Rook at H8");

        for (int col = 0; col < 8; col++) {
            pieces[6][col] = new Pawn("white", convertToChessNotation(6, col));
            System.out.println("DEBUG: Added White Pawn at " + convertToChessNotation(6, col));
        }

        // Initialize black pieces
        pieces[0][0] = new Rook("black", convertToChessNotation(0, 0));
        System.out.println("DEBUG: Added Black Rook at A1");

        pieces[0][1] = new Knight("black", convertToChessNotation(0, 1));
        System.out.println("DEBUG: Added Black Knight at B1");

        pieces[0][2] = new Bishop("black", convertToChessNotation(0, 2));
        System.out.println("DEBUG: Added Black Bishop at C1");

        pieces[0][3] = new Queen("black", convertToChessNotation(0, 3));
        System.out.println("DEBUG: Added Black Queen at D1");

        pieces[0][4] = new King("black", convertToChessNotation(0, 4));
        System.out.println("DEBUG: Added Black King at E1");

        pieces[0][5] = new Bishop("black", convertToChessNotation(0, 5));
        System.out.println("DEBUG: Added Black Bishop at F1");

        pieces[0][6] = new Knight("black", convertToChessNotation(0, 6));
        System.out.println("DEBUG: Added Black Knight at G1");

        pieces[0][7] = new Rook("black", convertToChessNotation(0, 7));
        System.out.println("DEBUG: Added Black Rook at H1");

        for (int col = 0; col < 8; col++) {
            pieces[1][col] = new Pawn("black", convertToChessNotation(1, col));
            System.out.println("DEBUG: Added Black Pawn at " + convertToChessNotation(1, col));
        }

        // Debug: Display the board state after initialization
        System.out.println("DEBUG: Initial Board State After Setup:");
        displayBoard();
    }

    /**
     * Retrieves the piece at a specific position on the board.
     *
     * @param row the row index (0-based).
     * @param col the column index (0-based).
     * @return the Piece at the given position, or null if the square is empty.
     */
    public Piece getPieceAt(int row, int col) {
        if (row >= 0 && row < 8 && col >= 0 && col < 8) {
            return pieces[row][col]; // Return the piece at the specified position
        }
        return null; // Return null if the position is out of bounds
    }

    /**
     * Attempts to move a piece from one position to another.
     *
     * @param from the starting position in chess notation (e.g., "E2").
     * @param to   the destination position in chess notation (e.g., "E4").
     * @return true if the move is successful; false otherwise.
     */
    public boolean movePiece(String from, String to) {
        int[] fromPos = parsePosition(from);
        int[] toPos = parsePosition(to);

        int fromRow = fromPos[0];
        int fromCol = fromPos[1];
        int toRow = toPos[0];
        int toCol = toPos[1];

        Piece movingPiece = pieces[fromRow][fromCol];
        if (movingPiece == null) {
            System.out.println("DEBUG: No piece at the starting square.");
            return false;
        }

        // Validate the move
        List<String> validMoves = movingPiece.possibleMoves(pieces);
        if (!validMoves.contains(to)) {
            System.out.println("DEBUG: Invalid move for piece " + movingPiece.getClass().getSimpleName());
            return false;
        }

        // Proceed with the move
        Piece targetPiece = pieces[toRow][toCol];
        pieces[toRow][toCol] = movingPiece;
        pieces[fromRow][fromCol] = null;
        movingPiece.setPosition(to);

        // Check for check condition and revert if necessary
        boolean kingInCheck = isKingInCheck(movingPiece.getColor());
        if (kingInCheck) {
            pieces[fromRow][fromCol] = movingPiece;
            pieces[toRow][toCol] = targetPiece;
            System.out.println("DEBUG: Move leaves the king in check.");
            return false;
        }

        // Additional game state updates (e.g., switch turns) can follow here.
        return true;
    }

    /**
     * Checks if a king of the specified color is in check.
     *
     * @param color the color of the king to check ("white" or "black").
     * @return true if the king is in check; false otherwise.
     */
    public boolean isKingInCheck(String color) {
        int[] kingPosition = findKing(color);
        if (kingPosition == null) {
            return false; // No king found (should not happen in a valid game)
        }

        int kingRow = kingPosition[0];
        int kingCol = kingPosition[1];

        // Check if any opposing piece can attack the king's position
        String opponentColor = color.equals("white") ? "black" : "white";
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = pieces[row][col];
                if (piece != null && piece.getColor().equals(opponentColor)) {
                    List<String> moves = piece.possibleMoves(pieces);
                    for (String move : moves) {
                        int[] movePos = parsePosition(move);
                        if (movePos[0] == kingRow && movePos[1] == kingCol) {
                            return true; // King is under attack
                        }
                    }
                }
            }
        }

        return false; // King is safe
    }

    /**
     * Finds the position of the king of a specified color.
     *
     * @param color the color of the king to locate.
     * @return an array with the row and column indices of the king, or null if not found.
     */
    private int[] findKing(String color) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = pieces[row][col];
                if (piece != null && piece instanceof King && piece.getColor().equals(color)) {
                    return new int[]{row, col};
                }
            }
        }
        return null; // King not found (should not happen in a valid game)
    }

    // ########### POSSIBLE REDUNDANCY W/ isValidMove FROM PIECE##########
    /**
     * Checks if a position is within the bounds of the chessboard.
     *
     * @param row the row index.
     * @param col the column index.
     * @return true if the position is within bounds; false otherwise.
     */
    public static boolean isInBounds(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    // ##### POSSIBLE REDUNDANCY FROM PIECE - DETERMINE WHERE CODE SHOULD BE  #########
    /**
     * Converts board indices to chess notation (e.g., 0, 0 -> "A8").
     *
     * @param row the row index.
     * @param col the column index.
     * @return the corresponding position in chess notation.
     */
    public static String convertToChessNotation(int row, int col) {
        char file = (char) ('A' + col);
        int rank = 8 - row;
        return "" + file + rank;
    }

    // ##### POSSIBLE REDUNDANCY FROM PIECE - DETERMINE WHERE CODE SHOULD BE  #########
    /**
     * Parses a position in chess notation (e.g., "A8") into row and column indices.
     *
     * @param position the chess notation string.
     * @return an array with the row and column indices.
     */
    public static int[] parsePosition(String position) {
        int col = position.charAt(0) - 'A'; // Column from file
        int row = 8 - Character.getNumericValue(position.charAt(1)); // Row from rank
        return new int[]{row, col};
    }

    /**
     * Displays the current state of the board in the console.
     */
    public void displayBoard() {
        System.out.println("DEBUG: Current Board State:");
        System.out.println("  A B C D E F G H");
        for (int row = 0; row < 8; row++) {
            System.out.print((8 - row) + " "); // Print row numbers
            for (int col = 0; col < 8; col++) {
                if (pieces[row][col] == null) {
                    System.out.print("- ");
                } else {
                    System.out.print(pieces[row][col].getUnicode() + " ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    /**
     * Checks if the specified color is in checkmate.
     *
     * @param color the color of the player to check for checkmate ("white" or "black").
     * @return true if the player is in checkmate; false otherwise.
     */
    public boolean isCheckmate(String color) {
        if (!isKingInCheck(color)) {
            return false; // Not in check, so not a checkmate
        }

        // Check if the player has any valid moves left
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = pieces[row][col];
                if (piece != null && piece.getColor().equals(color)) {
                    List<String> validMoves = piece.possibleMoves(pieces);
                    for (String move : validMoves) {
                        int[] toPos = parsePosition(move);
                        int toRow = toPos[0];
                        int toCol = toPos[1];

                        // Simulate the move
                        Piece targetPiece = pieces[toRow][toCol];
                        pieces[toRow][toCol] = piece;
                        pieces[row][col] = null;

                        boolean kingStillInCheck = isKingInCheck(color);

                        // Revert the move
                        pieces[row][col] = piece;
                        pieces[toRow][toCol] = targetPiece;

                        if (!kingStillInCheck) {
                            return false; // At least one valid move exists
                        }
                    }
                }
            }
        }

        return true; // No valid moves left, so it's checkmate
    }

    /**
     * Notifies the players that the game is over and provides the option to play again.
     *
     * @param winner the color of the winning player ("white" or "black").
     */
    public void notifyGameOver(String winner) {
        String message = winner + " wins! Would you like to play again?";
        int choice = JOptionPane.showConfirmDialog(null, message, "Game Over", JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            System.out.println("DEBUG: Player chose to play again.");
            Game.getInstance().resetGame();
        } else {
            System.out.println("DEBUG: Player chose to exit.");
            System.exit(0);
        }
    }

    /**
     * Resets the board to the initial game state for a new game.
     */
    public void resetBoard() {
        // Clear all squares on the board
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                pieces[row][col] = null; // Clear the square
            }
        }

        // Reinitialize all pieces to their starting positions
        initializeBoard();

        // Debugging: Display the board state after reset
        System.out.println("DEBUG: Logical board has been reset.");
        displayBoard();
    }

    /**
     * Checks if the specified color is in stalemate.
     *
     * @param color the color of the player to check for stalemate ("white" or "black").
     * @return true if the player is in stalemate; false otherwise.
     */
    public boolean isStalemate(String color) {
        // The king is not in check
        if (isKingInCheck(color)) {
            return false;
        }

        // Check if the player has any valid moves
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = pieces[row][col];
                if (piece != null && piece.getColor().equals(color)) {
                    List<String> validMoves = piece.possibleMoves(pieces);
                    for (String move : validMoves) {
                        int[] toPos = parsePosition(move);
                        int toRow = toPos[0];
                        int toCol = toPos[1];

                        // Simulate the move
                        Piece targetPiece = pieces[toRow][toCol];
                        pieces[toRow][toCol] = piece;
                        pieces[row][col] = null;

                        boolean kingStillInCheck = isKingInCheck(color);

                        // Revert the move
                        pieces[row][col] = piece;
                        pieces[toRow][toCol] = targetPiece;

                        // If the king is not in check after the move, stalemate is false
                        if (!kingStillInCheck) {
                            return false;
                        }
                    }
                }
            }
        }

        // If no valid moves exist and the king is not in check, it's stalemate
        return true;
    }

    /**
     * Creates a deep copy of the given board.
     *
     * @param board a 2D array of Piece objects representing the current board state.
     * @return a new 2D array of Piece objects with the same state as the original board.
     */
    public static Piece[][] copyBoard(Piece[][] board) {
        Piece[][] boardCopy = new Piece[8][8];
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board[row][col];
                if (piece != null) {
                    // Create a new piece with the same type, color, and position
                    boardCopy[row][col] = createPieceCopy(piece);
                }
            }
        }
        return boardCopy;
    }

    /**
     * Creates a copy of a Piece object based on its type, color, and position.
     *
     * @param piece the Piece object to copy.
     * @return a new Piece object of the same type, color, and position as the original.
     */
    private static Piece createPieceCopy(Piece piece) {
        String color = piece.getColor();
        String position = piece.getPosition();

        if (piece instanceof Pawn) {
            return new Pawn(color, position);
        } else if (piece instanceof Rook) {
            return new Rook(color, position);
        } else if (piece instanceof Knight) {
            return new Knight(color, position);
        } else if (piece instanceof Bishop) {
            return new Bishop(color, position);
        } else if (piece instanceof Queen) {
            return new Queen(color, position);
        } else if (piece instanceof King) {
            return new King(color, position);
        }
        return null; // Should not reach here if all piece types are covered
    }


    // ##### POSSIBLE REDUNDANCY UNUSED CODE  #########
    private boolean isSafeMove(String from, String to) {
        int[] fromPos = parsePosition(from);
        int[] toPos = parsePosition(to);

        Piece movingPiece = pieces[fromPos[0]][fromPos[1]];
        Piece capturedPiece = pieces[toPos[0]][toPos[1]];

        pieces[toPos[0]][toPos[1]] = movingPiece;
        pieces[fromPos[0]][fromPos[1]] = null;

        boolean safe = !isKingInCheck(movingPiece.getColor());

        pieces[fromPos[0]][fromPos[1]] = movingPiece;
        pieces[toPos[0]][toPos[1]] = capturedPiece;

        return safe;
    }

    public boolean isGameEnded() {
        return false;
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public String getGameEndReason() {
        return "Game Ended";
    }

    public Piece[][] getPieces() {
        return pieces;
    }

    private void notifyCheck(String color) {
        String message = color.equals("white") ? "White King is in Check!" : "Black King is in Check!";
        System.out.println("DEBUG: " + message);

        // Show a dialog box for notification
        JOptionPane.showMessageDialog(null, message, "King in Check!", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Updates the board after a move.
     */
    public void updateBoard(String from, String to) {
        int[] fromPos = parsePosition(from);
        int[] toPos = parsePosition(to);

        int fromRow = fromPos[0];
        int fromCol = fromPos[1];
        int toRow = toPos[0];
        int toCol = toPos[1];

        Piece movingPiece = pieces[fromRow][fromCol];
        if (movingPiece != null) {
            pieces[toRow][toCol] = movingPiece; // Move the piece
            pieces[fromRow][fromCol] = null;   // Clear the original square
            movingPiece.setPosition(to);       // Update the piece's position
            System.out.println("DEBUG: Updated board for move from " + from + " to " + to);
        } else {
            System.out.println("DEBUG: No piece to move from " + from);
        }
    }

//    /**
//     * Checks if a square is occupied by an opponent's piece.
//     *
//     * @param row   the row index of the square.
//     * @param col   the column index of the square.
//     * @param board a 2D array representing the chessboard.
//     * @return true if the square is occupied by an opponent's piece; false otherwise.
//     */
//    protected boolean isOpponentPiece(int row, int col, Piece[][] board) {
//        if (board[row][col] == null) {
//            return false; // No piece at the specified location
//        }
//
//        boolean isOpponent = !board[row][col].getColor().equals(this.color);
//        System.out.println("DEBUG: Checking opponent piece at (" + row + ", " + col + "): " +
//                board[row][col].getColor() + " -> Opponent: " + isOpponent);
//        return isOpponent;
//    }

}
