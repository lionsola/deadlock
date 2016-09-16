package ability;

import character.ControlledCharacter;
import core.Projectile;
import core.World;
import game.Game;

public abstract class TimedGrenade extends Projectile {
	private long timeLeft;
	public TimedGrenade(ControlledCharacter source, double direction, double speed) {
		super(source, direction, speed,100);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHitWall(World w, double x, double y) {
		// TODO Auto-generated method stub
		double bounceX = x - getDx();
		double bounceY = y - getDy();
		if (w.getArena().getTileAt(bounceX, y).isWalkable()) {
			setDirection(Math.PI - getDirection());
		} else if (w.getArena().getTileAt(x, bounceY).isWalkable()) {
			setDirection(- getDirection());
		}
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
	}
	
	protected abstract void explode(World w);
}
