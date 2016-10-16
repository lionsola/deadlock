package server.world;

import java.awt.geom.Point2D;

import client.gui.GameWindow;
import server.character.PlayerCharacter;
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
	
	private double dx;
	private double dy;
	
	private int lastHitId;
	
	private final double size;
	private double direction;
	
	private double speed;

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
	public Projectile(PlayerCharacter source, double direction, double speed, double size) {
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
		int checks = (int)Math.ceil(speed*GameWindow.MS_PER_UPDATE/Geometry.LINE_SAMPLE_THRESHOLD);
		
		for (int i=0;i<checks && !isConsumed();i++) {
			double oldX = x;
			double oldY = y;
			x = x + dx*GameWindow.MS_PER_UPDATE/checks;
			y = y + dy*GameWindow.MS_PER_UPDATE/checks;
			
			// check projectile vs character
			for (PlayerCharacter ch : w.getCharacters()) {
				// HIT
				if (ch.id!=lastHitId && Point2D.distance(x,y, ch.getX(),ch.getY()) < ch.getRadius()) {
					lastHitId = ch.id;
					ch.applyArmor(this,oldX,oldY);
					if (isConsumed())
						break;
					ch.applyArmor(this,oldX,oldY);
					onHitCharacter(w,ch,x,y);
					break;
				}
			}

			// check projectile vs wall collision
			int tileX = (int) (x / TileBG.tileSize);
			int tileY = (int) (y / TileBG.tileSize);
			Tile t = w.getArena().get(tileX, tileY);
			if (!t.isWalkable()) {
				onHitWall(w,x,y,t);	
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
	
	protected abstract void onHitWall(World w, double x, double y, Tile t);

	protected abstract void onHitCharacter(World w, PlayerCharacter ch, double x, double y);
	
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
