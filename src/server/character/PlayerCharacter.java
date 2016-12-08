package server.character;

import server.ability.Ability;
import server.ability.ChangeForm;
import server.ability.ChargedAbility;
import server.ability.HearingAmplifier;
import server.ability.Optics;
import server.passive.Assault;
import server.passive.Backstab;
import server.passive.BloodSucker;
import server.passive.Mark;
import server.passive.Overwatch;
import server.passive.Passive;
import server.passive.Shield;
import server.weapon.Weapon;
import server.weapon.WeaponFactory;
import server.world.Geometry;
import server.world.World;
import shared.network.FullCharacterData;
import shared.network.GameDataPackets.InputPacket;
import shared.network.event.GameEvent.*;
import java.awt.geom.Point2D;

import client.gui.GameWindow;

/**
 * The <code>ControlledCharacter</code> class defines the behaviour that all types of Characters
 * must inherit.
 * 
 * A <code>ControlledCharacter</code> also uses a specific set of <code>Weapons</code>.
 */
public class PlayerCharacter extends Character {
	
	public static final double MOVEMENT_DISPERSION_FACTOR = 0.007;
	public static final double ROTATION_DISPERSION_FACTOR = 0.05;
	
	public static final double MAX_DISPERSION_ANGLE = 0.1;
	public static final double DISPERSION_DEC = 0.006;
	
	private double instaF = 1;
	
	private int typeID; // the type of the server.character, e.g. Sniper

	private double targetDirection = 0;

	private Weapon primary; // the primary server.weapon of the server.character class
	private Ability ability;
	private Passive passive;
	
	private InputPacket input = new InputPacket();

	/**
	 * Creating a new controlled server.character
	 * 
	 * @param id
	 *            the id of the server.character
	 * @param team
	 *            the team the server.character is on
	 */
	protected PlayerCharacter(int id, int team, ClassStats cs, Weapon weapon, Ability ability, Passive passive) {
		super(cs,id,team);

		this.primary = weapon;
		this.ability = ability;
		this.passive = passive;
	}

	protected PlayerCharacter(int id, int team, ClassStats cs) {
		super(cs,id,team);
	}
	
	/**
	 * Update the server.character in the specified world
	 * 
	 * @param world
	 *            the world in which the server.character is in.
	 */
	@Override
	public void update(World world) {
		synchronized (input) {
			if (isDead()) {
				input.up = false;
				input.down = false;
				input.left = false;
				input.right = false;
			}
			
			// translate directional input into movement vector
			processInput();
			// apply collision detection to correct the movement vector
			super.updateCollision(world);
			
			super.updatePerception(world);
			
			// send ping
			if (passive!=null)
				passive.update(world);
			
			if (ability.isEnabled() && ability!=null)
				ability.update(world);
				
			if (!isDead()) {
				if (primary.isEnabled() && primary!=null)
					primary.update(world, this);
				
				
				super.updateStatusEffects();
				
				super.updatePosition(world);
				super.updateNoise(world);
			}
			
			updateCrosshair();
		}
	}
	
	private void updateCrosshair() {
		// add the direction swaying when walking / running
		//addDispersion(instaF*MOVEMENT_DISPERSION_FACTOR*getCurrentSpeed()*GameWindow.MS_PER_UPDATE);
		direction += sway*instaF*MOVEMENT_DISPERSION_FACTOR*getCurrentSpeed()*GameWindow.MS_PER_UPDATE/2;
		
		// move character direction toward the target direction slowly 
		//addDispersion(-DISPERSION_DEC*(0.5+charDispersion));
		double delta = Geometry.wrapAngle(targetDirection - direction);
		double stabFactor = (0.5+Math.abs(delta)/MAX_DISPERSION_ANGLE);
		direction += Math.copySign(0.01*MAX_DISPERSION_ANGLE*stabFactor,delta);
		
		// limit the difference
		if (Math.abs(delta)>MAX_DISPERSION_ANGLE) {
			direction = targetDirection + Math.copySign(MAX_DISPERSION_ANGLE,-delta);
		}
	}

	public double getCrosshairSize() {
		//double angle = Math.abs(gunDirection)+getWeapon().type.gunDispersion;
		double angle = getWeapon().type.gunDispersion;
		return Math.tan(angle)*Point2D.distance(getX(),getY(),getInput().cx,getInput().cy);
	}

	/**
	 * Returns the primary server.weapon of the server.character
	 * 
	 * @return the primary server.weapon of the server.character
	 */
	public Weapon getWeapon() {
		return primary;
	}

	/**
	 * Returns the characters type ID, i.e. the characters class
	 * 
	 * @return the characters type ID
	 */
	public int getTypeID() {
		return typeID;
	}

	/**
	 * Process the input
	 * 
	 * @param input
	 *            the input to be processed
	 */
	private void processInput() {
		// update direction
		if (!isDead()) {
			double newDirection = Math.atan2(getY() - getInput().cy, getInput().cx - getX());
			double dDirection = Geometry.wrapAngle(newDirection - targetDirection);
			direction += dDirection*(1-ROTATION_DISPERSION_FACTOR);
			targetDirection = newDirection;
		}
		if (getInput().down && getInput().up) {
			getInput().down = false;
			getInput().up = false;
		}
		if (getInput().left && getInput().right) {
			getInput().left = false;
			getInput().right = false;
		}
		
		double dx = 0, dy = 0;
		
		if (getInput().down)
			dy = 1;
		else if (getInput().up)
			dy = -1;
		if (getInput().right)
			dx = 1;
		else if (getInput().left)
			dx = -1;
		
		if (dx!=0 && dy!=0) {
			double SQRT2INV = 1/Math.sqrt(2);
			dx *= SQRT2INV;
			dy *= SQRT2INV;
		}
		
		if (getInput().sneaking) {
			dx *= 0.5;
			dy *= 0.5;
		}
		
		double diffAngle = Math.abs(Geometry.wrapAngle(getMovingDirection()-getDirection()));
		double ratio = (Math.PI/2 - diffAngle)/(Math.PI/2);
		double speed = getSpeed()*(1+ratio*0.1);
		setDx(dx*speed);
		setDy(dy*speed);
	}
	
	public void setInput(InputPacket input) {
		this.input = input;
	}
	
	/**
	 * Return the full server.character data.
	 * 
	 * @return the full server.character data.
	 */
	public FullCharacterData generate() {
		FullCharacterData fc = new FullCharacterData();
		fc.id = (short) id;
		fc.healthPoints = (float) getHealthPoints();
		fc.radius = (float) getRadius();
		if (primary.isReady())
			fc.weaponCooldown = 1;
		else {
			fc.weaponCooldown = (float)primary.getCooldownPercent();
		}
		if (ability.isReady())
			fc.abilityCooldown = 1;
		else {
			fc.abilityCooldown = (float)ability.getCooldownPercent();
		}
		fc.direction = (float) getDirection();
		fc.viewAngle = (float) getFovAngle();
		fc.viewRange = (float) getFovRange();
		fc.x = (float) getX();
		fc.y = (float) getY();
		fc.crosshairSize = (float) getCrosshairSize();
		if (getArmor()!=null) {
			fc.armorAngle = (float) getArmor().getAngle();
			fc.armorStart = (float) getArmor().getStart();
		}
		return fc;
	}

	public InputPacket getInput() {
		return input;
	}
	
	public void setWeapon(Weapon w) {
		primary = w;
	}
	
	public void setAbilty(Ability a) {
		ability = a;
	}
	
	public void setPassive(Passive p) {
		passive = p;
	}
	
	public double getInstaF() {
		return instaF;
	}
	
	public void addInstaMod(double instaMod) {
		this.instaF += instaMod;
	}
	
	@Override
	public void resetStats() {
		super.resetStats();
		primary.reset();
		ability.reset();
	}
	
	@Override
	public double getNoiseF() {
		return super.getNoiseF()*(getInput().sneaking?0.5:1);
	}
	
	@Override
	public void onHit(World w, double damage, int sourceId) {
		if (isDead())
			return;
		super.onHit(w, damage, sourceId);
		if (getHealthPoints()<=0) {
			w.getEventListener().onEventReceived(new PlayerDieEvent(sourceId, id));
		}
	}

	public Point2D getHead() {
		Point2D head = Geometry.PolarToCartesian(getRadius()*0.4, getDirection());
		head.setLocation(head.getX()+getX(),head.getY()+getY());
		return head;
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
	public static PlayerCharacter newCharacter(int id, int team, int type) {
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
			case 3:
				PlayerCharacter shield =  new PlayerCharacter(id, team, ClassStats.classStats.get(type));
				shield.setWeapon(WeaponFactory.createGun(3, shield));
				shield.setAbilty(new ChargedAbility.ThrowFlashGrenade(shield));
				shield.setPassive(new Shield(shield));
				return shield;
			case 2:
				PlayerCharacter scout =  new PlayerCharacter(id, team, ClassStats.classStats.get(type));
				scout.setWeapon(WeaponFactory.createGun(2,scout));
				scout.setAbilty(new Optics.Binocular(scout));
				scout.setPassive(new Mark(scout));
				return scout;
			case 1:
				PlayerCharacter sniper =  new PlayerCharacter(id, team, ClassStats.classStats.get(type));
				sniper.setWeapon(WeaponFactory.createGun(1,sniper));
				sniper.setAbilty(new Optics.Scope(sniper));
				sniper.setPassive(new Overwatch(sniper));
				return sniper;
			case 4:
				PlayerCharacter agent =  new PlayerCharacter(id, team, ClassStats.classStats.get(type));
				agent.setWeapon(WeaponFactory.createGun(4,agent));
				agent.setAbilty(new HearingAmplifier(agent));
				agent.setPassive(new Backstab(agent));
				return agent;
			case 0:
				PlayerCharacter gren =  new PlayerCharacter(id, team, ClassStats.classStats.get(type));
				gren.setWeapon(WeaponFactory.createGun(0,gren));
				gren.setAbilty(new ChargedAbility.ThrowFragGrenade(gren));
				gren.setPassive(new Assault(gren));
				return gren;
			case 5:
				PlayerCharacter vam =  new PlayerCharacter(id, team, ClassStats.classStats.get(type));
				vam.setWeapon(WeaponFactory.createGun(ChangeForm.CF_HUMAN_WEAPON,vam));
				vam.setAbilty(new ChangeForm(vam));
				vam.setPassive(new BloodSucker(vam));
				return vam;
			default:
				System.out.println("Error: Wrong type id");
				System.exit(-1);
				return null;
		}
	}

	public double getTargetDirection() {
		return targetDirection;
	}
}
