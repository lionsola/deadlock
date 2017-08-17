package server.character;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Scanner;

import editor.SpawnPoint.CharType;

public class ClassStats {
	public static HashMap<Integer,ClassStats> classStats = null;
	
	private double sizeF, speedF, maxHP, noiseF;
	private int bodyId, weaponId, abilityId, passiveId;
	
	private CharType classID;
	/**
	 * @return the classID
	 */
	public CharType getClassID() {
		return classID;
	}
	/**
	 * @return the size
	 */
	public double getSize() {
		return sizeF;
	}
	/**
	 * @return the speed
	 */
	public double getSpeedF() {
		return speedF;
	}

	/**
	 * @return the maxHP
	 */
	public double getMaxHP() {
		return maxHP;
	}
	/**
	 * @return the volume
	 */
	public double getNoise() {
		return noiseF;
	}
	
	/**
	 * @return the weaponId
	 */
	public int getWeaponId() {
		return weaponId;
	}
	/**
	 * @return the abilityId
	 */
	public int getAbilityId() {
		return abilityId;
	}
	/**
	 * @return the passiveId
	 */
	public int getPassiveId() {
		return passiveId;
	}
	
	/**
	 * @return the bodyId
	 */
	public int getBodyId() {
		return bodyId;
	}
	public static void initClassStats() {
		if (classStats==null){
			
			classStats = new HashMap<Integer,ClassStats>();
			Scanner sc = null;
			
			InputStream is = ClassStats.class.getResourceAsStream("/character/classStats");
			if (is!=null) {
				try {
					sc = new Scanner(is);
					while(sc.hasNext()) {
						ClassStats cs = new ClassStats();
						cs.classID = CharType.valueOf(sc.next());
						cs.sizeF = sc.nextDouble();
						cs.speedF = sc.nextDouble();
						cs.maxHP = sc.nextDouble();
						cs.noiseF = sc.nextDouble();
						
						cs.bodyId = sc.nextInt();
						cs.weaponId = sc.nextInt();
						cs.abilityId = sc.nextInt();
						cs.passiveId = sc.nextInt();
						
						classStats.put(cs.classID.id,cs);
					}
					sc.close();
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
