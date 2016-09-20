package character;

import java.awt.geom.Area;
import java.util.LinkedList;
import java.util.List;

import network.GameEvent.FootStepEvent;
import core.Arena;
import core.LineOfSight;
import core.Tile;
import core.World;
import game.Game;
import status.StatusEffect;

/**
 * This class will define the base behaviour of every type of Character.
 * 
 * @author Connor Cartwright
 * @author Anh D Pham
 */
public class AbstractCharacter {
	public static final double BASE_SPEED	= 0.0035;
	public static final double BASE_HP		= 100;
	public static final double BASE_RADIUS	= 0.9;
	public static final double BASE_FOVRANGE= 20;
	public static final double BASE_FOVANGLE= Math.toRadians(90);
	public static final double BASE_NOISE = 30;
	
	public static final double BASE_HEARING_THRES = 10;
	
	private double x = 0; // x position of the character
	private double y = 0; // y position of the character
	private double dx = 0; // delta x, left = -speed, right = speed
	private double dy = 0; // delta y, upwards = - speed, down = speed
	
	private double fovRangeF;
	private double fovAngleF;
	private double speedF;
	private double noiseF; // the level of noise that the character makes, standard = 1
	private double sizeF;
	private double hearF;
	public final ClassStats cs;
	
	private double direction; // direction the character is facing in radiants
	private double noise = 0; // character current noise
	
	private final double maxHP;
	private double healthPoints; // the number of health points the character class has
	private LineOfSight los = new LineOfSight();

	private List<StatusEffect> statusEffects = new LinkedList<StatusEffect>();
	/**
	 * Creates a new abstract character.
	 * 
	 * @param healthPoints
	 *            the health points of the characters
	 * @param speed
	 *            the speed of the character
	 * @param radius
	 *            the radius of the character
	 * @param noise
	 *            the noise the character makes
	 * @param viewRange
	 *            the range of the characters line of sight
	 * @param viewAngle
	 *            the angle of the characters line of sight
	 */
	public AbstractCharacter(ClassStats cs) {
		this.maxHP = cs.getMaxHP()*BASE_HP;
		this.healthPoints = maxHP;
		this.speedF = cs.getSpeedF();
		this.sizeF = cs.getSize();
		this.noiseF = cs.getNoise();
		this.hearF = 1;
		this.fovRangeF = 1;
		this.fovAngleF = 1;
		this.cs = cs;
	}

	public void update(World world) {
		updateStatusEffects();
		updateCoordinate(world);
		updateNoise(world);
	}

	protected void updateStatusEffects() {
		List<StatusEffect> remove = new LinkedList<StatusEffect>();
		for (StatusEffect se:statusEffects) {
			if (!se.isFinished()) {
				se.update();
			} else {
				remove.add(se);
			}
		}
		statusEffects.removeAll(remove);
	}
	
	/**
	 * Updates the coordinates of the character in a world.
	 * 
	 * @param world
	 *            the world to update the coordinates in.
	 */
	protected void updateCoordinate(World world) {
		if (dx == 0 && dy == 0)
			return;
		Arena arena = world.getArena();

		double newX = x + dx*Game.MS_PER_UPDATE;
		double newY = y + dy*Game.MS_PER_UPDATE;
		// boundBox.setLocation((int)(newX-radius),(int)(newY-radius));
		// check each corner of box if walkable
		
		int tileX1 = (int) ((newX - getRadius()) / Tile.tileSize);
		int tileY1 = (int) ((getY() - getRadius()) / Tile.tileSize);
		int tileX2 = (int) ((newX + getRadius()) / Tile.tileSize);
		int tileY2 = (int) ((getY() + getRadius()) / Tile.tileSize);
		boolean blocked = false;;
		for (int x=tileX1;x<=tileX2;x++) {
			for (int y=tileY1;y<=tileY2;y++) {
				if (!arena.get(x, y).isWalkable()) {
					blocked = true;
					break;
				}
			}
		}
		if (!blocked) {
			x = newX;
		} else {
			dx = 0;
		}

		int tileX3 = (int) ((getX() - getRadius()) / Tile.tileSize);
		int tileY3 = (int) ((newY - getRadius()) / Tile.tileSize);
		int tileX4 = (int) ((getX() + getRadius()) / Tile.tileSize);
		int tileY4 = (int) ((newY + getRadius()) / Tile.tileSize);
		
		blocked = false;
		for (int x=tileX3;x<=tileX4;x++) {
			for (int y=tileY3;y<=tileY4;y++) {
				if (!arena.get(x, y).isWalkable()) {
					blocked = true;
					break;
				}
			}
		}

		if (!blocked) {
			y = newY;
		} else {
			dy = 0;
		}
	}

	/**
	 * Updates the noise in the world based on the character.
	 * 
	 * @param world
	 *            the world in which to update the noise.
	 */
	protected void updateNoise(World world) {
		double inc = -0.25;
		if (dx != 0 || dy != 0)
			inc = Math.sqrt(dx * dx + dy * dy) / getSpeed();

		noise = Math.max(0, getNoise() + inc);
		if (noise > 30) {
			world.getEventListener().onEventReceived(new FootStepEvent(getX(), getY(), (float) (noise * inc)));
			noise -= 30;
		}
	}

	private double getNoise() {
		return noise;
	}

	/**
	 * Returns the line of sight of the character.
	 * 
	 * @param a
	 *            the arena in which to get the los.
	 * @return the line of sight area.
	 */
	public Area getLoS(Arena a) {
		return los.genLOSAreaMeter(getX(), getY(), getFovRange(), getFovAngle(), direction, a);
	}

	/**
	 * Returns the direction of the character.
	 * 
	 * @return the direction of the character
	 */
	public double getDirection() {
		return direction;
	}

	/**
	 * Sets the direction of the character
	 * 
	 * @param dir
	 *            the direction of the character
	 */
	public void setDirection(double dir) {
		direction = dir;
	}

	/**
	 * Sets the view range of the character
	 * 
	 * @param viewRange
	 *            the view range of the character
	 */
	public void setViewRangeF(double fovRangeF) {
		this.fovRangeF = fovRangeF;
		
	}

	/**
	 * Sets the view angle of the character
	 * 
	 * @param viewAngle
	 *            the view angle of the character
	 */
	public void setViewAngleF(double fovAngleF) {
		this.fovAngleF = fovAngleF;
	}
	
	/**
	 * Gets the view range of the character
	 * 
	 * @return the view range of the character
	 */
	public double getFovRangeF() {
		return fovRangeF;
	}

	/**
	 * Gets the view angle of the character
	 * 
	 * @return the view angle of the character
	 */
	public double getFovAngleF() {
		return fovAngleF;
	}
	
	/**
	 * Gets the view range of the character
	 * 
	 * @return the view range of the character
	 */
	public double getFovRange() {
		return BASE_FOVRANGE*fovRangeF;
	}

	/**
	 * Gets the view angle of the character
	 * 
	 * @return the view angle of the character
	 */
	public double getFovAngle() {
		return BASE_FOVANGLE*fovAngleF;
	}

	/**
	 * Gets the speed of the character
	 * 
	 * @return the speed of the character
	 */
	public double getSpeed() {
		return BASE_SPEED*speedF;
	}

	/**
	 * Sets the speed of the character
	 * 
	 * @param speed
	 *            the speed of the character
	 */
	public void setSpeedF(double speedF) {
		this.speedF = speedF;
	}

	public double getSpeedF() {
		return speedF;
	}
	
	/**
	 * Gets the radius of the character
	 * 
	 * @return the radius of the character
	 */
	public double getRadius() {
		return BASE_RADIUS*sizeF;
	}

	public double getSizeF() {
		return sizeF;
	}
	
	public void setSizeF(double sizeF) {
		this.sizeF = sizeF;
	}
	
	public double getNoiseF() {
		return noiseF;
	}
	
	public void setNoiseF(double noiseF) {
		this.noiseF = noiseF;
	}
	

	/**
	 * Returns the x coord of the character
	 * 
	 * @return the x coord of the character
	 */
	public double getX() {
		return x;
	}

	/**
	 * Returns the y coord of the character
	 * 
	 * @return the y coord of the character
	 */
	public double getY() {
		return y;
	}

	/**
	 * Sets the x coord of the character
	 * 
	 * @param x
	 *            the x coord of the character
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * Sets the y coord of the character
	 * 
	 * @param y
	 *            the y coord of the character
	 */
	public void setY(double y) {
		this.y = y;
	}

	/**
	 * Gets the delta x of the character
	 * 
	 * @return the delta x of the character
	 */
	public double getDx() {
		return dx;
	}

	/**
	 * Gets the delta y of the character
	 * 
	 * @return the delta y of the character
	 */
	public double getDy() {
		return dy;
	}

	/**
	 * Sets the delta x of the character
	 * 
	 * @param dx
	 *            the delta x of the character
	 */
	protected void setDx(double dx) {
		this.dx = dx;
	}

	/**
	 * Sets the delta y of the character
	 * 
	 * @param dy
	 *            the delta y of the character
	 */
	protected void setDy(double dy) {
		this.dy = dy;
	}

	/**
	 * Returns the health points of the character
	 * 
	 * @return the health points of the character
	 */
	public double getHealthPoints() {
		return healthPoints;
	}

	/**
	 * Sets the health points of the character
	 * 
	 * @param healthPoints
	 *            the health points of the character
	 */
	public void setHealthPoints(double healthPoints) {
		this.healthPoints = healthPoints;
	}

	/**
	 * Reset the HP of the character
	 */
	public void resetStats() {
		healthPoints = maxHP;
	}

	public double getHearF() {
		return hearF;
	}

	public void setHearF(double hearF) {
		this.hearF = hearF;
	}
	
	public double getHearThres() {
		return hearF*BASE_HEARING_THRES;
	}
}
