package server.character;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class ClassStats {
	public static ArrayList<ClassStats> classStats = null;
	
	private double sizeF, speedF, maxHP, noiseF;
	
	private int classID;
	/**
	 * @return the classID
	 */
	public int getClassID() {
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
	
	public static void initClassStats() {
		if (classStats==null){
			classStats = new ArrayList<ClassStats>();
			Scanner sc = null;
			try {
				sc = new Scanner(new File("resource/character/classStats"));
				while(sc.hasNext()) {
					ClassStats cs = new ClassStats();
					cs.classID = sc.nextInt();
					cs.sizeF = sc.nextDouble();
					cs.speedF = sc.nextDouble();
					cs.maxHP = sc.nextDouble();
					cs.noiseF = sc.nextDouble();
					
					classStats.add(cs);
				}
				sc.close();
			} catch (IOException e) {
				System.err.println("Fail to load class stats");
				System.exit(-1);
				e.printStackTrace();
			} finally {
				if (sc!=null) {
					sc.close();
				}
			}
			
		}
	}
}
