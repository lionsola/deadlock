package server.character;

import java.awt.geom.Area;
import java.util.LinkedList;
import java.util.List;

import client.gui.GameWindow;
import server.status.StatusEffect;
import server.world.Arena;
import server.world.LineOfSight;
import server.world.Projectile;
import server.world.Sound;
import server.world.Tile;
import server.world.World;

/**
 * This class will define the base behaviour of every type of Character.
 * 
 * @author Connor Cartwright
 * @author Anh D Pham
 */
public class Character {
	public static final double BASE_SPEED	= 0.0025;
	public static final double BASE_HP		= 100;
	public static final double BASE_RADIUS	= 0.55;
	public static final double BASE_FOVRANGE= 16;
	public static final double BASE_FOVANGLE= Math.toRadians(100);
	public static final double BASE_NOISE = 30;
	
	public static final double BASE_HEARING_THRES = 0;
	
	private double x = 0; // x position of the server.character
	private double y = 0; // y position of the server.character
	private double dx = 0; // delta x, left = -speed, right = speed
	private double dy = 0; // delta y, upwards = - speed, down = speed
	
	private double fovRangeF;
	private double fovAngleF;
	private double speedF;
	private double noiseF; // the level of volume that the server.character makes, standard = 1
	private double sizeF;
	private double hearF; // the 
	public final ClassStats cs;
	
	private double direction; // direction the server.character is facing in radiants
	private double noise = 0; // server.character current volume
	
	private final double maxHP;
	private double healthPoints; // the number of health points the server.character class has
	private LineOfSight los = new LineOfSight();
	private Armor armor;
	private List<StatusEffect> statusEffects = new LinkedList<StatusEffect>();
	//private List<Shiel>
	/**
	 * Creates a new abstract server.character.
	 * 
	 * @param healthPoints
	 *            the health points of the characters
	 * @param speed
	 *            the speed of the server.character
	 * @param radius
	 *            the radius of the server.character
	 * @param volume
	 *            the volume the server.character makes
	 * @param viewRange
	 *            the range of the characters line of sight
	 * @param viewAngle
	 *            the angle of the characters line of sight
	 */
	public Character(ClassStats cs) {
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

	/*
	 * Main update method called every frame. If overriding this, please
	 * make sure to call all other update methods (collision, server.status effects, volume, position) 
	 */
	public void update(World world) {
		updateCollision(world);
		updateStatusEffects();
		updateNoise(world);
		updatePosition();
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
	 * Updates the coordinates of the server.character in a world.
	 * 
	 * @param world
	 *            the world to update the coordinates in.
	 */
	protected void updateCollision(World world) {
		if (dx == 0 && dy == 0)
			return;
		Arena arena = world.getArena();

		double newX = x + dx*GameWindow.MS_PER_UPDATE;
		double newY = y + dy*GameWindow.MS_PER_UPDATE;
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
		if (blocked) {
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

		if (blocked) {
			dy = 0;
		}
	}

	protected void updatePosition() {
		x += dx*GameWindow.MS_PER_UPDATE;
		y += dy*GameWindow.MS_PER_UPDATE;
	}
	
	/**
	 * Updates the volume in the world based on the server.character.
	 * 
	 * @param world
	 *            the world in which to update the volume.
	 */
	protected void updateNoise(World world) {
		double inc = 0.1;
		if (dx != 0 || dy != 0)
			inc = getCurrentSpeed() / getSpeed();

		noise = Math.max(0, noise + inc);
		double noiseThres = getNoiseF()*Character.BASE_NOISE;
		if (noise > noiseThres) {
			world.addSound(Sound.FOOTSTEP.id,noiseThres*inc,getX(),getY());
			noise -= noiseThres;
		}
	}

	/**
	 * Returns the line of sight of the server.character.
	 * 
	 * @param a
	 *            the arena in which to get the los.
	 * @return the line of sight area.
	 */
	public Area getLoS(Arena a) {
		return los.genLOSAreaMeter(getX(), getY(), getFovRange(), getFovAngle(), getDirection(), a);
	}

	/**
	 * Returns the direction of the server.character.
	 * 
	 * @return the direction of the server.character
	 */
	public double getDirection() {
		return direction;
	}

	/**
	 * Sets the direction of the server.character
	 * 
	 * @param dir
	 *            the direction of the server.character
	 */
	public void setDirection(double dir) {
		direction = dir;
	}

	public void addFovRangeMod(double fovRangeMod) {
		this.fovRangeF += fovRangeMod; 
	}
	
	public void addFovAngleMod(double fovAngleMod) {
		this.fovAngleF += fovAngleMod; 
	}
	
	public void addSpeedMod(double speedMod) {
		this.speedF += speedMod;
	}
	
	public void addNoiseMod(double noiseMod) {
		this.noiseF += noiseMod;
	}
	
	public void addSizeMod(double sizeMod) {
		this.sizeF += sizeMod;
	}
	
	
	/**
	 * Gets the view range of the server.character
	 * 
	 * @return the view range of the server.character
	 */
	public double getFovRangeF() {
		return fovRangeF;
	}

	/**
	 * Gets the view angle of the server.character
	 * 
	 * @return the view angle of the server.character
	 */
	public double getFovAngleF() {
		return fovAngleF;
	}
	
	/**
	 * Gets the view range of the server.character
	 * 
	 * @return the view range of the server.character
	 */
	public double getFovRange() {
		return BASE_FOVRANGE*fovRangeF;
	}

	/**
	 * Gets the view angle of the server.character
	 * 
	 * @return the view angle of the server.character
	 */
	public double getFovAngle() {
		return BASE_FOVANGLE*fovAngleF;
	}

	/**
	 * Gets the speed of the server.character
	 * 
	 * @return the speed of the server.character
	 */
	public double getSpeed() {
		return BASE_SPEED*speedF;
	}

	public double getSpeedF() {
		return speedF;
	}
	
	public double getCurrentSpeed() {
		return Math.sqrt(getDx()*getDx()+getDy()*getDy());
	}
	
	/**
	 * Gets the radius of the server.character
	 * 
	 * @return the radius of the server.character
	 */
	public double getRadius() {
		return BASE_RADIUS*sizeF;
	}

	public double getSizeF() {
		return sizeF;
	}
	
	public double getNoiseF() {
		return noiseF;
	}
	

	/**
	 * Returns the x coord of the server.character
	 * 
	 * @return the x coord of the server.character
	 */
	public double getX() {
		return x;
	}

	/**
	 * Returns the y coord of the server.character
	 * 
	 * @return the y coord of the server.character
	 */
	public double getY() {
		return y;
	}

	/**
	 * Sets the x coord of the server.character
	 * 
	 * @param x
	 *            the x coord of the server.character
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * Sets the y coord of the server.character
	 * 
	 * @param y
	 *            the y coord of the server.character
	 */
	public void setY(double y) {
		this.y = y;
	}

	/**
	 * Gets the delta x of the server.character
	 * 
	 * @return the delta x of the server.character
	 */
	public double getDx() {
		return dx;
	}

	/**
	 * Gets the delta y of the server.character
	 * 
	 * @return the delta y of the server.character
	 */
	public double getDy() {
		return dy;
	}

	/**
	 * Sets the delta x of the server.character
	 * 
	 * @param dx
	 *            the delta x of the server.character
	 */
	protected void setDx(double dx) {
		this.dx = dx;
	}

	/**
	 * Sets the delta y of the server.character
	 * 
	 * @param dy
	 *            the delta y of the server.character
	 */
	protected void setDy(double dy) {
		this.dy = dy;
	}

	/**
	 * Returns the health points of the server.character
	 * 
	 * @return the health points of the server.character
	 */
	public double getHealthPoints() {
		return healthPoints;
	}

	/**
	 * Sets the health points of the server.character
	 * 
	 * @param healthPoints
	 *            the health points of the server.character
	 */
	public void setHealthPoints(double healthPoints) {
		this.healthPoints = healthPoints;
	}

	protected Armor getArmor() {
		return armor;
	}
	
	public void setArmor (Armor a) {
		armor = a;
	}
	
	public void applyArmor(Projectile p, double oldX, double oldY) {
		if (armor!=null)
			armor.applyArmor(p,oldX,oldY);
	}
	
	/**
	 * Reset the HP of the server.character
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
	
	public void addStatusEffect(StatusEffect effect) {
		statusEffects.add(effect);
	}
	
	public boolean isMoving() {
		return getDx()!=0 || getDy()!=0;
	}
	
	public double getMovingDirection() {
		return Math.atan2(-dy, dx);
	}
}
