import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * A class representing the "How to Play" screen of the game.
 * This class displays an instructional interface for the game.
 * It includes  buttons to move between help panels and return to the main menu.
 */
public class HowToPlay extends JFrame {
    private BufferedImage backgroundBuffered;
    private ImageIcon[] helpImages;
    private Font pixelFont;
    private int currentPanel = 0;
    private static final int TOTAL_PANELS = 7;
    private boolean isFromMenu;

    /**
     * Converts an Image to a BufferedImage.
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
     * Constructs the HowToPlay window and initializes the resources (images and fonts).
     * @param isFromMenu Whether the HowToPlay window was opened from the menu screen
     */
    public HowToPlay(boolean isFromMenu) {
        this.isFromMenu = isFromMenu;
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
            ImageIcon backgroundImage = new ImageIcon("assets/menubackground.png");
            Image img = backgroundImage.getImage();
            MediaTracker tracker = new MediaTracker(this);
            tracker.addImage(img, 0);
            tracker.waitForAll();
            backgroundBuffered = toBufferedImage(img);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading background image");
        }

        // Load all help menu images
        helpImages = new ImageIcon[TOTAL_PANELS];
        try {
            for (int i = 0; i < TOTAL_PANELS; i++) {
                String imagePath = String.format("assets/helpmenu/htp%d.png", i + 1);
                helpImages[i] = new ImageIcon(imagePath);
                Image img = helpImages[i].getImage();
                MediaTracker tracker = new MediaTracker(this);
                tracker.addImage(img, i);
                tracker.waitForAll();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading help menu images");
        }

        setupWindow();
    }

    /**
     * Sets up the window layout, including buttons, images, and event listeners.
     */
    private void setupWindow() {
        setTitle("How to Play - Jungle King");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create custom background panel with help image overlay
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

                    // Draw current help menu image centered
                    if (helpImages != null && helpImages[currentPanel] != null) {
                        Image helpImg = helpImages[currentPanel].getImage();
                        int helpWidth = helpImg.getWidth(null);
                        int helpHeight = helpImg.getHeight(null);
                        
                        // Scale help image to fit while maintaining aspect ratio
                        double helpScale = Math.min(
                            (getWidth() * 1.0) / helpWidth,
                            (getHeight() * 1.0) / helpHeight
                        );
                        
                        int scaledHelpWidth = (int) (helpWidth * helpScale);
                        int scaledHelpHeight = (int) (helpHeight * helpScale);
                        int helpX = (getWidth() - scaledHelpWidth) / 2;
                        int helpY = (getHeight() - scaledHelpHeight) / 2;
                        
                        g2d.drawImage(helpImg, helpX, helpY, scaledHelpWidth, scaledHelpHeight, this);
                    }
                }
            }
        };
        setContentPane(backgroundPanel);

        // Create Next and Back buttons with images
        JButton nextButton = new JButton();
        JButton backButton = new JButton();
        ImageIcon normalNextIcon = null;
        ImageIcon hoverNextIcon = null;
        ImageIcon normalBackIcon = null;
        ImageIcon hoverBackIcon = null;
        try {
            // Load both normal and hover icons for next button
            normalNextIcon = new ImageIcon("assets/nextbutton.png");
            hoverNextIcon = new ImageIcon("assets/nextbuttonhover.png");
            
            // Load both normal and hover icons for back button
            normalBackIcon = new ImageIcon("assets/backbutton.png");
            hoverBackIcon = new ImageIcon("assets/backbuttonhover.png");
            
            // Scale all images
            Image normalNextImg = normalNextIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            Image hoverNextImg = hoverNextIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            Image normalBackImg = normalBackIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            Image hoverBackImg = hoverBackIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            
            normalNextIcon = new ImageIcon(normalNextImg);
            hoverNextIcon = new ImageIcon(hoverNextImg);
            normalBackIcon = new ImageIcon(normalBackImg);
            hoverBackIcon = new ImageIcon(hoverBackImg);
            
            nextButton.setIcon(normalNextIcon);
            backButton.setIcon(normalBackIcon);
        } catch (Exception e) {
            e.printStackTrace();
            nextButton.setText("Next");
            backButton.setText("Back");
        }

        // Style both buttons
        nextButton.setContentAreaFilled(false);
        nextButton.setBorderPainted(false);
        nextButton.setFocusPainted(false);
        nextButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        backButton.setContentAreaFilled(false);
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect for next button
        final ImageIcon finalNormalNextIcon = normalNextIcon;
        final ImageIcon finalHoverNextIcon = hoverNextIcon;
        nextButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (finalHoverNextIcon != null) {
                    nextButton.setIcon(finalHoverNextIcon);
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (finalNormalNextIcon != null) {
                    nextButton.setIcon(finalNormalNextIcon);
                }
            }
        });

        // Add hover effect for back button
        final ImageIcon finalNormalBackIcon = normalBackIcon;
        final ImageIcon finalHoverBackIcon = hoverBackIcon;
        backButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (finalHoverBackIcon != null) {
                    backButton.setIcon(finalHoverBackIcon);
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (finalNormalBackIcon != null) {
                    backButton.setIcon(finalNormalBackIcon);
                }
            }
        });

        nextButton.addActionListener(e -> {
            currentPanel++;
            if (currentPanel >= TOTAL_PANELS) {
                // On last panel, clicking next returns to menu only if opened from menu
                dispose();
                if (isFromMenu) {
                    SwingUtilities.invokeLater(() -> {
                        new MenuScreen().setVisible(true);
                    });
                }
            } else {
                updateNavigationButtons(nextButton, backButton);
                repaint();
            }
        });

        backButton.addActionListener(e -> {
            if (currentPanel > 0) {
                currentPanel--;
                updateNavigationButtons(nextButton, backButton);
                repaint();
            }
        });

        // Create panels for the buttons
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JPanel leftPanel = new JPanel(null);
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new Dimension(200, 440));
        backButton.setBounds(70, 25, 50, 50);
        leftPanel.add(backButton);

        JPanel centerPanel = new JPanel(null);
        centerPanel.setOpaque(false);
        
        // Create Back to Menu button only if opened from menu
        if (isFromMenu) {
            JButton menuButton = new JButton();
            int buttonWidth = 150;  // Default width if image loading fails
            try {
                final ImageIcon normalMenuIcon = new ImageIcon("assets/backtomenu.png");
                final ImageIcon hoverMenuIcon = new ImageIcon("assets/backtomenuhover.png");
                
                // Get original dimensions to maintain aspect ratio
                int originalWidth = normalMenuIcon.getIconWidth();
                int originalHeight = normalMenuIcon.getIconHeight();
                double ratio = (double) originalWidth / originalHeight;
                
                // Scale height to 50px and calculate width to maintain ratio
                int targetHeight = 50;
                buttonWidth = (int)(targetHeight * ratio);
                
                Image normalMenuImg = normalMenuIcon.getImage().getScaledInstance(buttonWidth, targetHeight, Image.SCALE_SMOOTH);
                Image hoverMenuImg = hoverMenuIcon.getImage().getScaledInstance(buttonWidth, targetHeight, Image.SCALE_SMOOTH);
                
                menuButton.setIcon(new ImageIcon(normalMenuImg));
                
                // Add hover effect for menu button
                menuButton.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        menuButton.setIcon(new ImageIcon(hoverMenuImg));
                    }
                    
                    @Override
                    public void mouseExited(MouseEvent e) {
                        menuButton.setIcon(new ImageIcon(normalMenuImg));
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                menuButton.setText("Menu");
            }

            menuButton.setContentAreaFilled(false);
            menuButton.setBorderPainted(false);
            menuButton.setFocusPainted(false);
            menuButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

            menuButton.addActionListener(e -> {
                dispose();
                SwingUtilities.invokeLater(() -> {
                    new MenuScreen().setVisible(true);
                });
            });

            // Center the button
            int x = (200 - buttonWidth) / 2;  // Center in a 200px wide panel
            menuButton.setBounds(310, 370, buttonWidth, 50);
            centerPanel.add(menuButton);
        }

        JPanel rightPanel = new JPanel(null);
        rightPanel.setOpaque(false);
        rightPanel.setPreferredSize(new Dimension(200, 440));
        nextButton.setBounds(70, 25, 50, 50);
        rightPanel.add(nextButton);

        buttonPanel.add(leftPanel, BorderLayout.WEST);
        buttonPanel.add(centerPanel, BorderLayout.CENTER);
        buttonPanel.add(rightPanel, BorderLayout.EAST);
        backgroundPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Initialize button visibility
        updateNavigationButtons(nextButton, backButton);

        // Setup repaint timer for smooth rendering
        Timer repaintTimer = new Timer(100, e -> {
            if (isVisible()) {
                repaint();
                Toolkit.getDefaultToolkit().sync();
            }
        });
        repaintTimer.start();
    }

    /**
     * Updates the visibility of the navigation buttons based on the current panel index.
     *
     * @param nextButton The next button.
     * @param backButton The back button.
     */
    private void updateNavigationButtons(JButton nextButton, JButton backButton) {
        // Show/hide back button based on current panel
        backButton.setVisible(currentPanel > 0);
        
        // Show/hide next button based on current panel
        nextButton.setVisible(currentPanel < TOTAL_PANELS - 1);
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(pixelFont.deriveFont(16f));
        button.setForeground(new Color(249, 244, 166)); // Pale yellow
        button.setBackground(new Color(106, 41, 66)); // Pink
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(126, 61, 86)); // Lighter pink on hover
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(106, 41, 66)); // Original pink
            }
        });

        return button;
    }
} 
