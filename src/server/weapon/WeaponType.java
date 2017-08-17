package server.weapon;

public class WeaponType implements Cloneable {

	public final int weaponId;
	public final int weaponType;
	
	private final double gunDispersion;
	private final double instability;
	private final double damage;
	private final double size; // how far can the server.weapon fire projectiles before they drop
	private final double projectileSpeed; // how fast the projectiles are, that the server.weapon fires
	
	private final int cooldown; // the time between each shot in ms
	private final int noise; // the volume released by firing this weapon
	private final int reloadTime; // how long it takes for the gun to reload
	private final int magSize; // the amount of projectiles in each magazine
	private final int bulletsNo;
	private final int soundId;
	
	public final double length;
	
	public WeaponType(int weaponId, int weaponType,
			double dispersion, double instability, double damage, double size, double projectileSpeed,
			int cooldown, int noise, int reloadTime, int magSize, int bulletsNo, int soundId, double length) {
		this.weaponId = weaponId;
		this.weaponType = weaponType;
		this.gunDispersion = dispersion;
		this.instability = instability;
		this.damage = damage;
		this.size = size;
		this.projectileSpeed = projectileSpeed;
		
		this.cooldown = cooldown;
		this.noise = noise;
		this.reloadTime = reloadTime;
		this.magSize = magSize;
		this.bulletsNo = bulletsNo;
		this.soundId = soundId;
		this.length = length;
	}
	
	/**
	 * @return the weaponId
	 */
	public int getWeaponId() {
		return weaponId;
	}

	/**
	 * @return the weaponType
	 */
	public int getWeaponType() {
		return weaponType;
	}

	/**
	 * @return the gunDispersion
	 */
	public double getGunDispersion() {
		return gunDispersion;
	}

	/**
	 * @return the instability
	 */
	public double getInstability() {
		return instability;
	}

	/**
	 * @return the damage
	 */
	public double getDamage() {
		return damage;
	}

	/**
	 * @return the size
	 */
	public double getSize() {
		return size;
	}

	/**
	 * @return the projectileSpeed
	 */
	public double getProjectileSpeed() {
		return projectileSpeed;
	}

	/**
	 * @return the cooldown
	 */
	public int getCooldown() {
		return cooldown;
	}

	/**
	 * @return the noise
	 */
	public int getNoise() {
		return noise;
	}

	/**
	 * @return the reloadTime
	 */
	public int getReloadTime() {
		return reloadTime;
	}

	/**
	 * @return the magSize
	 */
	public int getMagSize() {
		return magSize;
	}

	/**
	 * @return the bulletsNo
	 */
	public int getBulletsNo() {
		return bulletsNo;
	}

	/**
	 * @return the soundId
	 */
	public int getSoundId() {
		return soundId;
	}

	/**
	 * @return the length
	 */
	public double getLength() {
		return length;
	}

}
