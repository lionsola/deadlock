package server.character;

import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;

import client.gui.GameWindow;
import server.status.StatusEffect;
import server.world.Arena;
import server.world.Projectile;
import server.world.Terrain;
import server.world.Utils;
import server.world.Visibility;
import server.world.World;
import shared.network.CharData;
import shared.network.ProjectileData;
import shared.network.Vision;
import shared.network.GameDataPackets.WorldStatePacket;
import shared.network.GameEvent;
import shared.network.GameEvent.AnimationEvent;
import shared.network.GameEvent.SoundEvent;

/**
 * This class will define the base behaviour of every type of Character.
 */
public class Character {
	public static final double BASE_SPEED	= 0.003;
	public static final double BASE_HP		= 100;
	public static final double BASE_RADIUS	= 0.5;
	public static final double BASE_FOVRANGE= 20;
	public static final double BASE_FOVANGLE= Math.toRadians(120);
	
	public static final double BASE_HEARING_THRES = 0;
	
	public static final int FOOTSTEP_SOUND_ID = 30;
	public static final double FOOTSTEP_SOUND_VOLUME = 20;
	
	private double x = 0;
	private double y = 0;
	//private Vector2D pos;
	private double dx = 0;
	private double dy = 0;
	//private Vector2D vel;
	
	public final int id; // the player's ID
	
	private double fovRangeF;
	private double fovAngleF;
	private double speedF;
	private double noiseF; // the level of volume that the server.character makes, standard = 1
	private double sizeF;
	private double hearF; // the 
	public final ClassStats cs;
	public final int team;
	
	protected double direction; // direction the server.character is facing in radiants
	
	protected int sway = 1;
	protected double noise = 0; // character current volume
	
	private final double maxHP;
	private double healthPoints; // the number of health points the server.character class has
	private Visibility los = new Visibility();
	private Armor armor;
	private List<StatusEffect> statusEffects = new LinkedList<StatusEffect>();
	private WorldStatePacket perception = new WorldStatePacket();

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
	public Character(ClassStats cs, int id, int team) {
		this.maxHP = cs.getMaxHP()*BASE_HP;
		this.healthPoints = maxHP;
		this.speedF = cs.getSpeedF();
		this.sizeF = cs.getSize();
		this.noiseF = cs.getNoise();
		this.hearF = 0;
		this.fovRangeF = 1;
		this.fovAngleF = 1;
		this.cs = cs;
		this.id = id;
		this.team = team;
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
		updatePerception(world);
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
		Arena arena = world.getArena();
		int curTileY = (int)(y/Terrain.tileSize);
		int curTileX = (int)(x/Terrain.tileSize);
		if (!arena.get(curTileX,curTileY).isTraversable())
			return;
		
		// CHECK & FIX CURRENT POSITION
		int leftX = (int)((x-getRadius())/Terrain.tileSize);
		int rightX = (int)((x+getRadius())/Terrain.tileSize);
		int topY = (int)((y-getRadius())/Terrain.tileSize);
		int btmY = (int)((y+getRadius())/Terrain.tileSize);
		
		if (!arena.get(leftX,curTileY).isTraversable()||
				!arena.get(rightX,curTileY).isTraversable()) {
			double curTileCX = (curTileX+0.5)*Terrain.tileSize;
			x =  curTileCX + 0.99*Math.copySign(0.5*Terrain.tileSize-getRadius(),
					x-curTileCX);
		} else if (!arena.get(curTileX,topY).isTraversable() ||
				!arena.get(curTileX,btmY).isTraversable()) {
			double curTileCY = (curTileY+0.5)*Terrain.tileSize;
			y = curTileCY + 0.99*Math.copySign(0.5*Terrain.tileSize-getRadius(),
					y-curTileCY);
		}  

		// CHECK AND FIX DX AND DY
		double newX = x + dx*GameWindow.MS_PER_UPDATE;
		double newY = y + dy*GameWindow.MS_PER_UPDATE;
		
		if (true) {
			// go through the tiles that this character occupies
			// if it continues to move horizontally in the X axis
			int tileY1 = (int) ((y - getRadius()) / Terrain.tileSize);
			int newTileX = (int) ((newX + Math.copySign(getRadius(),dx)) / Terrain.tileSize);
			int tileY2 = (int) ((y + getRadius()) / Terrain.tileSize);
			
			int wallY=0;
			boolean blocked = false;
			for (int y=tileY1;y<=tileY2;y++) {
				// if there's one that's untraversable, the whole movement is blocked
				if (!arena.get(newTileX, y).isTraversable()) {
					wallY = y;
					blocked = true;
					break;
				}
			}
			if (blocked) {
				double t = y-(0.5+wallY)*Terrain.tileSize;
				// if it's close to the edge of an obstacle
				if (Math.abs(t)>Terrain.tileSize/2 &&
						arena.get(newTileX,curTileY).isTraversable() &&
						dy==0) {
					// well, *slide vertically toward the empty space
					setDy(Math.copySign(Math.abs(dx*0.7),t));
				}
				// block the horizontal movement
				dx = 0;
			}
		}
		if (true) {
			// go through the tiles that this character occupies
			// if it continues to move vertically in the Y axis
			int tileX1 = (int) ((x - getRadius()) / Terrain.tileSize);
			int newTileY = (int) ((newY +Math.copySign(getRadius(), dy)) / Terrain.tileSize);
			int tileX2 = (int) ((x + getRadius()) / Terrain.tileSize);
			int wallX=0;
			boolean blocked = false;
			for (int x=tileX1;x<=tileX2;x++) {
				// if there's one that's untraversable, the whole movement is blocked
				if (!arena.get(x, newTileY).isTraversable()) {
					wallX = x;
					blocked = true;
					break;
				}
			}
	
			if (blocked) {
				double t = x-(0.5+wallX)*Terrain.tileSize;
				// if it's close to the edge of an obstacle
				if (Math.abs(t)>Terrain.tileSize/2 &&
						arena.get(curTileX,newTileY).isTraversable() &&
						dx==0) {
					// well, *slide horizontally toward the empty space
					setDx(Math.copySign(Math.abs(dy*0.7),t));
				}
				// block the vertical movement
				dy = 0;
			}
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
		double inc = -0.1;
		if (dx != 0 || dy != 0)
			inc = getNoiseF()*1;

		noise = Math.max(0, noise + inc);
		double noiseThres = 30;
		if (noise > noiseThres) {
			world.addSound(FOOTSTEP_SOUND_ID,FOOTSTEP_SOUND_VOLUME*inc,getX(),getY());
			noise -= noiseThres;
			sway *= -1;
		}
	}

	protected void updatePerception(World w) {
		List<GameEvent> events = perception.events;
		perception = new WorldStatePacket();
		perception.events.addAll(events);
		perception.visions = new LinkedList<Vision>();
		perception.characters = new LinkedList<CharData>();
		perception.projectiles = new LinkedList<ProjectileData>();
		Area los = new Area();
		if (!isDead()) {
			los = getLoS(w.getArena());
			perception.visions.add(getVision());
		} else {
			for (Character c : w.getCharacters()) {
				if (c.team==team) {
					los.add(c.getLoS(w.getArena()));
					perception.visions.add(c.getVision());
				}
			}
		}
		// add the characters if they are inside vision
		for (Character c : w.getCharacters()) {
			if (c.id!=id && (c.team==team || c.intersects(los))) {
				perception.characters.add(c.generatePartial());
			}
		}

		// copy the projectiles over if they are in line of sight
		
		for (Projectile pr : w.getProjectiles()) {
			if (los.contains(pr.getX(),pr.getY()) ||
					los.contains(pr.getPrevX(), pr.getPrevY())) {
				perception.projectiles.add(pr.getData());
			}
		}
	}
	
	
	public WorldStatePacket getPerception() {
		return perception;
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
		return isDead()?0:BASE_FOVRANGE*fovRangeF;
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

	public boolean isDead() {
		return getHealthPoints()<=0;
	}

	
	/**
	 * Returns the health points of the server.character
	 * 
	 * @return the health points of the server.character
	 */
	public double getHealthPoints() {
		return healthPoints;
	}

	public void onHit(World w, double damage, int sourceId) {
		healthPoints = Math.max(0, healthPoints-damage);
	}
	
	/**
	 * Sets the health points of the server.character
	 * 
	 * @param healthPoints
	 *            the health points of the server.character
	 */
	protected void setHealthPoints(double healthPoints) {
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
		for (StatusEffect se:statusEffects) {
			se.onFinish();
		}
		statusEffects.clear();
	}

	public double getHearF() {
		return hearF;
	}

	protected void setHearF(double hearF) {
		this.hearF = hearF;
	}
	
	public void addHearMod(double hearMod) {
		hearF += hearMod;
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
	
	public void clearEvents() {
		perception.events.clear();
	}
	
	public Rectangle2D getBoundingBox() {
		return new Rectangle2D.Double(getX()-getRadius(), getY()-getRadius(), getRadius()*2, getRadius()*2);
	}
	
	public CharData generatePartial() {
		CharData data = new CharData();
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
	
	public void filterSound(int id, double volume, double x, double y) {
		double distance = Point2D.distance(x, y, getX(), getY());
		double perceivedVolume = Utils.getVolumeAtDistance(volume, distance, getHearF());
		if (perceivedVolume >= Character.BASE_HEARING_THRES) {
			SoundEvent e = new SoundEvent(id,perceivedVolume,x,y);
			getPerception().events.add(e);
		}
	}
	
	public void filterVisualAnimation(int id, double x, double y, double direction) {
		if (Point2D.distance(x, y, getX(), getY())<getFovRange()+5)
			addAnimation(id,x,y,direction);
	}
	
	public void addAnimation(int id, double x, double y, double direction) {
		getPerception().events.add(new AnimationEvent(id,x,y,direction));
	}
	
	public Vision getVision() {
		Vision v = new Vision();
		v.x = getX();
		v.y = getY();
		v.radius = getRadius();
		v.direction = getDirection();
		v.range = getFovRange();
		v.angle = getFovAngle();
		return v;
	}
	
	public boolean intersects(Shape s) {
		double r = getRadius()*0.7;
		return s.contains(x, y) ||
				s.contains(x-r,y-r) ||
				s.contains(x-r,y+r) ||
				s.contains(x+r,y+r) ||
				s.contains(x+r,y-r) ||
				s.contains(x+getRadius(),y) ||
				s.contains(x-getRadius(),y) ||
				s.contains(x,y+getRadius()) ||
				s.contains(x,y-getRadius()); 
	}
}
