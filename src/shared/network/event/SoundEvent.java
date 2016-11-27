package shared.network.event;

public class SoundEvent extends GameEvent {
	private static final long serialVersionUID = 4014556827130454737L;

	public static final int FOOTSTEP_SOUND_ID = 30;
	public static final int BULLET_WALL_SOUND_ID = 31;
	public static final int GRENADE_EXPLODE_SOUND_ID = 32;
	public static final int PING_SOUND_ID = 34;
	public static final int DOOR_OPEN_ID = 36;
	
	public static final double BULLET_WALL_SOUND_VOLUME = 25;
	public static final double PING_SOUND_VOLUME = 30;
	public static final double FOOTSTEP_SOUND_VOLUME = 15;
	
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