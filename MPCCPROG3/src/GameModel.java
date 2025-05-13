import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.File;

/**
 * The GameModel class handles the game's logic
 * Manages the board, turns, movement of pieces, and determining the winner.
 * It updates the game state and communicates with the view to reflect the changes.
 */
public class GameModel {
    private Board board;
    private boolean isPlayerOneTurn;
    private String statusMessage;
    private boolean gameEnded;
    private GameView view;
    private String player1Name = "Player 1";
    private String player2Name = "Player 2";
    private Font pixelFont;

    /**
     * Constructs a GameModel object and initializes the board, status message,
     * and game state.
     */
    public GameModel() {
        board = new Board();
        isPlayerOneTurn = true;
        gameEnded = false;
        
        // Load Pokemon font
        try {
            pixelFont = Font.createFont(Font.TRUETYPE_FONT, new File("assets/fonts/Pokemon Classic.ttf"));
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(pixelFont);
        } catch (Exception e) {
            e.printStackTrace();
            pixelFont = new Font("Arial", Font.PLAIN, 12);
        }
    }

    /**
     * Gets the current status message of the game, typically related to piece movement
     * or game events.
     *
     * @return The current status message.
     */
    public String getStatusMessage() {
        return statusMessage;
    }

    /**
     * Clears the current status message.
     */
    public void clearStatusMessage() {
        statusMessage = "";
    }

    /**
     * Initializes the game board and sets the first player.
     *
     * @param p1First Indicates whether Player 1 should go first.
     */
    public void initializeBoard(boolean p1First) {
        // Set who goes first
        isPlayerOneTurn = p1First;
        gameEnded = false;
        clearStatusMessage(); // Clear any existing status message
    }

    /**
     * Gets the name of the piece at a specific location on the board.
     *
     * @param row The row of the square.
     * @param col The column of the square.
     * @return The name of the piece at the given location, or null if there is no piece.
     */
    public String getPieceAt(int row, int col) {
        Square square = board.getSquare(row, col);
        if (square != null && square.isOccupied()) {
            return square.getPiece().getName();
        } else if (square != null && square.getTerrain() != null) {
            if (square.getTerrain() instanceof Trap) {
                return "X";
            } else if (square.getTerrain() instanceof Lake) {
                return "~";
            }
        }
        return null;
    }

    /**
     * Handles the end of the game, displaying the winner and offering game options.
     *
     * @param player1Wins Indicates whether Player 1 won the game.
     */
    private void handleGameEnd(boolean player1Wins) {
        // Set game ended state immediately
        gameEnded = true;
        String winner = player1Wins ? player1Name : player2Name;
        
        // Update the view to reflect game ended state
        if (view != null) {
            view.updateBoard(this);
        }
        
        // Delay the popup to show the piece reaching home base
        Timer timer = new Timer(200, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Create styled message label
                JLabel messageLabel = new JLabel("<html><div style='text-align: center;'>" + winner + " wins the game!</div></html>");
                messageLabel.setFont(pixelFont.deriveFont(16f));
                messageLabel.setForeground(new Color(106, 41, 66));  // Dark pink

                // Show winner message with styled label
                JOptionPane.showMessageDialog(null, 
                    messageLabel,
                    "Game Over", 
                    JOptionPane.INFORMATION_MESSAGE);

                // Create styled options label
                JLabel optionsLabel = new JLabel("<html><div style='text-align: center;'>What would you like to do?</div></html>");
                optionsLabel.setFont(pixelFont.deriveFont(16f));
                optionsLabel.setForeground(new Color(106, 41, 66));

                // Create options for the user
                Object[] options = {"New Game", "Title Screen", "Exit"};
                int choice = JOptionPane.showOptionDialog(null,
                    optionsLabel,
                    "Game Over",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);

                switch (choice) {
                    case 0: // New Game
                        if (view != null) {
                            view.dispose();
                        }
                        SwingUtilities.invokeLater(() -> {
                            new PreGame().setVisible(true);
                        });
                        break;
                        
                    case 1: // Title Screen
                        if (view != null) {
                            view.dispose();
                        }
                        SwingUtilities.invokeLater(() -> {
                            new TitleScreen().setVisible(true);
                        });
                        break;
                        
                    default: // Exit or window closed
                        System.exit(0);
                        break;
                }
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    /**
     * Checks if any piece has reached the opponent's home base, indicating the
     * end of the game.
     *
     * @return true if a piece has reached the opponent's home base, false otherwise.
     */
    public boolean hasPieceReachedHomeBase() {
        // Check if any piece has reached a home base
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 9; j++) {
                Square square = board.getSquare(i, j);
                if (square != null && square.isOccupied()) {
                    Piece piece = square.getPiece();
                    // Check if the piece is on the enemy's home base
                    if ((piece.getPlayer().getId() == 1 && square.isHomeBase(board.getPlayer2())) ||
                        (piece.getPlayer().getId() == 2 && square.isHomeBase(board.getPlayer1()))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Moves a piece from one square to another and handles all the related logic
     * such as updating the board, switching turns, and checking for game-ending conditions.
     *
     * @param fromRow The row of the piece to move.
     * @param fromCol The column of the piece to move.
     * @param toRow The row to move the piece to.
     * @param toCol The column to move the piece to.
     * @return true if the move is valid and successfully completed, false otherwise.
     */
    public boolean movePiece(int fromRow, int fromCol, int toRow, int toCol) {
        // Immediately return if game has ended
        if (gameEnded) {
            return false;
        }

        // Clear any previous status message
        clearStatusMessage();

        // Basic validation
        if (fromRow < 0 || fromRow >= 7 || fromCol < 0 || fromCol >= 9 ||
            toRow < 0 || toRow >= 7 || toCol < 0 || toCol >= 9) {
            return false;
        }

        // Get the piece to move
        Square fromSquare = board.getSquare(fromRow, fromCol);
        if (fromSquare == null || !fromSquare.isOccupied()) {
            return false;
        }

        Piece piece = fromSquare.getPiece();

        // Verify it's the correct player's piece based on piece ownership
        if ((isPlayerOneTurn && piece.getPlayer().getId() != 1) || 
            (!isPlayerOneTurn && piece.getPlayer().getId() != 2)) {
            return false;
        }

        // Get destination square
        Square toSquare = board.getSquare(toRow, toCol);
        if (toSquare == null) {
            return false;
        }

        // Create a custom PrintStream to capture console output
        StringBuilder output = new StringBuilder();
        PrintStream originalOut = System.out;
        PrintStream customOut = new PrintStream(new OutputStream() {
            @Override
            public void write(int b) {
                output.append((char) b);
            }
        });
        System.setOut(customOut);

        boolean result = false;

        // Handle piece-specific movement
        if (piece instanceof Rat) {
            // Convert coordinates to direction for rat movement
            char direction = getDirection(fromRow, fromCol, toRow, toCol);
            if (direction == ' ') {
                return false;
            }
            result = piece.move(direction, board);

            if (result) {
                if (hasPieceReachedHomeBase()) {
                    handleGameEnd(isPlayerOneTurn);
                }
                // If the opponent still has pieces, switch turns
                else if (board.hasPiecesLeft(isPlayerOneTurn ? 2 : 1)) {
                    isPlayerOneTurn = !isPlayerOneTurn;
                }
                // Else, keep the current player moving since opponent has no pieces left
            }

        } else if (piece instanceof Lion || piece instanceof Tiger) {
            // Lion/Tiger movement is handled in their respective classes
            // We just need to convert the coordinates to a direction
            char direction = getDirection(fromRow, fromCol, toRow, toCol);
            if (direction == ' ') {
                return false;
            }
            result = piece.move(direction, board);

            if (result) {
                if (hasPieceReachedHomeBase()) {
                    handleGameEnd(isPlayerOneTurn);
                }
                // If the opponent still has pieces, switch turns
                else if (board.hasPiecesLeft(isPlayerOneTurn ? 2 : 1)) {
                    isPlayerOneTurn = !isPlayerOneTurn;
                }
                // Else, keep the current player moving since opponent has no pieces left
            }

        } else {
            // For other pieces, just move normally
            result = board.movePiece(piece, toRow, toCol);

            if (result) {
                if (hasPieceReachedHomeBase()) {
                    handleGameEnd(isPlayerOneTurn);
                }
                // If the opponent still has pieces, switch turns
                else if (board.hasPiecesLeft(isPlayerOneTurn ? 2 : 1)) {
                    isPlayerOneTurn = !isPlayerOneTurn;
                }
                // Else, keep the current player moving since opponent has no pieces left
            }
        }

        // Restore original PrintStream and store the captured output
        System.setOut(originalOut);
        statusMessage = output.toString().trim();

        return result;
    }

    /**
     * Converts the row and column coordinates to a direction string ('W', 'S', 'A', 'D').
     *
     * @param fromRow The row of the starting position.
     * @param fromCol The column of the starting position.
     * @param toRow The row of the target position.
     * @param toCol The column of the target position.
     * @return The direction as a character (W, A, S, D), or ' ' if no valid direction.
     */
    private char getDirection(int fromRow, int fromCol, int toRow, int toCol) {
        if (toRow < fromRow) return 'W';
        if (toRow > fromRow) return 'S';
        if (toCol < fromCol) return 'A';
        if (toCol > fromCol) return 'D';
        return ' ';
    }

    /**
     * Checks if it is currently Player 1's turn.
     *
     * @return true if it is Player 1's turn, false otherwise.
     */
    public boolean isPlayerOneTurn() {
        return isPlayerOneTurn;
    }

    /**
     * Gets the game board.
     *
     * @return The Board instance.
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Sets the view to update the user interface.
     *
     * @param view The GameView instance to be used for the user interface.
     */
    public void setView(GameView view) {
        this.view = view;
    }

    /**
     * Checks if the game has ended.
     *
     * @return true if the game has ended, false otherwise.
     */
    public boolean isGameEnded() {
        return gameEnded;
    }

    /**
     * Sets the names of the players.
     *
     * @param p1Name Name of player 1
     * @param p2Name Name of player 2
     */
    public void setPlayerNames(String p1Name, String p2Name) {
        this.player1Name = p1Name;
        this.player2Name = p2Name;
    }
}
