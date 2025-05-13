/**
 * Represents a Lion piece in the  game.
 * The Lion can move in any of the four directions (W, A, S, D), and it has the special ability to jump over lakes.
 */
public class Lion extends Piece {
    /**
     * Constructs a Lion piece with a specified position and owner.
     *
     * @param x The initial x-coordinate of the Lion.
     * @param y The initial y-coordinate of the Lion.
     * @param player The player who owns the Lion.
     */
    public Lion(int x, int y, Player player) {
        super("Lion", x, y, player, PieceType.Lion);
    }
    
    /**
     * Moves the Lion piece in the specified direction (W, A, S, D).
     * The Lion can jump over lakes, but it cannot land on a lake or a blocked square.
     *
     * @param direction The direction in which to move the piece. It can be 'W' for up, 'A' for left, 
     *                  'S' for down, or 'D' for right.
     * @param board The current game board, used to validate and perform the move.
     * @return {@code true} if the move was successful, {@code false} otherwise.
     */
    @Override
    public boolean move(char direction, Board board) {
        int dx = 0, dy = 0;

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
        Square nextSquare = board.getSquare(newX, newY);

        // Check if the move is out of bounds
        if (nextSquare == null) {
            System.out.println("Move out of bounds.");
            return false;
        }

        // Check if Lion is entering a lake
        if (nextSquare.getTerrain() != null && nextSquare.getTerrain().isLake()) {
            // Skip all lake tiles until we find a non-lake square
            while (nextSquare != null && nextSquare.getTerrain() != null && nextSquare.getTerrain().isLake()) {
                // Check if a rat is blocking the lake
                if (nextSquare.isOccupied() && nextSquare.getPiece() instanceof Rat) {
                    System.out.println("Lion cannot jump because a Rat blocks the path.");
                    return false;
                }
                newX += dx;
                newY += dy;
                nextSquare = board.getSquare(newX, newY);
            }

            // Ensure lion lands on a valid tile
            if (nextSquare == null || (nextSquare.getTerrain() != null && nextSquare.getTerrain().isLake())) {
                System.out.println("Lion must land immediately after the lake.");
                return false;
            }

            System.out.println("Lion jumps over the lake to (" + newX + ", " + newY + ")");
        }

        // Handle normal move or jump move
        Square destinationSquare = board.getSquare(newX, newY);
        if (destinationSquare != null) {
            Piece destinationPiece = destinationSquare.getPiece();
            if (canCapture(destinationPiece)) {
                destinationPiece.capture(board);
            }
        }

        return board.movePiece(this, newX, newY);
    }
}
