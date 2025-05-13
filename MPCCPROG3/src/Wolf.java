/**
 * Represents the Wolf piece in the Jungle King game. The Wolf can move one square
 * in any of the four cardinal directions (up, down, left, right) and cannot
 * move onto squares with a lake terrain.
 */
public class Wolf extends Piece {
    /**
     * Constructs a Wolf piece at a specific position with the specified player.
     *
     * @param x The x-coordinate of the Wolf piece on the board.
     * @param y The y-coordinate of the Wolf piece on the board.
     * @param player The player who owns this piece.
     */
    public Wolf(int x, int y, Player player) {
        super("Wolf", x, y, player, PieceType.Wolf);
    }
    
    /**
     * Moves the Wolf piece based on the given direction. 
     * The move is invalid if the destination square is out of bounds or contains a lake.
     * If the move is valid, the piece may capture an opponent's piece on the destination square.
     *
     * @param direction The direction the Wolf piece should move ('W', 'A', 'S', or 'D').
     * @param board The current game board.
     * @return true if the move is successful, false otherwise.
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
