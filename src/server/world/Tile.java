package server.world;

import java.io.Serializable;

import server.world.trigger.Trigger;

public class Tile implements Serializable {
	private static final long serialVersionUID = 5249052371261483524L;

	transient private Terrain terrain;
	
	transient private Thing thing;
	private SpriteConfig thingConfig;
	
	transient private Thing misc;
	private SpriteConfig miscConfig;
	
	private Trigger trigger;
	
	public boolean isTraversable() {
		return thing==null || thing.isWalkable();
	}
	
	public boolean isClear() {
		return (thing==null || thing.isClear()) && (misc==null || misc.isClear());
	}
	
	public int coverType() {
		return thing==null?0:thing.getCoverType();
	}
	
	public Terrain getTerrain() {
		return terrain;
	}
	
	public Thing getThing() {
		return thing;
	}
	
	public Trigger getTrigger() {
		return trigger;
	}

	public void setTerrain(Terrain terrain) {
		this.terrain = terrain;
	}
	
	public void setThing(Thing thing) {
		this.thing = thing;
	}
	
	public void setThingConfig(SpriteConfig config) {
		this.thingConfig = config;
	}
	
	public SpriteConfig getThingConfig() {
		return thingConfig;
	}
	
	@Override
	public String toString() {
		return "Thing: "+ (thing==null?"empty":thing)
				+ ", terrain: "+(terrain==null?"empty":terrain);
	}

	public void setTrigger(Trigger trigger) {
		this.trigger = trigger;
	}

	public Thing getMisc() {
		return misc;
	}

	public void setMisc(Thing misc) {
		this.misc = misc;
	}
	
	public SpriteConfig getMiscConfig() {
		return miscConfig;
	}

	public void setMiscConfig(SpriteConfig miscConfig) {
		this.miscConfig = miscConfig;
	}
}
