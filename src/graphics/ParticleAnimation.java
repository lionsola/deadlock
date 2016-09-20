package graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 * Creates 2D particles and enables their movement.
 * 
 * @author Anh Pham
 * @author Shobitha Shivakumar
 */
public class ParticleAnimation extends Animation {

	private Vector2D loc; // location of the particle
	private Vector2D vel; // velocity of the particle
	private Vector2D acc; // acceleration of the particle
	private Vector2D size; // size of the particle
	private Vector2D maxSize; // maximum size allowed for the particle
	private Vector2D growth; // growth in size of particle
	private Color color; // colour of particle

	private boolean ultSize = false;
	private boolean defaultSize = false;

	/**
	 * Creates a moving particle.
	 * 
	 * @param x
	 *            the x coordinate of the particle.
	 * @param y
	 *            the y coordinate of the particle.
	 * @param direction
	 *            angle of movement of particle
	 * @param speed
	 *            speed of the particle
	 * @param size
	 *            size of the particle
	 * @param life
	 *            how long the particle lasts
	 * @param c
	 *            Colour of the particle
	 */
	public ParticleAnimation(double x, double y, double direction, double speed, double size, long life, Color c) {
		super((long) life);
		this.loc = new Vector2D(x, y);
		double dx = Math.cos(direction) * speed;
		double dy = -Math.sin(direction) * speed;
		this.vel = new Vector2D(dx, dy);
		this.acc = new Vector2D(0, 0);
		this.life = life;
		this.size = new Vector2D(size, size);
		this.growth = new Vector2D(0, 0);
		this.maxSize = new Vector2D(Double.MAX_VALUE, Double.MAX_VALUE);
		this.color = c;
	}

	/**
	 * Updates the different fields of the particle continuously to result in its movement.
	 * 
	 * @return boolean indicates whether a particle has finished updating itself.
	 */
	@Override
	public boolean update() {
		if (super.update())
			return true;
		vel.add(acc);
		loc.add(vel);
		size.add(growth);

		if (defaultSize) {
			if (size.x >= maxSize.x) {
				if (size.y >= maxSize.y)
					return true;
				else
					size.x = maxSize.x;
			}
			if (size.y >= maxSize.y)
				size.y = maxSize.y;
			if (size.x <= 0)
				if (size.y <= 0)
					return true;
				else
					size.x = 1;
			if (size.y <= 0)
				size.y = 1;
			return false;
		}

		if (ultSize) {
			if (size.x > maxSize.x) {
				size.x = maxSize.x;
				growth.x *= -1;
			}
			if (size.y > maxSize.y) {
				size.y = maxSize.y;
				growth.y *= -1;
			}
			if (size.x <= 0) {
				size.x = 1;
				growth.x *= -1;
			}
			if (size.y <= 0) {
				size.y = 1;
				growth.y *= -1;
			}
		} else { // We stop growing or shrinking.
			if (size.x > maxSize.x)
				size.x = maxSize.x;
			if (size.y > maxSize.y)
				size.y = maxSize.y;
			if (size.x <= 0)
				size.x = 1;
			if (size.y <= 0)
				size.y = 1;
		}
		return false;
	}

	/**
	 * Renders the particle onto the screen.
	 */
	@Override
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(color);
		Renderer.fillCircle(g2d,loc.x,loc.y,size.x);
	}

	/**
	 * Sets a new location for the particle.
	 * 
	 * @param x
	 *            the x coordinate of the particle.
	 * @param y
	 *            the y coordinate of the particle.
	 */
	public void setLoc(double x, double y) {
		loc.x = x;
		loc.y = y;
	}

	/**
	 * Sets a new velocity for the particle.
	 * 
	 * @param x
	 *            change in the x coordinate of the particle.
	 * @param y
	 *            change in the y coordinate of the particle.
	 */
	public void setVel(double x, double y) {
		vel.x = x;
		vel.y = y;
	}

	/**
	 * Sets the acceleration for a particle.
	 * 
	 * @param x
	 *            change in the x coordinate of the acceleration of the particle.
	 * @param y
	 *            change in the y coordinate of the acceleration of the particle.
	 */
	public void setAcc(double x, double y) {
		acc.x = x;
		acc.y = y;
	}

	/**
	 * Sets the size for a particle.
	 * 
	 * @param x
	 *            change in the x coordinate of the size of the particle.
	 * @param y
	 *            change in the y coordinate of the size of the particle.
	 */
	public void setSize(double x, double y) {
		size.x = x;
		size.y = y;
	}

	/**
	 * Sets the maximum size of a particle.
	 * 
	 * @param x
	 *            change in the x coordinate of the maximum size of the particle.
	 * @param y
	 *            change in the y coordinate of the maximum size of the particle.
	 */
	public void setMaxSize(double x, double y) {
		maxSize.x = x;
		maxSize.y = y;
	}

	/**
	 * Sets the growth allowed for a particle.
	 * 
	 * @param x
	 *            change in the x coordinate of the growth of the particle.
	 * @param y
	 *            change in the y coordinate of the growth of the particle.
	 */
	public void setGrowth(double x, double y) {
		growth.x = x;
		growth.y = y;
	}

	/**
	 * Sets the life for a particle.
	 * 
	 * @param x
	 *            change in the x coordinate of the life of particle.
	 * @param y
	 *            change in the y coordinate of the life of the particle.
	 */
	public void setLife(long num) {
		life = num;
	}

	public void setSizeDefault(boolean c) {
		defaultSize = c;
	}

	public void setUltSize(boolean c) {
		defaultSize = false;
		ultSize = c;
	}

	/**
	 * Sets the colour for a particle.
	 * 
	 * @param c
	 *            The new colour to be set.
	 */
	public void setColor(Color c) {
		color = c;
	}

	/**
	 * Gets the location of a particle.
	 */
	public Vector2D getLoc() {
		return loc;
	}

	/**
	 * Gets the velocity of a particle.
	 */
	public Vector2D getVel() {
		return vel;
	}

}
