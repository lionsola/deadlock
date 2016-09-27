package server.world;

public enum Sound {
	FOOTSTEP(30,15),
	BULLETWALL(31,25),
	GRENADE_EXPLODE(32,100),
	FLASH_EXPLODE(32,170);
	
	public final int id;
	public final double volume;
	
	Sound(int id, double volume) {
		this.id = id;
		this.volume = volume;
	}
}
