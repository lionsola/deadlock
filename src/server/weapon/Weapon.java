package server.weapon;

import client.graphics.AnimationSystem;
import client.gui.GameWindow;
import server.ability.Ability;
import server.character.PlayerCharacter;
import server.world.Utils;
import server.world.World;

/**
 * The <code>Weapon</code> class defines the behaviour of ALL weapons, both Primary Secondary, etc.
 * Here the Accessor and Mutator methods are located for ease of access elsewhere in the code and to
 * avoid code duplication. The Weapon class encompasses how ALL weapons will behave.
 * 
 * A specific set of <code>Weapons</code> are used by each <code>ControlledCharacter<code>.
 * 
 * @see PlayerCharacter
 * @see PrimaryWeapon
 * @see SecondaryWeapon
 * @author Team D1
 * @author Connor Cartwright
 *
 */
public abstract class Weapon extends Ability {
	public final WeaponType type;
	private double instability;
	private int ammoLeft;
	private long reloadTimer = 0;

	public Weapon(PlayerCharacter self, WeaponType type) {
		super(self,type.cooldown);
		this.type = type;
		instability = type.instability;
		ammoLeft = type.magSize;
	}

	public void update(World w, PlayerCharacter c) {
		super.update(w);
		if (c.getInput().fire1 && isReady()) {
			double direction = c.disperseDirection();
			fire(w,c,direction);
			
			w.addAnimation(AnimationSystem.GUNSHOT, c.getX(), c.getY(), direction);
			w.addSound(type.soundId, type.noise, c.getX(), c.getY());
			
			c.addDispersion(getInstability());
			ammoLeft -= 1;
			startCooldown();
			if (ammoLeft<=0) {
				reloadTimer = 0;
			}
		} else if (ammoLeft==0) {
			if (reloadTimer>type.reloadTime) {
				ammoLeft = type.magSize;
			}
			else {
				reloadTimer += GameWindow.MS_PER_UPDATE;
			}
		}
	}

	protected abstract void fire(World w, PlayerCharacter c, double direction);
	
	protected double disperseDirection(double gunDirection) {
		return gunDirection + type.gunDispersion*Utils.random().nextGaussian()/2;
	}
	
	private static double randomizeStat(double stat, double limit) {
		return stat*(1+limit*Utils.random().nextGaussian()/2);
	}
	
	protected void fireOneBullet (World w, PlayerCharacter c, double direction) {
		w.addProjectile(new Bullet(c, direction, randomizeStat(type.projectileSpeed,0.1), type.size));
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
	public void reset() {
		super.reset();
		instability = type.instability;
		ammoLeft = type.magSize;
		reloadTimer = 0;
	}
	
	public double getInstability() {
		return instability;
	}
	
	public void addRecoilMod(double recoilMod) {
		this.instability += recoilMod;
	}
}
