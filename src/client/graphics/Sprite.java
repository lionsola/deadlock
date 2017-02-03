package client.graphics;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

import editor.SpawnPoint.CharType;
import server.ability.Ability;
import server.passive.Passive;
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
	public static HashMap<Integer,BufferedImage> guns = new HashMap<Integer,BufferedImage>();
	public static HashMap<Integer,String> ability = new HashMap<Integer,String>();

	public static HashMap<Integer,BufferedImage> heads = new HashMap<Integer,BufferedImage>();
	
	private static final String ABILITY_ICON_DIR = "resource/weapon/";
	private static final String WEAPON_SPRITE_DIR = "resource/weapon/";

	public static void initImage() {
		//images = new Image[5][2];
		
		try {
			heads.put(CharType.Alpha.id,ImageIO.read(new FileInputStream("resource/character/alpha_head.png")));
			heads.put(CharType.Pi.id,ImageIO.read(new FileInputStream("resource/character/pi_head.png")));
			heads.put(CharType.Officer.id,ImageIO.read(new FileInputStream("resource/character/officer_head.png")));
			
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
			
			ability.put(Passive.ASSAULT_ID, ABILITY_ICON_DIR+"ability_bino.png");
			ability.put(Passive.BACKSTAB_ID, ABILITY_ICON_DIR+"ability_bino.png");
			ability.put(Passive.MARK_ID, ABILITY_ICON_DIR+"ability_bino.png");
			ability.put(Passive.OVERWATCH_ID, ABILITY_ICON_DIR+"ability_bino.png");
			ability.put(Passive.SHIELD_ID, ABILITY_ICON_DIR+"ability_bino.png");
			
		} catch (Exception e) {
			System.out.println("Error while loading server.character images");
			System.out.println(e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
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
