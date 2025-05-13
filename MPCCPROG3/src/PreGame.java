import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * The PreGame class represents the player setup screen for the Jungle King game.
 * It allows players to enter their names and select their pieces (Pokémon).
 */
public class PreGame extends JFrame {
    private final String[] PIECES = {"Copperajah", "Pyroar", "Arcanine", "Liepard", "Poochyena", "Fidough", "Skitty", "Dedenne"};
    private final String[] PIECE_NAMES = {"Elephant", "Lion", "Tiger", "Leopard", "Wolf", "Dog", "Cat", "Rat"};
    private final String[] POKEBALLS = {
            "pokeball", "greatball", "ultraball", "masterball",
            "pinkball", "premierball", "loveball", "healball"
    };
    private final HashMap<String, Integer> PIECE_POWER = new HashMap<>();
    private JButton[] pokeballButtons;
    private String[] randomizedPieces; // Array to hold randomized piece assignments
    private String player1Choice = null;
    private String player2Choice = null;
    private int player1Index = -1; // Store the index of player 1's choice
    private int player2Index = -1; // Store the index of player 2's choice
    private JLabel statusLabel;
    private String player1Name = "";
    private String player2Name = "";
    private JPanel namePanel;
    private JPanel selectionPanel;
    private Font pixelFont;
    private ImageIcon backgroundImage;
    private ImageIcon textboxImage;
    private BufferedImage backgroundBuffered;
    private JPanel textBoxPanel;
    private JLabel messageLabel;
    private Timer typingTimer;
    private String fullMessage = "";
    private int currentCharIndex = 0;
    private static final int TYPING_SPEED = 50; // milliseconds per character
    private Timer repaintTimer;

    /**
     * Converts a given Image to a BufferedImage.
     *
     * @param img The Image to be converted.
     * @return The corresponding BufferedImage.
     */
    private BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }
        BufferedImage bimage = new BufferedImage(
                img.getWidth(null),
                img.getHeight(null),
                BufferedImage.TYPE_INT_ARGB
        );
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();
        return bimage;
    }

    /**
     * Initializes the PreGame setup with necessary UI components and assets.
     * It randomizes the piece assignments, loads the font, background images,
     * and prepares the window for player name input.
     */
    public PreGame() {
        // Initialize piece power rankings
        for (int i = 0; i < PIECES.length; i++) {
            PIECE_POWER.put(PIECES[i], PIECES.length - i);
        }

        // Create and shuffle the pieces array
        randomizePieces();

        // Load custom font
        try {
            pixelFont = Font.createFont(Font.TRUETYPE_FONT, new File("assets/fonts/Pokemon Classic.ttf"));
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(pixelFont);
        } catch (Exception e) {
            e.printStackTrace();
            pixelFont = new Font("Arial", Font.PLAIN, 12);
        }

        // Load and prepare background image
        try {
            backgroundImage = new ImageIcon("assets/menubackground.png");
            Image img = backgroundImage.getImage();
            MediaTracker tracker = new MediaTracker(this);
            tracker.addImage(img, 0);
            tracker.waitForAll();
            backgroundBuffered = toBufferedImage(img);
            backgroundImage = new ImageIcon(backgroundBuffered);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading background image");
        }

        // Load textbox image
        textboxImage = new ImageIcon("assets/textbox.png");

        setupWindow();
        showNameInput();

        // Setup repaint timer
        repaintTimer = new Timer(100, e -> {
            if (isVisible()) {
                repaint();
                Toolkit.getDefaultToolkit().sync();
            }
        });
        repaintTimer.start();
    }

    /**
     * Sets up the main window with a custom background and status label.
     */
    private void setupWindow() {
        setTitle("Jungle King - Player Setup");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create custom background panel
        JPanel backgroundPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundBuffered != null) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                    // Calculate scaling to fill width while maintaining aspect ratio
                    double widthRatio = getWidth() / (double) backgroundBuffered.getWidth();
                    double heightRatio = getHeight() / (double) backgroundBuffered.getHeight();
                    double scale = Math.max(widthRatio, heightRatio);

                    int scaledWidth = (int) (backgroundBuffered.getWidth() * scale);
                    int scaledHeight = (int) (backgroundBuffered.getHeight() * scale);
                    int x = (getWidth() - scaledWidth) / 2;
                    int y = (getHeight() - scaledHeight) / 2;

                    g2d.drawImage(backgroundBuffered, x, y, scaledWidth, scaledHeight, this);
                } else {
                    g.setColor(Color.BLACK);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        setContentPane(backgroundPanel);

        // Status label with custom font
        statusLabel = new JLabel("Enter Player Names", SwingConstants.CENTER);
        statusLabel.setFont(pixelFont.deriveFont(20f));
        statusLabel.setForeground(new Color(247, 237, 163));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));  // Add 10px top margin
        add(statusLabel, BorderLayout.NORTH);

        // Force initial validation and repaint
        backgroundPanel.revalidate();
        backgroundPanel.repaint();
        this.revalidate();
        this.repaint();
        Toolkit.getDefaultToolkit().sync();
    }

    /**
     * Displays the player name input panels where players can input their names.
     */
    private void showNameInput() {
        // Create name input panel with transparent background
        namePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setOpaque(false);
            }
        };
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
        namePanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        namePanel.setOpaque(false);

        // Player 1 name input with transparent background
        JPanel p1Panel = new JPanel(new FlowLayout(FlowLayout.CENTER)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setOpaque(false);
            }
        };
        JLabel p1Label = new JLabel("Player 1 Name: ");
        p1Label.setForeground(new Color(250, 241, 173));  // #faf1ad
        p1Label.setFont(pixelFont.deriveFont(16f));
        JTextField p1Field = new JTextField(15);
        p1Field.setFont(pixelFont.deriveFont(16f));
        p1Field.setBackground(new Color(255, 255, 245));  // Very pale yellow
        p1Field.setForeground(new Color(106, 41, 66));  // #6a2942
        p1Panel.add(p1Label);
        p1Panel.add(p1Field);
        p1Panel.setOpaque(false);

        // Player 2 name input with transparent background
        JPanel p2Panel = new JPanel(new FlowLayout(FlowLayout.CENTER)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setOpaque(false);
            }
        };
        JLabel p2Label = new JLabel("Player 2 Name: ");
        p2Label.setForeground(new Color(250, 241, 173));  // #faf1ad
        p2Label.setFont(pixelFont.deriveFont(16f));
        JTextField p2Field = new JTextField(15);
        p2Field.setFont(pixelFont.deriveFont(16f));
        p2Field.setBackground(new Color(255, 255, 245));  // Very pale yellow
        p2Field.setForeground(new Color(106, 41, 66));  // #6a2942
        p2Panel.add(p2Label);
        p2Panel.add(p2Field);
        p2Panel.setOpaque(false);

        // Continue button with custom images
        ImageIcon normalIcon = new ImageIcon("assets/pokeballselect.png");
        ImageIcon hoverIcon = new ImageIcon("assets/pokeballselecthover.png");
        JLabel continueButton = new JLabel(normalIcon);
        continueButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        continueButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                continueButton.setIcon(hoverIcon);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                continueButton.setIcon(normalIcon);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                String p1Name = p1Field.getText().trim();
                String p2Name = p2Field.getText().trim();

                if (p1Name.isEmpty() || p2Name.isEmpty()) {
                    showStyledMessageDialog(
                        PreGame.this,
                        "Please enter names for both players!",
                        "Missing Names",
                        JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }

                player1Name = p1Name;
                player2Name = p2Name;

                // Switch to Pokéball selection
                remove(namePanel);
                setupPokemonSelection();
                revalidate();
                repaint();
            }
        });

        // Center the continue button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setOpaque(false);
            }
        };
        buttonPanel.setOpaque(false);
        buttonPanel.add(continueButton);

        // Add components to name panel
        namePanel.add(p1Panel);
        namePanel.add(Box.createRigidArea(new Dimension(0, 20)));
        namePanel.add(p2Panel);
        namePanel.add(Box.createRigidArea(new Dimension(0, 40)));
        namePanel.add(buttonPanel);

        // Add name panel to frame
        add(namePanel, BorderLayout.CENTER);
    }

    /**
     * Shows a styled message dialog with Pokemon font and custom colors.
     *
     * @param parentComponent the parent component of the dialog
     * @param message the message to display
     * @param title the title of the dialog
     * @param messageType the type of message (e.g., WARNING_MESSAGE)
     */
    private void showStyledMessageDialog(Component parentComponent, String message, String title, int messageType) {
        // Create a styled label for the message
        JLabel label = new JLabel("<html><div style='text-align: center;'>" + message + "</div></html>");
        label.setFont(pixelFont.deriveFont(16f));
        label.setForeground(new Color(106, 41, 66));  // #6a2942 - Dark pink
        
        // Create the option pane with custom styling
        JOptionPane pane = new JOptionPane(
            label,
            messageType,
            JOptionPane.DEFAULT_OPTION
        );
        
        // Create and style the dialog
        JDialog dialog = pane.createDialog(parentComponent, title);
        
        // Style the title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(pixelFont.deriveFont(18f));
        titleLabel.setForeground(new Color(106, 41, 66));
        
        // Get the title bar and set its font
        try {
            // Try to access the title pane through the root pane
            JRootPane root = dialog.getRootPane();
            Container titlePane = ((JComponent) root.getComponent(1));
            
            // Look for the title label in the title pane
            for (Component comp : titlePane.getComponents()) {
                if (comp instanceof JLabel) {
                    ((JLabel) comp).setFont(pixelFont.deriveFont(18f));
                    ((JLabel) comp).setForeground(new Color(106, 41, 66));
                    break;
                }
            }
        } catch (Exception e) {
            // If we can't style the title, just continue
            e.printStackTrace();
        }
        
        // Set dialog background
        Container contentPane = dialog.getContentPane();
        contentPane.setBackground(new Color(255, 247, 243));  // Warm white
        
        // Style the OK button
        for (Component comp : ((JOptionPane) contentPane.getComponent(0)).getComponents()) {
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                button.setFont(pixelFont.deriveFont(16f));
                button.setForeground(new Color(106, 41, 66));
                button.setBackground(new Color(251, 199, 218));  // Light pink
                button.setFocusPainted(false);
                button.setBorderPainted(false);
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
                
                // Add hover effect
                button.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        button.setBackground(new Color(230, 178, 186));  // Darker pink on hover
                    }
                    
                    @Override
                    public void mouseExited(MouseEvent e) {
                        button.setBackground(new Color(251, 199, 218));  // Original light pink
                    }
                });
            }
        }
        
        dialog.setVisible(true);
    }

    /**
     * Sets up the Pokémon selection screen, including randomizing pieces,
     * creating and styling the selection grid, and adding functionality to the
     * Pokémon selection buttons.
     */
    private void setupPokemonSelection() {
        // Randomize pieces again when setting up selection
        randomizePieces();

        setTitle("Jungle King - Choose Your Pokéball");
        statusLabel.setText(player1Name + ", choose a Pokéball!");

        // Main selection panel
        selectionPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setOpaque(false);
            }
        };
        selectionPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Create a wrapper panel to center the grid
        JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setOpaque(false);
            }
        };

        // Pokéball grid panel with adjusted spacing
        JPanel pokeballGrid = new JPanel(new GridLayout(2, 4, 30, 30)) {  // Increased spacing between buttons
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setOpaque(false);
            }
        };
        pokeballGrid.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));  // Adjusted padding

        // Create Pokéball buttons with scaled images
        pokeballButtons = new JButton[8];
        for (int i = 0; i < 8; i++) {
            final int index = i;

            // Create a layered pane for each pokeball slot
            JLayeredPane layeredPane = new JLayeredPane();
            layeredPane.setPreferredSize(new Dimension(100, 100));  // Increased size of layered pane

            pokeballButtons[i] = new JButton();
            pokeballButtons[i].setContentAreaFilled(false);
            pokeballButtons[i].setBorderPainted(false);
            pokeballButtons[i].setFocusPainted(false);

            // Load and scale images
            ImageIcon normalIcon = new ImageIcon("assets/pokeballs/" + POKEBALLS[i] + ".png");
            ImageIcon hoverIcon = new ImageIcon("assets/pokeballs/" + POKEBALLS[i] + "hover.png");

            // Scale images to fit window
            Image normalImg = normalIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);  // Adjusted size
            Image hoverImg = hoverIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);  // Adjusted size

            pokeballButtons[i].setIcon(new ImageIcon(normalImg));
            pokeballButtons[i].setRolloverIcon(new ImageIcon(hoverImg));
            pokeballButtons[i].setCursor(new Cursor(Cursor.HAND_CURSOR));

            // Center the button in the layered pane
            pokeballButtons[i].setBounds(10, 10, 80, 80);  // Adjusted position and size
            layeredPane.add(pokeballButtons[i], JLayeredPane.DEFAULT_LAYER);

            pokeballButtons[i].addActionListener(e -> handlePokemonSelection(index, layeredPane));
            final ImageIcon finalNormalIcon = new ImageIcon(normalImg);
            final ImageIcon finalHoverIcon = new ImageIcon(hoverImg);

            pokeballButtons[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    pokeballButtons[index].setIcon(finalHoverIcon);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    pokeballButtons[index].setIcon(finalNormalIcon);
                }
            });

            pokeballGrid.add(layeredPane);
        }

        // Text box panel with scaled dimensions
        textBoxPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setOpaque(false);
                // Scale textbox image to fit window
                int maxWidth = 500; // Reduced from 600 to 400
                int maxHeight = 180; // Reduced from 200 to 150

                double scale = Math.min(
                        maxWidth / (double) textboxImage.getIconWidth(),
                        maxHeight / (double) textboxImage.getIconHeight()
                );

                int scaledWidth = (int) (textboxImage.getIconWidth() * scale);
                int scaledHeight = (int) (textboxImage.getIconHeight() * scale);
                int x = (getWidth() - scaledWidth) / 2;

                g.drawImage(textboxImage.getImage(), x, 0, scaledWidth, scaledHeight, this);
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(400, 150);  // Reduced from 600x200 to 400x150
            }

            @Override
            public Dimension getMinimumSize() {
                return getPreferredSize();
            }

            @Override
            public Dimension getMaximumSize() {
                return getPreferredSize();
            }
        };
        textBoxPanel.setOpaque(false);

        // Message label with adjusted font size and HTML formatting for line breaks
        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setFont(pixelFont.deriveFont(14f));
        messageLabel.setForeground(new Color(250, 241, 173));  // #faf1ad
        messageLabel.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));  // Reduced top padding from 30 to 25
        textBoxPanel.add(messageLabel, BorderLayout.CENTER);

        // Initially hide the text box panel
        textBoxPanel.setVisible(false);

        // Add the grid to the wrapper and the wrapper to the selection panel
        centerWrapper.add(pokeballGrid);
        selectionPanel.add(centerWrapper, BorderLayout.CENTER);
        selectionPanel.add(textBoxPanel, BorderLayout.SOUTH);

        // Add the selection panel to the frame
        add(selectionPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    /**
     * Randomizes the pieces by shuffling their order.
     */
    private void randomizePieces() {
        // Create a copy of the PIECES array
        randomizedPieces = PIECES.clone();
        // Create a list for shuffling
        ArrayList<String> piecesList = new ArrayList<>();
        for (String piece : randomizedPieces) {
            piecesList.add(piece);
        }
        // Shuffle the list
        Collections.shuffle(piecesList);
        // Copy back to array
        for (int i = 0; i < randomizedPieces.length; i++) {
            randomizedPieces[i] = piecesList.get(i);
        }
    }

    /**
     * Starts the typing animation for displaying the message.
     * @param message The message to display.
     * @param p1First Boolean indicating whether player 1 goes first.
     */
    private void startTypingAnimation(String message, boolean p1First) {
        fullMessage = message;
        currentCharIndex = 0;
        StringBuilder visibleText = new StringBuilder("<html><div style='text-align: center; width: 500px;'>");
        String contentToType = message.replaceAll("<[^>]*>", ""); // Remove HTML tags for typing

        if (typingTimer != null && typingTimer.isRunning()) {
            typingTimer.stop();
        }

        typingTimer = new Timer(TYPING_SPEED, new ActionListener() {
            private int textIndex = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (textIndex < contentToType.length()) {
                    visibleText.append(contentToType.charAt(textIndex++));
                    // Add line breaks back in
                    String displayText = visibleText.toString()
                            .replace("\n", "<br>")
                            + "</div></html>";
                    messageLabel.setText(displayText);
                } else {
                    ((Timer)e.getSource()).stop();

                    // Create and style the start button with custom images
                    ImageIcon normalIcon = new ImageIcon("assets/startgame.png");
                    ImageIcon hoverIcon = new ImageIcon("assets/startgamehover.png");
                    JLabel startButton = new JLabel(normalIcon);
                    startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

                    // Add hover effect
                    startButton.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseEntered(MouseEvent e) {
                            startButton.setIcon(hoverIcon);
                        }

                        @Override
                        public void mouseExited(MouseEvent e) {
                            startButton.setIcon(normalIcon);
                        }

                        @Override
                        public void mouseClicked(MouseEvent e) {
                            startMainGame(player1Choice, player2Choice, p1First);
                        }
                    });

                    // Remove any existing glass pane
                    if (getGlassPane() != null) {
                        getGlassPane().setVisible(false);
                    }

                    // Create a new glass pane for the button
                    JPanel glassPane = new JPanel(null) {
                        @Override
                        protected void paintComponent(Graphics g) {
                            super.paintComponent(g);
                            setOpaque(false);
                        }
                    };
                    glassPane.setOpaque(false);

                    // Position the button in the absolute center
                    startButton.setSize(normalIcon.getIconWidth(), normalIcon.getIconHeight());
                    int buttonX = (getWidth() - startButton.getWidth()) / 2;
                    int buttonY = (getHeight() - startButton.getHeight()) / 2;
                    startButton.setBounds(buttonX, buttonY, startButton.getWidth(), startButton.getHeight());

                    glassPane.add(startButton);
                    setGlassPane(glassPane);
                    glassPane.setVisible(true);
                }
            }
        });
        typingTimer.start();
    }

    /**
     * Disables all the Pokéball buttons and changes the cursor to the default.
     */
    private void disableAllPokeballs() {
        for (JButton button : pokeballButtons) {
            button.setEnabled(false);
            button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }

    /**
     * Handles the selection of a Pokémon by the player.
     * @param index The index of the selected Pokéball.
     * @param layeredPane The layered pane where the Pokéball is displayed.
     */
    private void handlePokemonSelection(int index, JLayeredPane layeredPane) {
        String selectedPokeball = POKEBALLS[index];
        pokeballButtons[index].setEnabled(false);

        if (player1Choice == null) {
            player1Choice = randomizedPieces[index];
            player1Index = index;

            // Show Player 1's animal icon
            String animalName = PIECE_NAMES[PIECES.length - PIECE_POWER.get(player1Choice)];
            showAnimalIcon(layeredPane, animalName, "p1");

            statusLabel.setText(player2Name + ", choose a Pokéball!");
        } else {
            player2Choice = randomizedPieces[index];
            player2Index = index;

            if (player1Index == player2Index) {
                // Reset second player's choice and re-enable their button
                pokeballButtons[index].setEnabled(true);
                player2Choice = null;
                player2Index = -1;
                statusLabel.setText(player2Name + ", choose a Pokéball!");
                JOptionPane.showMessageDialog(this,
                        "Please choose a different Pokéball!",
                        "Invalid Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Show Player 2's animal icon
            String animalName = PIECE_NAMES[PIECES.length - PIECE_POWER.get(player2Choice)];
            showAnimalIcon(layeredPane, animalName, "p2");

            // Disable all Pokéball buttons
            disableAllPokeballs();

            // Show text box and start animation
            textBoxPanel.setVisible(true);
            String player1Piece = PIECE_NAMES[PIECES.length - PIECE_POWER.get(player1Choice)];
            String player2Piece = PIECE_NAMES[PIECES.length - PIECE_POWER.get(player2Choice)];
            boolean p1First = PIECE_POWER.get(player1Choice) > PIECE_POWER.get(player2Choice);

            String message = String.format(
                    "%s chose %s (%s)!\n%s chose %s (%s)!\n\n%s will go first!",
                    player1Name, PIECES[PIECES.length - PIECE_POWER.get(player1Choice)], player1Piece,
                    player2Name, PIECES[PIECES.length - PIECE_POWER.get(player2Choice)], player2Piece,
                    p1First ? player1Name : player2Name
            );

            startTypingAnimation(message, p1First);
        }
    }

    /**
     * Displays the animal icon in the center of the selected Pokéball.
     * @param layeredPane The layered pane where the icon will be displayed.
     * @param animalName The name of the animal.
     * @param player The player identifier ("p1" or "p2").
     */
    private void showAnimalIcon(JLayeredPane layeredPane, String animalName, String player) {
        try {
            ImageIcon animalIcon = new ImageIcon("assets/" + animalName + player + ".png");
            Image scaledAnimal = animalIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);  // Increased from 50x50 to 65x65
            JLabel animalLabel = new JLabel(new ImageIcon(scaledAnimal));

            // Position the animal icon in the center of the pokeball
            // LayeredPane is 100x100, animal is 65x65, so we need to offset by 17.5 pixels
            animalLabel.setBounds(17, 17, 65, 65);  // Adjusted position for larger size
            layeredPane.add(animalLabel, JLayeredPane.POPUP_LAYER);
            layeredPane.moveToFront(animalLabel);
            layeredPane.revalidate();
            layeredPane.repaint();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading animal icon: " + animalName + player);
        }
    }

    /**
     * Starts the main game after Pokémon selection.
     * @param p1Piece The piece chosen by Player 1.
     * @param p2Piece The piece chosen by Player 2.
     * @param p1First Boolean indicating whether Player 1 goes first.
     */
    private void startMainGame(String p1Piece, String p2Piece, boolean p1First) {
        dispose(); // Close PreGame window
        SwingUtilities.invokeLater(() -> {
            GameModel model = new GameModel();
            // Initialize with the correct first player based on piece strength
            model.initializeBoard(p1First);
            GameController controller = new GameController(model);
            GameView view = controller.getView();
            model.setView(view); // Set the view in the model
            view.setPlayerNames(player1Name, player2Name); // Set player names
            view.updateBoard(model); // Make sure board is updated
            view.setVisible(true);
        });
    }
}
