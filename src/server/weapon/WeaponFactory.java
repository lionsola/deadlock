package server.weapon;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

import server.ability.ChangeForm;
import server.character.InputControlledEntity;
import server.world.World;

public class WeaponFactory {
	public static void initWeapons() {
		if (weaponTypes==null){
			weaponTypes = new HashMap<Integer,WeaponType>();
			Scanner sc = null;
			try {
				sc = new Scanner(new File("resource/weapon/gunStats"));
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
							sc.nextInt());
					weaponTypes.put(type.weaponId,type);
				}
				sc.close();
			} catch (IOException e) {
				System.err.println("Fail to load weapon stats");
				System.exit(-1);
				e.printStackTrace();
			} finally {
				if (sc!=null) {
					sc.close();
				}
			}
			
		}
	}
	
	private static HashMap<Integer, WeaponType> weaponTypes;
	
	public static WeaponType getWeaponData(int weaponID) {
		return weaponTypes.get(weaponID);
	}
	
	public static Weapon createGun(int weaponID, InputControlledEntity self) {
		WeaponType type = weaponTypes.get(weaponID);
		if (weaponID==ChangeForm.CF_HUMAN_WEAPON) {
			return new Weapon(self,type) {
				@Override
				protected void fire(World w, InputControlledEntity c, double direction) {
					for (int i=0;i<type.bulletsNo;i++) {
						//BatAI bat = new BatAI(ClassStats.classStats.get(21),c.id,c.team);
						//bat.setX(c.getX());
						//bat.setY(c.getY());
						//bat.setDirection(direction);
						//bat.set
						//w.addNPC();
					}
				}
			};
		}
		else if (type.weaponType==1) {
			return new Weapon(self,type) {
				@Override
				public void fire(World w, InputControlledEntity c, double direction) {
					for (int i=0;i<type.bulletsNo;i++) {
						double speed = randomizeStat(type.projectileSpeed,0.1);
						fireOneBullet(w,c,disperseDirection(direction),speed);
					}
				}
			};
		} else if (type.weaponType==2) {
			return new Weapon(self,type) {
				@Override
				public void fire(World w, InputControlledEntity c, double direction) {
					double gunDirection = direction;
					for (int i=0;i<type.bulletsNo;i++) {
						double bulletDirection = 2*type.gunDispersion*(i/(type.bulletsNo-1.0)-0.5);
						double speed = randomizeStat(type.projectileSpeed,0.1);
						fireOneBullet(w,c,gunDirection+bulletDirection,speed);
					}
				}
			};
		}
		else {
			return new Weapon(self,type) {
				@Override
				protected void fire(World w, InputControlledEntity c, double direction) {
					super.fireOneBullet(w, c, disperseDirection(direction),type.projectileSpeed);
				}
			};
		}
	}

}
