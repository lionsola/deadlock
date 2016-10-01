package server.weapon;

import java.awt.geom.Line2D;

import client.graphics.Animation;
import client.gui.GameWindow;
import network.GameEvent.PlayerDieEvent;
import server.character.ControlledCharacter;
import server.world.Geometry;
import server.world.Projectile;
import server.world.Sound;
import server.world.Tile;
import server.world.World;

public class Bullet extends Projectile {
	private static final double BULLET_AIR_RESIST_CONSTANT = 0.0005;
	private static final double BULLET_PENETRATION_CONSTANT = 0.3;
	Tile lastHit;
	
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
		//w.getEventListener().onEventReceived(new BulletHitPlayerEvent( x, y));
		double damageRatio;
		double HEADSHOT_DISTANCE = ch.getRadius()/10;
		double bulletDist = Line2D.ptLineDist(getX()-getDx(), getY()-getDy(), x, y, ch.getX(), ch.getY());
		if (bulletDist<=HEADSHOT_DISTANCE) {
			damageRatio = 99999;
		}
		else {
			damageRatio = (1.25-0.5*bulletDist/ch.getRadius());
		}
		
		System.out.println("Damage ratio: "+damageRatio);
		ch.onHit(damageRatio*getDamage());
		w.addAnimation(Animation.BLOOD,x,y,this.getDirection());
		// KILL
		if (ch.getHealthPoints() <= 0) {
			w.getEventListener().onEventReceived(new PlayerDieEvent(id, ch.id));
			w.onPlayerDeath(ch);
		}
		
	}

	protected void onHitWall(World w, double x, double y, Tile t) {
		setSpeed(getSpeed()-BULLET_PENETRATION_CONSTANT*Geometry.LINE_SAMPLE_THRESHOLD);
		if (t!=lastHit){
			w.addSound(Sound.BULLETWALL.id,Sound.BULLETWALL.volume*Math.max(0.5,Math.min(2,getSize()/5)),x,y);
			lastHit = t;
		}
	}
	
	public double getDamage() {
		return getSpeed()*getSize()*10;
	}
	
	@Override
	public void update(World w) {
		super.update(w);
		if (getSize()<50)
			setSpeed(getSpeed() - BULLET_AIR_RESIST_CONSTANT*getSize()*getSpeed()*GameWindow.MS_PER_UPDATE) ;
		if (w.getArena().getTileAt(getX(), getY()).isWalkable())
			lastHit = null;
	}

	@Override
	public boolean isConsumed() {
		return getSpeed()<=0;
	}
}
