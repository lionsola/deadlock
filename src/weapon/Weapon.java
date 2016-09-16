package weapon;

import network.GameEvent.GunShotEvent;
import character.ControlledCharacter;
import core.Utils;
import core.World;

/**
 * The <code>Weapon</code> class defines the behaviour of ALL weapons, both Primary Secondary, etc.
 * Here the Accessor and Mutator methods are located for ease of access elsewhere in the code and to
 * avoid code duplication. The Weapon class encompasses how ALL weapons will behave.
 * 
 * A specific set of <code>Weapons</code> are used by each <code>ControlledCharacter<code>.
 * 
 * @see ControlledCharacter
 * @see PrimaryWeapon
 * @see SecondaryWeapon
 * @author Team D1
 * @author Connor Cartwright
 *
 */
public abstract class Weapon {
	
	public final WeaponType type;
	private double instability;
	private long lastFire;
	private int ammoLeft;
	private long lastReload;

	public Weapon(WeaponType type) {
		this.type = type;
		instability = type.instability;
		ammoLeft = type.magSize;
	}

	public void update(World w, ControlledCharacter c) {
		if (c.getInput().fire1 && isReady()) {
			double direction = c.disperseDirection();
			w.getEventListener().onEventReceived(new GunShotEvent(c.getX(), c.getY(), (float)direction, type.weaponId));
			
			lastFire = System.currentTimeMillis();
			fire(w,c,direction);
			c.addDispersion(getInstability());
			ammoLeft -= 1;
			if (ammoLeft<=0) {
				lastReload = System.currentTimeMillis();
			}
		} else if (ammoLeft==0 && timeSinceReload()>type.reloadTime) {
			ammoLeft = type.magSize;
		}
	}

	protected abstract void fire(World w, ControlledCharacter c, double direction);
	
	protected double disperseDirection(double gunDirection) {
		return gunDirection + type.gunDispersion*Utils.random().nextGaussian()/2;
	}
	
	protected void fireOneBullet (World w, ControlledCharacter c, double direction) {
		w.addProjectile(new Bullet(c, type.damage, direction, type.projectileSpeed, type.size));
	}

	public boolean isReady() {
		return timeSinceFire()>type.cooldown && ammoLeft>0; 
	}
	
	public long timeSinceFire() {
		return System.currentTimeMillis()-lastFire;
	}
	
	public long timeSinceReload() {
		return System.currentTimeMillis()-lastReload;
	}
	
	public double getInstability() {
		return instability;
	}

	public void setInstability(double instability) {
		this.instability = instability;
	}
}
