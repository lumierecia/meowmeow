/**
 * Represents a Leopard piece in the game.
 * The Leopard can move in any of the four directions (W, A, S, D), but it cannot move onto a lake.
 */
public class Leopard extends Piece {

    /**
     * Constructs a Leopard piece with a specified position and owner.
     *
     * @param x The initial x-coordinate of the Leopard.
     * @param y The initial y-coordinate of the Leopard.
     * @param player The player who owns the Leopard.
     */
    public Leopard(int x, int y, Player player) {
        super("Leopard", x, y, player, PieceType.Leopard);
    }
    
    /**
     * Moves the Leopard piece in the specified direction (W, A, S, D).
     *
     * @param direction The direction in which to move the piece. It can be 'W' for up, 'A' for left, 
     *                  'S' for down, or 'D' for right.
     * @param board The current game board, used to validate and perform the move.
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
