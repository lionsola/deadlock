package server.world.trigger;

import java.io.Serializable;

import server.world.Thing;

public class TileSwitchPreset implements Serializable {
	private static final long serialVersionUID = 1954603658126830693L;
	public static final int THING = 0;
	public static final int MISC = 1;
	
	private int presetID;
	private String name;
	private int soundID;
	private double soundVolume;
	private int switchThingID;
	private int originalThingID;
	
	private int triggerType;
	private int itemType;
	
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

	public TileSwitchPreset(int id) {
		presetID = id;
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

	public int getTriggerType() {
		return triggerType;
	}
	
	public void setTriggerType(int triggerType) {
		this.triggerType = triggerType;
	}

	/**
	 * @return the itemType
	 */
	public int getItemType() {
		return itemType;
	}

	/**
	 * @param itemType the itemType to set
	 */
	public void setItemType(int itemType) {
		this.itemType = itemType;
	}
}
