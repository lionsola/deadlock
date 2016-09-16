package weapon;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import character.ControlledCharacter;
import core.World;

public class WeaponFactory {
	public static void initWeapons() {
		if (weaponTypes==null){
			weaponTypes = new ArrayList<WeaponType>();
			Scanner sc = null;
			try {
				sc = new Scanner(new File("resource/weapon/gunStats"));
				while(sc.hasNext()) {
					weaponTypes.add(new WeaponType(
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
							sc.nextInt()));
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
	
	private static ArrayList<WeaponType> weaponTypes;
	
	public static WeaponType getWeaponData(int weaponID) {
		return weaponTypes.get(weaponID);
	}
	
	public static Weapon createGun(int weaponID) {
		WeaponType type = weaponTypes.get(weaponID);
		if (type.weaponType==1) {
			return new Weapon(type) {
				@Override
				public void fire(World w, ControlledCharacter c, double direction) {
					for (int i=0;i<type.bulletsNo;i++) {
						fireOneBullet(w,c,disperseDirection(direction));
					}
				}
			};
		} else if (type.weaponType==2) {
			return new Weapon(type) {
				@Override
				public void fire(World w, ControlledCharacter c, double direction) {
					double gunDirection = disperseDirection(direction);
					for (int i=0;i<type.bulletsNo;i++) {
						double bulletDirection = i*type.gunDispersion/type.bulletsNo-type.gunDispersion/2; 
						fireOneBullet(w,c,gunDirection+bulletDirection);
					}
				}
			};
		}
		else {
			return new Weapon(type) {
				@Override
				protected void fire(World w, ControlledCharacter c, double direction) {
					super.fireOneBullet(w, c, disperseDirection(direction));
				}
			};
		}
	}

}
