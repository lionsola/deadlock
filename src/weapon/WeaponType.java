package weapon;

public class WeaponType {
	public final int weaponId;
	public final int weaponType;
	
	public final double gunDispersion;
	public final double instability;
	public final double damage;
	public final double size; // how far can the weapon fire projectiles before they drop
	public final double projectileSpeed; // how fast the projectiles are, that the weapon fires
	
	public final int cooldown; // the time between each shot in ms
	public final int noise; // the noise released by firing this weapon
	public final int reloadTime; // how long it takes for the gun to reload
	public final int magSize; // the amount of projectiles in each magazine
	public final int bulletsNo;
	
	public WeaponType(int weaponId, int weaponType,
			double dispersion, double instability, double damage, double size, double projectileSpeed,
			int cooldown, int noise, int reloadTime, int magSize, int bulletsNo) {
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
	}
}
