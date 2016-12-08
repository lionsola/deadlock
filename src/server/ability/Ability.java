package server.ability;

import client.gui.GameWindow;
import server.character.PlayerCharacter;
import server.world.World;

/**
 * This abstract class defines the framework
 * which should be followed by all abilities.
 */
public abstract class Ability {
	public static final int FRAG_ID = 100;
	public static final int FLASH_ID = 101;
	public static final int BINO_ID = 102;
	public static final int SCOPE_ID = 103;
	public static final int AMP_ID = 104;	
	
	public final long cooldown;
	private long cooldownTimer;
	private PlayerCharacter self;
	private int id;
	private boolean enabled = true;
	
	public Ability (int id, PlayerCharacter self, long cooldown) {
		this.cooldown = cooldown;
		this.self = self;
		cooldownTimer = 0;
	}
	
	public int getId() {
		return id;
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
	
	public int timeLeft() {
		return (int) Math.max(0,cooldown-cooldownTimer);
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setStatus(boolean enabled) {
		this.enabled = enabled;
		if (!enabled) {
			startCooldown();
		}
	}
	
	public double getCooldownPercent() {
		return Math.min(1,1.0*cooldownTimer/cooldown);
	}
	
	public void reset() {
		cooldownTimer = cooldown;
		enabled = true;
	}
}
