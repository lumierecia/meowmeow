import java.util.ArrayList;
import java.util.List;

/**
 * Represents the game board for the Jungle King game.
 * The board consists of a 7x9 grid, with designated traps, lakes, and home bases.
 * It has player pieces, movement, and game logic.
 */
public class Board {
    private static final int ROWS = 7;
    private static final int COLS = 9;

    private Player player1;
    private Player player2;

    private Square[][] grid;
    private List<Trap> traps;
    private List<Lake> lakes;

    /**
     * Constructs a new Board instance and initializes its layout and pieces.
     */
    public Board() {
        grid = new Square[ROWS][COLS];
        traps = new ArrayList<>();
        lakes = new ArrayList<>();

        player1 = new Player("Player 1", 1); // Player 1 with ID 1
        player2 = new Player("Player 2", 2); // Player 2 with ID 2

        initializeBoard();
        initializePieces();
    }

    /**
     * Initializes the board with squares, traps, home base and lakes.
     */
    private void initializeBoard() {
        // Initialize the grid with squares
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                grid[i][j] = new Square(i, j);
            }
        }

        // Add traps (correct positions for a 7x9 board)
        traps.add(new Trap(2, 0)); // Player 1's trap
        traps.add(new Trap(4, 0)); // Player 1's trap
        traps.add(new Trap(3, 1)); // Player 1's trap

        traps.add(new Trap(2, 8)); // Player 2's trap
        traps.add(new Trap(4, 8)); // Player 2's trap
        traps.add(new Trap(3, 7)); // Player 2's trap

        grid[3][0].setHomeBase(player1);  // Player 1's home base
        grid[3][8].setHomeBase(player2);  // Player 2's home base

        // Add lakes (correct positions for a 7x9 board)
        int[][] lakePositions = {
                {1, 3}, {1, 4}, {1, 5},
                {2, 3}, {2, 4}, {2, 5},
                {4, 3}, {4, 4}, {4, 5},
                {5, 3}, {5, 4}, {5, 5}
        };

        for (int[] pos : lakePositions) {
            int x = pos[0];
            int y = pos[1];

            if (x >= 0 && x < ROWS && y >= 0 && y < COLS) { // Ensure valid positions
                lakes.add(new Lake(x, y));
            } else {
                System.out.println("Invalid lake position: (" + x + "," + y + ")");
            }
        }

        // Assign terrain to squares
        for (Trap trap : traps) {
            grid[trap.getX()][trap.getY()].setTerrain(trap);
        }
        for (Lake lake : lakes) {
            grid[lake.getX()][lake.getY()].setTerrain(lake);
        }
    }

    /**
     * Initializes the pieces on the board.
     */
    public void initializePieces() {
        //player 1
        placePiece(new Elephant(0, 2, player1), 0, 2);
        placePiece(new Lion(6, 0, player1), 6, 0);
        placePiece(new Tiger(0, 0, player1), 0, 0);
        placePiece(new Leopard(4, 2, player1), 4, 2);
        placePiece(new Wolf(2, 2, player1), 2, 2);
        placePiece(new Dog(5, 1, player1), 5, 1);
        placePiece(new Cat(1, 1, player1), 1, 1);
        placePiece(new Rat(6, 2, player1), 6, 2);

        //player 2
        placePiece(new Elephant(6, 6, player2), 6, 6);
        placePiece(new Lion(0, 8, player2), 0, 8);
        placePiece(new Tiger(6, 8, player2), 6, 8);
        placePiece(new Leopard(2, 6, player2), 2, 6);
        placePiece(new Wolf(4, 6, player2), 4, 6);
        placePiece(new Dog(1, 7, player2), 1, 7);
        placePiece(new Cat(5, 7, player2), 5, 7);
        placePiece(new Rat(0, 6, player2), 0, 6);
    }

    /**
     * Retrieves the square at the given coordinates.
     *
     * @param x The row index.
     * @param y The column index.
     * @return The Square object at (x, y), or null if out of bounds.
     */
    public Square getSquare(int x, int y) {
        if (x >= 0 && x < ROWS && y >= 0 && y < COLS) {
            return grid[x][y];
        }
        return null;
    }

    /**
     * Places a piece on the board at a given position.
     *
     * @param piece The piece to be placed.
     * @param x The row index.
     * @param y The column index.
     * @return true if the placement was successful, false otherwise.
     */
    public boolean placePiece(Piece piece, int x, int y) {
        Square square = getSquare(x, y);
        if (square == null || square.isOccupied()) {
            return false;
        }

        square.setPiece(piece);
        piece.setPosition(x, y);

        piece.getPlayer().getPieces().add(piece);

        return true;
    }

    /**
     * Moves a piece to the specified coordinates if valid.
     *
     * @param piece The piece to be moved.
     * @param newX The target row index.
     * @param newY The target column index.
     * @return true if the move was successful, false otherwise.
     */
    public boolean movePiece(Piece piece, int newX, int newY) {
        Square currentSquare = getSquare(piece.getX(), piece.getY());
        Square destinationSquare = getSquare(newX, newY);
        StringBuilder message = new StringBuilder();

        //make sure move is in board
        if (currentSquare == null || destinationSquare == null) {
            message.append("Invalid move: Out of bounds.");
            System.out.println(message.toString());
            return false;
        }

        // Prevent a player from moving onto their own home base
        if ((piece.getPlayer().getId() == 1 && newX == 3 && newY == 0) ||  // Player 1's home base
                (piece.getPlayer().getId() == 2 && newX == 3 && newY == 8)) {  // Player 2's home base
            message.append("You cannot land on your own home base!");
            System.out.println(message.toString());
            return false;
        }

        // Special case: Check for rat-vs-rat capture first
        if (piece instanceof Rat && destinationSquare.isOccupied() && destinationSquare.getPiece() instanceof Rat) {
            Piece destinationPiece = destinationSquare.getPiece();
            if (piece.getPlayer() != destinationPiece.getPlayer()) {
                // Allow rat-vs-rat capture regardless of lake
                destinationPiece.capture(this);
                message.append(destinationPiece.getName()).append(" of Player ").append(destinationPiece.getPlayer().getId()).append(" has been captured.");
                System.out.println(message.toString());
                
                // Move the piece
                currentSquare.removePiece();
                destinationSquare.setPiece(piece);
                piece.setPosition(newX, newY);
                return true;
            }
        }

        // Check if the destination is a lake and if the piece can cross it
        if (destinationSquare.getTerrain() instanceof Lake) {
            Lake lake = (Lake) destinationSquare.getTerrain();
            if (!(piece instanceof Rat)) {  // Skip lake check for Rats
                if (!lake.canCross(piece)) {
                    message.append(piece.getName()).append(" cannot cross or land on a lake.");
                    System.out.println(message.toString());
                    return false;
                }
            }
        }

        // Check if the destination square is occupied
        if (destinationSquare.isOccupied()) {
            Piece destinationPiece = destinationSquare.getPiece();
            if (piece.canCapture(destinationPiece)) {
                message.append(destinationPiece.getName()).append(" of Player ").append(destinationPiece.getPlayer().getId()).append(" has been captured.");
                System.out.println(message.toString());
                destinationPiece.capture(this); // Pass the board instance
            } else {
                message.append("Cannot capture ").append(destinationPiece.getName()).append(".");
                System.out.println(message.toString());
                return false;
            }
        }

        // Move the piece
        currentSquare.removePiece();
        destinationSquare.setPiece(piece);
        piece.setPosition(newX, newY);

        // Check if the piece landed on a home base
        if (destinationSquare.isHomeBase(player1) || destinationSquare.isHomeBase(player2)) {
            message.append(String.format("%s reached the home base at (%d, %d)!", piece.getName(), newX + 1, newY + 1));
            System.out.println(message.toString());
            destinationSquare.setHomeBase(piece.getPlayer()); // Assign the home base to the new player
            return true; // Signal that home base was captured
        }

        // Check if the piece is on a trap
        if (destinationSquare.getTerrain() instanceof Trap) {
            // Only weaken if the trap belongs to the enemy player
            boolean isPlayer1Trap = (newX == 2 && newY == 0) || (newX == 4 && newY == 0) || (newX == 3 && newY == 1);
            boolean isPlayer2Trap = (newX == 2 && newY == 8) || (newX == 4 && newY == 8) || (newX == 3 && newY == 7);
            
            if ((piece.getPlayer().getId() == 1 && isPlayer2Trap) || 
                (piece.getPlayer().getId() == 2 && isPlayer1Trap)) {
                piece.setWeakened(true);
                message.append(piece.getName()).append(" is weakened by a trap.");
                System.out.println(message.toString());
            } else {
                piece.setWeakened(false);
            }
        } else {
            piece.setWeakened(false);
        }

        message.append(String.format("%s moved to (%d, %d)", piece.getName(), newX + 1, newY + 1));
        System.out.println(message.toString());

        return true;
    }


    /**
     * Checks if a player has any remaining pieces on the board.
     *
     * @param playerId The ID of the player to check.
     * @return true if the player has at least one piece left, false otherwise.
     */
    public boolean hasPiecesLeft(int playerId) {
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 9; j++) {
                Square square = getSquare(i, j);
                if (square != null && square.isOccupied() && square.getPiece().getPlayer().getId() == playerId) {
                    return true; // Found at least one piece, player is still in the game
                }
            }
        }
        return false; // No pieces left for this player
    }

    /**
     * Retrieves Player 1.
     *
     * @return The first player.
     */
    public Player getPlayer2() {
        return player2;
    }

    /**
     * Retrieves Player 2.
     *
     * @return The second player.
     */
    public Player getPlayer1() {
        return player1;
    }

    
}
