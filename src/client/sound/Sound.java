package client.sound;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

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
