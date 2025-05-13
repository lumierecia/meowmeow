/**
 * Represents a piece in the game. Each piece has a name, position,
 * an associated player, and different conditions
 */
public abstract class Piece {
    protected String name;         // The name of the piece
    protected int x, y;            // The piece's coordinates on the board
    protected Player player;        // The player who owns this piece
    protected boolean isCaptured;   // Indicates if the piece has been captured
    protected boolean isWeakened;   // Indicates if the piece is weakened by trap
    protected PieceType type;       // Indicates type of piece for capturing
    /**
     * Constructs a Piece with a name, position, player and status
     *
     * @param name   The name of the piece
     * @param x      The x-coordinate of the piece
     * @param y      The y-coordinate of the piece
     * @param player The player who owns the piece
     */
    public Piece(String name, int x, int y, Player player, PieceType type) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.player = player;
        this.isCaptured = false;
        this.isWeakened = false;
        this.type = type;
    }

    public PieceType getType() {
        return type;
    }
    enum PieceType {
        Elephant(8), Lion(7), Tiger(6), Leopard(5),
        Wolf(4), Dog(3), Cat(2), Rat(1);

        private final int strength;

        PieceType(int strength) {
            this.strength = strength;
        }

        public int getStrength() {
            return strength;
        }
    }

    /**
     * Moves the piece in the given direction on the board
     *
     * @param direction The direction to move
     * @param board     The game board where the piece moves
     * @return True if the move was successful
     */
    public abstract boolean move(char direction, Board board);

    /**
     * Captures piece, remove from the game
     *
     * @param board The game board from where the piece is removed
     */
    public void capture(Board board) {
        isCaptured = true;
        System.out.println(name + " of Player " + player.getId() + " has been captured.");

        // Remove the piece from the board
        Square currentSquare = getCurrentSquare(board);
        if (currentSquare != null) {
            currentSquare.removePiece(this);
        }

        // Remove the piece from the player's list
        player.removePiece(this);
    }

    /**
     * Gets the name of the piece
     *
     * @return The piece's name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the player who owns this piece
     *
     * @return The player who owns the piece
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets the x-coordinate of the piece
     *
     * @return The x-coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the y-coordinate of the piece
     *
     * @return The y-coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * Updates the piece's position to the new coordinates
     *
     * @param newX The new x-coordinate
     * @param newY The new y-coordinate
     */
    public void setPosition(int newX, int newY) {
        this.x = newX;
        this.y = newY;
    }

    /**
     * Checks if the piece is weakened
     *
     * @return True if the piece is weakened
     */
    public boolean isWeakened() {
        return isWeakened;
    }

    /**
     * Sets the weakened state of the piece
     *
     * @param weakened True to weaken the piece
     */
    public void setWeakened(boolean weakened) {
        this.isWeakened = weakened;
    }

    /**
     * Determines if this piece can capture another piece
     *
     * @param other The target piece to capture
     * @return True if the capture is allowed
     */
    public boolean canCapture(Piece other) {
        if (other == null || other.getPlayer() == this.player) {
            return false; // Cannot capture self pieces
        }

        // If the target piece is weakened, any piece can capture it
        if (other.isWeakened()) return true;

        //Rat can capture Elephant
        if (this instanceof Rat && other.getType() == PieceType.Elephant) return true;

        // Weakened pieces cannot capture
        if (this.isWeakened) return false;

        // Strength rule: Attacker must be stronger
        return this.getType().getStrength() >= other.getType().getStrength();
    }

    /**
     * Retrieves the square that piece currently occupies on the board
     *
     * @param board The board
     * @return The square occupied by this piece
     */
    public Square getCurrentSquare(Board board) {
        return board.getSquare(this.x, this.y);
    }
}
