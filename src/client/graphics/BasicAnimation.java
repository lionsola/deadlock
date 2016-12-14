package client.graphics;

import java.awt.Graphics2D;

/**
 * Base class of every animation in the game. All subclasses must implement render and usually also
 * override update.
 */
public abstract class BasicAnimation {

	protected final long duration;
	protected long life;
	protected long lastTick = 0;
	protected long delay;

	/**
	 * Constructor for BasicAnimation.
	 * @param life Duration of the animation, in millisecond.
	 */
	public BasicAnimation(long life) {
		this(life, 0);
	}

	/**
	 * Constructor for BasicAnimation.
	 * @param life Duration of the animation, in millisecond.
	 * @param delay The delay before the animation starts, in millisecond.
	 */
	public BasicAnimation(long life, long delay) {
		this.duration = life;
		this.life = life;
		this.delay = delay;
		this.lastTick = System.currentTimeMillis();
	}

	/**
	 * Manage the life circle of the animation.
	 * 
	 * @return
	 */
	public void update() {
		long elapsed = System.currentTimeMillis() - lastTick;
		if (delay > 0) {
			delay -= elapsed;
		} else {
			life -= elapsed;
		}
		lastTick = System.currentTimeMillis();
	}

	/**
	 * Render the animation to the client.graphics object.
	 * 
	 * @param g
	 *            The client.graphics object to be rendered into.
	 */
	abstract public void render(Graphics2D g2D);

	public boolean isExpired() {
		return life < 0;
	}
}
