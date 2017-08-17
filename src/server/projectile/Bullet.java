package server.projectile;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import client.gui.GameWindow;
import server.character.Entity;
import server.character.InputControlledEntity;
import server.status.Stunned;
import server.world.Thing;
import server.world.Tile;
import server.world.World;
import shared.network.event.AnimationEvent;
import shared.network.event.GameEvent;
import shared.network.event.SoundEvent;

public class Bullet extends Projectile {
	private static final double BULLET_AIR_RESIST_CONSTANT = 0.0001;
	private static final double BULLET_SPEED_REDUCTION_CONSTANT = 0.00001;
	private static final double BULLET_PENETRATION_CONSTANT = 0.15;
	
	private final double baseDamage;
	private final double baseSpeed;
	private Thing lastHit;
	
	public Bullet(Entity source, double x, double y, double direction, double speed, double size, double damage) {
		this(source, x, y, direction, speed, size, damage, Double.MAX_VALUE);
	}
	
	public Bullet(Entity source, double x, double y, double direction, double speed, double size, double damage, double range) {
		super(source, x, y, direction, speed, size, range);
		baseDamage = damage;
		baseSpeed = speed;
	}
	
	public Bullet(Projectile source, double direction, double speed, double size, double damage) {
		super(source, direction, speed, size);
		baseDamage = damage;
		baseSpeed = speed;
	}
	
	@Override
	public void onHitCharacter(World w, InputControlledEntity ch) {
		//w.getEventListener().onEventReceived(new BulletHitPlayerEvent( x, y));
		if (!ch.isDead()) {
			if (getHMod()!=null)
				getHMod().onHitEntity(w, ch, this);
			double damageRatio;
			double HEADSHOT_DISTANCE = ch.getRadius()/20;
			Point2D h = ch.getHead();
			double bulletDist = Line2D.ptLineDist(getX()-getDx(), getY()-getDy(),getX(),getY(), h.getX(), h.getY());
			if (bulletDist-getSize()*getSize()/500.0<=HEADSHOT_DISTANCE) {
				damageRatio = 2;
				w.addEvent(new GameEvent.Headshot(id,ch.id,getX(),getY()));
				if (ch.isEnabled()) {
					ch.addStatusEffect(new Stunned(ch, Math.min(200, (long)(4*getSize()*getSize()))));
				}
			}
			else {
				damageRatio = (1.25-0.75*bulletDist/ch.getRadius());
			}
			
			System.out.println("Damage ratio: "+damageRatio);
			ch.onHit(w,damageRatio*getDamage(),id);
			AnimationEvent e = new AnimationEvent(AnimationEvent.BLOOD,ch.team,getX(),getY(),getDirection());
			e.value = (float) (damageRatio*getDamage());
			w.addGlobalAnimation(e);
			
			setSpeed(0);
			/*
			double speedReduced = BULLET_PENETRATION_CONSTANT*Entity.BASE_RADIUS*2/3.0
					+ BULLET_SPEED_REDUCTION_CONSTANT;
			setSpeed(getSpeed()-speedReduced);
			*/
		}
	}

	@Override
	protected void onHitWall(World w, Tile t) {
		if (getHMod()!=null)
			getHMod().onHitWall(w, t, this);
		
		double speedReduced = BULLET_PENETRATION_CONSTANT*Projectile.RAYCAST_DISTANCE*t.getCoverType()/3.0
				+ BULLET_SPEED_REDUCTION_CONSTANT;
		setSpeed(getSpeed()-speedReduced);
		if (t.getThing()!=lastHit){
			//w.addSound(SoundEvent.BULLET_WALL_SOUND_ID,SoundEvent.BULLET_WALL_SOUND_VOLUME*Math.max(0.5,Math.min(2,getSize()/5)),getX(),getY());
			w.addSound(-1,SoundEvent.BULLET_WALL_SOUND_VOLUME*Math.max(0.5,Math.min(2,getSize()/5)),getX(),getY());
			//w.addAnimation(AnimationEvent.BULLETWALL, x, y, 0);
			lastHit = t.getThing();
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
		return super.isConsumed() || (getSpeed()<=0.0001);
	}
}
