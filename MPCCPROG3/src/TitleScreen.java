import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Represents the title screen of the Jungle King game. This screen features
 * a background image, a "Click Anywhere" prompt with fading effects, and
 * handles the transition to the menu screen when clicked.
 */
public class TitleScreen extends JFrame {
    private Image originalTitleImage;
    private Image originalClickImage;
    private float alpha = 1.0f;
    private boolean fadingIn = false;
    private Timer fadeTimer;

    /**
     * Constructs the TitleScreen window, sets up the layout, image loading,
     * and fading effects, and adds functionality to the "Click Anywhere" prompt.
     */
    public TitleScreen() {
        setTitle("Jungle King");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Add window listener to stop music when window closes
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                MusicPlayer.getInstance().close();
            }
        });

        // Load the original images
        originalTitleImage = new ImageIcon("assets/titlescreen.png").getImage();
        originalClickImage = new ImageIcon("assets/clickanywhere.png").getImage();

        // Create fade timer
        fadeTimer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (fadingIn) {
                    alpha += 0.05f;
                    if (alpha >= 1.0f) {
                        alpha = 1.0f;
                        fadingIn = false;
                    }
                } else {
                    alpha -= 0.05f;
                    if (alpha <= 0.0f) {
                        alpha = 0.0f;
                        fadingIn = true;
                    }
                }
                repaint();
            }
        });
        fadeTimer.start();

        // Create a custom JPanel for background scaling
        JPanel backgroundPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Draw the background image scaled to panel size
                g.drawImage(originalTitleImage, 0, 0, getWidth(), getHeight(), this);

                // Calculate position for click anywhere image
                int clickWidth = 415;
                int clickHeight = 60;
                int x = (getWidth() - clickWidth) / 2;
                int y = (int)(getHeight() * 0.8);

                // Draw the click anywhere image with transparency
                Graphics2D g2d = (Graphics2D) g;
                Composite oldComposite = g2d.getComposite();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                g2d.drawImage(originalClickImage, x, y, clickWidth, clickHeight, this);
                g2d.setComposite(oldComposite);
            }
        };

        // Make the panel clickable
        backgroundPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                fadeTimer.stop(); // Stop the animation
                dispose(); // Close title screen
                SwingUtilities.invokeLater(() -> {
                    new MenuScreen().setVisible(true);
                });
            }
        });

        add(backgroundPanel, BorderLayout.CENTER);

        // Add component listener to handle resize events
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                backgroundPanel.repaint();
            }
        });
    }

    /**
     * The main method starts the code with the TitleScreen.
     *
     * @param args argument
     */
    public static void main(String[] args) {
        // Initialize music player before creating any windows
        MusicPlayer.getInstance();  // This will start the music immediately

        SwingUtilities.invokeLater(() -> {
            TitleScreen titleScreen = new TitleScreen();
            titleScreen.setVisible(true);
        });
    }
} 
