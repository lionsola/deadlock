package character;

import network.FullCharacterData;
import network.PartialCharacterData;
import network.GameDataPackets.InputPacket;
import network.GameDataPackets.WorldStatePacket;
import passive.Passive;
import weapon.Weapon;
import java.awt.geom.Point2D;

import ability.Ability;
import core.Geometry;
import core.Utils;
import core.World;
import game.Game;

/**
 * The <code>ControlledCharacter</code> class defines the behaviour that all types of Characters
 * must inherit. Here the Accessor and Mutator methods are located for ease of access elsewhere in
 * the code and to avoid code duplication. The ControlledCharacter class encompasses how ALL
 * Characters have to behave.
 * 
 * A <code>ControlledCharacter</code> also uses a specific set of <code>Weapons</code>.
 * 
 * @see Weapon
 * @author Team D1
 * @author Connor Cartwright
 *
 */
public class ControlledCharacter extends Character {
	public enum MovementMode {WALK,SNEAK,STOP}
	
	private static final double MOVEMENT_DISPERSION_FACTOR = 0.1;
	private static final double ROTATION_DISPERSION_FACTOR = 0.3;
	
	private static final double MAX_DISPERSION_ANGLE = 0.1;
	private static final double DISPERSION_DEC = 0.007;

	private double instaF = 1;
	
	
	public final int id; // the player's ID

	public final int team; // the team the player is on

	private int typeID; // the type of the character, e.g. Sniper

	private float cx = 0; // x position of the crosshairs
	private float cy = 0; // y position of the crosshairs
	private double charDispersion = 0;
	private MovementMode mode = MovementMode.STOP;

	// The initial fields below is the result of providing parameters by subclassing
	// (compared to using Type Object pattern) - there's no easy way to access,
	// say, Scout's max HP (although it is already defined there)
	// even from inside the ControlledCharacter class. With type object, we can type.getHP().
	// With subclassing, we can't getLowestLevelClass().MAXHP. So now we have two options:
	//
	// + (used below) to save every single initial stat in ControlledCharacter objects instead
	// (an already big class) although all scouts will have the same initial stats anyway,
	// i.e duplicated fields in all objects. The initial fields also grow along with
	// each field added later.
	//
	// + to have abstract accessors to initial fields in ControlledCharacter that
	// EVERY subclass has to implement, and then call character.getMaxHealth()
	// to achieve polymorphism (duplicated code in all the subclasses).
	//
	// Both solutions make the codebase look poorly written and maintaining & extending
	// devilishly hard, over the gain of a nice-looking character type hierarchy.
	//
	// This could be solved easily by either ditching the character inheritance hierarchy completely
	// and providing a data table (rows = types, columns = fields) to look up when creating
	// a new character with a numerical "typeID", hence to access default fields we just need
	// table[typeID][FIELD]; or storing a Type Object as a field inside ControlledCharacter,
	// hence to access default fields we just need type.getInitHP().
	//
	// Data table & Type Object, both solutions are widely used to provide persistent data
	// (things that don't change) in games.

	// private final double initialStamina;
	// private final int initialRadius;
	// private final double initialSpeed;
	// private final int initialViewRange;
	// private final int initialViewAngle;

	private Weapon primary; // the primary weapon of the character class
	private Ability ability;
	private Passive passive;
	
	private InputPacket input = new InputPacket();
	private WorldStatePacket perception = new WorldStatePacket();

	/**
	 * Creating a new controlled character
	 * 
	 * @param id
	 *            the id of the character
	 * @param typeID
	 *            the type id, i.e. character type of the character
	 * @param team
	 *            the team the character is on
	 * @param healthPoints
	 *            the characters health points
	 * @param stamina
	 *            the characters stamina value
	 * @param speed
	 *            the characters movement speed value
	 * @param noise
	 *            the characters noise value
	 * @param viewRange
	 *            the characters view range, i.e. line of sight length
	 * @param viewAngle
	 *            the characters view angle, i.e. line of sight width
	 * @param radius
	 *            the characters radius
	 * @param primary
	 *            the characters primary weapon
	 * @param secondary
	 *            the character secondary weapon
	 */
	protected ControlledCharacter(int id, int team, ClassStats cs, Weapon weapon, Ability ability) {
		super(cs);
		this.id = id;
		this.team = team;

		// this.initialStamina = stamina;
		// this.initialRadius = radius;
		// this.initialSpeed = speed;
		// this.initialViewAngle = viewAngle;
		// this.initialViewRange = viewRange;

		this.primary = weapon;
		this.ability = ability;
	}

	protected ControlledCharacter(int id, int team, ClassStats cs) {
		this(id,team,cs,null,null);
	}
	
	/**
	 * Update the character in the specified world
	 * 
	 * @param world
	 *            the world in which the character is in.
	 */
	@Override
	public void update(World world) {
		// translate directional input into movement vector
		processInput();
		
		// apply collision detection to correct the movement vector
		super.updateCollision(world);
		
		updateMode();
		
		
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
		addDispersion(instaF*MOVEMENT_DISPERSION_FACTOR*getCurrentSpeed()*Game.MS_PER_UPDATE);
		addDispersion(-DISPERSION_DEC*(0.5+charDispersion));
	}

	public double getCurrentSpeed() {
		return Math.sqrt(getDx()*getDx()+getDy()*getDy());
	}
	
	/**
	 * Update the cursor position on screen
	 * 
	 * @param cx2
	 *            cursor x position
	 * @param cy2
	 *            cursor y position
	 */
	public void updateCursor(float cx2, float cy2) {
		
	}

	public double getCrosshairSize() {
		double angle = charDispersion*MAX_DISPERSION_ANGLE+getPrimary().type.gunDispersion;
		return Math.tan(angle)*Point2D.distance(getX(),getY(),cx,cy);
	}
	
	public double disperseDirection() {
		return getDirection()+charDispersion*MAX_DISPERSION_ANGLE*Utils.random().nextGaussian()/2;
	}

	/**
	 * Returns the primary weapon of the character
	 * 
	 * @return the primary weapon of the character
	 */
	public Weapon getPrimary() {
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
		
		double speed = getSpeed();
		setDx(dx*speed);
		setDy(dy*speed);
	}
	
	protected void updateMode() {
		if (getInput().sneaking) {
			mode = MovementMode.SNEAK;
		} else if (getDx()!=0 || getDy()!=0) {
			mode = MovementMode.WALK;
		} else {
			mode = MovementMode.STOP;
		}
	}
	
	public void setInput(network.GameDataPackets.InputPacket input) {
		this.input = input;
	}
	
	/**
	 * Return the full character data.
	 * 
	 * @return the full character data.
	 */
	public FullCharacterData generate() {
		FullCharacterData fc = new FullCharacterData();
		fc.healthPoints = (float) getHealthPoints();
		fc.radius = (float) getRadius();
		if (primary.isReady())
			fc.reloadPercent = 1;
		else {
			float cooldownPercent = (float)primary.timeSinceFire() / primary.type.cooldown;
			float reloadPercent = (float)primary.timeSinceReload() / primary.type.reloadTime;
			fc.reloadPercent = Math.min(cooldownPercent, reloadPercent);
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

	public PartialCharacterData generatePartial() {
		PartialCharacterData data = new PartialCharacterData();
		data.id = (short) id;
		data.team = (byte) team;
		data.x = (float) getX();
		data.y = (float) getY();
		data.healthPoints = (float) getHealthPoints();
		data.radius = (float) getRadius();
		data.direction = (float) getDirection();
		if (getArmor()!=null) {
			data.armorAngle = (float)getArmor().getAngle();
			data.armorStart = (float)getArmor().getStart();
		}
		return data;
	}
	
	

	public InputPacket getInput() {
		return input;
	}

	public void addDispersion(double dispersion) {
		charDispersion = Math.max(0,Math.min(1,charDispersion+dispersion));
	}
	
	protected void setWeapon(Weapon w) {
		primary = w;
	}
	
	protected void setAbilty(Ability a) {
		ability = a;
	}
	
	protected void setPassive(Passive p) {
		passive = p;
	}
	
	public void setPerception(WorldStatePacket wsp) {
		this.perception = wsp;
	}
	
	public WorldStatePacket getPerception() {
		return perception;
	}

	public double getInstaF() {
		return instaF;
	}
	
	public void addInstaMod(double instaMod) {
		this.instaF += instaMod;
	}
}
