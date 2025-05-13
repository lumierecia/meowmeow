/**
 * Represents a trap on the board
 */
public class Trap extends Terrain {

    /**
     * Constructs a Trap at a specific position
     * @param x The x-coordinate of the trap
     * @param y The y-coordinate of the trap
     */
    public Trap(int x, int y) {
        super("trap");
        this.x = x;
        this.y = y;
    }

    /**
     * Gets the x-coordinate of the trap
     * @return The x-coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the y-coordinate of the trap
     * @return The y-coordinate
     */
    public int getY() {
        return y;
    }

}
