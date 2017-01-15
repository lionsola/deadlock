package shared.network.event;

public class SoundEvent extends GameEvent {
	private static final long serialVersionUID = 4014556827130454737L;

	public static final int FOOTSTEP_GRASS_ID = 101;
	public static final int FOOTSTEP_DIRT_ID = 102;
	public static final int FOOTSTEP_WATER_ID = 103;
	public static final int FOOTSTEP_CARPET_ID = 104;
	public static final int FOOTSTEP_HARD_ID = 105;
	public static final int FOOTSTEP_DEFAULT_ID = 100;
	
	public static final int BULLET_WALL_SOUND_ID = 31;
	public static final int GRENADE_EXPLODE_SOUND_ID = 32;
	public static final int PING_SOUND_ID = 34;
	public static final int DOOR_OPEN_ID = 36;
	
	public static final int STATIC_NOISE_ID = 50;
	public static final int CRITTER_NOISE_ID = 51;
	public static final int TV_NOISE_ID = 52;
	public static final int SWITCH_ID = 53;
	public static final int TICK_ID = 54;
	
	public static final float BULLET_WALL_SOUND_VOLUME = 25;
	public static final float PING_SOUND_VOLUME = 30;
	public static final float FOOTSTEP_SOUND_VOLUME = 15;
	
	public final float x;
	public final float y;
	public final float volume;
	public final byte id;

	

	public SoundEvent(int id, double volume, double x, double y) {
		this.x = (float)x;
		this.y = (float)y;
		this.volume = (float)volume;
		this.id = (byte)id;
	}
}