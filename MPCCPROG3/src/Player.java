import java.util.List;
import java.util.ArrayList;
/**
 * Represents a player in the game.
 * Each player has a name, an ID (1 for Player 1, 2 for Player 2),
 * and a list of pieces they own.
 */
public class Player {
    private String name; // The name of the player
    private int id; // 1 for Player 1, 2 for Player 2
    private List<Piece> pieces; // Stores the player's pieces

    /**
     * Constructs a Player with the specified name and ID.
     *
     * @param name The name of the player.
     * @param id The player's ID (1 for Player 1, 2 for Player 2).
     */
    public Player(String name, int id) { // Accept id in constructor
        this.name = name;
        this.id = id;
        this.pieces = new ArrayList<>(); // Initialize list to prevent NullPointerException
    }

    /**
     * Gets the player's ID.
     *
     * @return The player's ID.
     */
    public int getId() {
        return id;
    }


    /**
     * Removes a piece from the player's list of pieces.
     *
     * @param piece The piece to be removed.
     */
    public void removePiece(Piece piece) {
        pieces.remove(piece);
    }
    

    /**
     * Gets the list of pieces owned by the player.
     *
     * @return The list of pieces owned by the player.
     */
    public List<Piece> getPieces() {
        return pieces; // Getter for pieces
    }
}
