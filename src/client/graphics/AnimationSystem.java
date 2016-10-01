package client.graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import network.GameEvent.AnimationEvent;

/**
 * Manage the animation effects and provide high-level methods to create new animations in the game
 * without having to deal with low-level animation classes.
 */
public class AnimationSystem {
	
	private ConcurrentLinkedQueue<BasicAnimation> animations;

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
			double randomDirection = direction + (Math.PI/6) * server.world.Utils.random().nextGaussian() / 2;
			ParticleAnimation p = new ParticleAnimation(x, y, randomDirection, 0.05, 0.15, 50, Color.WHITE);
			p.setGrowth(-0.002, -0.002);
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
		for (int i = 0; i < 5; i++) {
			double randomDirection = server.world.Utils.random().nextGaussian() * Math.PI * 2;
			ParticleAnimation p = new ParticleAnimation(x, y, randomDirection, 0.01, 0.2, 200, Color.RED);
			p.setGrowth(-0.001, -0.001);
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
			double randomDirection = i * Math.PI * 2 / n + server.world.Utils.random().nextGaussian() / 5;
			ParticleAnimation p = new ParticleAnimation(x, y, randomDirection, 0.1,0.5, 500, Color.WHITE);
			p.setGrowth(-0.35, -0.35);
			p.setSizeDefault(true);
			animations.add(p);
		}
	}

	public void addAnimation(AnimationEvent e) {
		switch(e.id) {
			case Animation.GUNSHOT:
				addShotAnimation(e.x,e.y,e.direction);
				break;
			case Animation.BLOOD:
				addBloodAnimation(e.x,e.y,e.direction);
				break;
			case Animation.ENEMYMARK:
				addCustomAnimation(new ExpandingCircleAnimation(e.x,e.y,2,500, 0,Color.RED));
			default:
				System.err.println("");
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
	 * Render it on the client.graphics object.
	 * 
	 * @param g
	 *            The client.graphics object to be rendered to.
	 */
	public void render(Graphics g) {
		for (BasicAnimation a : animations) {
			a.render(g);
		}
	}
	
}
