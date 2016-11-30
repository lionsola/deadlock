package client.graphics;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

import server.ability.Ability;
import server.weapon.Weapon;
import server.world.Utils;

/**
 * Load and store the sprites needed in-game.
 */
public class Sprite {
	public static final int TILE_SPRITE_SIZE = 32;
	
	public static final int MAXTYPE = 4;
	/*
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
	*/
	public static BufferedImage[] COVER;
	//private static Image[][] images; 
	private static BufferedImage[] BLOOD;
	public static HashMap<Integer,BufferedImage> guns = new HashMap<Integer,BufferedImage>();
	public static HashMap<Integer,String> ability = new HashMap<Integer,String>();
	
	private static final String ABILITY_ICON_DIR = "resource/weapon/";
	private static final String WEAPON_SPRITE_DIR = "resource/weapon/";

	public static void initImage() {
		//images = new Image[5][2];
		BLOOD = new BufferedImage[4];
		
		try {
			for (int i=0;i<BLOOD.length;i++) {
				BLOOD[i] = ImageIO.read(new FileInputStream("resource/animation/blood"+i+".png"));
			}
			/*
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
			*/
			
			COVER = new BufferedImage[3];
			
			COVER[0] = ImageIO.read(new FileInputStream("resource/shield1.png"));
			COVER[1] = ImageIO.read(new FileInputStream("resource/shield2.png"));
			COVER[2] = ImageIO.read(new FileInputStream("resource/shield3.png"));
			
			guns.put(Weapon.MP7_ID, ImageIO.read(new FileInputStream(WEAPON_SPRITE_DIR+"mp7.png")));
			guns.put(Weapon.SHOTGUN_ID, ImageIO.read(new FileInputStream(WEAPON_SPRITE_DIR+"shotgun.png")));
			guns.put(Weapon.MARKMAN_RIFLE_ID, ImageIO.read(new FileInputStream(WEAPON_SPRITE_DIR+"sniper.png")));
			guns.put(Weapon.SILENT_PISTOL_ID, ImageIO.read(new FileInputStream(WEAPON_SPRITE_DIR+"pistol.png")));
			guns.put(Weapon.ASSAULT_RIFLE_ID, ImageIO.read(new FileInputStream(WEAPON_SPRITE_DIR+"m16.png")));
			
			ability.put(Weapon.MP7_ID, ABILITY_ICON_DIR+"weapon_mp7.png");
			ability.put(Weapon.SHOTGUN_ID, ABILITY_ICON_DIR+"weapon_shotgun.png");
			ability.put(Weapon.MARKMAN_RIFLE_ID, ABILITY_ICON_DIR+"weapon_sniper.png");
			ability.put(Weapon.SILENT_PISTOL_ID, ABILITY_ICON_DIR+"weapon_pistol.png");
			ability.put(Weapon.ASSAULT_RIFLE_ID, ABILITY_ICON_DIR+"weapon_assault.png");
			
			ability.put(Ability.BINO_ID, ABILITY_ICON_DIR+"ability_bino.png");
			ability.put(Ability.AMP_ID, ABILITY_ICON_DIR+"ability_amp.png");
			ability.put(Ability.SCOPE_ID, ABILITY_ICON_DIR+"ability_scope.png");
			ability.put(Ability.FLASH_ID, ABILITY_ICON_DIR+"ability_flash.png");
			ability.put(Ability.FRAG_ID, ABILITY_ICON_DIR+"ability_frag.png");
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
	
	public static BufferedImage getAbilityIcon(int id) {
		try {
			return ImageIO.read(new FileInputStream(ability.get(id)));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
