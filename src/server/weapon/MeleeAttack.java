package server.weapon;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import server.character.InputControlledEntity;
import server.world.Projectile;
import server.world.Thing;
import server.world.World;
import shared.network.event.AnimationEvent;
import shared.network.event.SoundEvent;

public class MeleeAttack extends Projectile {
	private final double MAX_RANGE;
	private double distanceTravelled;
	private double damage;
	public MeleeAttack(InputControlledEntity source, double range, double direction, double speed, double size, double damage) {
		super(source, direction, speed, size);
		this.damage = damage;
		MAX_RANGE = range;
	}
	
	@Override
	public void update(World w) {
		super.update(w);
		distanceTravelled += Point2D.distance(getX(), getY(), getPrevX(), getPrevY());
	}
	
	@Override
	public void onHitCharacter(World w, InputControlledEntity ch, double x, double y) {
		//w.getEventListener().onEventReceived(new BulletHitPlayerEvent( x, y));
		if (ch.id==this.id) {
			return;
		}
		if (!ch.isDead()) {
			double damageRatio;
			double bulletDist = Line2D.ptLineDist(getX()-getDx(), getY()-getDy(), x, y, ch.getX(), ch.getY());
			damageRatio = (1.25-0.75*bulletDist/ch.getRadius());
			System.out.println("Damage ratio: "+damageRatio);
			ch.onHit(w,damageRatio*damage,id);
			distanceTravelled = MAX_RANGE;
		}
		w.addSound(SoundEvent.MELEE_HIT_ID, 15, x, y);
		w.addGlobalAnimation(new AnimationEvent(AnimationEvent.BLOOD,ch.team,x,y,this.getDirection()));
	}

	@Override
	protected void onHitWall(World w, double x, double y, Thing t) {
		w.addSound(SoundEvent.MELEE_HIT_ID, 15, x, y);
	}

	@Override
	public boolean isConsumed() {
		return distanceTravelled>=MAX_RANGE;
	}
}
