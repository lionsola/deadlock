package server.world;

import java.io.Serializable;

public class SoundSource implements Serializable {
	private static final long serialVersionUID = 7234827501082028797L;
	
	private int x, y;
	private int soundId;
	private double soundVolume;
	
	private boolean random;
	private double frequency;
	
	public SoundSource(int id, double volume) {
		soundId = id;
		soundVolume = volume;
	}

	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * @return the soundId
	 */
	public int getSoundId() {
		return soundId;
	}

	/**
	 * @param soundId the soundId to set
	 */
	public void setSoundId(int soundId) {
		this.soundId = soundId;
	}

	/**
	 * @return the soundVolume
	 */
	public double getSoundVolume() {
		return soundVolume;
	}

	/**
	 * @param soundVolume the soundVolume to set
	 */
	public void setSoundVolume(double soundVolume) {
		this.soundVolume = soundVolume;
	}

	/**
	 * @return the random
	 */
	public boolean isRandom() {
		return random;
	}

	/**
	 * @param random the random to set
	 */
	public void setRandom(boolean random) {
		this.random = random;
	}

	/**
	 * @return the frequency
	 */
	public double getFrequency() {
		return frequency;
	}

	/**
	 * @param frequency the frequency to set
	 */
	public void setFrequency(double frequency) {
		this.frequency = frequency;
	}
	
}
