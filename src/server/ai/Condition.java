package server.ai;
import server.character.InputControlledEntity;

public enum Condition {
	ENEMY_IN_SIGHT {
		@Override
		public boolean check(Brain b, InputControlledEntity c) {
			return !b.enemies.isEmpty();
		}
	},
	LOW_HP {
		@Override
		public boolean check(Brain b, InputControlledEntity c) {
			return c.getHealthPoints()<50;
		}
	},
	LOW_AMMO {
		@Override
		public boolean check(Brain b, InputControlledEntity c) {
			return c.getWeapon().getAmmo()<=c.getWeapon().type.magSize/2;
		}
	},
	WEAPON_READY {
		@Override
		public boolean check(Brain b, InputControlledEntity c) {
			return c.getWeapon().timeLeft()<500;
		}
	},
	BLINDED {
		@Override
		public boolean check(Brain b, InputControlledEntity c) {
			return c.getFovRangeF()<0.1 || c.getFovAngleF()<0.1;
		}
	},
	;
	
	private Condition() {
		
	}
	
	public abstract boolean check(Brain b, InputControlledEntity c);
}
