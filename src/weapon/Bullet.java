package weapon;

import java.awt.geom.Line2D;
import character.ControlledCharacter;
import core.Geometry;
import core.Projectile;
import core.World;
import game.Game;
import network.GameEvent.BulletHitPlayerEvent;
import network.GameEvent.PlayerDieEvent;

public class Bullet extends Projectile {
	private static final double BULLET_AIR_RESIST_CONSTANT = 0.0005;
	private static final double BULLET_PENETRATION_CONSTANT = 0.3;
	
	public Bullet(ControlledCharacter source, double direction, double speed, double size) {
		super(source, direction, speed, size);
		// TODO Auto-generated constructor stub
	}
	
	public Bullet(Projectile source, double direction, double speed, double size) {
		super(source, direction, speed, size);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onHitCharacter(World w, ControlledCharacter ch, double x, double y) {
		w.getEventListener().onEventReceived(new BulletHitPlayerEvent( x, y));
		double damageRatio;
		double HEADSHOT_DISTANCE = 0.1;
		double bulletDist = Line2D.ptLineDist(getX()-getDx(), getY()-getDy(), x, y, ch.getX(), ch.getY());
		if (bulletDist<=HEADSHOT_DISTANCE) {
			damageRatio = 9999;
		}
		else {
			damageRatio = (1.25-0.5*bulletDist/ch.getRadius());
		}
		
		System.out.println("Damage ratio: "+damageRatio);
		ch.setHealthPoints(ch.getHealthPoints() - damageRatio*getDamage());

		// KILL
		if (ch.getHealthPoints() <= 0) {
			w.getEventListener().onEventReceived(new PlayerDieEvent(id, ch.id));
			w.onPlayerDeath(ch);
		}
		
	}

	protected void onHitWall(World w, double x, double y) {
		setSpeed(getSpeed()-BULLET_PENETRATION_CONSTANT*Geometry.LINE_SAMPLE_THRESHOLD);
	}
	
	public double getDamage() {
		return getSpeed()*getSize()*10;
	}

	@Override
	protected void onHitDestination(World w) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void update(World w) {
		super.update(w);
		setSpeed(getSpeed() - BULLET_AIR_RESIST_CONSTANT*getSize()*getSpeed()*Game.MS_PER_UPDATE) ;
	}

	@Override
	public boolean isConsumed() {
		return getSpeed()<=0;
	}
}
