package server.weapon;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import client.gui.GameWindow;
import server.character.PlayerCharacter;
import server.world.Projectile;
import server.world.Thing;
import server.world.World;
import shared.network.event.AnimationEvent;
import shared.network.event.GameEvent;
import shared.network.event.SoundEvent;

public class Bullet extends Projectile {
	private static final double BULLET_AIR_RESIST_CONSTANT = 0.0001;
	private static final double BULLET_SPEED_REDUCTION_CONSTANT = 0.00001;
	private static final double BULLET_PENETRATION_CONSTANT = 0.2;
	
	private final double baseDamage;
	private final double baseSpeed;
	Thing lastHit;
	
	public Bullet(PlayerCharacter source, double direction, double speed, double size, double damage) {
		super(source, direction, speed, size);
		baseDamage = damage;
		baseSpeed = speed;
	}
	
	public Bullet(Projectile source, double direction, double speed, double size, double damage) {
		super(source, direction, speed, size);
		baseDamage = damage;
		baseSpeed = speed;
	}
	
	@Override
	public void onHitCharacter(World w, PlayerCharacter ch, double x, double y) {
		//w.getEventListener().onEventReceived(new BulletHitPlayerEvent( x, y));
		if (!ch.isDead()) {
			double damageRatio;
			double HEADSHOT_DISTANCE = ch.getRadius()/12;
			Point2D h = ch.getHead();
			double bulletDist = Line2D.ptLineDist(getX()-getDx(), getY()-getDy(), x, y, h.getX(), h.getY());
			if (bulletDist<=HEADSHOT_DISTANCE) {
				damageRatio = 99999;
				w.addEvent(new GameEvent.HeadshotEvent(id,ch.id));
			}
			else {
				damageRatio = (1.25-0.75*bulletDist/ch.getRadius());
			}
			
			System.out.println("Damage ratio: "+damageRatio);
			ch.onHit(w,damageRatio*getDamage(),id);
		}
		w.addGlobalAnimation(AnimationEvent.BLOOD,x,y,this.getDirection());
	}

	protected void onHitWall(World w, double x, double y, Thing t) {
		double speedReduced = BULLET_PENETRATION_CONSTANT*Projectile.RAYCAST_DISTANCE*t.getCoverType()/3.0
				+ BULLET_SPEED_REDUCTION_CONSTANT;
		setSpeed(getSpeed()-speedReduced);
		if (t!=lastHit){
			w.addSound(SoundEvent.BULLET_WALL_SOUND_ID,SoundEvent.BULLET_WALL_SOUND_VOLUME*Math.max(0.5,Math.min(2,getSize()/5)),x,y);
			w.addAnimation(AnimationEvent.BULLETWALL, x, y, 0);
			lastHit = t;
		}
	}
	
	public double getDamage() {
		return baseDamage*getSpeed()/baseSpeed;
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
		return getSpeed()<=0.0001;
	}
}
