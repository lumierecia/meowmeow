/**
 * The Elephant class represents an Elephant piece in the game.
 *
 */
public class Elephant extends Piece {

    /**
     * Constructs an Elephant piece with the given position and player.
     *
     * @param x       The initial x-coordinate of the Elephant.
     * @param y       The initial y-coordinate of the Elephant.
     * @param player  The player to whom the Elephant belongs.
     */
    public Elephant(int x, int y, Player player) {
        super("Elephant", x, y, player, PieceType.Elephant);
    }

    /**
     * Moves the Elephant in the specified direction on the board.
     * The Elephant can move up (W), left (A), down (S), or right (D).
     * The move is valid only if it does not go out of bounds, land in a lake,
     * or break movement rules.
     *
     * @param direction The direction to move ('W', 'A', 'S', 'D').
     * @param board     The game board.
     * @return {@code true} if the move was successful, {@code false} otherwise.
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
                System.out.println("Invalid move. Use W, A, S, or D.");
                return false;
        }

        int newX = x + dx;
        int newY = y + dy;

        if (board.getSquare(newX, newY) == null) {
            System.out.println("Move out of bounds.");
            return false;
        }

        Square destinationSquare = board.getSquare(newX, newY);
        //prevent piece on going on lake
        if (destinationSquare != null && destinationSquare.getTerrain() != null && destinationSquare.getTerrain().isLake()) {
            return false;
        }

        if (destinationSquare != null) {
            Piece destinationPiece = destinationSquare.getPiece();

            if (destinationPiece != null && canCapture(destinationPiece)) {
                destinationPiece.capture(board); // Capture the opponent's piece
            }
        }

        return board.movePiece(this, newX, newY);
    }
}
