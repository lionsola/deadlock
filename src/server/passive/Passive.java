package server.passive;

import server.character.InputControlledEntity;
import server.world.World;

public abstract class Passive {
	public static final int ASSAULT_ID = 200;
	public static final int BACKSTAB_ID = 201;
	public static final int MARK_ID = 202;
	public static final int OVERWATCH_ID = 203;
	public static final int SHIELD_ID = 204;
	
	private InputControlledEntity self;
	private double level;
	public Passive(InputControlledEntity self) {
		this.self = self;
	}
	
	public void update(World w) {
		if (!self().isDead()) {
			double oldTrigger = level;
			level = calculateActivationLevel(w);
			if (oldTrigger<=0 && level>0) {
				onActivate(w);
			} else if (oldTrigger>0 && level<=0) {
				onDeactivate(w);
			}
			onUpdate(w);
		}
	}
	
	protected void onDeactivate(World w) {};

	protected void onActivate(World w) {};

	protected InputControlledEntity self() {return self;}
	
	public double getActivationLevel() {return level;}
	
	protected abstract double calculateActivationLevel(World w);
	
	protected void onUpdate(World w) {};
}
