package client.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import client.graphics.ParticleAnimation.GroundInteraction;
import client.gui.Camera;
import server.world.Geometry;
import server.world.Utils;
import shared.core.Vector2D;
import shared.network.event.AnimationEvent;

/**
 * Manage the animation effects and provide high-level methods to create new animations in the game
 * without having to deal with low-level animation classes.
 */
public class AnimationSystem {
	//private ConcurrentLinkedQueue<ParticleEmitter> particleEmitters;
	private ConcurrentLinkedQueue<BasicAnimation> animations;
	private ConcurrentLinkedQueue<BasicAnimation> pending;
	/**
	 * Constructor. Create a new animation system.
	 */
	public AnimationSystem() {
		animations = new ConcurrentLinkedQueue<BasicAnimation>();
		pending = new ConcurrentLinkedQueue<BasicAnimation>();
	}

	/**
	 * Create a volume animation.
	 * 
	 * @param x
	 *            The x coordinate of the volume.
	 * @param y
	 *            The y coordinate of the volume.
	 * @param volume
	 *            The loudness of the volume.
	 */
	public void addNoiseAnimation(double x, double y, float noise) {
		long duration = 1000 + (int) noise * 5;
		pending.add(new CircleAnimation(x, y, 0.5 + noise / 30, duration, 0));
	}

	public void addParticles(List<ParticleSource> sources) {
		for (ParticleSource ps:sources) {
			ps.update(this);
		}
	}
	
	/**
	 * Create particle effect when a gun shot happens.
	 * 
	 * @param x
	 *            The x coordinate of the gun shot.
	 * @param y
	 *            The y coordinate of the gun shot.
	 * @param direction
	 */
	public void addShotAnimation(double x, double y, double direction) {
		for (int i = 0; i < 4; i++) {
			double randomDirection = direction -(Math.PI/4) + (Math.PI/2)*i/4;
			ParticleAnimation p = new ParticleAnimation(150);
			p.setLoc(x, y, 1);
			p.setVel(0, 0, -0.02);
			p.setDirection(randomDirection, 0.15);
			p.setColor(new Color(0x7fffffff,true));
			p.setSize(0.07);
			p.setTrail(true);
			pending.add(p);
		}
	}

	/**
	 * Create blood effect when a bullet hits a server.character.
	 * 
	 * @param x
	 *            The x coordinate of the animation.
	 * @param y
	 *            The y coordinate of the animation.
	 */
	public void addPersistentBloodAnimation(double x, double y, double direction, int team) {
		for (int i = 0; i < 5; i++) {
			double randomDirection = direction + (Math.PI/2) * Utils.random().nextGaussian()/2;
			double d = 1-0.5*Math.abs(Geometry.wrapAngle(randomDirection - direction))/(Math.PI/2);
			BloodAnimation p = new BloodAnimation(x, y, randomDirection, 0.15*d, Renderer.teamColors[team],500, 0);
			pending.add(p);
		}
	}

	public void addVisualBloodAnimation(double x, double y, double direction, int team) {
		for (int i = 0; i < 10; i++) {
			double randomDirection = direction + (Math.PI) * Utils.random().nextGaussian()/2;
			double speed = 0.12*Math.pow((1-0.9*Math.abs(Geometry.wrapAngle(randomDirection - direction))/(Math.PI*0.9)),2);
			ParticleAnimation p = new ParticleAnimation(700);
			p.setTrail(true);
			p.setLoc(x, y, 0.5);
			p.setAcc(0, 0, -0.004);
			p.setGroundInteraction(GroundInteraction.Stop);
			p.setSize(0.25);
			Color c = Renderer.teamColors2[team].brighter();
			p.setColor(new Color(c.getRed(),c.getGreen(),c.getBlue(),0xa0));
			p.setRotationSpeed(0.02);
			p.setDirection(randomDirection, speed);
			
			pending.add(p);
		}
	}

	
	
	/**
	 * Create particle effect when a bullet hits the wall.
	 * 
	 * @param x
	 *            The x coordinate of the animation.
	 * @param y
	 *            The y coordinate of the animation.
	 */
	public void addBulletWallAnimation(double x, double y) {
		int n = 8;
		for (int i = 0; i < n; i++) {
			//double randomDirection = Math.PI*2*Utils.random().nextDouble();
			double randomDirection = Math.PI*2.0*i/(n-1);
			ParticleAnimation p = new ParticleAnimation(x, y, randomDirection, 0.01, 0.1, 100, Color.WHITE);
			p.setTrail(true);
			p.setVel(0, 0, -0.01);
			//p.setGrowth(-0.0006);
			pending.add(p);
		}
	}

	public void addVisualNoiseAnimation(double x, double y, double volume) {
		int n = 4+(int)(volume/30);
		double offset = Utils.random().nextDouble()*Math.PI*2/n;
		for (int i = 0; i < n; i++) {
			//double randomDirection = Math.PI*2*Utils.random().nextDouble();
			double direction = offset + Math.PI*2.0*i/n;
			ParticleAnimation p = new ParticleAnimation((long)(500+volume*10));
			p.setTrail(true);
			p.setVel(0, 0, -0.08);
			p.setLoc(x, y, 1);
			p.setDirection(direction, 0.05+volume*0.001);
			p.setColor(new Color(0x3fffffff,true));
			p.setSize(0.12);
			p.setGroundInteraction(GroundInteraction.Stop);
			pending.add(p);
		}
	}
	
	/**
	 * Create a trailing effect for a projectile (bullet, grenade, etc.).
	 */
	public void addProjectileTrail(double x, double y, double direction, double speed, double size) {
		if (size<50)
			addCustomAnimation(new LineAnimation(70,x,y,direction,speed,Math.min(0.05,size/500)));
		else {
			addCustomAnimation(new LineAnimation(70,x,y,direction,speed,size/3000));
		}
	}
	
	public void addAnimation(AnimationEvent e) {
		switch(e.id) {
			case AnimationEvent.GUNSHOT:
				addShotAnimation(e.x,e.y,e.direction);
				break;
			case AnimationEvent.BULLETWALL:
				addBulletWallAnimation(e.x,e.y);
				break;
			case AnimationEvent.ENEMYMARK:
				addCustomAnimation(new CircleAnimation(e.x,e.y,2,1000, 0,Color.RED));
				break;
			case AnimationEvent.PING_ANIMATION_ID:
				addCustomAnimation(new CircleAnimation(e.x,e.y,1.5,1000, 0,Color.RED));
				break;
			case AnimationEvent.BLOOD:
				addVisualBloodAnimation(e.x,e.y,e.direction,e.team);
				break;
			default:
				System.err.println("UNKNOWN ANIMATION RECEIVED");
				break;
		}
	}
	
	/**
	 * Avoid using this method; written just for testing. Always use pre-defined add animation
	 * methods, or write a new one if what you want doesn't exist yet.
	 */
	public void addCustomAnimation(BasicAnimation animation) {
		pending.add(animation);
	}

	/**
	 * Update the animation system.
	 */
	public void update() {
		animations.addAll(pending);
		pending.clear();
		
		List<BasicAnimation> removed = new LinkedList<BasicAnimation>();
		for (BasicAnimation a : animations) {
			a.update(this);
			if (a.isExpired())
				removed.add(a);
		}
		animations.removeAll(removed);
	}

	/**
	 * Render it on the graphics object.
	 * 
	 * @param g The graphics object to be rendered to.
	 */
	public void render(Graphics2D g2D, Camera camera, Shape clip) {
		for (BasicAnimation a : animations) {
			if (a instanceof ParticleAnimation) {
				ParticleAnimation pa = (ParticleAnimation) a;
				double zRatio = pa.getLoc().z / (camera.getZ()-pa.getLoc().z);
				double offset = zRatio * Point2D.distance(pa.getLoc().x, pa.getLoc().y, camera.getX(), camera.getY());
				Vector2D v = new Vector2D(pa.getLoc().x-camera.getX(),pa.getLoc().y-camera.getY());
				v.setMagnitude(offset);
				if (clip==null || clip.contains(pa.getLoc().x+v.x, pa.getLoc().y+v.y)) {
					g2D.translate(v.x*Renderer.getPPM(), v.y*Renderer.getPPM());
					a.render(g2D);
					g2D.translate(-v.x*Renderer.getPPM(), -v.y*Renderer.getPPM());
				}
			} else {
				a.render(g2D);
			}
		}
	}
	
}
