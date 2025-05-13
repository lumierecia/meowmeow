/**
 * Represents the Tiger piece in the game, inheriting from the Piece class.
 * The Tiger can move in standard directions and can jump over lakes if a rat is not blocking the path.
 */
public class Tiger extends Piece {

    /**
     * Constructs a Tiger piece at the specified coordinates and assigns it to a player.
     * The Tiger has the ability to jump over lakes.
     *
     * @param x The x-coordinate of the Tiger piece on the board.
     * @param y The y-coordinate of the Tiger piece on the board.
     * @param player The player who controls the Tiger piece.
     */
    public Tiger(int x, int y, Player player) {
        super("Tiger", x, y, player, PieceType.Tiger);
    }

    /**
     * Moves the Tiger piece in the specified direction on the board.
     * The Tiger can move normally or jump over lakes if no rat is blocking the path.
     * The movement is determined by the direction input, which can be 'W' (up), 'A' (left), 'S' (down), or 'D' (right).
     *
     * @param direction The direction to move the Tiger piece (W, A, S, or D).
     * @param board The board on which the piece is being moved.
     * @return {@code true} if the move is successful, {@code false} if the move is invalid.
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

        // Check if Tiger is entering a lake
        if (nextSquare.getTerrain() != null && nextSquare.getTerrain().isLake()) {
            // Skip all lake tiles until we find a non-lake square
            while (nextSquare != null && nextSquare.getTerrain() != null && nextSquare.getTerrain().isLake()) {
                // Check if a rat is blocking the lake
                if (nextSquare.isOccupied() && nextSquare.getPiece() instanceof Rat) {
                    System.out.println("Tiger cannot jump because a Rat blocks the path.");
                    return false;
                }
                newX += dx;
                newY += dy;
                nextSquare = board.getSquare(newX, newY);
            }

            // Ensure tiger lands on a valid tile
            if (nextSquare == null || (nextSquare.getTerrain() != null && nextSquare.getTerrain().isLake())) {
                System.out.println("Tiger must land immediately after the lake.");
                return false;
            }

            System.out.println("Tiger jumps over the lake to (" + newX + ", " + newY + ")");
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
