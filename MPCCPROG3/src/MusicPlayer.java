import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * A class that plays background music in a loop.
 * It loads and plays a background music file with adjustable volume.
 */
public class MusicPlayer {
    private Clip clip;
    private static MusicPlayer instance;

    /**
     * Private constructor that initializes the music player.
     * Loads and prepares the background music file and starts playing it in a loop.
     */
    private MusicPlayer() {
        try {
            File musicFile = new File("assets/music/background.wav");  // Make sure to add your music file
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            
            // Set the volume (reduced by 40dB total)
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(-30.0f); // Reduced volume by 40 decibels
            
            // Start playing immediately
            clip.setFramePosition(0);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
            
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
            System.out.println("Error loading music file: " + e.getMessage());
        }
    }

    /**
     * Retrieves the singleton instance of the MusicPlayer.
     *
     * @return The instance of the MusicPlayer.
     */
    public static MusicPlayer getInstance() {
        if (instance == null) {
            instance = new MusicPlayer();
        }
        return instance;
    }

    /**
     * Closes the music player and releases resources.
     */
    public void close() {
        if (clip != null) {
            clip.close();
        }
    }
} 
