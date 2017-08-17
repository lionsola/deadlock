package client.sound;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Representing a single sound, the most basic type of sound.
 */
public class SingleSound implements Sound {
	private AudioFormat format;
	private byte[] samples;
	public final String file;

	// a pointer points to the sample
	/**
	 * Set up the file of a client.sound
	 * 
	 * @param fileName
	 *            The filepath of the client.sound
	 */
	public SingleSound(String fileName) {
		file = fileName;
		try {
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(SingleSound.class.getResourceAsStream(fileName));
			this.format = audioInputStream.getFormat();
			this.samples = getSamples(audioInputStream, format);

		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets the sames of the client.sound
	 * 
	 * @return Returns a byte array of a client.sound
	 */
	public byte[] getSamples() {
		return samples;
	}

	/**
	 * Gets the format of a client.sound
	 * 
	 * @return returns the format of a client.sound
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

	@Override
	public SingleSound getSound() {
		return this;
	}
}
