package ChessGame;

import ChessGame.pieces.King;
import ChessGame.pieces.Piece;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

/**
 * The Game class manages the graphical user interface (GUI) and overall control of a chess game.
 * It initializes the chessboard, handles user interactions, and enforces game rules and turn order.
 */
public class Game {
    private final JFrame frame = new JFrame("Chess Game");
    private final Board board = new Board();
    private final JPanel[][] boardSquares = new JPanel[8][8];
    private final Color lightColor = new Color(240, 217, 181); // Light square color
    private final Color darkColor = new Color(181, 136, 99);   // Dark square color
    private final Color highlightColor = Color.LIGHT_GRAY; // Color for selected square
    private boolean dragging = false;                          // Indicates if a piece is being dragged
    private boolean selected = false;
    private Point dragOffset;     
    private Point mousePressPoint;
    private JLabel floatingPieceLabel;
    private JPanel selectedSquarePanel;
    private JLabel selectedPieceLabel;
    private String currentTurn;
    private static Game instance;

    /**
     * Constructs the Game instance, setting up the GUI and initializing the chessboard.
     * The game starts with the "white" player's turn.
     */
    public Game() {
        instance = this; // Set the singleton instance
        currentTurn = "white"; // Set initial turn to white
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setSize(600, 600);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);


        JPanel boardPanel = new JPanel(new GridLayout(8, 8));
        frame.add(boardPanel, BorderLayout.CENTER);

        PieceMovementHandler pieceHandler = new PieceMovementHandler();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                JPanel squarePanel = new JPanel(new BorderLayout());
                squarePanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                squarePanel.setBackground((row + col) % 2 == 0 ? lightColor : darkColor);

                String pieceUnicode = getPieceUnicode(row, col);
                JLabel pieceLabel = new JLabel(pieceUnicode);
                pieceLabel.setFont(new Font("Serif", Font.BOLD, 64));
                pieceLabel.setHorizontalAlignment(SwingConstants.CENTER);

                if (!pieceUnicode.isEmpty()) {
                    squarePanel.add(pieceLabel, BorderLayout.CENTER);
                }

                squarePanel.addMouseListener(pieceHandler);
                squarePanel.addMouseMotionListener(pieceHandler);

                boardSquares[row][col] = squarePanel;
                boardPanel.add(squarePanel);
            }
        }

        System.out.println("DEBUG: Initial Board Display in Console:");
        board.displayBoard(); // Initial display for debugging
    }

    /**
     * Starts the chess game by making the GUI visible.
     */
    public void start() {
        frame.setVisible(true);
    }

    /**
     * Executes a move from one square to another, updating the logical and graphical board.
     *
     * @param from the starting position of the piece in chess notation (e.g., "E2").
     * @param to   the destination position of the piece in chess notation (e.g., "E4").
     */
    private void executeMove(String from, String to) {
        System.out.println("DEBUG: Current turn is " + currentTurn);
        System.out.println("DEBUG: Requested move from " + from + " to " + to);

        Piece movingPiece = board.getPieceAt(Board.parsePosition(from)[0], Board.parsePosition(from)[1]);

        if (board.movePiece(from, to)) {
            // Update GUI after a successful move
            int[] fromCoords = Board.parsePosition(from);
            int[] toCoords = Board.parsePosition(to);
            updateGUI(fromCoords[0], fromCoords[1], toCoords[0], toCoords[1]);

            // Display the updated board state
            board.displayBoard();

            // Check for check or checkmate
            String opponentColor = currentTurn.equals("white") ? "black" : "white";
            if (board.isKingInCheck(opponentColor)) {
                if (board.isCheckmate(opponentColor)) {
                    JOptionPane.showMessageDialog(null, opponentColor + "'s King is in Checkmate!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
                    board.notifyGameOver(currentTurn); // End the game with the current player as the winner
                } else {
                    JOptionPane.showMessageDialog(null, opponentColor + "'s King is in Check!", "Check", JOptionPane.WARNING_MESSAGE);
                }
            }

            // Check other game-ending conditions (e.g., stalemate)
            checkGameEndingConditions();

            // Switch turns
            currentTurn = currentTurn.equals("white") ? "black" : "white";
            System.out.println("DEBUG: Turn switched to " + currentTurn);
        } else {
            // If the move is invalid and the piece is a king, show a warning message
            if (movingPiece instanceof King && board.isKingInCheck(movingPiece.getColor())) {
                JOptionPane.showMessageDialog(null, "Invalid move! The king must move out of check.", "Invalid Move", JOptionPane.ERROR_MESSAGE);
            }
            System.out.println("DEBUG: Move failed. Not switching turns.");
        }
    }

    /**
     * Updates the graphical representation of the board after a move.
     *
     * @param fromRow the row index of the starting square.
     * @param fromCol the column index of the starting square.
     * @param toRow   the row index of the destination square.
     * @param toCol   the column index of the destination square.
     */
    private void updateGUI(int fromRow, int fromCol, int toRow, int toCol) {
        // Get the piece label from the source square
        JLabel pieceLabel = (JLabel) boardSquares[fromRow][fromCol].getComponent(0);

        // Remove the piece from the source square
        boardSquares[fromRow][fromCol].remove(pieceLabel);

        // Add the piece to the target square
        boardSquares[toRow][toCol].removeAll(); // Clear any existing component
        boardSquares[toRow][toCol].add(pieceLabel);

        // Repaint and revalidate the source and target squares
        boardSquares[fromRow][fromCol].revalidate();
        boardSquares[fromRow][fromCol].repaint();
        boardSquares[toRow][toCol].revalidate();
        boardSquares[toRow][toCol].repaint();

        // Ensure the logical board matches the GUI
        board.displayBoard(); // Debug: Display board in console
    }

    /**
     * Retrieves the Unicode symbol for the piece at a specific board position.
     *
     * @param row the row index of the square.
     * @param col the column index of the square.
     * @return the Unicode symbol of the piece, or an empty string if the square is empty.
     */
    private String getPieceUnicode(int row, int col) {
        String position = Board.convertToChessNotation(row, col); // Convert array indices to chess notation
        Piece piece = board.getPieceAt(row, col); // Retrieve the piece at the given position
        return piece != null ? piece.getUnicode() : ""; // Return its Unicode or empty if no piece
    }

    /**
     * Checks for game-ending conditions (checkmate or stalemate) and notifies the players.
     */
    private void checkGameEndingConditions() {
        String opponentColor = currentTurn.equals("white") ? "black" : "white";

        if (board.isCheckmate(opponentColor)) {
            board.notifyGameOver(currentTurn); // Notify the winner
        } else if (board.isStalemate(opponentColor)) {
            notifyStalemate(); // Notify if it's a stalemate
        }
    }

    /**
     * Displays a stalemate notification and offers the option to restart the game.
     */
    private void notifyStalemate() {
        String message = "Stalemate! Would you like to play again?";
        int choice = JOptionPane.showConfirmDialog(null, message, "Game Over", JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            board.resetBoard();
            resetGUI();
        } else {
            System.exit(0); // Exit the game
        }
    }

    /**
     * Resets the graphical user interface to match the initial state of the chessboard.
     */
    public void resetGUI() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                JPanel squarePanel = boardSquares[row][col];
                squarePanel.removeAll(); // Clear any existing pieces

                // Get the updated piece from the logical board
                String pieceUnicode = getPieceUnicode(row, col);
                if (!pieceUnicode.isEmpty()) {
                    JLabel pieceLabel = new JLabel(pieceUnicode);
                    pieceLabel.setFont(new Font("Serif", Font.BOLD, 64));
                    pieceLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    squarePanel.add(pieceLabel); // Add the piece to the GUI square
                }

                squarePanel.revalidate();
                squarePanel.repaint();
            }
        }

        // Reset the turn to white
        currentTurn = "white";
        System.out.println("DEBUG: GUI has been reset.");
    }

    /**
     * Retrieves the singleton instance of the Game class.
     *
     * @return the Game instance.
     */
    public static Game getInstance() {
        return instance;
    }

    /**
     * Resets the game to its initial state, including the board and GUI.
     */
    public void resetGame() {
        board.resetBoard(); // Reset the logical board
        resetGUI();         // Reset the GUI
        currentTurn = "white"; // Reset the turn to white
        System.out.println("DEBUG: Game has been reset. Turn set to white.");
    }

    /**
     * Inner class to handle mouse interactions for moving chess pieces.
     * It listens for mouse events such as press, release, and drag, and updates
     * the board and GUI accordingly.
     */
    private class PieceMovementHandler extends MouseAdapter {
        /**
         * Handles the mouse press event.
         * Selects a piece if it belongs to the current player and prepares for dragging.
         *
         * @param e the MouseEvent triggered by the user.
         */
        @Override
        public void mousePressed(MouseEvent e) {
            JPanel sourceSquare = (JPanel) e.getComponent();
            String position = getSquarePosition(sourceSquare);

           
            if(selected){ // piece is already selected and we click on a square
                if(sourceSquare != selectedSquarePanel){
                    String fromPosition = getSquarePosition(selectedSquarePanel);
                    executeMove(fromPosition, position);
                }
                // Reset selection
                resetSquareColor(selectedSquarePanel);
                selected = false;
                selectedPieceLabel = null;
                selectedSquarePanel = null;
            }
            else {
                if(sourceSquare.getComponentCount()>0){
                    Piece piece = board.getPieceAt(Board.parsePosition(position)[0], Board.parsePosition(position)[1]);

                    if (piece == null || !piece.getColor().equalsIgnoreCase(currentTurn)) {
                    System.out.println("DEBUG: Not the current player's turn.");
                    return;
                    }
                    selectedPieceLabel = (JLabel) sourceSquare.getComponent(0);
                    selectedSquarePanel = sourceSquare;

                    sourceSquare.setBackground(highlightColor);

                    //prepare for dragging
                    floatingPieceLabel = new JLabel(selectedPieceLabel.getText());
                    floatingPieceLabel.setFont(new Font("Serif", Font.BOLD, 64));
                    floatingPieceLabel.setSize(selectedPieceLabel.getSize());
                    // Calculate drag offset
                    dragOffset = SwingUtilities.convertPoint(sourceSquare, e.getPoint(), floatingPieceLabel);
                    dragging = false;  // Dragging not yet active
                    selected = true;

                    System.out.println("DEBUG: Selected piece: " + selectedPieceLabel.getText());
                }
            }
        }

        /**
         * Handles mouse drag events to update the location of the piece being dragged.
         *
         * @param e Mouse event triggered when the mouse is dragged.
         */
        @Override
        public void mouseDragged(MouseEvent e) {
            if (selectedPieceLabel != null && !dragging) {
                dragging = true;

                // Add the floating piece to the layered pane
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(selectedSquarePanel);
                JLayeredPane layeredPane = frame.getLayeredPane();
                layeredPane.add(floatingPieceLabel, JLayeredPane.DRAG_LAYER);
            }

            // Update the location of the floating piece label to follow the cursor
            if (dragging) {
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(e.getComponent());
                JLayeredPane layeredPane = frame.getLayeredPane();

                Point locationOnScreen = e.getLocationOnScreen();
                Point locationOnLayeredPane = SwingUtilities.convertPoint(e.getComponent(),
                        locationOnScreen.x - dragOffset.x,
                        locationOnScreen.y - dragOffset.y,
                        layeredPane);

                floatingPieceLabel.setLocation(locationOnLayeredPane);
                floatingPieceLabel.repaint();
            }
        }

        /**
         * Handles the mouse release event.
         * Attempts to place the dragged piece on the closest valid square.
         *
         * @param e the MouseEvent triggered by the user.
         */
        @Override
        public void mouseReleased(MouseEvent e) {
            if (dragging) {
                if (selectedPieceLabel != null && selectedSquarePanel != null) {
                JPanel targetSquare = findClosestSquare(e.getLocationOnScreen());
                JLayeredPane layeredPane = frame.getLayeredPane();
                if (targetSquare != null && selectedSquarePanel != targetSquare) {
                    String from = getSquarePosition(selectedSquarePanel);
                    String to = getSquarePosition(targetSquare);

                    System.out.println("DEBUG: Dragging piece from " + from + " to " + to);
                    executeMove(from, to);
                } else {
                    System.out.println("DEBUG: Invalid drop location or no movement detected.");
                }

                // Clear selection and remove floating piece label from layered pane
                layeredPane.remove(floatingPieceLabel);
                layeredPane.repaint();

                resetSquareColor(selectedSquarePanel);
                dragging = false;
                selected = false;
                selectedPieceLabel = null;
                selectedSquarePanel = null;
                floatingPieceLabel = null;
            
                }
            }
        }
        

        /**
         * Finds the closest square to the given point on the screen.
         *
         * @param point the location of the mouse pointer on the screen.
         * @return the JPanel representing the closest square, or null if none found.
         */
        private JPanel findClosestSquare(Point point) {
            JPanel closestSquare = null;
            double minDistance = Double.MAX_VALUE;

            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    JPanel square = boardSquares[row][col];
                    Point squareCenter = getSquareCenter(square);

                    double distance = Point.distance(point.x, point.y, squareCenter.x, squareCenter.y);
                    if (distance < minDistance) {
                        minDistance = distance;
                        closestSquare = square;
                    }
                }
            }
            return closestSquare;
        }

        /**
         * Calculates the center point of a square on the board.
         *
         * @param square the JPanel representing the square.
         * @return a Point object representing the center of the square.
         */
        private Point getSquareCenter(JPanel square) {
            Point location = square.getLocationOnScreen();
            return new Point(location.x + square.getWidth() / 2, location.y + square.getHeight() / 2);
        }

        /**
         * Retrieves the chess notation of a given square.
         *
         * @param square the JPanel representing the square.
         * @return the position in chess notation (e.g., "E2"), or null if not found.
         */
        private String getSquarePosition(JPanel square) {
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    if (boardSquares[row][col] == square) {
                        return toChessNotation(row, col);
                    }
                }
            }
            return null;
        }

        /**
         * Resets the square to its original state if the drag-and-drop action is invalid.
         */
        private void resetSquare() {
            if (selectedSquarePanel != null && selectedPieceLabel != null) {
                selectedSquarePanel.add(selectedPieceLabel);
                selectedSquarePanel.revalidate();
                selectedSquarePanel.repaint();
            }
            dragging = false;
            selectedPieceLabel = null;
            selectedSquarePanel = null;
            floatingPieceLabel = null;
        }
        private void resetSquareColor(JPanel square) {
            int row = -1, col = -1;
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (boardSquares[i][j] == square) {
                        row = i;
                        col = j;
                        break;
                    }
                }
            }
            if (row != -1 && col != -1) {
                square.setBackground((row + col) % 2 == 0 ? lightColor : darkColor);
            }
        }
    }

    /**
     * Converts board indices to chess notation (e.g., 0, 0 -> "A8").
     *
     * @param row the row index (0-based).
     * @param col the column index (0-based).
     * @return the position in chess notation (e.g., "E2").
     */
    private String toChessNotation(int row, int col) {
        char file = (char) ('A' + col);
        int rank = 8 - row;
        return file + String.valueOf(rank);
    }

}