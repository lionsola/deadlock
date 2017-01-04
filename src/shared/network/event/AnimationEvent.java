package shared.network.event;

public class AnimationEvent extends GameEvent {
	private static final long serialVersionUID = 991627988551330392L;
	public final float x;
	public final float y;
	public final float direction;
	public final byte id;
	public final byte team;
	public final boolean global;
	
	public static final byte GUNSHOT = 0;
	public static final byte BLOOD = 1;
	public static final byte BULLETWALL = 2;
	public static final byte ENEMYMARK = 3;
	public static final byte BULLETTRAIL = 4;
	public static final byte PING_ANIMATION_ID = 5;
	
	public AnimationEvent(int id, double x, double y, double rotation) {
		this(id,-1,x,y,rotation,false);
	}
	
	public AnimationEvent(int id, int team, double x, double y, double rotation) {
		this(id,team,x,y,rotation,false);
	}
	
	public AnimationEvent(int id, int team, double x, double y, double rotation, boolean global) {
		this.x = (float)x;
		this.y = (float)y;
		this.direction = (float)rotation;
		this.id = (byte) id;
		this.global = global;
		this.team = (byte) team;
	} 
}