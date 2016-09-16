package sound;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * MusicPlayer is used to load and play music files, which have a somewhat longer
 * duration and should not be pre-sampled like sounds.
 * 
 * @author Anh Pham
 */
public class MusicPlayer extends Thread {
	
	public static final float DEFAULT_VOLUME = -10;
	public static int STATUS;

	public static final int PAUSED = 1;
	public static final int STOPPED = 2;
	public static final int PLAYING = 3;

	private String filename;
	private float volume;

	private final int EXTERNAL_BUFFER_SIZE = 524288; // 128Kb
	private SourceDataLine auline;
	private boolean loop = false;


   /** 
    * Constructor. Create a new MusicPlayer. 
    * @param wavfile The name of the music file.
    * @param volume The volume to play at.
    */
    public MusicPlayer(String wavfile, float volume, boolean loop) {
        STATUS = PLAYING;
        this.loop = loop;
        this.volume = volume;
        filename = wavfile;
    }

    /**
     * Constructor. Create a new MusicPlayer. 
    * @param wavfile The name of the music file.
     */
    public MusicPlayer(String wavfile) {
        this(wavfile,DEFAULT_VOLUME,false);
    }
    
    /**
     * Sets music to stopped
     */
    public void setMusicStop() {
        STATUS = STOPPED;
        auline.stop();
        auline.close();
    }
    
    /**
     * Play the music
     */
    public void run() {
        // Get the target file
        File soundFile = new File(filename);
        if (!soundFile.exists()) {
            // Show error message if file not found
            System.err.println("Wave file not found: " + filename);
            return;
        }
        // Init a AudioInputStream for reading music file
        AudioInputStream audioInputStream = null;
        while (loop&& STATUS==PLAYING) {
            try {
    
                audioInputStream = AudioSystem.getAudioInputStream(soundFile);
            } catch (Throwable t) {
                // Do nothing when error
            }
    
            // Identify the music file format
            AudioFormat format = audioInputStream.getFormat();
            auline = null;
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
    
            try {
                // Try to get music file content
                auline = (SourceDataLine) AudioSystem.getLine(info);
                auline.open(format);
            } catch (LineUnavailableException e) {
                e.printStackTrace();
                return;
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            // Set the volume
            FloatControl gainControl = 
                    (FloatControl) auline.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(volume);
            auline.start();
    
            int nBytesRead = 0;
            byte[] abData = new byte[EXTERNAL_BUFFER_SIZE];
    
            try {
                while (nBytesRead != -1 && STATUS != STOPPED) {
                    // Play music
                    if (STATUS == PLAYING) {
                        nBytesRead = audioInputStream
                                .read(abData, 0, abData.length);
                        if (nBytesRead >= 0)
                            auline.write(abData, 0, nBytesRead);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            } finally {
                // Close io
                auline.drain();
                auline.close();
            }
        }
    }
}