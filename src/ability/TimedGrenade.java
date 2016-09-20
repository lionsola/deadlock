package ability;

import character.ControlledCharacter;
import core.Projectile;
import core.World;
import game.Game;

public abstract class TimedGrenade extends Projectile {
	private static final double RESIST_CONSTANT = 0.00000001;
	private long timeLeft;
	
	public TimedGrenade(ControlledCharacter source, double direction, double speed, long timeLeft) {
		super(source, direction, speed,200);
		this.timeLeft = timeLeft;
	}

	@Override
	protected void onHitWall(World w, double x, double y) {
		// TODO Auto-generated method stub
		double bounceX = x - getDx()*Game.MS_PER_UPDATE;
		double bounceY = y - getDy()*Game.MS_PER_UPDATE;
		if (w.getArena().getTileAt(bounceX, y).isWalkable()) {
			setDirection(Math.PI - getDirection());
		} else if (w.getArena().getTileAt(x, bounceY).isWalkable()) {
			setDirection(- getDirection());
		} else {
			setDirection(getDirection()+Math.PI);
		}
		setSpeed(getSpeed() - 1000*RESIST_CONSTANT*getSize());
	}


	@Override
	protected void onHitDestination(World w) {
	}

	@Override
	protected void onHitCharacter(World w, ControlledCharacter ch, double x, double y) {
	}

	@Override
	public void update(World w) {
		super.update(w);
		timeLeft -= Game.MS_PER_UPDATE;
		if (timeLeft<=0) {
			explode(w);
			consumeProjectile();
		}
		else {
			setSpeed(getSpeed() - RESIST_CONSTANT*getSize()*Game.MS_PER_UPDATE);
		}
	}
	
	protected abstract void explode(World w);
}
