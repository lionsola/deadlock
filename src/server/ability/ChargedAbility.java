package server.ability;

import client.gui.GameWindow;
import server.character.InputControlledEntity;
import server.world.World;

/**
 * Charged abilities are abilities that changes based on
 * how long the activate button is held.
 */
public abstract class ChargedAbility extends Ability {
	private long timeElapsed;
	private boolean charging;
	
	public ChargedAbility(int id, InputControlledEntity c, long cooldown) {
		super(id,c,cooldown);
	}

	@Override
	public void update(World w) {
		super.update(w);
		if (!self().isDead() && !charging && isReady() && self().getInput().fire2 && !self().getInput().alt) {
			charging = true;
			timeElapsed = 0;
		} else if (charging && (self().isDead() || (!self().getInput().fire2 || self().getInput().alt))) {
			charging = false;
			startCooldown();
			activate(w,timeElapsed);
		}
		
		if (charging) {
			timeElapsed += GameWindow.MS_PER_UPDATE;
		}
	}
	
	protected abstract void activate(World w, long chargeTime);
	
	public static class ThrowFragGrenade extends ChargedAbility {
		static final double THROWFRAG_SPEED = 0.01;
		static final long THROWFRAG_COOLDOWN = 15000;
		static final long THROWFRAG_FUSE = 5000;
		
		public ThrowFragGrenade(InputControlledEntity c) {
			super(FRAG_ID,c,THROWFRAG_COOLDOWN);
		}
		
		@Override
		protected void activate(World w, long chargeTime) {
			w.addProjectile(new TimedGrenade.FragGrenade(self(), self().getDirection(), THROWFRAG_SPEED, THROWFRAG_FUSE-chargeTime));
		}
	}
	
	public static class ThrowFlashGrenade extends ChargedAbility {
		static final double THROWFLASH_SPEED = 0.01;
		static final long THROWFLASH_COOLDOWN = 10000;
		static final long THROWFLASH_FUSE = 5000;
		
		public ThrowFlashGrenade(InputControlledEntity c) {
			super(FLASH_ID,c,THROWFLASH_COOLDOWN);
		}
		
		@Override
		protected void activate(World w, long chargeTime) {
			w.addProjectile(new TimedGrenade.FlashGrenade(self(), self().getDirection(), THROWFLASH_SPEED, THROWFLASH_FUSE-chargeTime));
		}
	}
}
