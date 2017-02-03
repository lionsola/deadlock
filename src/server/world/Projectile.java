package server.world;

import java.awt.geom.Point2D;

import client.gui.GameWindow;
import server.character.InputControlledEntity;
import shared.network.ProjectileData;

/**
 * Used to represent a projectile.
 * 
 * @author Anh Pham
 * @author Connor Cartwright
 */
public abstract class Projectile {
	transient final public int id;

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
	public Projectile(InputControlledEntity source, double direction, double speed, double size) {
		id = source.id;
		x = source.getX();
		y = source.getY();
		lastHitId = source.id;
		
		this.direction = direction;
		this.speed = speed;
		computeDxDy();
		this.speed += (source.getDx()*dx + source.getDy()*dy)/speed;
		computeDxDy();
		this.size = size;
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
	public Projectile(InputControlledEntity source, double x, double y, double direction, double speed, double size) {
		id = source.id;
		this.x = x;
		this.y = y;
		lastHitId = source.id;
		
		this.direction = direction;
		this.speed = speed;
		computeDxDy();
		this.speed += (source.getDx()*dx + source.getDy()*dy)/speed;
		computeDxDy();
		this.size = size;
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
						onHitCharacter(w,ch,x,y);
					}
				}
			}

			// check projectile vs wall collision
			int tileX = (int) (x / Terrain.tileSize);
			int tileY = (int) (y / Terrain.tileSize);
			Tile t = w.getArena().get(tileX, tileY);
			if (!t.isTraversable() || t.coverType()>0) {
				onHitWall(w,x,y,t.getThing());
			}
		}
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
	
	protected abstract void onHitWall(World w, double x, double y, Thing t);

	protected abstract void onHitCharacter(World w, InputControlledEntity ch, double x, double y);
	
	public abstract boolean isConsumed();

	protected void setX(double x) {
		this.x = x;
	}
	
	protected void setY(double y) {
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
	
	protected void setDirection(double d) {
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
}
