import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JOptionPane;
/**
 * The GameController class manages user input and game interactions
 * for the game. It gets keyboard inputs
 * and piece selection events to control game mechanics.
 */
public class GameController implements KeyListener {
    private GameModel model;
    private GameView view;
    private int selectedRow = -1, selectedCol = -1;

    /**
     * Constructs a GameController and initializes the game view.
     *
     * @param model The game model managing game logic.
     */
    public GameController(GameModel model) {
        this.model = model;
        this.view = new GameView(this);
        view.addKeyListener(this);
        view.setFocusable(true);
        view.requestFocus();
        view.updateBoard(model);
    }

    /**
     * Returns the game model.
     *
     * @return The game model.
     */
    public GameModel getModel() {
        return model;
    }

    /**
     * Returns the game view.
     *
     * @return The game view.
     */
    public GameView getView() {
        return view;
    }

    /**
     * Handles user clicks on the game board, allowing selection of pieces
     * that belong to the current player.
     *
     * @param row The row index of the clicked square.
     * @param col The column index of the clicked square.
     */
    public void handlePieceClick(int row, int col) {
        // If game has ended, ignore clicks
        if (model.isGameEnded()) {
            return;
        }

        String piece = model.getPieceAt(row, col);

        // Check if it's a valid piece (not empty, not a trap, not a lake)
        if (piece != null && !piece.equals("X") && !piece.contains("~")) {
            // Check if the piece belongs to the current player based on piece ownership
            Square square = model.getBoard().getSquare(row, col);
            if (square != null && square.isOccupied()) {
                Piece pieceObj = square.getPiece();
                boolean isPlayer1Piece = pieceObj.getPlayer().getId() == 1;
                boolean isPlayer2Piece = pieceObj.getPlayer().getId() == 2;

                // Allow selection if it's the player's piece and it's their turn
                if ((model.isPlayerOneTurn() && isPlayer1Piece) ||
                        (!model.isPlayerOneTurn() && isPlayer2Piece)) {

                    // If a piece was previously selected, clear its highlight
                    if (selectedRow != -1 && selectedCol != -1) {
                        view.clearSelection();
                    }

                    // Select the new piece
                    selectedRow = row;
                    selectedCol = col;
                    view.highlightSelectedPiece(row, col);
                    view.requestFocus();
                }
            }
        }
    }

    /**
     * Handles key presses for moving the selected piece.
     *
     * @param e The key event triggered by the user.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        // If game has ended or no piece selected, ignore key presses
        if (model.isGameEnded() || selectedRow == -1 || selectedCol == -1) {
            return;
        }

        int newRow = selectedRow;
        int newCol = selectedCol;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_W: // Up
                newRow--;
                break;
            case KeyEvent.VK_S: // Down
                newRow++;
                break;
            case KeyEvent.VK_A: // Left
                newCol--;
                break;
            case KeyEvent.VK_D: // Right
                newCol++;
                break;
            default:
                return;
        }

        // Check if we're trying to capture with a rat that's on a lake
        Square selectedSquare = model.getBoard().getSquare(selectedRow, selectedCol);
        Square targetSquare = model.getBoard().getSquare(newRow, newCol);

        if (selectedSquare != null && selectedSquare.isOccupied() &&
                targetSquare != null && targetSquare.isOccupied()) {
            Piece selectedPiece = selectedSquare.getPiece();
            Piece targetPiece = targetSquare.getPiece();

            // If the selected piece is a rat
            if (selectedPiece.getName().equals("Rat")) {
                // Check if it's on a lake first
                if (selectedSquare.getTerrain() instanceof Lake &&
                        ((model.isPlayerOneTurn() && targetPiece.getPlayer().getId() == 2) ||
                                (!model.isPlayerOneTurn() && targetPiece.getPlayer().getId() == 1))) {
                }
                // If not on lake, check if trying to capture non-elephant and not weakened
                else if (((model.isPlayerOneTurn() && targetPiece.getPlayer().getId() == 2) ||
                        (!model.isPlayerOneTurn() && targetPiece.getPlayer().getId() == 1)) &&
                        !targetPiece.getName().equals("Elephant") &&
                        !targetPiece.isWeakened()) {  // Allow capture if piece is weakened
                    view.addMoveToHistory("Cannot capture " + targetPiece.getName() + ".");
                    return;
                }
            }
        }

        // Store the current status message
        String previousStatus = model.getStatusMessage();

        // Check if the destination is a lake and if the piece can cross it
        Square destinationSquare = model.getBoard().getSquare(newRow, newCol);
        if (destinationSquare != null && destinationSquare.getTerrain() instanceof Lake) {
            if (selectedSquare != null && selectedSquare.isOccupied()) {
                Piece piece = selectedSquare.getPiece();
                Lake lake = (Lake) destinationSquare.getTerrain();
                if (!lake.canCross(piece)) {
                    String pieceName = piece.getName();
                    view.addMoveToHistory(pieceName + " cannot cross or land on a lake.");
                    return;
                }
            }
        }

        if (model.movePiece(selectedRow, selectedCol, newRow, newCol)) {
            // Get the new status message which contains the console output
            String newStatus = model.getStatusMessage();

            // Format lake jumping messages differently
            if (newStatus.contains("jumps over the lake")) {
                String pieceName = newStatus.substring(0, newStatus.indexOf(" jumps"));
                String landingCoords = newStatus.substring(newStatus.lastIndexOf("("));
                view.addMoveToHistory(pieceName + " jumps over the lake and landed at " + landingCoords);
            } else {
                // Add the status message to history if it changed
                if (!newStatus.equals(previousStatus)) {
                    view.addMoveToHistory(newStatus);
                }
            }

            view.clearSelection();
            selectedRow = -1;
            selectedCol = -1;
            view.updateBoard(model);
        } else {
            // Move was invalid, add the error message to history
            String errorMessage = model.getStatusMessage();
            if (!errorMessage.isEmpty() && !errorMessage.equals(previousStatus)) {
                view.addMoveToHistory(errorMessage);
            }
        }
    }
    /**
     * Handles key typing events. This method is required for KeyListener
     * but is not used in this implementation.
     *
     * @param e The key event triggered by the user.
     */
    @Override
    public void keyTyped(KeyEvent e) {}
    /**
     * Handles key release events. This method is required for KeyListener 
     * but is not used in this implementation.
     *
     * @param e The key event triggered by the user.
     */
    @Override
    public void keyReleased(KeyEvent e) {}
}
