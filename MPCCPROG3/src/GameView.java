import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.SimpleAttributeSet;
/**
 * GameView class is the graphical user interface (GUI) for the game
 * has visual elements for the game board, move history, and user interaction with the game
 * including button clicks and updating the interface based on the game
 */
public class GameView extends JFrame {
    private JButton[][] boardButtons;
    private GameController controller;
    private final Color PASTEL_BLUE = new Color(173, 216, 230); // Light blue color for lakes
    private final Color WARM_WHITE = new Color(255, 247, 243); // #fff7f3
    private final Color LIGHT_PINK = new Color(251, 199, 218); // #fbc7da
    private final Color PLAYER1_STROKE = new Color(230, 178, 186); // #E6B2BA
    private final Color PLAYER2_STROKE = new Color(197, 153, 182); // #C599B6
    private Image originalLakeImageWhite;
    private Image originalLakeImagePink;
    private Image originalGrassImage;
    private Image originalDirtImage;
    // Home base images
    private ImageIcon homeBaseP1Icon;
    private ImageIcon homeBaseP2Icon;
    private ImageIcon scaledHomeBaseP1;
    private ImageIcon scaledHomeBaseP2;
    // Lake tiles
    private ImageIcon lake13Icon, lake14Icon, lake15Icon;
    private ImageIcon lake23Icon, lake24Icon, lake25Icon;
    private ImageIcon lake43Icon, lake44Icon, lake45Icon;
    private ImageIcon lake53Icon, lake54Icon, lake55Icon;
    private ImageIcon scaledLakeImageWhite;
    private ImageIcon scaledLakeImagePink;
    private ImageIcon scaledGrassImage;
    private ImageIcon scaledDirtImage;
    // Scaled lake tiles
    private ImageIcon scaledLake13, scaledLake14, scaledLake15;
    private ImageIcon scaledLake23, scaledLake24, scaledLake25;
    private ImageIcon scaledLake43, scaledLake44, scaledLake45;
    private ImageIcon scaledLake53, scaledLake54, scaledLake55;
    // Trap icons
    private ImageIcon trapIconP120;  // Player 1 trap at (2,0)
    private ImageIcon trapIconP131;  // Player 1 trap at (3,1)
    private ImageIcon trapIconP140;  // Player 1 trap at (4,0)
    private ImageIcon trapIconP228;  // Player 2 trap at (2,8)
    private ImageIcon trapIconP237;  // Player 2 trap at (3,7)
    private ImageIcon trapIconP248;  // Player 2 trap at (4,8)
    // Animal icons for both players
    private ImageIcon ratIconP1, ratIconP2;
    private ImageIcon catIconP1, catIconP2;
    private ImageIcon dogIconP1, dogIconP2;
    private ImageIcon wolfIconP1, wolfIconP2;
    private ImageIcon leopardIconP1, leopardIconP2;
    private ImageIcon tigerIconP1, tigerIconP2;
    private ImageIcon lionIconP1, lionIconP2;
    private ImageIcon elephantIconP1, elephantIconP2;
    private JLabel turnLabel;
    private JLabel statusLabel;
    private JButton selectedButton = null;
    private JTextPane moveHistoryArea;
    private int moveCount = 0;
    private String player1Name;
    private String player2Name;
    private ImageIcon helpIcon;
    private ImageIcon helpIconHover;

    /**
     * Constructor to initialize the game view and its components.
     * Sets up the main game window, top panel (including help button and turn label),
     * the game board, and the move history panel
     */
    public GameView(GameController controller) {
        this.controller = controller;
        // Load the lake images and store original images
        originalLakeImageWhite = new ImageIcon("assets/laketilewhite.png").getImage();
        originalLakeImagePink = new ImageIcon("assets/laketilepink.png").getImage();
        originalGrassImage = new ImageIcon("assets/grasstile.png").getImage();
        originalDirtImage = new ImageIcon("assets/dirttile.png").getImage();

        // Load home base images
        homeBaseP1Icon = new ImageIcon("assets/homebasep1.png");
        homeBaseP2Icon = new ImageIcon("assets/homebasep2.png");

        // Load lake tiles
        lake13Icon = new ImageIcon("assets/lake13.png");
        lake14Icon = new ImageIcon("assets/lake14.png");
        lake15Icon = new ImageIcon("assets/lake15.png");
        lake23Icon = new ImageIcon("assets/lake23.png");
        lake24Icon = new ImageIcon("assets/lake24.png");
        lake25Icon = new ImageIcon("assets/lake25.png");
        lake43Icon = new ImageIcon("assets/lake43.png");
        lake44Icon = new ImageIcon("assets/lake44.png");
        lake45Icon = new ImageIcon("assets/lake45.png");
        lake53Icon = new ImageIcon("assets/lake53.png");
        lake54Icon = new ImageIcon("assets/lake54.png");
        lake55Icon = new ImageIcon("assets/lake55.png");

        // Load trap icons
        trapIconP120 = new ImageIcon("assets/trapp120.png");
        trapIconP131 = new ImageIcon("assets/trapp131.png");
        trapIconP140 = new ImageIcon("assets/trapp140.png");
        trapIconP228 = new ImageIcon("assets/trapp228.png");
        trapIconP237 = new ImageIcon("assets/trapp237.png");
        trapIconP248 = new ImageIcon("assets/trapp248.png");

        // Load all animal icons
        ratIconP1 = new ImageIcon("assets/ratp1.png");
        ratIconP2 = new ImageIcon("assets/ratp2.png");
        catIconP1 = new ImageIcon("assets/catp1.png");
        catIconP2 = new ImageIcon("assets/catp2.png");
        dogIconP1 = new ImageIcon("assets/dogp1.png");
        dogIconP2 = new ImageIcon("assets/dogp2.png");
        wolfIconP1 = new ImageIcon("assets/wolfp1.png");
        wolfIconP2 = new ImageIcon("assets/wolfp2.png");
        leopardIconP1 = new ImageIcon("assets/leopardp1.png");
        leopardIconP2 = new ImageIcon("assets/leopardp2.png");
        tigerIconP1 = new ImageIcon("assets/tigerp1.png");
        tigerIconP2 = new ImageIcon("assets/tigerp2.png");
        lionIconP1 = new ImageIcon("assets/lionp1.png");
        lionIconP2 = new ImageIcon("assets/lionp2.png");
        elephantIconP1 = new ImageIcon("assets/elephantp1.png");
        elephantIconP2 = new ImageIcon("assets/elephantp2.png");

        // Initial scaling will be done when the window is first shown
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateLakeImages();
                updateBoard(GameView.this.controller.getModel());
            }
        });

        setTitle("Jungle King");
        setSize(1200, 700); // Increased width from 1100 to 1200 to accommodate wider sidebar
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create top panel for labels
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(251, 199, 218)); // Light pink background

        // Help button panel (left side)
        JPanel helpPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2)); // Reduced padding
        helpPanel.setBackground(new Color(251, 199, 218));
        helpPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); // Remove border padding

        // Load help button icons
        helpIcon = new ImageIcon("assets/ingamehelp.png");
        helpIconHover = new ImageIcon("assets/ingamehelphover.png");

        // Create help button
        JButton helpButton = new JButton();
        helpButton.setPreferredSize(new Dimension(55, 55));
        helpButton.setBorderPainted(false);
        helpButton.setContentAreaFilled(false);
        helpButton.setFocusPainted(false);
        helpButton.setMargin(new Insets(0, 0, 0, 0)); // Remove button internal padding

        // Scale the icons to fit the button
        Image img = helpIcon.getImage().getScaledInstance(55, 55, Image.SCALE_SMOOTH);
        Image imgHover = helpIconHover.getImage().getScaledInstance(55, 55, Image.SCALE_SMOOTH);
        helpButton.setIcon(new ImageIcon(img));
        helpButton.setRolloverIcon(new ImageIcon(imgHover));

        helpButton.addActionListener(e -> {
            HowToPlay howToPlay = new HowToPlay(false);
            howToPlay.setVisible(true);
        });

        helpPanel.add(helpButton);
        topPanel.add(helpPanel, BorderLayout.WEST);

        // Center panel for turn label
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(new Color(251, 199, 218));

        // Load Pokemon font
        Font pixelFont;
        try {
            pixelFont = Font.createFont(Font.TRUETYPE_FONT, new File("assets/fonts/Pokemon Classic.ttf")).deriveFont(20f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(pixelFont);
        } catch (Exception e) {
            e.printStackTrace();
            pixelFont = new Font("Arial", Font.BOLD, 20);
        }

        // Turn label
        turnLabel = new JLabel("Player 1's Turn", SwingConstants.CENTER);
        turnLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        turnLabel.setFont(pixelFont);
        turnLabel.setForeground(new Color(106, 41, 66)); // Dark pink text color
        centerPanel.add(Box.createVerticalStrut(10)); // Add some padding
        centerPanel.add(turnLabel);
        centerPanel.add(Box.createVerticalStrut(10)); // Add some padding

        topPanel.add(centerPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        // Create main game panel with board and move history
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Game board panel
        JPanel boardPanel = new JPanel(new GridLayout(7, 9, 0, 0)); // Set grid gaps to 0
        boardPanel.setBackground(new Color(251, 199, 218)); // Set to light pink to match checkerboard
        boardPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); // Remove panel border

        boardButtons = new JButton[7][9];
        Font pieceFont = new Font("Segoe UI Emoji", Font.PLAIN, 36); // Larger font for pieces

        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 9; j++) {
                boardButtons[i][j] = new JButton();
                boardButtons[i][j].setPreferredSize(new Dimension(80, 80));
                boardButtons[i][j].setBackground((i + j) % 2 == 0 ? WARM_WHITE : LIGHT_PINK);
                boardButtons[i][j].setBorder(null); // Remove button borders completely
                boardButtons[i][j].setFont(pieceFont);
                boardButtons[i][j].setFocusPainted(false); // Disable focus painting
                final int row = i;
                final int col = j;
                boardButtons[i][j].addActionListener(e -> controller.handlePieceClick(row, col));
                boardPanel.add(boardButtons[i][j]);
            }
        }

        mainPanel.add(boardPanel, BorderLayout.CENTER);

        // Move history panel
        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBackground(new Color(251, 199, 218)); // Light pink background
        historyPanel.setBorder(BorderFactory.createEmptyBorder(-10, 10, 10, 10));
        historyPanel.setPreferredSize(new Dimension(300, 0)); // Set fixed width for history panel

        JLabel historyLabel = new JLabel("Move History", SwingConstants.CENTER);
        historyLabel.setFont(pixelFont.deriveFont(16f));
        historyLabel.setForeground(new Color(106, 41, 66)); // Dark pink text color
        historyPanel.add(historyLabel, BorderLayout.NORTH);

        moveHistoryArea = new JTextPane();
        moveHistoryArea.setEditable(false);
        moveHistoryArea.setFont(pixelFont.deriveFont(12f));
        moveHistoryArea.setBackground(new Color(255, 235, 241)); // Very light pink
        moveHistoryArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Enable word wrapping
        StyledDocument doc = moveHistoryArea.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_LEFT);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
        // Set the default text color
        Style defaultStyle = moveHistoryArea.addStyle("default", null);
        StyleConstants.setForeground(defaultStyle, new Color(106, 41, 66));

        JScrollPane scrollPane = new JScrollPane(moveHistoryArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(106, 41, 66), 2)); // Dark pink border
        historyPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(historyPanel, BorderLayout.EAST);

        add(mainPanel, BorderLayout.CENTER);
        setLocationRelativeTo(null);
        pack();
    }

    /**
     * Adds a move to the move history area.
     *
     * @param move The move to be added to the history.
     */
    public void addMoveToHistory(String move) {
        moveCount++;

        // Check if the message contains any animal being weakened by a trap
        if (move.contains(" is weakened by a trap.")) {
            // Find the animal name by looking at the start of the message up to " is weakened"
            int weakenedIndex = move.indexOf(" is weakened");
            if (weakenedIndex > 0) {
                String animalName = move.substring(0, weakenedIndex);
                // Split by the trap message for this specific animal
                String[] parts = move.split(animalName + " is weakened by a trap\\.");
                // Reconstruct with only one trap message and add space after period
                move = parts[0] + animalName + " is weakened by a trap. " + parts[parts.length - 1];
            }
        }

        // Check if the message contains a capture message
        if (move.contains(" has been captured.")) {
            // Find the piece description by looking at the start of the message up to " has been captured"
            int captureIndex = move.indexOf(" has been captured");
            if (captureIndex > 0) {
                String pieceDesc = move.substring(0, captureIndex);
                // Split by the capture message
                String[] parts = move.split(pieceDesc + " has been captured\\.");
                // Reconstruct with only one capture message and add space after period
                move = parts[0] + pieceDesc + " has been captured. " + parts[parts.length - 1];
            }
        }

        // Add space after any remaining periods that are followed by a letter
        move = move.replaceAll("\\.([A-Za-z])", ". $1");

        // Get the document and create a style
        StyledDocument doc = moveHistoryArea.getStyledDocument();
        Style style = moveHistoryArea.addStyle("moveStyle", null);

        // Check if this is a lake restriction message or capture restriction message
        String moveLower = move.toLowerCase();
        if (moveLower.contains("cannot cross") ||
                moveLower.contains("cannot land on") ||
                moveLower.contains("cannot capture") ||
                move.equals("Rat is on the lake and is unable to capture any animal!")) {
            StyleConstants.setForeground(style, new Color(29, 152, 145)); // Teal color #1d9891
        } else {
            // Get the current color based on whose turn it is
            boolean isP1Turn = controller.getModel().isPlayerOneTurn();
            Color moveColor = !isP1Turn ? new Color(189, 102, 134) : new Color(157, 127, 166); // #bd6686 for P1, #9d7fa6 for P2
            StyleConstants.setForeground(style, moveColor);
        }

        try {
            doc.insertString(doc.getLength(), moveCount + ". " + move + "\n", style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        // Scroll to the bottom
        moveHistoryArea.setCaretPosition(moveHistoryArea.getDocument().getLength());
    }

    /**
     * Adds a message to the move history area indicating that the Rat is on the lake
     * and cannot capture any animal.
     */
    public void addRatLakeCaptureMessage() {
        String message = "Rat is on the lake and is unable to capture any animal!";

        // Get the document and create a style
        StyledDocument doc = moveHistoryArea.getStyledDocument();
        Style style = moveHistoryArea.addStyle("moveStyle", null);
        StyleConstants.setForeground(style, new Color(29, 152, 145)); // Teal color #1d9891

        try {
            doc.insertString(doc.getLength(), ++moveCount + ". " + message + "\n", style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        // Scroll to the bottom
        moveHistoryArea.setCaretPosition(moveHistoryArea.getDocument().getLength());
    }

    /**
     * Updates the images of various elements on the board, scaling them according to
     * the current button size.
     */
    private void updateLakeImages() {
        // Get current button size
        if (boardButtons != null && boardButtons[0][0] != null) {
            int buttonWidth = boardButtons[0][0].getWidth();
            int buttonHeight = boardButtons[0][0].getHeight();

            // Only update if button has valid size
            if (buttonWidth > 0 && buttonHeight > 0) {
                // Scale images to match current button size
                Image scaledWhite = originalLakeImageWhite.getScaledInstance(buttonWidth, buttonHeight, Image.SCALE_SMOOTH);
                Image scaledPink = originalLakeImagePink.getScaledInstance(buttonWidth, buttonHeight, Image.SCALE_SMOOTH);
                Image scaledGrass = originalGrassImage.getScaledInstance(buttonWidth, buttonHeight, Image.SCALE_SMOOTH);
                Image scaledDirt = originalDirtImage.getScaledInstance(buttonWidth, buttonHeight, Image.SCALE_SMOOTH);

                // Scale home base images
                scaledHomeBaseP1 = scaleIcon(homeBaseP1Icon, buttonWidth, buttonHeight);
                scaledHomeBaseP2 = scaleIcon(homeBaseP2Icon, buttonWidth, buttonHeight);

                // Scale lake tiles
                scaledLake13 = scaleIcon(lake13Icon, buttonWidth, buttonHeight);
                scaledLake14 = scaleIcon(lake14Icon, buttonWidth, buttonHeight);
                scaledLake15 = scaleIcon(lake15Icon, buttonWidth, buttonHeight);
                scaledLake23 = scaleIcon(lake23Icon, buttonWidth, buttonHeight);
                scaledLake24 = scaleIcon(lake24Icon, buttonWidth, buttonHeight);
                scaledLake25 = scaleIcon(lake25Icon, buttonWidth, buttonHeight);
                scaledLake43 = scaleIcon(lake43Icon, buttonWidth, buttonHeight);
                scaledLake44 = scaleIcon(lake44Icon, buttonWidth, buttonHeight);
                scaledLake45 = scaleIcon(lake45Icon, buttonWidth, buttonHeight);
                scaledLake53 = scaleIcon(lake53Icon, buttonWidth, buttonHeight);
                scaledLake54 = scaleIcon(lake54Icon, buttonWidth, buttonHeight);
                scaledLake55 = scaleIcon(lake55Icon, buttonWidth, buttonHeight);

                // Scale trap icons
                trapIconP120 = scaleIcon(trapIconP120, buttonWidth, buttonHeight);
                trapIconP131 = scaleIcon(trapIconP131, buttonWidth, buttonHeight);
                trapIconP140 = scaleIcon(trapIconP140, buttonWidth, buttonHeight);
                trapIconP228 = scaleIcon(trapIconP228, buttonWidth, buttonHeight);
                trapIconP237 = scaleIcon(trapIconP237, buttonWidth, buttonHeight);
                trapIconP248 = scaleIcon(trapIconP248, buttonWidth, buttonHeight);

                // Scale all animal icons
                scaleAnimalIcons(buttonWidth, buttonHeight);

                scaledLakeImageWhite = new ImageIcon(scaledWhite);
                scaledLakeImagePink = new ImageIcon(scaledPink);
                scaledGrassImage = new ImageIcon(scaledGrass);
                scaledDirtImage = new ImageIcon(scaledDirt);
            }
        }
    }

    /**
     * Scales the animal icons to match the given width and height.
     *
     * @param width  The width to scale the icons to.
     * @param height The height to scale the icons to.
     */
    private void scaleAnimalIcons(int width, int height) {
        ratIconP1 = scaleIcon(ratIconP1, width, height);
        ratIconP2 = scaleIcon(ratIconP2, width, height);
        catIconP1 = scaleIcon(catIconP1, width, height);
        catIconP2 = scaleIcon(catIconP2, width, height);
        dogIconP1 = scaleIcon(dogIconP1, width, height);
        dogIconP2 = scaleIcon(dogIconP2, width, height);
        wolfIconP1 = scaleIcon(wolfIconP1, width, height);
        wolfIconP2 = scaleIcon(wolfIconP2, width, height);
        leopardIconP1 = scaleIcon(leopardIconP1, width, height);
        leopardIconP2 = scaleIcon(leopardIconP2, width, height);
        tigerIconP1 = scaleIcon(tigerIconP1, width, height);
        tigerIconP2 = scaleIcon(tigerIconP2, width, height);
        lionIconP1 = scaleIcon(lionIconP1, width, height);
        lionIconP2 = scaleIcon(lionIconP2, width, height);
        elephantIconP1 = scaleIcon(elephantIconP1, width, height);
        elephantIconP2 = scaleIcon(elephantIconP2, width, height);
    }

    /**
     * Scales an icon to the given width and height.
     *
     * @param icon  The icon to scale.
     * @param width The desired width of the scaled icon.
     * @param height The desired height of the scaled icon.
     * @return The scaled icon.
     */
    private ImageIcon scaleIcon(ImageIcon icon, int width, int height) {
        return new ImageIcon(icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
    }

    /**
     * Gets the icon associated with a specific piece and player ID.
     *
     * @param piece    The name of the piece (e.g., "Rat", "Cat", etc.).
     * @param playerId The player ID (1 or 2).
     * @return The icon for the specified piece and player.
     */
    private ImageIcon getIconForPiece(String piece, int playerId) {
        return switch (piece) {
            case "Rat" -> playerId == 1 ? ratIconP1 : ratIconP2;
            case "Cat" -> playerId == 1 ? catIconP1 : catIconP2;
            case "Dog" -> playerId == 1 ? dogIconP1 : dogIconP2;
            case "Wolf" -> playerId == 1 ? wolfIconP1 : wolfIconP2;
            case "Leopard" -> playerId == 1 ? leopardIconP1 : leopardIconP2;
            case "Tiger" -> playerId == 1 ? tigerIconP1 : tigerIconP2;
            case "Lion" -> playerId == 1 ? lionIconP1 : lionIconP2;
            case "Elephant" -> playerId == 1 ? elephantIconP1 : elephantIconP2;
            default -> null;
        };
    }

    /**
     * Updates the game board to reflect the current state.
     * This includes updating the turn label, the lake images, and the piece icons for each square.
     * It also checks for the presence of pieces (such as Rats or Traps) and updates the button's appearance accordingly.
     *
     * @param model The current GameModel representing the state of the game.
     */
    public void updateBoard(GameModel model) {
        // Update turn label with player name
        boolean isP1Turn = model.isPlayerOneTurn();
        turnLabel.setText((isP1Turn ? player1Name : player2Name) + "'s Turn");
        // Set color based on whose turn it is
        turnLabel.setForeground(isP1Turn ? new Color(147, 80, 108) : new Color(157, 127, 166)); // #93506c for P1, #9d7fa6 for P2

        // Update lake images if needed
        updateLakeImages();

        for (int row = 0; row < 7; row++) {
            for (int col = 0; col < 9; col++) {
                String piece = model.getPieceAt(row, col);
                JButton button = boardButtons[row][col];

                Square square = model.getBoard().getSquare(row, col);
                //set checkerboard pattern
                if (square == null) {
                    if ((row + col) % 2 == 0) {
                        button.setBackground(null);
                        button.setIcon(scaledGrassImage);
                    } else {
                        button.setBackground(null);
                        button.setIcon(scaledDirtImage);
                    }
                    button.setText("");
                    continue;
                }

                //even if rat is on water, lake bg is still image
                Terrain squared = square.getTerrain();
                if (squared != null && squared.isLake()) {
                    // Set the specific lake tile based on position
                    ImageIcon lakeIcon = null;
                    if (row == 1 && col == 3) lakeIcon = scaledLake13;
                    else if (row == 1 && col == 4) lakeIcon = scaledLake14;
                    else if (row == 1 && col == 5) lakeIcon = scaledLake15;
                    else if (row == 2 && col == 3) lakeIcon = scaledLake23;
                    else if (row == 2 && col == 4) lakeIcon = scaledLake24;
                    else if (row == 2 && col == 5) lakeIcon = scaledLake25;
                    else if (row == 4 && col == 3) lakeIcon = scaledLake43;
                    else if (row == 4 && col == 4) lakeIcon = scaledLake44;
                    else if (row == 4 && col == 5) lakeIcon = scaledLake45;
                    else if (row == 5 && col == 3) lakeIcon = scaledLake53;
                    else if (row == 5 && col == 4) lakeIcon = scaledLake54;
                    else if (row == 5 && col == 5) lakeIcon = scaledLake55;

                    if (lakeIcon != null) {
                        button.setIcon(lakeIcon);
                    } else {
                        button.setIcon(null);
                        button.setBackground(PASTEL_BLUE);
                    }
                    button.setBackground((row + col) % 2 == 0 ? WARM_WHITE : LIGHT_PINK);

                    // If there's a rat on the lake
                    if (piece != null && piece.equals("Rat")) {
                        button.setText("");
                        // Create a layered icon with lake and rat
                        if (model.getBoard().getSquare(row, col).getPiece().getPlayer().getId() == 1) {
                            ImageIcon ratIcon = ratIconP1;
                            if (button.getIcon() != null) {
                                // Create composite icon
                                Image lakeImg = ((ImageIcon)button.getIcon()).getImage();
                                Image ratImg = ratIcon.getImage();
                                BufferedImage combined = new BufferedImage(
                                        lakeImg.getWidth(null),
                                        lakeImg.getHeight(null),
                                        BufferedImage.TYPE_INT_ARGB
                                );
                                Graphics g = combined.getGraphics();
                                g.drawImage(lakeImg, 0, 0, null);
                                g.drawImage(ratImg, 0, 0, null);
                                g.dispose();
                                button.setIcon(new ImageIcon(combined));
                            }
                        } else {
                            ImageIcon ratIcon = ratIconP2;
                            if (button.getIcon() != null) {
                                // Create composite icon
                                Image lakeImg = ((ImageIcon)button.getIcon()).getImage();
                                Image ratImg = ratIcon.getImage();
                                BufferedImage combined = new BufferedImage(
                                        lakeImg.getWidth(null),
                                        lakeImg.getHeight(null),
                                        BufferedImage.TYPE_INT_ARGB
                                );
                                Graphics g = combined.getGraphics();
                                g.drawImage(lakeImg, 0, 0, null);
                                g.drawImage(ratImg, 0, 0, null);
                                g.dispose();
                                button.setIcon(new ImageIcon(combined));
                            }
                        }
                    }
                    continue;
                }

                // Reset transparency for non-lake tiles
                button.setContentAreaFilled(true);
                button.setOpaque(true);
                button.setIcon(null);

                if (piece != null) {
                    if (piece.equals("X")) {
                        ImageIcon trapIcon = null;
                        // Determine which trap icon to use based on position
                        if (row == 2 && col == 0) trapIcon = trapIconP120;
                        else if (row == 3 && col == 1) trapIcon = trapIconP131;
                        else if (row == 4 && col == 0) trapIcon = trapIconP140;
                        else if (row == 2 && col == 8) trapIcon = trapIconP228;
                        else if (row == 3 && col == 7) trapIcon = trapIconP237;
                        else if (row == 4 && col == 8) trapIcon = trapIconP248;

                        if (trapIcon != null) {
                            if ((row + col) % 2 == 0) {
                                button.setBackground(null);
                                // Create composite icon with grass background and trap
                                Image grassImg = scaledGrassImage.getImage();
                                Image trapImg = trapIcon.getImage();
                                BufferedImage combined = new BufferedImage(
                                        grassImg.getWidth(null),
                                        grassImg.getHeight(null),
                                        BufferedImage.TYPE_INT_ARGB
                                );
                                Graphics g = combined.getGraphics();
                                g.drawImage(grassImg, 0, 0, null);
                                g.drawImage(trapImg, 0, 0, null);
                                g.dispose();
                                button.setIcon(new ImageIcon(combined));
                            } else {
                                button.setBackground(null);
                                // Create composite icon with dirt background and trap
                                Image dirtImg = scaledDirtImage.getImage();
                                Image trapImg = trapIcon.getImage();
                                BufferedImage combined = new BufferedImage(
                                        dirtImg.getWidth(null),
                                        dirtImg.getHeight(null),
                                        BufferedImage.TYPE_INT_ARGB
                                );
                                Graphics g = combined.getGraphics();
                                g.drawImage(dirtImg, 0, 0, null);
                                g.drawImage(trapImg, 0, 0, null);
                                g.dispose();
                                button.setIcon(new ImageIcon(combined));
                            }
                            button.setText("");
                        } else {  // Any other X positions still use flag emoji
                            if ((row + col) % 2 == 0) {
                                button.setBackground(null);
                                button.setIcon(scaledGrassImage);
                            } else {
                                button.setBackground(null);
                                button.setIcon(scaledDirtImage);
                            }
                            button.setForeground(Color.RED);
                            button.setText("🚩");
                        }
                    } else if (piece.contains("~")) {
                        button.setBackground(PASTEL_BLUE);
                        button.setText("");
                        button.setIcon(null);
                    } else {
                        if ((row + col) % 2 == 0) {
                            button.setBackground(null);
                            ImageIcon pieceIcon = getIconForPiece(piece, model.getBoard().getSquare(row, col).getPiece().getPlayer().getId());
                            if (pieceIcon != null) {
                                // Create composite icon with grass background and piece
                                Image grassImg = scaledGrassImage.getImage();
                                Image animalImg = pieceIcon.getImage();
                                BufferedImage combined = new BufferedImage(
                                        grassImg.getWidth(null),
                                        grassImg.getHeight(null),
                                        BufferedImage.TYPE_INT_ARGB
                                );
                                Graphics g = combined.getGraphics();
                                g.drawImage(grassImg, 0, 0, null);
                                g.drawImage(animalImg, 0, 0, null);
                                g.dispose();
                                button.setIcon(new ImageIcon(combined));
                            } else {
                                button.setIcon(scaledGrassImage);
                            }
                        } else {
                            button.setBackground(null);
                            ImageIcon pieceIcon = getIconForPiece(piece, model.getBoard().getSquare(row, col).getPiece().getPlayer().getId());
                            if (pieceIcon != null) {
                                // Create composite icon with dirt background and piece
                                Image dirtImg = scaledDirtImage.getImage();
                                Image animalImg = pieceIcon.getImage();
                                BufferedImage combined = new BufferedImage(
                                        dirtImg.getWidth(null),
                                        dirtImg.getHeight(null),
                                        BufferedImage.TYPE_INT_ARGB
                                );
                                Graphics g = combined.getGraphics();
                                g.drawImage(dirtImg, 0, 0, null);
                                g.drawImage(animalImg, 0, 0, null);
                                g.dispose();
                                button.setIcon(new ImageIcon(combined));
                            } else {
                                button.setIcon(scaledDirtImage);
                            }
                        }
                        button.setText("");
                    }
                } else {
                    // Check if this is a home base square
                    if (square != null && (square.isHomeBase(model.getBoard().getPlayer1()) ||
                            square.isHomeBase(model.getBoard().getPlayer2()))) {
                        button.setText("");
                        if ((row + col) % 2 == 0) {
                            button.setBackground(null);
                            // Create composite icon with grass background and home base
                            Image grassImg = scaledGrassImage.getImage();
                            Image homeBaseImg = square.isHomeBase(model.getBoard().getPlayer1()) ?
                                    scaledHomeBaseP1.getImage() :
                                    scaledHomeBaseP2.getImage();
                            BufferedImage combined = new BufferedImage(
                                    grassImg.getWidth(null),
                                    grassImg.getHeight(null),
                                    BufferedImage.TYPE_INT_ARGB
                            );
                            Graphics g = combined.getGraphics();
                            g.drawImage(grassImg, 0, 0, null);
                            g.drawImage(homeBaseImg, 0, 0, null);
                            g.dispose();
                            button.setIcon(new ImageIcon(combined));
                        } else {
                            button.setBackground(null);
                            // Create composite icon with dirt background and home base
                            Image dirtImg = scaledDirtImage.getImage();
                            Image homeBaseImg = square.isHomeBase(model.getBoard().getPlayer1()) ?
                                    scaledHomeBaseP1.getImage() :
                                    scaledHomeBaseP2.getImage();
                            BufferedImage combined = new BufferedImage(
                                    dirtImg.getWidth(null),
                                    dirtImg.getHeight(null),
                                    BufferedImage.TYPE_INT_ARGB
                            );
                            Graphics g = combined.getGraphics();
                            g.drawImage(dirtImg, 0, 0, null);
                            g.drawImage(homeBaseImg, 0, 0, null);
                            g.dispose();
                            button.setIcon(new ImageIcon(combined));
                        }
                    } else {
                        button.setText("");
                        if ((row + col) % 2 == 0) {
                            button.setBackground(null);
                            button.setIcon(scaledGrassImage);
                        } else {
                            button.setBackground(null);
                            button.setIcon(scaledDirtImage);
                        }
                    }
                }
            }
        }
        repaint();
    }

    /**
     * Highlights the selected piece on the board.
     *
     * @param row The row of the selected piece.
     * @param col The column of the selected piece.
     */
    public void highlightSelectedPiece(int row, int col) {
        // Reset previous selection
        if (selectedButton != null) {
            int prevRow = -1, prevCol = -1;
            // Find the previous button's position
            for (int i = 0; i < 7; i++) {
                for (int j = 0; j < 9; j++) {
                    if (boardButtons[i][j] == selectedButton) {
                        prevRow = i;
                        prevCol = j;
                        break;
                    }
                }
                if (prevRow != -1) break;
            }
            selectedButton.setBorder(null);
            selectedButton.setBackground((prevRow + prevCol) % 2 == 0 ? WARM_WHITE : LIGHT_PINK);
        }

        // Highlight new selection
        selectedButton = boardButtons[row][col];
        Square square = controller.getModel().getBoard().getSquare(row, col);
        if (square != null && square.getPiece() != null) {
            // Create a line border with the appropriate color based on the player
            Color strokeColor = square.getPiece().getPlayer().getId() == 1 ? PLAYER1_STROKE : PLAYER2_STROKE;
            selectedButton.setBorder(BorderFactory.createLineBorder(strokeColor, 3));
        }
    }

    /**
     * Clears the current selection from the board.
     */
    public void clearSelection() {
        if (selectedButton != null) {
            int row = -1, col = -1;
            // Find the button's position
            for (int i = 0; i < 7; i++) {
                for (int j = 0; j < 9; j++) {
                    if (boardButtons[i][j] == selectedButton) {
                        row = i;
                        col = j;
                        break;
                    }
                }
                if (row != -1) break;
            }
            selectedButton.setBorder(null);
            selectedButton.setBackground((row + col) % 2 == 0 ? WARM_WHITE : LIGHT_PINK);
            selectedButton = null;
        }
    }

    /**
     * Sets the names of the two players and updates the turn label accordingly.
     *
     * @param p1Name The name of player 1.
     * @param p2Name The name of player 2.
     */
    public void setPlayerNames(String p1Name, String p2Name) {
        this.player1Name = p1Name;
        this.player2Name = p2Name;
        controller.getModel().setPlayerNames(p1Name, p2Name);
        updateTurnLabel();
    }
    
    /**
     * Updates the turn label to reflect whose turn it is, changes color based on whose turn it is.
     * 
     */
    private void updateTurnLabel() {
        if (turnLabel != null) {
            boolean isP1Turn = controller.getModel().isPlayerOneTurn();
            turnLabel.setText((isP1Turn ? player1Name : player2Name) + "'s Turn");
            // Set color based on whose turn it is
            turnLabel.setForeground(isP1Turn ? new Color(147, 80, 108) : new Color(157, 127, 166)); // #93506c for P1, #9d7fa6 for P2
        }
    }
}
