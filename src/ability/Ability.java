package ability;

import character.ControlledCharacter;
import core.World;
import game.Game;

public abstract class Ability {
	public final long cooldown;
	private long cooldownTimer;
	private ControlledCharacter self;
	
	public Ability (ControlledCharacter self, long cooldown) {
		this.cooldown = cooldown;
		this.self = self;
		cooldownTimer = cooldown;
	}
	
	public void update(World w) {
		if (cooldownTimer<cooldown)
			cooldownTimer += Game.MS_PER_UPDATE;
	}
	
	public void startCooldown() {
		cooldownTimer = 0;
	}
	
	public boolean isReady() {
		return cooldownTimer>=cooldown;
	}
	
	protected ControlledCharacter self() {
		return self;
	}
}
