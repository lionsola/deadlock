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
	public static HashMap<Integer,String> icons = new HashMap<Integer,String>();

	public static HashMap<Integer,BufferedImage> heads = new HashMap<Integer,BufferedImage>();
	
	private static final String ABILITY_ICON_DIR = "resource/weapon/";
	private static final String WEAPON_SPRITE_DIR = "resource/weapon/";

	public static void initImage() {
		//images = new Image[5][2];
		
		try {
			heads.put(CharType.Alpha.id,ImageIO.read(new FileInputStream("resource/character/alpha_head.png")));
			heads.put(CharType.Pi.id,ImageIO.read(new FileInputStream("resource/character/pi_head.png")));
			heads.put(CharType.Officer.id,ImageIO.read(new FileInputStream("resource/character/officer_head.png")));
			heads.put(CharType.MOfficer.id,ImageIO.read(new FileInputStream("resource/character/officer_head.png")));
			
			COVER = new BufferedImage[3];
			
			COVER[0] = ImageIO.read(new FileInputStream("resource/shield1.png"));
			COVER[1] = ImageIO.read(new FileInputStream("resource/shield2.png"));
			COVER[2] = ImageIO.read(new FileInputStream("resource/shield3.png"));
			
			guns.put(Weapon.MP7_ID, ImageIO.read(new FileInputStream(WEAPON_SPRITE_DIR+"mp7.png")));
			guns.put(Weapon.SHOTGUN_ID, ImageIO.read(new FileInputStream(WEAPON_SPRITE_DIR+"shotgun.png")));
			guns.put(Weapon.MARKMAN_RIFLE_ID, ImageIO.read(new FileInputStream(WEAPON_SPRITE_DIR+"sniper.png")));
			guns.put(Weapon.SILENT_PISTOL_ID, ImageIO.read(new FileInputStream(WEAPON_SPRITE_DIR+"pistol.png")));
			guns.put(Weapon.ASSAULT_RIFLE_ID, ImageIO.read(new FileInputStream(WEAPON_SPRITE_DIR+"m16.png")));
			guns.put(7, ImageIO.read(new FileInputStream(WEAPON_SPRITE_DIR+"pistol.png")));
			guns.put(Weapon.MELEE_ID, ImageIO.read(new FileInputStream(WEAPON_SPRITE_DIR+"melee.png")));
			
			icons.put(Weapon.MP7_ID, ABILITY_ICON_DIR+"weapon_mp7.png");
			icons.put(Weapon.SHOTGUN_ID, ABILITY_ICON_DIR+"weapon_shotgun.png");
			icons.put(Weapon.MARKMAN_RIFLE_ID, ABILITY_ICON_DIR+"weapon_sniper.png");
			icons.put(Weapon.SILENT_PISTOL_ID, ABILITY_ICON_DIR+"weapon_pistol.png");
			icons.put(Weapon.ASSAULT_RIFLE_ID, ABILITY_ICON_DIR+"weapon_assault.png");
			icons.put(Weapon.MELEE_ID, ABILITY_ICON_DIR+"weapon_melee.png");
			
			icons.put(Ability.BINO_ID, ABILITY_ICON_DIR+"ability_bino.png");
			icons.put(Ability.AMP_ID, ABILITY_ICON_DIR+"ability_amp.png");
			icons.put(Ability.SCOPE_ID, ABILITY_ICON_DIR+"ability_scope.png");
			icons.put(Ability.FLASH_ID, ABILITY_ICON_DIR+"ability_flash.png");
			icons.put(Ability.FRAG_ID, ABILITY_ICON_DIR+"ability_frag.png");
			
			icons.put(Passive.ASSAULT_ID, ABILITY_ICON_DIR+"ability_bino.png");
			icons.put(Passive.BACKSTAB_ID, ABILITY_ICON_DIR+"ability_bino.png");
			icons.put(Passive.MARK_ID, ABILITY_ICON_DIR+"ability_bino.png");
			icons.put(Passive.OVERWATCH_ID, ABILITY_ICON_DIR+"ability_bino.png");
			icons.put(Passive.SHIELD_ID, ABILITY_ICON_DIR+"ability_bino.png");
			
		} catch (Exception e) {
			System.out.println("Error while loading server.character images");
			System.out.println(e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public static BufferedImage getAbilityIcon(int id) {
		try {
			return ImageIO.read(new FileInputStream(icons.get(id)));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
