/**
 * Represents a Rat piece in the game
 * The Rat is being able to enter lakes and has special capture rules:
 * - A rat in a lake can only capture another rat in a lake
 * - A rat in a lake cannot capture land pieces
 * - A rat on land follows normal capture rules
 */
public class Rat extends Piece {

    /**
     * Constructs a Rat piece with a position and player
     *
     * @param x      The x-coordinate of the Rat on the board
     * @param y      The y-coordinate of the Rat on the board
     * @param player The player who owns Rat
     */
    public Rat(int x, int y, Player player) {
        super("Rat", x, y, player, PieceType.Rat);
    }

    /**
     * Moves the Rat in the given direction on the board
     * The Rat can also enter lakes with special capture rules.
     *
     * @param direction The direction to move
     * @param board     The game board on where the Rat moves
     * @return True if the move was successful
     */
    @Override
    public boolean move(char direction, Board board) {
        int dx = 0, dy = 0;

        // Determine movement based on direction input
        switch (Character.toUpperCase(direction)) {
            case 'W': dx = -1; break;
            case 'A': dy = -1; break;
            case 'S': dx = 1; break;
            case 'D': dy = 1; break;
            default:
                System.out.println("Invalid. Use W, A, S, or D.");
                return false;
        }

        int newX = x + dx;
        int newY = y + dy;
        Square currentSquare = board.getSquare(x, y);
        Square destinationSquare = board.getSquare(newX, newY);

        if (destinationSquare != null) {
            Piece destinationPiece = destinationSquare.getPiece();
            
            // Only proceed if there's a piece to potentially capture
            if (destinationPiece != null && destinationPiece.getPlayer() != getPlayer()) {
                boolean isCurrentlyInLake = currentSquare.getTerrain() != null && currentSquare.getTerrain().isLake();
                boolean isDestInLake = destinationSquare.getTerrain() != null && destinationSquare.getTerrain().isLake();
                
                // Special lake capture rules
                if (isCurrentlyInLake) {
                    // When in lake, can capture another rat regardless of terrain
                    if (destinationPiece instanceof Rat) {
                        destinationPiece.capture(board);
                    } else {
                        // Cannot capture non-rat pieces when in lake
                        return false;
                    }
                } else {
                    // Normal capture rules when on land
                    if (canCapture(destinationPiece)) {
                        destinationPiece.capture(board);
                    } else {
                        return false;
                    }
                }
            }
        }

        // Move the Rat to the new position
        return board.movePiece(this, newX, newY);
    }
}
