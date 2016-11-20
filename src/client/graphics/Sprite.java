package client.graphics;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.util.HashMap;

import javax.imageio.ImageIO;

import server.world.Utils;

/**
 * Load and store the sprites needed in-game.
 */
public class Sprite {
	public static final int TILE_SPRITE_SIZE = 32;
	
	public static final int MAXTYPE = 4;
	private static Image COMMANDO_RED;
	private static Image SCOUT_RED;
	private static Image SNIPER_RED;
	private static Image TANK_RED;
	private static Image SPECOPS_RED;
	private static Image COMMANDO_GREEN;
	private static Image SCOUT_GREEN;
	private static Image SNIPER_GREEN;
	private static Image TANK_GREEN;
	private static Image SPECOPS_GREEN;
	public static BufferedImage[] SHIELD;
	private static Image[][] images = new Image[5][2];
	private static BufferedImage[] BLOOD = new BufferedImage[4];
	public static HashMap<Integer,BufferedImage> guns = new HashMap<Integer,BufferedImage>();

	public static void initImage() {
		try {
			for (int i=0;i<BLOOD.length;i++) {
				BLOOD[i] = ImageIO.read(new FileInputStream("resource/animation/blood"+i+".png"));
			}
			
			COMMANDO_RED = ImageIO.read(new FileInputStream("resource/character/commando_red.png"));
			SCOUT_RED = ImageIO.read(new FileInputStream("resource/character/scout_red.png"));
			SNIPER_RED = ImageIO.read(new FileInputStream("resource/character/sniper_red.png"));
			TANK_RED = ImageIO.read(new FileInputStream("resource/character/tank_red.png"));
			SPECOPS_RED = ImageIO.read(new FileInputStream("resource/character/specops_red.png"));

			COMMANDO_GREEN = ImageIO.read(new FileInputStream("resource/character/commando_green.png"));
			SCOUT_GREEN = ImageIO.read(new FileInputStream("resource/character/scout_green.png"));
			SNIPER_GREEN = ImageIO.read(new FileInputStream("resource/character/sniper_green.png"));
			TANK_GREEN = ImageIO.read(new FileInputStream("resource/character/tank_green.png"));
			SPECOPS_GREEN = ImageIO.read(new FileInputStream("resource/character/specops_green.png"));
			
			int RED = 1;
			int GREEN = 0;
			images[2][RED] = SNIPER_RED;
			images[2][GREEN] = SNIPER_GREEN;
			images[1][RED] = SCOUT_RED;
			images[1][GREEN] = SCOUT_GREEN;
			images[0][RED] = TANK_RED;
			images[0][GREEN] = TANK_GREEN;
			images[4][RED] = COMMANDO_RED;
			images[4][GREEN] = COMMANDO_GREEN;
			images[3][RED] = SPECOPS_RED;
			images[3][GREEN] = SPECOPS_GREEN;
			
			SHIELD = new BufferedImage[3];
			
			SHIELD[0] = ImageIO.read(new FileInputStream("resource/shield1.png"));
			SHIELD[1] = ImageIO.read(new FileInputStream("resource/shield2.png"));
			SHIELD[2] = ImageIO.read(new FileInputStream("resource/shield3.png"));
			
			guns.put(0, ImageIO.read(new FileInputStream("resource/weapon/mp7.png")));
			guns.put(1, ImageIO.read(new FileInputStream("resource/weapon/shotgun.png")));
			guns.put(2, ImageIO.read(new FileInputStream("resource/weapon/sniper.png")));
			guns.put(3, ImageIO.read(new FileInputStream("resource/weapon/pistol.png")));
			guns.put(4, ImageIO.read(new FileInputStream("resource/weapon/m16.png")));
		} catch (Exception e) {
			System.out.println("Error while loading server.character images");
			System.out.println(e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
	}

	/**
	 * Get the correct image based on the type and colour (team).
	 * 
	 * @param type
	 *            used to select the appropriate image based on server.character type.
	 * @param color
	 *            used to select the appropriate image based on the characters team.
	 * @return
	 */
	//public static Image getImage(int type, int color) {
	//	return images[type][color];
	//}
	
	/**
	 * Return a random blood sprite.
	 */
	public static BufferedImage getBloodImage() {
		return BLOOD[Utils.random().nextInt(BLOOD.length)];
	}
}
