import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


/**
 * ChessBoardWithPiecesExample is a simple GUI-based representation of a chessboard.
 * It displays an 8x8 chessboard with pieces in their initial positions, allowing for
 * click-and-move and drag-and-drop functionality for each piece.
 */
public class ChessBoardWithPiecesExample {

    // Unicode characters for chess pieces
    private static final String[] UNICODE_PIECES = {
            "♔", "♕", "♖", "♗", "♘", "♙", "♚", "♛", "♜", "♝", "♞", "♟"
    };
    private JPanel[][] boardSquares = new JPanel[8][8];  // Chessboard squares
    private JPanel selectedSquarePanel;                  // Square of selected piece
    private JLabel selectedPieceLabel;                   // Label for the selected piece
    private JLabel floatingPieceLabel;                   // Temporary label for dragging a piece
    private Point dragOffset;                            // Offset for dragging
    private boolean dragging = false;                    // Indicates if a piece is being dragged
    private final Color highlightColor = Color.LIGHT_GRAY; // Color for selected square
    private final Color originalLightColor = new Color(240, 217, 181); // Light square color
    private final Color originalDarkColor = new Color(181, 136, 99);   // Dark square color

    /**
     * Initializes the chessboard GUI with pieces in their starting positions.
     */
    public ChessBoardWithPiecesExample() {
        JFrame frame = new JFrame("Chess Game");                // Main Outer Window
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel boardPanel = new JPanel(new GridLayout(8, 8));   // Container inside Main Window
        boardPanel.setPreferredSize(new Dimension(600, 600));

        // Instantiate a single instance of the piece movement handler
        PieceMovementHandler pieceHandler = new PieceMovementHandler();

        // Create the chessboard grid with initial piece placement
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                JPanel squarePanel = new JPanel(new BorderLayout());      // Each Square on the Chessboard
                squarePanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                squarePanel.setBackground((row + col) % 2 == 0 ? originalLightColor : originalDarkColor);

                JLabel pieceLabel = new JLabel(getPieceUnicode(row, col));  // Chess Peices
                pieceLabel.setFont(new Font("Serif", Font.BOLD, 64));
                pieceLabel.setHorizontalAlignment(SwingConstants.CENTER);

                squarePanel.add(pieceLabel, BorderLayout.CENTER);
                squarePanel.addMouseListener(pieceHandler);         // Attach the handler to each square
                squarePanel.addMouseMotionListener(pieceHandler);   // Needed for drag events

                boardSquares[row][col] = squarePanel;               // Store each square in the grid
                boardPanel.add(squarePanel);
            }
        }

        frame.add(boardPanel, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Returns the Unicode character for a piece based on its initial board position.
     *
     * @param row The row on the board (0 to 7).
     * @param col The column on the board (0 to 7).
     * @return The Unicode character for the chess piece in that position, or an empty string for an empty square.
     */
    private String getPieceUnicode(int row, int col) {
        if (row == 1) return UNICODE_PIECES[5];  // White Pawns
        if (row == 6) return UNICODE_PIECES[11]; // Black Pawns
        if (row == 0 || row == 7) {
            int offset = (row == 0) ? 0 : 6;
            return switch (col) {
                case 0, 7 -> UNICODE_PIECES[2 + offset];  // Rook
                case 1, 6 -> UNICODE_PIECES[4 + offset];  // Knight
                case 2, 5 -> UNICODE_PIECES[3 + offset];  // Bishop
                case 3 -> (row == 0) ? UNICODE_PIECES[1] : UNICODE_PIECES[7];  // Queen
                case 4 -> (row == 0) ? UNICODE_PIECES[0] : UNICODE_PIECES[6];  // King
                default -> "";
            };
        }
        return "";  // Empty square
    }

    /**
     * Inner class to handle click and drag events for piece movement.
     */
    private class PieceMovementHandler extends MouseAdapter {

        /**
         * Handles mouse press events to initiate piece selection and dragging.
         *
         * @param e Mouse event triggered when the mouse is pressed.
         */
        @Override
        public void mousePressed(MouseEvent e) {
            JPanel sourceSquare = (JPanel) e.getComponent();
            if (sourceSquare.getComponentCount() > 0) {  // Square has a piece
                selectedPieceLabel = (JLabel) sourceSquare.getComponent(0);
                selectedSquarePanel = sourceSquare;

                // Highlight the selected square
                sourceSquare.setBackground(highlightColor);

                // Prepare for dragging
                floatingPieceLabel = new JLabel(selectedPieceLabel.getText());
                floatingPieceLabel.setFont(new Font("Serif", Font.BOLD, 64));
                floatingPieceLabel.setSize(selectedPieceLabel.getSize());

                // Calculate drag offset
                dragOffset = SwingUtilities.convertPoint(sourceSquare, e.getPoint(), floatingPieceLabel);
                dragging = false;  // Dragging not yet active

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

                // Remove the piece from the original square at the start of dragging
                selectedSquarePanel.remove(selectedPieceLabel);
                selectedSquarePanel.revalidate();
                selectedSquarePanel.repaint();

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
         * Handles mouse release events to place the piece in the target square.
         *
         * @param e Mouse event triggered when the mouse is released.
         */
        @Override
        public void mouseReleased(MouseEvent e) {
            if (selectedPieceLabel != null && selectedSquarePanel != null) {
                // Retrieve the frame and layered pane
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(selectedSquarePanel);
                JLayeredPane layeredPane = frame.getLayeredPane();

                // Calculate the center point of the floating label
                Point releaseLocation = floatingPieceLabel.getLocation();
                int centerX = releaseLocation.x + floatingPieceLabel.getWidth() / 2;
                int centerY = releaseLocation.y + floatingPieceLabel.getHeight() / 2;

                // Find the closest square based on the center point of floating label
                JPanel closestSquare = null;
                double minDistance = Double.MAX_VALUE;

                for (int row = 0; row < 8; row++) {
                    for (int col = 0; col < 8; col++) {
                        JPanel squarePanel = boardSquares[row][col];
                        Point squareLocation = SwingUtilities.convertPoint(squarePanel, 0, 0, layeredPane);

                        int squareCenterX = squareLocation.x + squarePanel.getWidth() / 2;
                        int squareCenterY = squareLocation.y + squarePanel.getHeight() / 2;

                        double distance = Point.distance(centerX, centerY, squareCenterX, squareCenterY);
                        if (distance < minDistance) {
                            minDistance = distance;
                            closestSquare = squarePanel;
                        }
                    }
                }

                // Perform move to the closest square if it's different from the original
                if (closestSquare != null && closestSquare != selectedSquarePanel) {
                    performMove(closestSquare);
                } else {
                    // Reset to the original square if no valid target is found
                    selectedSquarePanel.add(selectedPieceLabel);
                    selectedSquarePanel.revalidate();
                    selectedSquarePanel.repaint();
                }

                // Clear selection and remove floating piece label from layered pane
                layeredPane.remove(floatingPieceLabel);
                layeredPane.repaint();

                // Reset selection variables
                selectedPieceLabel = null;
                selectedSquarePanel = null;
                floatingPieceLabel = null;
                dragging = false;
            }
        }


        /**
         * Moves the selected piece to the specified target square.
         *
         * @param targetSquare The JPanel representing the square where the piece will be moved.
         */
        // Helper method to move the piece to the target square and check for game-ending conditions
        private void performMove(JPanel targetSquare) {
            // Check if the target square already has a piece
            if (targetSquare.getComponentCount() > 0) {
                JLabel targetPiece = (JLabel) targetSquare.getComponent(0);
                String pieceText = targetPiece.getText();

                // Check if the captured piece is a King
                if (pieceText.equals("♔")) {
                    announceWinner("Black");
                } else if (pieceText.equals("♚")) {
                    announceWinner("White");
                }

                // Remove the captured piece
                targetSquare.remove(targetPiece);
            }

            // Move the piece to the new square
            targetSquare.removeAll();
            targetSquare.add(selectedPieceLabel);

            // Reset the original square's color and clear dragging state
            resetSquareColor(selectedSquarePanel);

            targetSquare.revalidate();
            targetSquare.repaint();
        }

        /**
         * Announces the winner and terminates the game.
         *
         * @param winningPlayer The player who captured the King (e.g., "White" or "Black").
         */
        private void announceWinner(String winningPlayer) {
            // Display a message dialog announcing the winner
            JOptionPane.showMessageDialog(null, winningPlayer + " wins by capturing the King!");

            // Terminate the game after the winner is announced
            System.exit(0);
        }

        /**
         * Resets the background color of the specified square.
         *
         * @param square The JPanel representing the square to reset.
         */
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
                square.setBackground((row + col) % 2 == 0 ? originalLightColor : originalDarkColor);
            }
        }
    }


    /**
     * Main method to start the application.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChessBoardWithPiecesExample::new);
    }
}
