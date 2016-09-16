package sound;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * The Sound class used to contain a sound's sample byte array
 * 
 * @author Anh Pham
 */
public class Sound {
	
	// the sound' sample byte array
	private AudioFormat format;
	private byte[] samples;

	// a pointer points to the sample
	/**
	 * Set up the file of a sound
	 * 
	 * @param fileName
	 *            The filepath of the sound
	 */
	public Sound(String fileName) {

		try {
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(fileName));
			this.format = audioInputStream.getFormat();
			this.samples = Sound.getSamples(audioInputStream, format);

		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the sames of the sound
	 * 
	 * @return Returns a byte array of a sound
	 */
	public byte[] getSamples() {
		return samples;
	}

	/**
	 * Gets the format of a sound
	 * 
	 * @return returns the format of a sound
	 */
	public AudioFormat getFormat() {
		return format;
	}

	/**
	 * Gets the samples from an AudioInputStream as an array of bytes.
	 */
	private static byte[] getSamples(AudioInputStream audioStream, AudioFormat format) {
		// get the number of bytes to read
		int length = (int) (audioStream.getFrameLength() * format.getFrameSize());

		// read the entire stream
		byte[] samples = new byte[length];
		DataInputStream is = new DataInputStream(audioStream);

		try {
			is.readFully(samples);
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		// return the samples
		return samples;
	}

}
