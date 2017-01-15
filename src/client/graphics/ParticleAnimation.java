package client.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.Serializable;

import shared.core.Vector3D;

/**
 * Creates 2D particles and enables their movement.
 * 
 * @author Anh Pham
 * @author Shobitha Shivakumar
 */
public class ParticleAnimation extends BasicAnimation implements Cloneable, Serializable {
	public enum GroundInteraction {
		Bounce {
			@Override
			public Vector3D getVelAfterImpact(Vector3D preVel) {
				return new Vector3D(preVel.x,preVel.y,-preVel.z*2/3);
			}
		}, Slide {
			@Override
			public Vector3D getVelAfterImpact(Vector3D preVel) {
				return new Vector3D(preVel.x,preVel.y,0);
			}
		}, Stop {
			@Override
			public Vector3D getVelAfterImpact(Vector3D preVel) {
				return new Vector3D(0,0,0);
			}
		};
		abstract public Vector3D getVelAfterImpact(Vector3D preVel);
	}
	private static final long serialVersionUID = 831617922864676426L;
	private Vector3D loc; // location of the particle
	private Vector3D vel; // velocity of the particle
	private Vector3D acc; // acceleration of the particle
	
	private double initSize; // size of the particle
	private double rotationSpeed;
	private Color color; // colour of particle
	private GroundInteraction gi = GroundInteraction.Bounce;
	
	transient private double rotation;
	transient private double size; 
	transient private int alpha;

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
		this.loc = new Vector3D(x, y, 1);
		double dx = Math.cos(direction) * speed;
		double dy = -Math.sin(direction) * speed;
		this.vel = new Vector3D(dx, dy, 0);
		this.acc = new Vector3D(0, 0, -0.016);
		this.life = life;
		this.initSize = size;
		this.size = size;
		this.color = c;
	}
	
	public ParticleAnimation(long life) {
		super(life);
		loc = new Vector3D(0,0,1);
		vel = new Vector3D(0,0,0);
		acc = new Vector3D(0,0,-0.016);
	}

	/**
	 * Updates the different fields of the particle continuously to result in its movement.
	 * 
	 * @return boolean indicates whether a particle has finished updating itself.
	 */
	@Override
	public void update(AnimationSystem as) {
		super.update(as);
		vel.add(acc);
		loc.add(vel);
		if (loc.z<0) {
			loc.z = 0;
			vel = gi.getVelAfterImpact(vel);
		}
		
		rotation += rotationSpeed;
		size = initSize*(0.5+loc.z/2);
		alpha = (int) Math.max(0, Math.min(color.getAlpha(),color.getAlpha()*2*life/duration));
		if (life/2>0) {
			Color c = new Color(color.getRed(),color.getGreen(),color.getBlue(),alpha);
			as.addCustomAnimation(new LineAnimation(Math.min(500,life/2), loc.x, loc.y, loc.x-vel.x, loc.y-vel.y, size, c));
		}
	}

	/**
	 * Renders the particle onto the screen.
	 */
	@Override
	public void render(Graphics2D g2D) {
		if (alpha>0) {
			Color c = new Color(color.getRed(),color.getGreen(),color.getBlue(),alpha);
			g2D.setColor(c);
			if (rotation!=0) {
				double px = Renderer.getPPM()*loc.x;
				double py = Renderer.getPPM()*loc.y;
				double rot = rotation;
				synchronized (g2D) {
					g2D.rotate(-rot, px, py);
					Renderer.fillRect(g2D,loc.x-size,loc.y-size,size*2,size*2);
					g2D.rotate(rot, px, py);
				}
			} else {
				Renderer.fillRect(g2D,loc.x-size,loc.y-size,size*2,size*2);
			}
		}
	}

	public void setDirection(double direction, double speed) {
		double dx = Math.cos(direction) * speed;
		double dy = -Math.sin(direction) * speed;
		setVel(dx, dy, vel.z);
	}
	
	public double getDirection() {
		return Math.atan2(-vel.y, vel.x);
	}
	
	/**
	 * Sets a new location for the particle.
	 * 
	 * @param x
	 *            the x coordinate of the particle.
	 * @param y
	 *            the y coordinate of the particle.
	 */
	public void setLoc(double x, double y, double z) {
		loc.x = x;
		loc.y = y;
		loc.z = z;
	}

	public void set2DLoc(double x, double y) {
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
	public void setVel(double x, double y, double z) {
		vel.x = x;
		vel.y = y;
		vel.z = z;
	}

	/**
	 * Sets the acceleration for a particle.
	 * 
	 * @param x
	 *            change in the x coordinate of the acceleration of the particle.
	 * @param y
	 *            change in the y coordinate of the acceleration of the particle.
	 */
	public void setAcc(double x, double y, double z) {
		acc.x = x;
		acc.y = y;
		acc.z = z;
	}

	/**
	 * Sets the size for a particle.
	 * 
	 * @param x
	 *            change in the x coordinate of the size of the particle.
	 * @param y
	 *            change in the y coordinate of the size of the particle.
	 */
	public void setSize(double size) {
		this.initSize = size;
	}

	public Vector3D getVelocity() {
		return vel;
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

	public void setRotationSpeed(double rotSpeed) {
		rotationSpeed = rotSpeed;
	}
	
	public double getSpeed() {
		return Math.sqrt(vel.x*vel.x + vel.y*vel.y);
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
	public Vector3D getLoc() {
		return loc;
	}

	/**
	 * Gets the velocity of a particle.
	 */
	public Vector3D getVel() {
		return vel;
	}
	
	public Vector3D getAcc() {
		return acc;
	}
	
	@Override
	public ParticleAnimation clone() {
		ParticleAnimation p = new ParticleAnimation(duration);
		p.acc = acc.clone();
		p.loc = loc.clone();
		p.vel = vel.clone();
		p.color = color;
		p.rotationSpeed = rotationSpeed;
		p.initSize = initSize;
		p.gi = gi;
		
		return p;
	}

	public Color getColor() {
		return color;
	}

	public GroundInteraction getGroundInteraction() {
		return gi;
	}

	public void setGroundInteraction(GroundInteraction gi) {
		this.gi = gi;
	}
}
