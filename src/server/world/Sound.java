package server.world;

public enum Sound {
	NONE(-1,0),
	FOOTSTEP(30,15),
	BULLETWALL(31,25),
	GRENADE_EXPLODE(32,100),
	FLASH_EXPLODE(32,170);
	
	public static final double DISTANCE_VOLUME_DROP_RATE = 2;
	
	public final int id;
	public final double volume;
	
	Sound(int id, double volume) {
		this.id = id;
		this.volume = volume;
	}
}
