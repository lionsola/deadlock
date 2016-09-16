/*package core;

import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.io.FileInputStream;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import character.ControlledCharacter;

*//**
 * This class will generate power ups.
 * 
 * @author Madyan Al-Jazaeri
 * @author Morgan Rees
 *//*
public class PowerUpFactory {

	// Power Up Types
	public static final int HEALTH_KIT = 0;
	public static final int NOISE_REDUCER = 1;
	public static final int DAMAGE_BOOSTER = 2;

	public static final int NUM_OF_TYPES = 3; // number of tyoes
	public static final Color[] COLORS = { new Color(217, 0, 29), new Color(254, 200, 14), new Color(56, 55, 200) }; // type
																														// colors
	public static final int[] PERIODS = { 0, 10, 10 }; // time period in second for power up to stay
														// active
	public static final int REGENERATE_PERIOD = 30;

	public static final int ICON_SIZE = 20; // icon size
	public static final double TILE_OFFSET = ((Tile.tileSize - ICON_SIZE) / 2); // icon offset from tile
	public static final int MIN_DISTANCE = 2; // minimum distance between power ups and players

	private static Image[] icons; // icons images to draw

	*//**
	 * Initializes all icon images.
	 *//*
	public static void initializeIcons() {
		icons = new Image[NUM_OF_TYPES];

		try {
			for (int i = 0; i < NUM_OF_TYPES; i++) {
				String filename = "resource/powerup/" + i + ".png";
				icons[i] = ImageIO.read(new FileInputStream(filename));
			}
		} catch (Exception e) {
			System.out.println("Error while loading powerup icons.");
			System.out.println(e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
	}

	*//**
	 * Returns the image icon for the given type of power-up.
	 * @param type
	 *            power up type
	 * @return power up icon
	 *//*
	public static Image getIcon(int type) {
		return icons[type];
	}

	*//**
	 * Generates power-ups in the given arena.
	 * @param arena
	 *            game arena
	 * @param characters
	 *            character list
	 * @return array of power ups
	 *//*
	public static PowerUp[] generatePowerUps(Arena arena, List<ControlledCharacter> characters) {
		int amount = calaculateAmount(arena.getWidth(), arena.getHeight());
		PowerUp[] powerUps = new PowerUp[amount];

		for (int i = 0; i < amount; i++) {
			Point coord = getValidCoord(i, powerUps, arena, characters);
			int x = coord.x;
			int y = coord.y;
			int type = getValidType();
			powerUps[i] = new PowerUp(x, y, type, icons[type]);
		}

		return powerUps;
	}

	*//**
	 * Calculates the number of power-ups to add to the arena.
	 * @param width
	 *            width of arena
	 * @param height
	 *            height of arena
	 * @return number of power ups
	 *//*
	private static int calaculateAmount(int width, int height) {
		return (width * height) / 200;
	}

	*//**
	 * Gets the valid coordinates of the power-up.
	 * @param powerUps
	 *            power ups array
	 * @param arena
	 *            game arena
	 * @param characters
	 *            character list
	 * @return coordinates
	 *//*
	public static Point getValidCoord(PowerUp[] powerUps, Arena arena, List<ControlledCharacter> characters) {
		int x, y;

		do {
			Random random = new Random();
			x = random.nextInt(arena.getWidth());
			y = random.nextInt(arena.getHeight());
		} while (!isValidCoord(powerUps, arena, characters, x, y));

		return new Point(x, y);
	}

	*//**
	 * Gets the valid coordinates of the power-up.
	 * @param index
	 *            power up index in array
	 * @param powerUps
	 *            power ups array
	 * @param arena
	 *            game arena
	 * @param characters
	 *            character list
	 * @return coordinates
	 *//*
	private static Point getValidCoord(int index, PowerUp[] powerUps, Arena arena, List<ControlledCharacter> characters) {
		int x, y;

		do {
			Random random = new Random();
			x = random.nextInt(arena.getWidth());
			y = random.nextInt(arena.getHeight());
		} while (!isValidCoord(index, powerUps, arena, characters, x, y));

		return new Point(x, y);
	}

	*//**
	 * Checks if the coordinates of the power-up are valid.
	 * @param powerUps
	 *            power ups array
	 * @param arena
	 *            game arena
	 * @param characters
	 *            character list
	 * @return coordinate is valid or not
	 *//*
	private static boolean isValidCoord(PowerUp[] powerUps, Arena arena, List<ControlledCharacter> characters, int x, int y) {
		if (!arena.get(x, y).isWalkable()) {
			return false;
		}

		for (int i = 0; i < powerUps.length; i++) {
			int deltaX = x - powerUps[i].getX();
			int deltaY = y - powerUps[i].getY();
			double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
			if (distance < MIN_DISTANCE) {
				return false;
			}
		}

		for (ControlledCharacter character : characters) {
			int deltaX = x - character.getX();
			int deltaY = y - character.getY();
			double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
			if (distance < MIN_DISTANCE) {
				return false;
			}
		}

		return true;
	}

	*//**
	 * Checks if the coordinates of the power-up are valid.
	 * @param index
	 *            power up index in array
	 * @param powerUps
	 *            power ups array
	 * @param arena
	 *            game arena
	 * @param characters
	 *            character list
	 * @return coordinate is valid or not
	 *//*
	private static boolean isValidCoord(int index, PowerUp[] powerUps, Arena arena, List<ControlledCharacter> characters, int x, int y) {
		if (!arena.get(x, y).isWalkable()) {
			return false;
		}

		for (int i = 0; i < index; i++) {
			int deltaX = x - powerUps[i].getX();
			int deltaY = y - powerUps[i].getY();
			double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
			if (distance < MIN_DISTANCE) {
				return false;
			}
		}

		for (ControlledCharacter character : characters) {
			int deltaX = x - character.getIntX();
			int deltaY = y - character.getIntY();
			double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
			if (distance < MIN_DISTANCE) {
				return false;
			}
		}

		return true;
	}

	*//**
	 * Returns random type of power-up.
	 * @return type
	 *//*
	private static int getValidType() {
		Random random = new Random();
		int type = random.nextInt(NUM_OF_TYPES);
		return type;
	}
}
*/