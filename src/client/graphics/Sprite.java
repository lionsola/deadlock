package client.graphics;

import java.awt.Image;
import java.io.FileInputStream;

import javax.imageio.ImageIO;

public class Sprite {
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
	private static Image[][] images = new Image[5][2];

	public static void initImage() {
		try {
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
	public static Image getImage(int type, int color) {
		return images[type][color];
	}
}
