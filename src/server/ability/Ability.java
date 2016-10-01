package server.ability;

import client.gui.GameWindow;
import server.character.ControlledCharacter;
import server.world.World;

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
			cooldownTimer += GameWindow.MS_PER_UPDATE;
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
	
	public double getCooldownPercent() {
		return Math.min(1,1.0*cooldownTimer/cooldown);
	}
}
