package client.graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import server.world.Utils;
import shared.network.GameEvent.AnimationEvent;

/**
 * Manage the animation effects and provide high-level methods to create new animations in the game
 * without having to deal with low-level animation classes.
 */
public class AnimationSystem {
	//private ConcurrentLinkedQueue<ParticleEmitter> particleEmitters;
	private ConcurrentLinkedQueue<BasicAnimation> animations;
	public static final byte PING_ANIMATION_ID = 5;
	public static final byte BULLETTRAIL = 4;
	public static final byte ENEMYMARK = 3;
	public static final byte BULLETWALL = 2;
	public static final byte BLOOD = 1;
	public static final byte GUNSHOT = 0;

	/**
	 * Constructor. Create a new animation system.
	 */
	public AnimationSystem() {
		animations = new ConcurrentLinkedQueue<BasicAnimation>();
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
		animations.add(new ExpandingCircleAnimation(x, y, 0.5 + noise / 30, duration, 0));
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
		for (int i = 0; i < 5; i++) {
			double randomDirection = direction -(Math.PI/4) + (Math.PI/2)*i/4;
			ParticleAnimation p = new ParticleAnimation(x, y, randomDirection, 0.012, 0.12, 100, Color.WHITE);
			p.setGrowth(-0.0007, -0.0007);
			p.setSizeDefault(true);
			animations.add(p);
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
	public void addBloodAnimation(double x, double y, double direction) {
		for (int i = 0; i < 10; i++) {
			double randomDirection = direction + (Math.PI/2) * Utils.random().nextGaussian()/2;
			ParticleAnimation p = new ParticleAnimation(x, y, randomDirection, 0.005, 0.2, 1000, Color.RED);
			p.setGrowth(-0.0005, -0.0005);
			p.setSizeDefault(true);
			animations.add(p);
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
			p.setGrowth(-0.0006, -0.0006);
			p.setSizeDefault(true);
			animations.add(p);
		}
	}

	/**
	 * Create a trailing effect for a projectile (bullet, grenade, etc.).
	 */
	public void addProjectileTrail(double x, double y, double direction, double speed, double size) {
		if (size<50)
			addCustomAnimation(new LineAnimation(50,x,y,direction,speed,Math.min(0.05,size/500)));
		else {
			addCustomAnimation(new LineAnimation(50,x,y,direction,speed,size/3000));
		}
	}
	
	public void addAnimation(AnimationEvent e) {
		switch(e.id) {
			case AnimationSystem.GUNSHOT:
				addShotAnimation(e.x,e.y,e.direction);
				break;
			case AnimationSystem.BULLETWALL:
				addBulletWallAnimation(e.x,e.y);
				break;
			case AnimationSystem.BLOOD:
				addBloodAnimation(e.x,e.y,e.direction);
				break;
			case AnimationSystem.ENEMYMARK:
				addCustomAnimation(new ExpandingCircleAnimation(e.x,e.y,2,1000, 0,Color.RED));
				break;
			case AnimationSystem.PING_ANIMATION_ID:
				addCustomAnimation(new ExpandingCircleAnimation(e.x,e.y,1.5,1000, 0,Color.RED));
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
		animations.add(animation);
	}

	/**
	 * Update the animation system.
	 */
	public void update() {
		List<BasicAnimation> removed = new LinkedList<BasicAnimation>();
		for (BasicAnimation a : animations) {
			if (a.update())
				removed.add(a);
		}
		animations.removeAll(removed);
	}

	/**
	 * Render it on the graphics object.
	 * 
	 * @param g The graphics object to be rendered to.
	 */
	public void render(Graphics g) {
		for (BasicAnimation a : animations) {
			a.render((Graphics2D)g);
		}
	}
	
}
