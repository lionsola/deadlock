package client.sound;

import javax.sound.sampled.AudioFormat;


public interface Sound {

	/**
	 * Gets the samples to play the sound
	 * 
	 * @return The byte array representing the sound
	 */
	public byte[] getSamples();

	/**
	 * Gets the format of the sound
	 * 
	 * @return The format of the sound
	 */
	public AudioFormat getFormat();
}
