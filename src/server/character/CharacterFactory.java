package server.character;

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
}
