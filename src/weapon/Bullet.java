package weapon;

import java.awt.geom.Line2D;

import character.ControlledCharacter;
import core.Projectile;
import core.World;
import game.Game;
import network.GameEvent.BulletHitPlayerEvent;
import network.GameEvent.BulletHitWallEvent;
import network.GameEvent.PlayerDieEvent;

public class Bullet extends Projectile {
	private static final double RESIST_CONSTANT = 0.0005;
	
	private double damage;
	
	public Bullet(ControlledCharacter source, double damage, double direction, double speed, double size) {
		super(source, direction, speed, size);
		this.damage = damage;
		// TODO Auto-generated constructor stub
	}
	
	public Bullet(Projectile source, double damage, double direction, double speed, double size) {
		super(source, direction, speed, size);
		this.damage = damage;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onHitCharacter(World w, ControlledCharacter ch, double x, double y) {
		w.getEventListener().onEventReceived(new BulletHitPlayerEvent( x, y));
		double damageRatio;
		double HEADSHOT_DISTANCE = 1;
		double bulletDist = Line2D.ptLineDist(getX(), getY(), x, y, ch.getX(), ch.getY());
		if (bulletDist<=HEADSHOT_DISTANCE) {
			damageRatio = 9999;
		}
		else {
			damageRatio = (1.45-bulletDist/ch.getRadius());
		}
		
		System.out.println("Damage ratio: "+damageRatio);
		ch.setHealthPoints(ch.getHealthPoints() - damageRatio*getDamage());

		// KILL
		if (ch.getHealthPoints() <= 0) {
			w.getEventListener().onEventReceived(new PlayerDieEvent(id, ch.id));
			w.onPlayerDeath(ch);
		}
		// consume the projectile
		consumeProjectile();
	}

	protected void onHitWall(World w, double x, double y) {
		w.getEventListener().onEventReceived(new BulletHitWallEvent(x,y));
		consumeProjectile();
	}
	
	public double getDamage() {
		return damage;
	}

	@Override
	protected void onHitDestination(World w) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void update(World w) {
		super.update(w);
		setSpeed(getSpeed() - RESIST_CONSTANT*getSize()*getSpeed()*Game.MS_PER_UPDATE) ;
	}
}
