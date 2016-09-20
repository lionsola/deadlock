package ability;

import character.ControlledCharacter;
import core.Utils;
import core.World;
import game.Game;
import weapon.Bullet;
import network.GameEvent.GrenadeExplodeEvent;

public class ThrowGrenadeAbility extends Ability {
	static final double THROW_SPEED = 0.1;
	static final double MAX_DISTANCE = 10;
	double distance;
	long timeElapsed;
	boolean throwing;
	
	public ThrowGrenadeAbility(ControlledCharacter c) {
		super(c,10000);
	}

	@Override
	public void update(World w) {
		if (!throwing && isReady() && self().getInput().fire2) {
			throwing = true;
			timeElapsed = 0;
		} else if (throwing && !self().getInput().fire2) {
			throwing = false;
			this.startCooldown();
			w.addProjectile(new TimedGrenade(self(), self().getDirection(), 0.01, 5000-timeElapsed) {
				@Override
				protected void explode(World w) {
					int minFrags = 20;
					w.getEventListener().onEventReceived(new GrenadeExplodeEvent(getX(),getY(),0));
					double BASE_SIZE = 10;
					double BASE_SPEED = 2;
					for (int i=0;i<minFrags;i++) {
						double direction = Math.PI*2*i/minFrags;
						double sizeF = Math.max(1,1 + 0.8*Utils.random().nextGaussian()/2);
						w.addProjectile(new Bullet(this, 20, direction, BASE_SPEED/Math.sqrt(sizeF), BASE_SIZE*sizeF));
					}
					double extraFrags = 0.2*minFrags;
					for (int i=0;i<extraFrags;i++) {
						double direction = Utils.random().nextDouble()*Math.PI*2;
						double sizeF = Math.max(1,1 + 0.8*Utils.random().nextGaussian()/2);
						w.addProjectile(new Bullet(this, 20, direction, BASE_SPEED/Math.sqrt(sizeF), BASE_SIZE*sizeF));
					}
				}});
		}
		
		if (throwing) {
			timeElapsed += Game.MS_PER_UPDATE;
		}
	}
	
	
}
