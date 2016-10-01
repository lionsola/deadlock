package client.sound;

import javax.sound.sampled.AudioFormat;

/**
 * The Sound class used to contain a client.sound's sample byte array
 * 
 * @author Anh Pham
 */
public interface Sound {

	/**
	 * Gets the sames of the client.sound
	 * 
	 * @return Returns a byte array of a client.sound
	 */
	public byte[] getSamples();

	/**
	 * Gets the format of a client.sound
	 * 
	 * @return returns the format of a client.sound
	 */
	public AudioFormat getFormat();
}
