package ability;

import character.ControlledCharacter;
import core.World;
import game.Game;

public abstract class ChargedAbility extends Ability {
	private long timeElapsed;
	private boolean throwing;
	
	public ChargedAbility(ControlledCharacter c, long cooldown) {
		super(c,cooldown);
	}

	@Override
	public void update(World w) {
		super.update(w);
		if (!throwing && isReady() && self().getInput().fire2) {
			throwing = true;
			timeElapsed = 0;
		} else if (throwing && !self().getInput().fire2) {
			throwing = false;
			this.startCooldown();
			activate(w,timeElapsed);
		}
		
		if (throwing) {
			timeElapsed += Game.MS_PER_UPDATE;
		}
	}
	
	protected abstract void activate(World w, long chargeTime);
	
	public static class ThrowFragGrenade extends ChargedAbility {
		static final double THROWFRAG_SPEED = 0.01;
		static final long THROWFRAG_COOLDOWN = 20000;
		static final long THROWFRAG_FUSE = 5000;
		
		public ThrowFragGrenade(ControlledCharacter c) {
			super(c,THROWFRAG_COOLDOWN);
		}
		
		@Override
		protected void activate(World w, long chargeTime) {
			w.addProjectile(new TimedGrenade.FragGrenade(self(), self().getDirection(), THROWFRAG_SPEED, THROWFRAG_FUSE-chargeTime));
		}
	}
	
	public static class ThrowFlashGrenade extends ChargedAbility {
		static final double THROWFLASH_SPEED = 0.01;
		static final long THROWFLASH_COOLDOWN = 20000;
		static final long THROWFLASH_FUSE = 5000;
		
		public ThrowFlashGrenade(ControlledCharacter c) {
			super(c,THROWFLASH_COOLDOWN);
		}
		
		@Override
		protected void activate(World w, long chargeTime) {
			w.addProjectile(new TimedGrenade.FlashGrenade(self(), self().getDirection(), THROWFLASH_SPEED, THROWFLASH_FUSE-chargeTime));
		}
	}
}
