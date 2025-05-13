/**
 * Represents a square on the board
 */
public class Square {
    private int x, y;          // Coordinates of the square
    private Piece piece;        // The piece currently occupying the square
    private Terrain terrain;    // The type of terrain on square
    private Player homeBase;    // Stores the player whose home base is at this square

    /**
     * Constructs a Square object with given coordinates
     *
     * @param x The x-coordinate of the square
     * @param y The y-coordinate of the square
     */
    public Square(int x, int y) {
        this.x = x;
        this.y = y;
        this.piece = null;
        this.terrain = null;
        this.homeBase = null;
    }

    /**
     * Gets the x-coordinate of the square
     *
     * @return The x-coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the y-coordinate of the square
     *
     * @return The y-coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * Gets the piece on the square
     *
     * @return The piece occupying the square
     */
    public Piece getPiece() {
        return piece;
    }

    /**
     * Gets the terrain type on the square
     *
     * @return terrain
     */
    public Terrain getTerrain() {
        return terrain;
    }

    /**
     * Checks if the square is occupied by a piece
     *
     * @return True if occupied
     */
    public boolean isOccupied() {
        return piece != null;
    }

    /**
     * Sets a piece on the square
     *
     * @param piece The piece to place on the square
     */
    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    /**
     * Removes the piece from the square
     */
    public void removePiece() {
        this.piece = null;
    }

    /**
     * Sets the terrain type of the square
     *
     * @param terrain The terrain to set
     */
    public void setTerrain(Terrain terrain) {
        this.terrain = terrain;
    }

    /**
     * Sets the home base of a player on the square
     *
     * @param player The player whose home base is at this square
     */
    public void setHomeBase(Player player) {
        this.homeBase = player;
    }

    /**
     * Checks if the square is a home base of the given player
     *
     * @param player The player to check
     * @return True if this square is the home base of the opponent
     */
    public boolean isHomeBase(Player player) {
        return this.homeBase != null && this.homeBase != player;
    }

    /**
     * Removes a specific piece from the square if it matches the current piece
     *
     * @param piece The piece to remove
     */
    public void removePiece(Piece piece) {
        if (this.piece == piece) {
            this.piece = null;
        }
    }
}
