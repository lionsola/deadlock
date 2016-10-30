package server.ability;

import client.gui.GameWindow;
import server.character.PlayerCharacter;
import server.world.World;

public abstract class Ability {
	public final long cooldown;
	private long cooldownTimer;
	private PlayerCharacter self;
	
	public Ability (PlayerCharacter self, long cooldown) {
		this.cooldown = cooldown;
		this.self = self;
		cooldownTimer = cooldown;
	}
	
	public void update(World w) {
		if (cooldownTimer<cooldown)
			cooldownTimer += GameWindow.MS_PER_UPDATE;
	}
	
	public void startCooldown() {
		cooldownTimer = 0;
	}
	
	public boolean isReady() {
		return cooldownTimer>=cooldown;
	}
	
	protected PlayerCharacter self() {
		return self;
	}
	
	public double getCooldownPercent() {
		return Math.min(1,1.0*cooldownTimer/cooldown);
	}
	
	public void reset() {
		cooldownTimer = cooldown;
	}
}
