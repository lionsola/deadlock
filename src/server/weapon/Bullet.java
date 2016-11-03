package server.weapon;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import client.graphics.AnimationSystem;
import client.gui.GameWindow;
import server.character.PlayerCharacter;
import server.world.Geometry;
import server.world.Projectile;
import server.world.Sound;
import server.world.Thing;
import server.world.World;

public class Bullet extends Projectile {
	private static final double BULLET_AIR_RESIST_CONSTANT = 0.0005;
	private static final double BULLET_SPEED_REDUCTION_CONSTANT = 0.0001;
	private static final double BULLET_PENETRATION_CONSTANT = 0.3;
	Thing lastHit;
	
	public Bullet(PlayerCharacter source, double direction, double speed, double size) {
		super(source, direction, speed, size);
	}
	
	public Bullet(Projectile source, double direction, double speed, double size) {
		super(source, direction, speed, size);
	}
	
	@Override
	public void onHitCharacter(World w, PlayerCharacter ch, double x, double y) {
		//w.getEventListener().onEventReceived(new BulletHitPlayerEvent( x, y));
		double damageRatio;
		double HEADSHOT_DISTANCE = ch.getRadius()/10;
		Point2D h = ch.getHead();
		double bulletDist = Line2D.ptLineDist(getX()-getDx(), getY()-getDy(), x, y, h.getX(), h.getY());
		if (bulletDist<=HEADSHOT_DISTANCE) {
			damageRatio = 99999;
		}
		else {
			damageRatio = (1.25-0.5*bulletDist/ch.getRadius());
		}
		
		System.out.println("Damage ratio: "+damageRatio);
		ch.onHit(w,damageRatio*getDamage(),id);
		w.addAnimation(AnimationSystem.BLOOD,x,y,this.getDirection());
	}

	protected void onHitWall(World w, double x, double y, Thing t) {
		setSpeed(getSpeed()-BULLET_PENETRATION_CONSTANT*Geometry.LINE_SAMPLE_THRESHOLD-BULLET_SPEED_REDUCTION_CONSTANT);
		if (t!=lastHit){
			w.addSound(Sound.BULLETWALL.id,Sound.BULLETWALL.volume*Math.max(0.5,Math.min(2,getSize()/5)),x,y);
			lastHit = t;
		}
	}
	
	public double getDamage() {
		return getSpeed()*getSize()*8;
	}
	
	@Override
	public void update(World w) {
		super.update(w);
		setSpeed(getSpeed() - BULLET_AIR_RESIST_CONSTANT*getSize()*getSpeed()*GameWindow.MS_PER_UPDATE);
		if (w.getArena().getTileAt(getX(), getY()).isTraversable())
			lastHit = null;
	}

	@Override
	public boolean isConsumed() {
		return getSpeed()<=0.00000001;
	}
}
