package ability;

import character.ControlledCharacter;
import core.World;

public class ThrowGrenadeAbility extends Ability {
	static final double THROW_SPEED = 0.1;
	static final double MAX_DISTANCE = 10;
	boolean throwing;
	double distance;
	long timeElapsed;
	
	public ThrowGrenadeAbility(ControlledCharacter c) {
		super(c,10000);
		
	}

	@Override
	public void update(World w) {
		if (!throwing && isReady() && self().getInput().fire2) {
			throwing = true;
		} else if (throwing && !self().getInput().fire2) {
			throwing = false;
			// TODO actually throw the thing
			//w.addProjectile(new TimedGrenade(c, distance, distance, distance, distance));
		}
		
		if (throwing) {
			distance += THROW_SPEED;
		}
	}

	
}
