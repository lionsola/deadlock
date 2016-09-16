package ability;

import character.ControlledCharacter;
import core.World;

public abstract class Ability {
	public final long cooldown;
	private long lastActivate = 0;
	private ControlledCharacter self;
	
	public Ability (ControlledCharacter self, long cooldown) {
		this.cooldown = cooldown;
		this.self = self;
	}
	
	public abstract void update(World w);
	
	public void startCooldown() {
		lastActivate = System.currentTimeMillis();
	}
	
	public boolean isReady() {
		return System.currentTimeMillis()-lastActivate>cooldown;
	}
	
	protected ControlledCharacter self() {
		return self;
	}
}
