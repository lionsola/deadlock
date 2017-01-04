package server.weapon;

import java.awt.geom.Point2D;

import client.gui.GameWindow;
import server.ability.Ability;
import server.character.InputControlledEntity;
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
	
	public final WeaponType type;
	private double instability;
	private int ammoLeft;
	private long reloadTimer;

	public Weapon(InputControlledEntity self, WeaponType type) {
		super(type.weaponId,self,type.cooldown);
		this.type = type;
		reset();
	}

	public void update(World w, InputControlledEntity c) {
		super.update(w);
		if (!self().isDead()) {
			if (c.getInput().fire1 && isReady()) {
				//double direction = c.disperseDirection();
				
				double direction = c.getDirection();
				
				fire(w,c,direction);
				final double RECOIL_DISTANCE = 0.2;
				Point2D p = Geometry.PolarToCartesian(RECOIL_DISTANCE*type.instability,
						Math.PI+direction);
				
				c.setPosition(w,c.getX()+p.getX(),c.getY()-p.getY());
				
				ammoLeft -= 1;
				w.addAnimation(AnimationEvent.GUNSHOT, c.getX(), c.getY(), direction);
				w.addSound(type.soundId, type.noise, c.getX(), c.getY());
				
				double maxRecoil = InputControlledEntity.MAX_DISPERSION_ANGLE*getInstability();
				double recoil = maxRecoil*Math.min(1,Math.abs(Utils.random().nextGaussian())); 
				recoil = Math.copySign(recoil,(c.getDirection()-c.getTargetDirection())*(Utils.random().nextDouble()<0.8?1:-1));
				c.setDirection(c.getDirection()+recoil);
				
				startCooldown();
				if (ammoLeft<=0) {
					reloadTimer = 0;
				}
			} else if (c.getInput().reload) {
				ammoLeft = 0;
				reloadTimer = 0;
			}
			if (ammoLeft==0) {
				if (reloadTimer>type.reloadTime) {
					ammoLeft = type.magSize;
				}
				else {
					reloadTimer += GameWindow.MS_PER_UPDATE;
				}
			}
		}
	}

	protected abstract void fire(World w, InputControlledEntity c, double direction);
	
	protected double disperseDirection(double gunDirection) {
		return gunDirection + type.gunDispersion*Utils.random().nextGaussian()/2;
	}
	
	public static double randomizeStat(double stat, double limit) {
		return stat*(1+limit*Utils.random().nextGaussian()/2);
	}
	
	protected void fireOneBullet (World w, InputControlledEntity c, double direction, double speed) {
		w.addProjectile(new Bullet(c, direction, speed, type.size, type.damage));
	}
	
	@Override
	public boolean isReady() {
		return super.isReady() && ammoLeft>0; 
	}
	
	@Override
	public double getCooldownPercent() {
		return Math.min(super.getCooldownPercent(), Math.min(1,1.0*reloadTimer/type.reloadTime));
	}
	
	@Override
	public int timeLeft() {
		if (ammoLeft>0) {
			return super.timeLeft();
		} else {
			return (int) (type.reloadTime-reloadTimer);
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
		instability = type.instability;
		ammoLeft = type.magSize;
		reloadTimer = type.reloadTime;
	}
	
	public double getInstability() {
		return instability;
	}
	
	public void addRecoilMod(double recoilMod) {
		this.instability += recoilMod;
	}
}
