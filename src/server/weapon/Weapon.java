package server.weapon;

import java.awt.geom.Point2D;

import client.gui.GameWindow;
import server.ability.Ability;
import server.character.InputControlledEntity;
import server.projectile.Bullet;
import server.projectile.HitMod;
import server.status.Stunned;
import server.world.Geometry;
import server.world.Utils;
import server.world.World;
import shared.network.event.AnimationEvent;

/**
 * The <code>Weapon</code> class defines the behaviour of ALL weapons, both Primary Secondary, etc.
 * Here the Accessor and Mutator methods are located for ease of access elsewhere in the code and to
 * avoid code duplication. The Weapon class encompasses how ALL weapons will behave.
 * 
 * A specific set of <code>Weapons</code> are used by each <code>ControlledCharacter<code>.
 * 
 * @see InputControlledEntity
 * @see PrimaryWeapon
 * @see SecondaryWeapon
 * @author Team D1
 * @author Connor Cartwright
 *
 */
public abstract class Weapon extends Ability {
	public static final int ASSAULT_RIFLE_ID = 0;
	public static final int MARKMAN_RIFLE_ID = 1;
	public static final int SHOTGUN_ID = 2;
	public static final int MP7_ID = 3;
	public static final int SILENT_PISTOL_ID = 4;
	public static final int MELEE_ID = 8;
	public static final int BITE_ID = 9;
	
	public final WeaponType type;
	private int ammoLeft;
	private long reloadTimer;
	private HitMod hMod;

	public Weapon(InputControlledEntity self, WeaponType type, HitMod hMod) {
		super(type.getWeaponId(),self,type.getCooldown());
		this.type = type;
		reset();
		this.hMod = hMod;
	}

	public void update(World w, InputControlledEntity c) {
		super.update(w);
		if (!self().isDead()) {
			if (c.getInput().fire1 && isReady()) {
				//double direction = c.disperseDirection();
				
				double direction = c.getDirection();
				
				fire(w,c,direction);
				final double RECOIL_DISTANCE = 0.3;
				Point2D p = Geometry.PolarToCartesian(RECOIL_DISTANCE*getRecoil(),
						Math.PI+direction);
				
				c.setPosition(w,c.getX()+p.getX(),c.getY()-p.getY());
				long stunDur = (long) (getRecoil()*200);
				if (stunDur>0) {
					c.setDx(0);
					c.setDy(0);
					c.addStatusEffect(new Stunned(c,stunDur));
					System.out.println("Stunned by recoil in " + stunDur + "ms");
				}
				
				
				ammoLeft -= 1;
				Point2D shotPoint = Geometry.PolarToCartesian(type.length, direction);
				w.addAnimation(AnimationEvent.GUNSHOT, c.getX()+shotPoint.getX(), c.getY()-shotPoint.getY(), direction);
				w.addSound(type.getSoundId(), type.getNoise(), c.getX()+shotPoint.getX(), c.getY()-shotPoint.getY());
				
				double maxRecoilAngle = InputControlledEntity.MAX_DISPERSION_ANGLE*getRecoil();
				double recoilAngle = maxRecoilAngle*Math.min(1,Math.abs(Utils.random().nextGaussian())); 
				recoilAngle = Math.copySign(recoilAngle,(c.getDirection()-c.getTargetDirection())*(Utils.random().nextDouble()<0.75?1:-1));
				c.setDirection(c.getDirection()+recoilAngle);
				
				startCooldown();
				if (ammoLeft<=0) {
					reloadTimer = 0;
				}
			} else if (c.getInput().reload) {
				ammoLeft = 0;
				reloadTimer = 0;
			}
			if (ammoLeft==0) {
				if (reloadTimer>type.getReloadTime()) {
					ammoLeft = type.getMagSize();
				}
				else {
					reloadTimer += GameWindow.MS_PER_UPDATE;
				}
			}
		}
	}

	protected abstract void fire(World w, InputControlledEntity c, double direction);
	
	protected double disperseDirection(double gunDirection) {
		return gunDirection + type.getGunDispersion()*Utils.random().nextGaussian()/2;
	}
	
	public static double randomizeStat(double stat, double limit) {
		return stat*(1+limit*Utils.random().nextGaussian()/2);
	}
	
	protected void fireOneBullet (World w, InputControlledEntity c, double direction, double speed) {
		Point2D p = Geometry.PolarToCartesian(type.getLength(), direction);
		Bullet b = new Bullet(c,c.getX()+p.getX(),c.getY()-p.getY(),direction, speed, type.getSize(), type.getDamage());
		b.setHMod(hMod);
		w.addProjectile(b);
	}
	
	@Override
	public boolean isReady() {
		return super.isReady() && ammoLeft>0; 
	}
	
	@Override
	public double getCooldownPercent() {
		return Math.min(super.getCooldownPercent(), Math.min(1,1.0*reloadTimer/type.getReloadTime()));
	}
	
	@Override
	public int timeLeft() {
		if (ammoLeft>0) {
			return super.timeLeft();
		} else {
			return (int) (type.getReloadTime()-reloadTimer);
		}
	}
	
	@Override
	public void setStatus(boolean enabled) {
		super.setStatus(enabled);
		if (!isEnabled()) {
			reloadTimer = 0;
		}
	}
	
	public int getAmmo() {
		return ammoLeft;
	}
	
	@Override
	public void reset() {
		super.reset();
		ammoLeft = type.getMagSize();
		reloadTimer = type.getReloadTime();
	}
	
	public double getRecoil() {
		return self().getRecoilMod()*type.getInstability();
	}
}
