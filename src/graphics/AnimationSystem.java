package graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Manage the animation effects and provide high-level methods to create new animations in the game
 * without having to deal with low-level animation classes.
 * 
 * @author Anh Pham
 * @author Shobitha Shivakumar
 */
public class AnimationSystem {
	
	private ConcurrentLinkedQueue<Animation> animations;

	/**
	 * Constructor. Create a new animation system.
	 */
	public AnimationSystem() {
		animations = new ConcurrentLinkedQueue<Animation>();
	}

	/**
	 * Create a noise animation.
	 * 
	 * @param x
	 *            The x coordinate of the noise.
	 * @param y
	 *            The y coordinate of the noise.
	 * @param noise
	 *            The loudness of the noise.
	 */
	public void addNoiseAnimation(double x, double y, float noise) {
		long duration = 1000 + (int) noise * 5;
		animations.add(new ExpandingCircleAnimation(x, y, noise / 24, duration, 0));
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
		for (int i = 0; i < 6; i++) {
			double randomDirection = direction + 2 * core.Utils.random().nextGaussian() / 5;
			ParticleAnimation p = new ParticleAnimation(x, y, randomDirection, 0.1, 0.5, 300, Color.WHITE);
			p.setGrowth(-0.4, -0.4);
			p.setSizeDefault(true);
			animations.add(p);
		}
	}

	/**
	 * Create blood effect when a bullet hits a character.
	 * 
	 * @param x
	 *            The x coordinate of the animation.
	 * @param y
	 *            The y coordinate of the animation.
	 */
	public void addBloodAnimation(double x, double y) {
		for (int i = 0; i < 5; i++) {
			double randomDirection = core.Utils.random().nextDouble() * Math.PI * 2;
			ParticleAnimation p = new ParticleAnimation(x, y, randomDirection, 0.1, 1, 500, Color.RED);
			p.setGrowth(-0.5, -0.5);
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
			double randomDirection = i * Math.PI * 2 / n + core.Utils.random().nextGaussian() / 5;
			ParticleAnimation p = new ParticleAnimation(x, y, randomDirection, 0.1,0.5, 500, Color.WHITE);
			p.setGrowth(-0.35, -0.35);
			p.setSizeDefault(true);
			animations.add(p);
		}
	}

	/**
	 * Avoid using this method; written just for testing. Always use pre-defined add animation
	 * methods, or write a new one if what you want doesn't exist yet.
	 */
	public void addCustomAnimation(Animation animation) {
		animations.add(animation);
	}

	/**
	 * Update the animation system.
	 */
	public void update() {
		List<Animation> removed = new LinkedList<Animation>();
		for (Animation a : animations) {
			if (a.update())
				removed.add(a);
		}
		animations.removeAll(removed);
	}

	/**
	 * Render it on the graphics object.
	 * 
	 * @param g
	 *            The graphics object to be rendered to.
	 */
	public void render(Graphics g) {
		for (Animation a : animations) {
			a.render(g);
		}
	}
	
}
