import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
/**
 * Represents the menu screen for the game.
 * This screen provides three buttons: Start, How to Play, and Quit.
 * The buttons change appearance and mode when hovered and perform actions when clicked.
 */
public class MenuScreen extends JFrame {
    private ImageIcon backgroundImage;
    private ImageIcon startButton, startButtonHover;
    private ImageIcon howToPlayButton, howToPlayButtonHover;
    private ImageIcon quitButton, quitButtonHover;
    private JLabel startLabel, howToPlayLabel, quitLabel;
    private Dimension originalButtonSize;

    /**
     * Constructs the MenuScreen for the Jungle King game.
     * It initializes the frame, background, and buttons, setting up
     * the layout and listeners for user interaction.
     */
    public MenuScreen() {
        setTitle("Jungle King");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 600));

        backgroundImage = new ImageIcon("assets/menubackground.png");
        startButton = new ImageIcon("assets/start.png");
        startButtonHover = new ImageIcon("assets/start_hover.png");
        howToPlayButton = new ImageIcon("assets/howtoplay.png");
        howToPlayButtonHover = new ImageIcon("assets/howtoplay_hover.png");
        quitButton = new ImageIcon("assets/quit.png");
        quitButtonHover = new ImageIcon("assets/quit_hover.png");

        originalButtonSize = new Dimension(startButton.getIconWidth(), startButton.getIconHeight());

        JPanel mainPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        
        startLabel = createButton(startButton, startButtonHover);
        howToPlayLabel = createButton(howToPlayButton, howToPlayButtonHover);
        quitLabel = createButton(quitButton, quitButtonHover);

        positionButtons(getWidth(), getHeight());

        mainPanel.add(startLabel);
        mainPanel.add(howToPlayLabel);
        mainPanel.add(quitLabel);

        add(mainPanel);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                positionButtons(getWidth(), getHeight());
                mainPanel.repaint();
            }
        });
    }

    /**
     * Positions the buttons dynamically based on the window size.
     * The buttons are scaled and arranged vertically in the center of the window.
     *
     * @param width The current width of the window.
     * @param height The current height of the window.
     */
    private void positionButtons(int width, int height) {
        double scaleFactor = Math.min(width / 1200.0, height / 700.0);
        scaleFactor = Math.max(scaleFactor, 0.3); // Ensure buttons are not too small

        int buttonWidth = (int)(originalButtonSize.width * scaleFactor);
        int buttonHeight = (int)(originalButtonSize.height * scaleFactor);

        ImageIcon scaledStart = scaleIcon(startButton, buttonWidth, buttonHeight);
        ImageIcon scaledHowToPlay = scaleIcon(howToPlayButton, buttonWidth, buttonHeight);
        ImageIcon scaledQuit = scaleIcon(quitButton, buttonWidth, buttonHeight);
        ImageIcon scaledStartHover = scaleIcon(startButtonHover, buttonWidth, buttonHeight);
        ImageIcon scaledHowToPlayHover = scaleIcon(howToPlayButtonHover, buttonWidth, buttonHeight);
        ImageIcon scaledQuitHover = scaleIcon(quitButtonHover, buttonWidth, buttonHeight);

        startLabel.setIcon(scaledStart);
        startLabel.putClientProperty("hoverIcon", scaledStartHover);
        
        howToPlayLabel.setIcon(scaledHowToPlay);
        howToPlayLabel.putClientProperty("hoverIcon", scaledHowToPlayHover);

        quitLabel.setIcon(scaledQuit);
        quitLabel.putClientProperty("hoverIcon", scaledQuitHover);

        int buttonSpacing = Math.max(20, height / 12);
        int totalHeightNeeded = (3 * buttonHeight) + (2 * buttonSpacing);
        int startY = Math.max(10, (height - totalHeightNeeded) / 2);

        startLabel.setBounds((width - buttonWidth) / 2, startY, buttonWidth, buttonHeight);
        howToPlayLabel.setBounds((width - buttonWidth) / 2, startY + buttonHeight + buttonSpacing, buttonWidth, buttonHeight);
        quitLabel.setBounds((width - buttonWidth) / 2, startY + 2 * (buttonHeight + buttonSpacing), buttonWidth, buttonHeight);
    }

    /**
     * Scales an icon to the specified width and height.
     *
     * @param icon The original icon to be scaled.
     * @param width The desired width of the scaled icon.
     * @param height The desired height of the scaled icon.
     * @return A new ImageIcon with the scaled image.
     */
    private ImageIcon scaleIcon(ImageIcon icon, int width, int height) {
        Image img = icon.getImage();
        Image scaledImage = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    /**
     * Creates a button with a normal and hover state. The button changes appearance
     * when the mouse enters or exits its bounds.
     *
     * @param normal The normal icon for the button.
     * @param hover The hover icon for the button.
     * @return A JLabel representing the button.
     */
    private JLabel createButton(ImageIcon normal, ImageIcon hover) {
        JLabel button = new JLabel(normal);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                ImageIcon hoverIcon = (ImageIcon) button.getClientProperty("hoverIcon");
                button.setIcon(hoverIcon);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setIcon(normal);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (button == startLabel) {
                    dispose();
                    SwingUtilities.invokeLater(() -> new PreGame().setVisible(true));
                } else if (button == howToPlayLabel) {
                    dispose();
                    SwingUtilities.invokeLater(() -> {
                        HowToPlay howToPlay = new HowToPlay(true);
                        howToPlay.setVisible(true);
                    });
                } else if (button == quitLabel) {
                    System.exit(0);
                }
            }
        });

        return button;
    }
}
