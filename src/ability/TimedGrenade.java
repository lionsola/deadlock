package ability;

import java.awt.geom.Area;

import character.ControlledCharacter;
import core.Geometry;
import core.LineOfSight;
import core.Projectile;
import core.Utils;
import core.World;
import game.Game;
import network.GameEvent.GrenadeExplodeEvent;
import status.Blinded;
import weapon.Bullet;

public abstract class TimedGrenade extends Projectile {
	private static final double RESIST_CONSTANT = 0.000000015;
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
		setSpeed(getSpeed() - Math.min(500*RESIST_CONSTANT*getSize(),getSpeed()*0.5));
	}


	@Override
	protected void onHitDestination(World w) {
	}

	@Override
	protected void onHitCharacter(World w, ControlledCharacter ch, double x, double y) {
	}

	@Override
	public boolean isConsumed() {
		return timeLeft<=0;
	}
	
	@Override
	public void update(World w) {
		super.update(w);
		timeLeft -= Game.MS_PER_UPDATE;
		if (timeLeft<=0) {
			explode(w);
		}
		else {
			setSpeed(getSpeed() - RESIST_CONSTANT*getSize()*Game.MS_PER_UPDATE);
		}
	}
	
	protected abstract void explode(World w);
	
	public static class FragGrenade extends TimedGrenade {

		public FragGrenade(ControlledCharacter source, double direction, double speed, long timeLeft) {
			super(source, direction, speed, timeLeft);
		}

		@Override
		protected void explode(World w) {
			final int FRAGS = 20;
			final double BASE_SIZE = 10;
			final double BASE_SPEED = 0.6;
			
			w.getEventListener().onEventReceived(new GrenadeExplodeEvent(getX(),getY(),0));
			
			for (int i=0;i<FRAGS*0.8;i++) {
				double direction = Math.PI*2*i/FRAGS;
				double sizeF = Math.max(1,1 + 0.8*Utils.random().nextGaussian()/2);
				w.addProjectile(new Bullet(this, direction, BASE_SPEED/Math.sqrt(sizeF), BASE_SIZE*sizeF));
			}
			for (int i=0;i<FRAGS*0.2;i++) {
				double direction = Utils.random().nextDouble()*Math.PI*2;
				double sizeF = Math.max(1,1 + 0.8*Utils.random().nextGaussian()/2);
				w.addProjectile(new Bullet(this, direction, BASE_SPEED/Math.sqrt(sizeF), BASE_SIZE*sizeF));
			}
		}
	}
	
	public static class FlashGrenade extends TimedGrenade {
		public static final double RANGE = 20;
		public static final long duration = 8000;
		public FlashGrenade(ControlledCharacter source, double direction, double speed, long timeLeft) {
			super(source, direction, speed, timeLeft);
		}

		@Override
		protected void explode(World w) {
			w.getEventListener().onEventReceived(new GrenadeExplodeEvent(getX(),getY(),0));
			LineOfSight los = new LineOfSight();
			Area a = los.genLOSAreaMeter(getX(), getY(), RANGE, Math.PI*2, 0, w.getArena());
			for (ControlledCharacter c:w.getCharacters()) {
				
				if (a.contains(c.getX(), c.getY())) {
					double angle = Math.abs(Geometry.wrapAngle(c.getDirection() - Math.atan2(c.getY()-getY(), getX()-c.getX())));
					if (angle<Math.PI/2) {
						// BLIND THEM ALL!
						Blinded b = new Blinded(c,duration);
						c.addStatusEffect(b);
					}
				}
			}
		}
	}
}
