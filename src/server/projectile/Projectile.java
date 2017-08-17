package server.projectile;

import java.awt.geom.Point2D;

import client.gui.GameWindow;
import server.character.Entity;
import server.character.InputControlledEntity;
import server.world.Terrain;
import server.world.Tile;
import server.world.World;
import shared.network.ProjectileData;

/**
 * Used to represent a projectile.
 */
public abstract class Projectile {
	transient final public int id;

	protected final double initX;
	protected final double initY;
	
	private double x;
	private double y;
	
	private double prevX;
	private double prevY;
	
	transient private double dx;
	transient private double dy;
	
	transient private int lastHitId;
	
	private final double size;
	private double direction;
	
	private double speed;
	
	private final double range;
	private double travelled = 0;
	private HitMod hMod = null;
	
	public static final double RAYCAST_DISTANCE = 0.2;

	/**
	 * Create a projectile to be use in the world
	 * 
	 * @param source
	 *            the source, ie the player, of which the projectile being fired from
	 * @param direction
	 *            the direction of the projectile
	 * @param speed
	 *            the speed of the projectile
	 * @param size
	 *            radius of the projectile
	 */
	public Projectile(Entity source, double direction, double speed, double size, double range) {
		this(source,source.getX(),source.getY(),direction,speed,size,Double.MAX_VALUE);
	}

	public Projectile(Entity source, double direction, double speed, double size) {
		this(source,direction,speed,size,Double.MAX_VALUE);
	}
	
	public Projectile(Entity source, double x, double y, double direction, double speed, double size) {
		this(source,x,y,direction,speed,size,Double.MAX_VALUE);
	}
	
	/**
	 * Create a projectile to be use in the world
	 * 
	 * @param source
	 *            the source, ie the player, of which the projectile being fired from
	 * @param x The x coordinate of the projectile.
	 * @param y The y coordinate of the projectile.
	 * @param direction
	 *            the direction of the projectile
	 * @param speed
	 *            the speed of the projectile
	 * @param size
	 *            radius of the projectile
	 */
	public Projectile(Entity source, double x, double y, double direction, double speed, double size, double range) {
		id = source.id;
		initX = x;
		initY = y;
		this.x = x;
		this.y = y;
		lastHitId = source.id;
		
		this.direction = direction;
		this.speed = speed;
		computeDxDy();
		this.speed += source.getSpeed();
		computeDxDy();
		this.size = size;
		this.range = range;
	}
	
	/**
	 * Create a projectile to be use in the world
	 * 
	 * @param source
	 *            the source, ie the player, of which the projectile being fired from
	 * @param direction
	 *            the direction of the projectile
	 * @param speed
	 *            the speed of the projectile
	 * @param size
	 *            radius of the projectile
	 */
	public Projectile(Projectile source, double direction, double speed, double size) {
		id = source.id;
		initX = x;
		initY = y;
		x = source.getX();
		y = source.getY();

		lastHitId = Integer.MAX_VALUE;
		this.direction = direction;
		this.speed = speed;
		computeDxDy();
		dx += source.getDx();
		dy += source.getDy();
		computeSpeedDir();
		this.size = size;
		this.range = Double.MAX_VALUE;
	}
	
	/**
	 * Update the projectile by checking if it is out of range every frame
	 */
	public void update(World w) {
		if (isConsumed()) {
			//consumeProjectile();
			return;
		}
		prevX = x;
		prevY = y;
		int checks = (int)Math.ceil(speed*GameWindow.MS_PER_UPDATE/RAYCAST_DISTANCE);
		
		double curDir = getDirection();
		
		for (int i=0;i<checks && !isConsumed();i++) {
			double oldX = x;
			double oldY = y;
			x = x + dx*GameWindow.MS_PER_UPDATE/checks;
			y = y + dy*GameWindow.MS_PER_UPDATE/checks;
			
			// check projectile vs character
			for (InputControlledEntity ch : w.getCharacters()) {
				// HIT
				if (ch.id!=lastHitId && Point2D.distance(x,y, ch.getX(),ch.getY()) < ch.getRadius()) {
					lastHitId = ch.id;
					ch.applyArmor(this,oldX,oldY);
					if (isConsumed())
						break;
					else {
						if (hMod!=null) {
							hMod.onHitEntity(w, ch, this);
						}
						onHitCharacter(w,ch);
					}
				}
			}
			
			// check projectile vs wall collision
			int tileX = (int) (x / Terrain.tileSize);
			int tileY = (int) (y / Terrain.tileSize);
			Tile t = w.getArena().get(tileX, tileY);
			if (!t.isTraversable() || t.getCoverType()>0) {
				onHitWall(w,t);
			}
			if (Math.abs(getDirection()-curDir)>0.02) {
				break;
			}
		}
		travelled += Point2D.distance(x, y, prevX, prevY);
	}
	
	public ProjectileData getData() {
		ProjectileData data = new ProjectileData();
		data.x = (float) getX();
		data.y = (float) getY();
		data.prevX = (float) getPrevX();
		data.prevY = (float) getPrevY();
		data.size = (float) getSize();
		return data;
	}
	
	protected abstract void onHitWall(World w, Tile t);

	protected abstract void onHitCharacter(World w, InputControlledEntity ch);
	
	public boolean isConsumed() {
		return travelled >= range;
	}

	public HitMod getHMod() {
		return hMod;
	}
	
	public void setHMod(HitMod hMod) {
		this.hMod = hMod;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getPrevX() {
		return prevX;
	}
	
	public double getPrevY() {
		return prevY;
	}
	
	public double getDx() {
		return dx;
	}

	public double getDy() {
		return dy;
	}

	public double getSpeed() {
		return speed;
	}

	public double getDirection() {
		return direction;
	}

	public double getSize() {
		return size;
	}
	
	public void setDirection(double d) {
		this.direction = d;
		computeDxDy();
	}
	
	public void setSpeed(double s) {
		speed = Math.max(0, s);
		computeDxDy();
	}
	
	private void computeDxDy() {
		dx = Math.cos(direction) * speed;
		dy = -Math.sin(direction) * speed;
	}
	
	private void computeSpeedDir() {
		speed = Math.sqrt(dx*dx + dy*dy);
		direction = Math.atan2(-dy, dx);
	}
	
	public void bounce(World w, final int LAYER) {
		int checks = (int)Math.ceil(speed*GameWindow.MS_PER_UPDATE/RAYCAST_DISTANCE);
		double bounceX = getX() - getDx()*GameWindow.MS_PER_UPDATE/checks;
		double bounceY = getY() - getDy()*GameWindow.MS_PER_UPDATE/checks;
		if (!w.getArena().getTileAt(bounceX, getY()).isBounceTile(LAYER)) {
			setDirection(Math.PI - getDirection());
			setX(bounceX);
		} else if (!w.getArena().getTileAt(getX(), bounceY).isBounceTile(LAYER)) {
			setDirection(- getDirection());
			setY(bounceY);
		} else {
			setX(bounceX);
			setY(bounceY);
			setDirection(getDirection()+Math.PI);
		}
	}
}
