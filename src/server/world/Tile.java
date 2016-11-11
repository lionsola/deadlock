package server.world;

public class Tile {
	private Terrain terrain;
	private Thing thing;
	private SpriteConfig config;
	private int oldLight;
	
	public boolean isTraversable() {
		return thing==null || thing.isWalkable();
	}
	
	public boolean isClear() {
		return thing==null || thing.isClear();
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
	
	public int getLightLevel() {
		return oldLight;
	}

	public void setTerrain(Terrain terrain) {
		this.terrain = terrain;
	}
	
	public void setThing(Thing thing) {
		this.thing = thing;
	}
	
	public void setSpriteConfig(SpriteConfig config) {
		this.config = config;
	}
	
	public SpriteConfig getSpriteConfig() {
		return config;
	}
	
	@Override
	public String toString() {
		return "Thing: "+ (thing==null?"empty":thing)
				+ ", terrain: "+(terrain==null?"empty":terrain);
	}
}
