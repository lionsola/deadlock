package server.world;

public enum Sound {
	FOOTSTEP(30,15),
	BULLETWALL(31,30),
	FRAG_EXPLODE(32,100),
	FLASH_EXPLODE(33,170);
	
	public final int id;
	public final double volume;
	
	Sound(int id, double volume) {
		this.id = id;
		this.volume = volume;
	}
}
