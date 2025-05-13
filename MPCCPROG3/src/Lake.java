/**
 * Represents a lake on the board
 */
public class Lake extends Terrain {
    /**
     * Constructs a Lake at a specific position
     * @param x The x-coordinate of the lake
     * @param y The y-coordinate of the lake
     */
    public Lake(int x, int y) {
        super("lake"); // Calls Terrain constructor with "lake"
        this.x = x;
        this.y = y;
    }

    /**
     * Gets the x-coordinate of the lake
     * @return The x-coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the y-coordinate of the lake.
     * @return The y-coordinate.
     */
    public int getY() {
        return y;
    }

    /**
     * @return true since this is a lake terrain
     */
    public boolean isLake() {
        return true;
    }

    /**
     * Checks if a piece can cross or land on this lake
     * @param piece The piece to check
     * @return true if the piece can cross/land on the lake (Rat can land, Lion and Tiger can cross)
     */
    public boolean canCross(Piece piece) {
        // Only Rat can land on lake tiles
        if (piece instanceof Rat) {
            return true;
        }
        // Lion and Tiger can cross but not land
        if (piece instanceof Lion || piece instanceof Tiger) {
            return true;
        }
        // All other pieces cannot cross or land
        return false;
    }
}
