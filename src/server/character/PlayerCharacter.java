package server.character;

import server.ability.Ability;
import server.passive.Passive;
import server.weapon.Weapon;
import server.world.Geometry;
import server.world.Sound;
import server.world.Utils;
import server.world.World;
import shared.network.FullCharacterData;
import shared.network.GameDataPackets.InputPacket;

import java.awt.geom.Point2D;

import client.gui.GameWindow;

/**
 * The <code>ControlledCharacter</code> class defines the behaviour that all types of Characters
 * must inherit. Here the Accessor and Mutator methods are located for ease of access elsewhere in
 * the code and to avoid code duplication. The ControlledCharacter class encompasses how ALL
 * Characters have to behave.
 * 
 * A <code>ControlledCharacter</code> also uses a specific set of <code>Weapons</code>.
 */
public class PlayerCharacter extends Character {
	public enum MovementMode {WALK,SNEAK,STOP}
	
	private static final double MOVEMENT_DISPERSION_FACTOR = 0.1;
	private static final double ROTATION_DISPERSION_FACTOR = 0.3;
	
	private static final double MAX_DISPERSION_ANGLE = 0.1;
	private static final double DISPERSION_DEC = 0.006;

	private double instaF = 1;
	
	private int typeID; // the type of the server.character, e.g. Sniper

	private float cx = 0; // x position of the crosshairs
	private float cy = 0; // y position of the crosshairs
	private double charDispersion = 0;

	private Weapon primary; // the primary server.weapon of the server.character class
	private Ability ability;
	private Passive passive;
	
	private InputPacket input = new InputPacket();
	

	/**
	 * Creating a new controlled server.character
	 * 
	 * @param id
	 *            the id of the server.character
	 * @param typeID
	 *            the type id, i.e. server.character type of the server.character
	 * @param team
	 *            the team the server.character is on
	 * @param healthPoints
	 *            the characters health points
	 * @param stamina
	 *            the characters stamina value
	 * @param pSpeed
	 *            the characters movement speed value
	 * @param volume
	 *            the characters volume value
	 * @param viewRange
	 *            the characters view range, i.e. line of sight length
	 * @param viewAngle
	 *            the characters view angle, i.e. line of sight width
	 * @param radius
	 *            the characters radius
	 * @param primary
	 *            the characters primary server.weapon
	 * @param secondary
	 *            the server.character secondary server.weapon
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
		// translate directional input into movement vector
		processInput();
		
		// apply collision detection to correct the movement vector
		super.updateCollision(world);
		super.updatePerception(world);
		
		if (passive!=null)
			passive.update(world);
		
		if (ability!=null)
			ability.update(world);
		
		if (primary!=null)
			primary.update(world, this);
		
		super.updateStatusEffects();
		super.updatePosition();
		super.updateNoise(world);
		updateCrosshair();
	}
	
	private void updateCrosshair() {
		addDispersion(instaF*MOVEMENT_DISPERSION_FACTOR*getCurrentSpeed()*GameWindow.MS_PER_UPDATE);
		addDispersion(-DISPERSION_DEC*(0.5+charDispersion));
	}

	public double getCrosshairSize() {
		double angle = charDispersion*MAX_DISPERSION_ANGLE+getWeapon().type.gunDispersion;
		return Math.tan(angle)*Point2D.distance(getX(),getY(),cx,cy);
	}
	
	public double disperseDirection() {
		return getDirection()+charDispersion*MAX_DISPERSION_ANGLE*Utils.random().nextGaussian()/2;
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
		cx = getInput().cx;
		cy = getInput().cy;
		// update direction
		double newDirection = Math.atan2(getY() - cy, cx - getX());
		double dDirection = Math.abs(Geometry.wrapAngle(newDirection - getDirection()));
		addDispersion(instaF*ROTATION_DISPERSION_FACTOR*dDirection);
		setDirection(newDirection);
		
		if (getInput().down && getInput().top) {
			getInput().down = false;
			getInput().top = false;
		}
		if (getInput().left && getInput().right) {
			getInput().left = false;
			getInput().right = false;
		}
		
		double dx = 0, dy = 0;
		
		if (getInput().down)
			dy = 1;
		else if (getInput().top)
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
		double speed = getSpeed()*(1+ratio*0.2);
		setDx(dx*speed);
		setDy(dy*speed);
	}
	
	public void setInput(shared.network.GameDataPackets.InputPacket input) {
		this.input = input;
	}
	
	/**
	 * Return the full server.character data.
	 * 
	 * @return the full server.character data.
	 */
	public FullCharacterData generate() {
		FullCharacterData fc = new FullCharacterData();
		fc.healthPoints = (float) getHealthPoints();
		fc.radius = (float) getRadius();
		if (primary.isReady())
			fc.reloadPercent = 1;
		else {
			fc.reloadPercent = (float)primary.getCooldownPercent();
		}
		fc.direction = (float) getDirection();
		fc.viewAngle = (float) getFovAngle();
		fc.viewRange = (float) getFovRange();
		fc.x = (float) getX();
		fc.y = (float) getY();
		fc.crosshairSize = (float) getCrosshairSize();
		fc.hearRange = (float) (80.0/(Sound.DISTANCE_VOLUME_DROP_RATE*(1-getHearF())));
		if (getArmor()!=null) {
			fc.armorAngle = (float) getArmor().getAngle();
			fc.armorStart = (float) getArmor().getStart();
		}
		return fc;
	}

	

	public InputPacket getInput() {
		return input;
	}

	public void addDispersion(double dispersion) {
		charDispersion = Math.max(0,Math.min(1,charDispersion+dispersion));
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
	public double getNoiseF() {
		return super.getNoiseF()*(getInput().sneaking?0.5:1);
	}
}
