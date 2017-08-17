package server.weapon;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Scanner;

import server.ability.ChangeForm;
import server.character.ClassStats;
import server.character.InputControlledEntity;
import server.projectile.Bullet;
import server.projectile.HitMod;
import server.world.World;

public class WeaponFactory {
	public static void initWeapons() {
		if (weaponTypes==null){
			weaponTypes = new HashMap<Integer,WeaponType>();
			Scanner sc = null;
			try {
				InputStream is = ClassStats.class.getResourceAsStream("/weapon/gunStats");
				if (is!=null) {
					sc = new Scanner(is);
					while(sc.hasNext()) {
						WeaponType type = new WeaponType(
								sc.nextInt(),
								sc.nextInt(),
								sc.nextDouble(),
								sc.nextDouble(),
								sc.nextDouble(),
								sc.nextDouble(),
								sc.nextDouble(),
								sc.nextInt(),
								sc.nextInt(),
								sc.nextInt(),
								sc.nextInt(),
								sc.nextInt(),
								sc.nextInt(),
								sc.nextDouble());
						weaponTypes.put(type.weaponId,type);
					}
					is.close();
					sc.close();
				}
			} catch (IOException e) {
				System.err.println("Fail to load weapon stats");
				System.exit(-1);
				e.printStackTrace();
			}
		}
	}
	
	private static HashMap<Integer, WeaponType> weaponTypes;
	
	public static WeaponType getWeaponData(int weaponID) {
		return weaponTypes.get(weaponID);
	}
	
	public static Weapon createGun(int weaponID, int modID, int hModID, InputControlledEntity self) {
		WeaponType type = weaponTypes.get(weaponID);
		WeaponMod mod = WeaponMod.get(modID);
		HitMod hMod = HitMod.get(hModID);
		if (mod!=null) {
			type = mod.getModifiedType(type);
		}
		if (weaponID==ChangeForm.CF_HUMAN_WEAPON) {
			return new Weapon(self,type,hMod) {
				@Override
				protected void fire(World w, InputControlledEntity c, double direction) {
					
				}
			};
		}
		else if (type.weaponType==0) {
			return new Weapon(self,type,hMod) {
				@Override
				public void fire(World w, InputControlledEntity c, double direction) {
					for (int i=0;i<type.getBulletsNo();i++) {
						double speed = randomizeStat(type.getProjectileSpeed(),0.1);
						fireOneBullet(w,c,disperseDirection(direction),speed);
					}
				}
			};
		} else if (type.weaponType==2) {
			return new Weapon(self,type,hMod) {
				@Override
				public void fire(World w, InputControlledEntity c, double direction) {
					double gunDirection = direction;
					for (int i=0;i<type.getBulletsNo();i++) {
						double bulletDirection = 2*type.getGunDispersion()*(i/(type.getBulletsNo()-1.0)-0.5);
						double speed = randomizeStat(type.getProjectileSpeed(),0.1);
						fireOneBullet(w,c,gunDirection+bulletDirection,speed);
					}
				}
			};
		}
		else if (type.weaponType==1) {
			return new Weapon(self,type,hMod) {
				@Override
				protected void fire(World w, InputControlledEntity c, double direction) {
					super.fireOneBullet(w, c, disperseDirection(direction),type.getProjectileSpeed());
				}
			};
		} else if (type.weaponType==3) {
			return new Weapon(self,type,hMod) {
				@Override
				protected void fire(World w, InputControlledEntity c, double direction) {
					w.addProjectile(new Bullet(c,c.getX(), c.getY(),direction,type.getProjectileSpeed(), type.getSize(), type.getDamage(), type.length));
				}
			};
		} else {
			return null;
		}
	}

}
