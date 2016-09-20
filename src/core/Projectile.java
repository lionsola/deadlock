package core;

import java.awt.geom.Point2D;
import java.util.List;

import character.ControlledCharacter;
import game.Game;
import network.ProjectileData;

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
	
	private double dx;
	private double dy;
	
	private final double size;
	private double direction;
	
	private double speed;
	private boolean consumed = false;

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
	public Projectile(ControlledCharacter source, double direction, double speed, double size) {
		id = source.id;
		x = source.getX();
		y = source.getY();

		
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
		if (speed<=0) {
			//consumeProjectile();
			return;
		}
		double newX = getX() + dx*Game.MS_PER_UPDATE, newY = getY() + dy*Game.MS_PER_UPDATE;
		List<Point2D> samples = Geometry.getLineSamples(getX(), getY(), newX, newY);

		for (Point2D pt : samples) {
			// check projectile vs character
			for (ControlledCharacter ch : w.characters) {
				// HIT
				if (ch.id != id && pt.distance(ch.getX(), ch.getY()) < ch.getRadius()) {
					onHitCharacter(w,ch,pt.getX(),pt.getY());
				}
			}

			// if alr hit character, done
			if (consumed)
				break;

			// check projectile vs wall collision
			int tileX = (int) (pt.getX() / Tile.tileSize);
			int tileY = (int) (pt.getY() / Tile.tileSize);

			if (!w.arena.get(tileX, tileY).isWalkable()) {
				onHitWall(w,pt.getX(),pt.getY());
				break;
			}
		}
		
		
		if (!consumed) {
			x += getDx()*Game.MS_PER_UPDATE;
			y += getDy()*Game.MS_PER_UPDATE;
		}
	}
	
	public ProjectileData getData() {
		ProjectileData data = new ProjectileData();
		data.x = (float) getX();
		data.y = (float) getY();
		data.speed = (float) getSpeed();
		data.direction = (float) getDirection();
		data.size = (float) getSize();
		return data;
	}
	
	protected abstract void onHitWall(World w, double x, double y);
	
	protected abstract void onHitDestination(World w);

	protected abstract void onHitCharacter(World w, ControlledCharacter ch, double x, double y);
	
	protected void consumeProjectile() {
		consumed = true;
	}
	
	public boolean isConsumed() {
		return consumed;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
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
	
	protected void setSpeed(double s) {
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
