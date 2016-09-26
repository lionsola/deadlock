package server.character;

import java.awt.Image;
import java.io.FileInputStream;

import javax.imageio.ImageIO;

import server.ability.ChargedAbility;
import server.ability.HearingAmplifier;
import server.ability.Optics;
import server.passive.Assault;
import server.passive.Backstab;
import server.passive.Mark;
import server.passive.Overwatch;
import server.passive.Shield;
import server.weapon.WeaponFactory;

/**
 * Typical Factory class used to create a new ControlledCharacter.
 * 
 * @author Anh D Pham
 * @author Connor Cartwright
 *
 */
public class CharacterFactory {
	
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
	private static String[] names = { "Tank", "Scout", "Sniper", "Agent", "Grenadier" };

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

			images[2][0] = SNIPER_RED;
			images[2][1] = SNIPER_GREEN;
			images[1][0] = SCOUT_RED;
			images[1][1] = SCOUT_GREEN;
			images[0][0] = TANK_RED;
			images[0][1] = TANK_GREEN;
			images[4][0] = COMMANDO_RED;
			images[4][1] = COMMANDO_GREEN;
			images[3][0] = SPECOPS_RED;
			images[3][1] = SPECOPS_GREEN;

		} catch (Exception e) {
			System.out.println("Error while loading server.character images");
			System.out.println(e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
	}

	/**
	 * Creates a new server.character.
	 * 
	 * @param id
	 *            the id of the server.character, used to keep track of them
	 * @param team
	 *            the team the server.character will be on.
	 * @param type
	 *            the type of the server.character, e.g. scout
	 * @return the new server.character
	 */
	public static ControlledCharacter newCharacter(int id, int team, int type) {
		switch (type) {
			/*
			case Grenadier.typeID:
				return new Grenadier(id, team);
			case Sniper.typeID:
				return new Sniper(id, team);
			case Tank.typeID:
				return new Tank(id, team);
			case Spy.typeID:
				return new Spy(id, team);
				*/
			case 0:
				ControlledCharacter shield =  new ControlledCharacter(id, team, ClassStats.classStats.get(type),
						WeaponFactory.createGun(3),null);
				shield.setAbilty(new ChargedAbility.ThrowFlashGrenade(shield));
				shield.setPassive(new Shield(shield));
				return shield;
			
			case 1:
				ControlledCharacter scout =  new ControlledCharacter(id, team, ClassStats.classStats.get(type),
						WeaponFactory.createGun(2),null);
				scout.setAbilty(new Optics(scout));
				scout.setPassive(new Mark(scout));
				return scout;
			case 2:
				ControlledCharacter sniper =  new ControlledCharacter(id, team, ClassStats.classStats.get(type),
						WeaponFactory.createGun(1),null);
				sniper.setAbilty(new Optics(sniper));
				sniper.setPassive(new Overwatch(sniper));
				return sniper;
			case 3:
				ControlledCharacter agent =  new ControlledCharacter(id, team, ClassStats.classStats.get(type),
						WeaponFactory.createGun(4),null);
				agent.setAbilty(new HearingAmplifier(agent));
				agent.setPassive(new Backstab(agent));
				return agent;
			case 4:
				ControlledCharacter gren =  new ControlledCharacter(id, team, ClassStats.classStats.get(type),
						WeaponFactory.createGun(0),null);
				gren.setAbilty(new ChargedAbility.ThrowFragGrenade(gren));
				gren.setPassive(new Assault(gren));
				return gren;
			default:
				System.out.println("Error: Wrong type id");
				System.exit(-1);
				return null;
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

	/**
	 * Returns the name of the server.character type.
	 * 
	 * @param type
	 *            the element in the server.character names type array to get.
	 * @return the name of the server.character type.
	 */
	public static String getName(int type) {
		return names[type];
	}
	
}
