package server.character;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;

import client.gui.GameWindow;
import server.status.StatusEffect;
import server.world.Arena;
import server.world.Light;
import server.world.Projectile;
import server.world.Terrain;
import server.world.Utils;
import server.world.Visibility;
import server.world.World;
import server.world.trigger.Trigger;
import shared.network.CharData;
import shared.network.ProjectileData;
import shared.network.Vision;
import shared.network.GameDataPackets.WorldStatePacket;
import shared.network.event.AnimationEvent;
import shared.network.event.GameEvent;
import shared.network.event.SoundEvent;

/**
 * This class will define the base behaviour of every type of Character.
 */
public class Entity {
	public static final double BASE_SPEED	= 0.0033;
	public static final double BASE_HP		= 100;
	public static final double BASE_RADIUS	= 0.5;
	public static final double BASE_FOVRANGE= 20;
	public static final double BASE_FOVANGLE= Math.toRadians(120);
	
	public static final double BASE_HEARING_THRES = -10;
	
	private double x = 0;
	private double y = 0;
	//private Vector2D pos;
	private double dx = 0;
	private double dy = 0;
	
	private double realDx = 0;
	private double realDy = 0;
	//private Vector2D vel;
	
	public final int id; // the player's ID
	
	private double fovRangeF;
	private double fovAngleF;
	private double speedF;
	private double noiseF; // the level of volume that the server.character makes, standard = 1
	private double sizeF;
	private double hearF; // the
	
	public final int team;
	public final int typeId;
	
	protected double direction; // direction the server.character is facing in radiants
	
	protected int sway = 1;
	protected double noise = 0; // character current volume
	
	private final double maxHP;
	private double healthPoints; // the number of health points the server.character class has
	
	private double exposure = 0;
	
	private Visibility visibility = new Visibility();
	private Armor armor;
	private List<StatusEffect> statusEffects = new LinkedList<StatusEffect>();
	private WorldStatePacket perception = new WorldStatePacket();
	private Area fov = new Area();
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
	public Entity(int id, int team, int typeId) {
		this.typeId = typeId;
		ClassStats cs = ClassStats.classStats.get(typeId); 
		this.maxHP = cs.getMaxHP()*BASE_HP;
		this.healthPoints = maxHP;
		this.speedF = cs.getSpeedF();
		this.sizeF = cs.getSize();
		this.noiseF = cs.getNoise();
		this.hearF = 1;
		this.fovRangeF = 1;
		this.fovAngleF = 1;
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
		updatePosition(world);
		updateExposure(world);
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
		
		double curTileCX = (curTileX+0.5)*Terrain.tileSize;
		double curTileCY = (curTileY+0.5)*Terrain.tileSize;
		
		if (!arena.get(leftX,curTileY).isTraversable()||
				!arena.get(rightX,curTileY).isTraversable()) {
			x =  curTileCX + 0.99*Math.copySign(0.5*Terrain.tileSize-getRadius(),
					x-curTileCX);
		} else {
			for (int tx=leftX;tx<=rightX;tx++) {
				if (!arena.get(tx,topY).isTraversable() || !arena.get(tx,btmY).isTraversable()) {
					y = curTileCY + 0.99*Math.copySign(0.5*Terrain.tileSize-getRadius(),y-curTileCY);
					break;
				}
			}
		}

		// CHECK AND FIX DX AND DY
		realDx = 0;
		realDy = 0;
		if (dx!=0) {
			// go through the tiles that this character occupies
			// if it continues to move horizontally in the X axis
			double newX = x + dx*GameWindow.MS_PER_UPDATE;
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
					realDy = Math.copySign(Math.abs(dx*0.7),t)*GameWindow.MS_PER_UPDATE;
				}
				
				// block the horizontal movement
				realDx = curTileCX + 0.99*Math.copySign(0.5*Terrain.tileSize-getRadius(),
						x-curTileCX) - x;
			} else {
				realDx = newX - x;
			}
		}
		if (dy!=0) {
			// go through the tiles that this character occupies
			// if it continues to move vertically in the Y axis
			double newY = y + dy*GameWindow.MS_PER_UPDATE;
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
					realDx = Math.copySign(Math.abs(dy*0.7),t)*GameWindow.MS_PER_UPDATE;
				}
				// block the vertical movement
				realDy = curTileCY + 0.99*Math.copySign(0.5*Terrain.tileSize-getRadius(),
						y-curTileCY) - y;
			} else {
				realDy = newY - y;
			}
		}
	}

	protected void updatePosition(World w) {
		int curTileY = (int)(y/Terrain.tileSize);
		int curTileX = (int)(x/Terrain.tileSize);
		
		final double checkRange = getRadius()+0.01;
		int leftX = (int)((x-checkRange)/Terrain.tileSize);
		int rightX = (int)((x+checkRange)/Terrain.tileSize);
		int topY = (int)((y-checkRange)/Terrain.tileSize);
		int btmY = (int)((y+checkRange)/Terrain.tileSize);
		
		x += realDx;
		y += realDy;
		
		int newTileY = (int)(y/Terrain.tileSize);
		int newTileX = (int)(x/Terrain.tileSize);
		
		if (newTileX != curTileX || newTileY != curTileY) {
			Trigger newT = w.getArena().get(newTileX,newTileY).getTrigger();
			if (newT!=null) {
				newT.onCharacterEnter(this,w);
			}
			
			Trigger oldT = w.getArena().get(curTileX,curTileY).getTrigger();
			if (oldT!=null) {
				oldT.onCharacterLeave(this, w);
			}
		}
		
		int newLeftX = (int)((x-checkRange)/Terrain.tileSize);
		int newRightX = (int)((x+checkRange)/Terrain.tileSize);
		int newTopY = (int)((y-checkRange)/Terrain.tileSize);
		int newBtmY = (int)((y+checkRange)/Terrain.tileSize);
		for (int x=newLeftX;x<=newRightX;x++) {
			for (int y=newTopY;y<=newBtmY;y++) {
				Trigger t = w.getArena().get(x,y).getTrigger();
				if (t!=null) {
					if ((x<leftX  /*&& dx<0*/) ||
							(x>rightX /*&& dx>0*/) ||
							(y<topY /*&& dy<0*/) ||
							(y>btmY /*&& dy>0*/)) {
						t.onCharacterTouch(this, w);
					}
				}
			}
		}
		
		for (int x=leftX;x<=rightX;x++) {
			for (int y=topY;y<=btmY;y++) {
				Trigger t = w.getArena().get(x,y).getTrigger();
				if (t!=null) {
					if ((x<newLeftX  /*&& dx>=0*/) ||
							(x>newRightX /*&& dx<=0*/) ||
							(y<newTopY /*&& dy>=0*/) ||
							(y>newBtmY /*&& dy<=0*/)) {
						t.onCharacterUntouch(this, w);
					}
				}
			}
		}
	}
	
	/**
	 * Updates the volume in the world based on the server.character.
	 * 
	 * @param world
	 *            the world in which to update the volume.
	 */
	protected void updateNoise(World world) {
		double inc = -0.1;
		if (realDx != 0 || realDy != 0)
			inc = getNoiseF()*1;

		noise = Math.max(0, noise + inc);
		double noiseThres = 30;
		if (noise > noiseThres) {
			Terrain te = world.getArena().getTileAt(x, y).getTerrain();
			if (te!=null) {
				world.addSound(te.getSoundId(),te.getVolume()*SoundEvent.FOOTSTEP_SOUND_VOLUME*inc,getX(),getY());
			} else {
				world.addSound(SoundEvent.FOOTSTEP_DEFAULT_ID,SoundEvent.FOOTSTEP_SOUND_VOLUME*inc,getX(),getY());
			}
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
		try {
			los = visibility.genLOSAreaMeter(getX(), getY(), getFovRange(), getFovAngle(), getDirection(), w.getArena());
		} catch (Exception e) {
			e.printStackTrace();
		}
		fov = los;
		perception.visions.add(getVision());
		
		double ts = Terrain.tileSize;
		
		for (Light l:w.getDynamicLights()) {
			double r = l.getRange()*ts;
			if (getLoS().intersects(l.getX()-r, l.getY()-r, r*2, r*2)) {
				perception.dynamicLights.add(l);
			}
		}
		
		// add the characters if they are inside vision
		for (InputControlledEntity c : w.getCharacters()) {
			if (c.id!=id && (c.team==team || c.intersects(los))) {
				if (c instanceof NPC) {
					perception.characters.add(((NPC)c).generatePartial());
				}
				else {
					perception.characters.add(c.generatePartial());
				}
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
	public Area getLoS() {
		return fov;
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
		return Math.sqrt(realDx*realDx + realDy*realDy);
	}
	
	public Point2D getPosition() {
		return new Point2D.Double(x, y);
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
	public void setPosition(World w, double x, double y) {
		realDx = x-getX();
		realDy = y-getY();
		updatePosition(w);
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
	
	public double getExposure() {
		return exposure;
	}
	
	public void setExposure(double exposure) {
		this.exposure = exposure;
	}
	
	public Armor getArmor() {
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
		return realDx!=0 || realDy!=0;
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
		CharData data = new CharData(this);
		return data;
	}
	
	public void filterSound(int id, double volume, double x, double y) {
		if (!isDead()) {
			double distance = Point2D.distance(x, y, getX(), getY());
			double perceivedVolume = Utils.getVolumeAtDistance(volume, distance, getHearF());
			if (perceivedVolume >= Entity.BASE_HEARING_THRES) {
				SoundEvent e = new SoundEvent(id,perceivedVolume,x,y);
				getPerception().events.add(e);
			}
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
		v.hearRange = (float) (getHearF()*SoundEvent.FOOTSTEP_SOUND_VOLUME/World.DISTANCE_VOLUME_DROP_RATE);
		return v;
	}
	
	public boolean intersects(Shape s) {
		for (Point2D p:getCheckPoints()) {
			if (s.contains(p)) {
				return true;
			}
		}
		return false;
	}
	
	protected void updateExposure(World w) {
		double targetExposure = 0;
		final double EXPOSURE_CHANGE = 0.01;
		List<Point2D> checkPoints = getCheckPoints();
		for (Point2D p:checkPoints) {
			Color l = new Color(w.getArena().getLightAt(p));
			float[] hsb = Color.RGBtoHSB(l.getRed(), l.getGreen(), l.getBlue(), null);
			targetExposure += hsb[2]/checkPoints.size();
		}
		if (isMoving()) {
			targetExposure *= (1+getNoiseF());
		}
		if (Math.abs(targetExposure-exposure)<EXPOSURE_CHANGE) {
			exposure = targetExposure;
		} else {
			exposure += Math.copySign(EXPOSURE_CHANGE, targetExposure-exposure);
		}
	}
	
	private List<Point2D> getCheckPoints() {
		List<Point2D> checkPoints = new LinkedList<Point2D>();
		double r = getRadius()*0.7;
		checkPoints.add(new Point2D.Double(x, y));
		checkPoints.add(new Point2D.Double(x-r, y-r));
		checkPoints.add(new Point2D.Double(x-r, y+r));
		checkPoints.add(new Point2D.Double(x+r, y+r));
		checkPoints.add(new Point2D.Double(x+r, y-r));
		checkPoints.add(new Point2D.Double(x+getRadius(), y));
		checkPoints.add(new Point2D.Double(x-getRadius(), y));
		checkPoints.add(new Point2D.Double(x, y+getRadius()));
		checkPoints.add(new Point2D.Double(x+getRadius(), y));
		// TODO gun
		return checkPoints;
	}
	
	public static List<Point2D> getCheckPoints(double x, double y, double radius, double dir, double l) {
		List<Point2D> checkPoints = new LinkedList<Point2D>();
		double r = radius*0.7;
		checkPoints.add(new Point2D.Double(x, y));
		checkPoints.add(new Point2D.Double(x-r, y-r));
		checkPoints.add(new Point2D.Double(x-r, y+r));
		checkPoints.add(new Point2D.Double(x+r, y+r));
		checkPoints.add(new Point2D.Double(x+r, y-r));
		checkPoints.add(new Point2D.Double(x+radius, y));
		checkPoints.add(new Point2D.Double(x-radius, y));
		checkPoints.add(new Point2D.Double(x, y+radius));
		checkPoints.add(new Point2D.Double(x+radius, y));
		// TODO gun
		return checkPoints;
	}
}
