/**
 * Represents a terrain type on the board
 */
public abstract class Terrain {
    private final String type;
    protected int x, y;

    /**
     * Constructs a Terrain with a given type
     * @param type The type of terrain (trap, lake, homeBase).
     */
    public Terrain(String type) {
        this.type = type;
    }

    /**
     * Checks if this terrain is a trap
     * @return true if the terrain is a trap
     */
    public boolean isTrap() {
        return "trap".equals(type);
    }

    /**
     * Checks if this terrain is a lake
     * @return true if the terrain is a lake
     */
    public boolean isLake() {
        return "lake".equals(type);
    }

    /**
     * Checks if this terrain is a home base
     * @return true if the terrain is a home base
     */
    public boolean isHomeBase() {
        return "homeBase".equals(type);
    }


}
