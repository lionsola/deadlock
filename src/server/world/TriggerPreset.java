package server.world;

import java.io.Serializable;

public class TriggerPreset implements Serializable {
	private static final long serialVersionUID = 1954603658126830693L;
	
	private int presetID;
	private String name;
	private int soundID;
	private double soundVolume;
	private int switchThingID;
	private int originalThingID;
	
	private transient Thing switchThing;
	private transient Thing originalThing;
	
	/**
	 * @return the switchThing
	 */
	public Thing getSwitchThing() {
		return switchThing;
	}

	/**
	 * @param switchThing the switchThing to set
	 */
	public void setSwitchThing(Thing switchThing) {
		this.switchThingID = switchThing.getId();
		this.switchThing = switchThing;
	}

	/**
	 * @return the originalThing
	 */
	public Thing getOriginalThing() {
		return originalThing;
	}

	/**
	 * @param originalThing the originalThing to set
	 */
	public void setOriginalThing(Thing originalThing) {
		this.originalThingID = originalThing.getId();
		this.originalThing = originalThing;
	}

	public TriggerPreset(int id) {
		
	}
	
	public int getId() {
		return presetID;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the soundID
	 */
	public int getSoundID() {
		return soundID;
	}
	/**
	 * @param soundID the soundID to set
	 */
	public void setSoundID(int soundID) {
		this.soundID = soundID;
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
	 * @return the switchThingID
	 */
	public int getSwitchThingID() {
		return switchThingID;
	}

	public int getOriginalThingID() {
		return originalThingID;
	}
}
