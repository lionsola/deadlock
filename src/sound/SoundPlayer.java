package sound;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * Used to play sounds.
 * 
 * @author Anh Pham
 */
public class SoundPlayer implements Runnable {

	private AudioFormat format;
	private ExecutorService threadPool;

	private InputStream inputStream;
	private float volume;

	/**
	 * 
	 * <p>
	 * Title:
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * get a sound player by sound and thread pool
	 * 
	 * @param sound
	 * @param threadPool
	 */
	public SoundPlayer(Sound sound, float volume, ExecutorService threadPool) {

		format = sound.getFormat();
		this.volume = volume;
		this.threadPool = threadPool;
		this.inputStream = new ByteArrayInputStream(sound.getSamples());
	}

	/**
	 * 
	 * @Title: play
	 * @Description:play the sound
	 * @param
	 * @return void
	 * @throws
	 */
	public void play() {
		threadPool.execute(this);
	}

	@Override
	public void run() {

		// use a short, 100ms (1/10th sec) buffer for real-time
		// change to the sound stream
		int bufferSize = format.getFrameSize() * Math.round(format.getSampleRate() / 10);
		byte[] buffer = new byte[bufferSize];

		// create a line to play to
		SourceDataLine line;
		try {
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
			line = (SourceDataLine) AudioSystem.getLine(info);
			line.open(format, bufferSize);
		} catch (LineUnavailableException ex) {
			ex.printStackTrace();
			return;
		}
		FloatControl gainControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
		gainControl.setValue(volume); // Reduce volume by 10 decibels.

		// start the line
		line.start();

		// copy data to the line
		try {
			int numBytesRead = 0;
			while (numBytesRead != -1) {
				numBytesRead = inputStream.read(buffer, 0, buffer.length);
				if (numBytesRead != -1) {
					line.write(buffer, 0, numBytesRead);
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		// wait until all data is played, then close the line
		line.drain();
		line.close();
	}

}
