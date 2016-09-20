package graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import game.Game;

/**
 * Represent an animation of an circle expanding until it reaches the max size and stops.
 * 
 * @author Anh Pham.
 */
public class ExpandingCircleAnimation extends Animation {
	
	private float maxRadius;
	private double x;
	private double y;
	private float radius;
	private float startWidth;
	private float width;
	
	private float growth;
	private Color color;

	/**
	 * Create an expanding circle animation with the default colour (WHITE).
	 * 
	 * @param x
	 *            The x coordinate of the centre of the animation.
	 * @param y
	 *            The y coordinate of the centre of the animation.
	 * @param finalRadius
	 *            The max size that the circle will reach.
	 * @param life
	 *            The duration of the animation.
	 * @param delay
	 *            The delay before the animation starts playing.
	 */
	public ExpandingCircleAnimation(double x, double y, float finalRadius, long life, long delay) {
		this(x, y, finalRadius, life, delay, Color.WHITE);
	}

	/**
	 * Create an expanding circle animation with a custom colour.
	 * 
	 * @param x
	 *            The x coordinate of the centre of the animation.
	 * @param y
	 *            The y coordinate of the centre of the animation.
	 * @param finalRadius
	 *            The max size that the circle will reach.
	 * @param life
	 *            The duration of the animation.
	 * @param delay
	 *            The delay before the animation starts playing.
	 * @param color
	 *            The color of the animation.
	 */
	public ExpandingCircleAnimation(double x, double y, float finalRadius, long life, long delay, Color color) {
		super(life, delay);
		this.maxRadius = finalRadius;
		this.x = x;
		this.y = y;
		this.startWidth = Math.max(0.4f,0.2f*maxRadius);
		float startGrowth = maxRadius / (life * 3 / 4) * Game.MS_PER_UPDATE;
		this.life = life;
		this.growth = startGrowth;
		this.color = color;
	}

	@Override
	public boolean update() {
		if (super.update())
			return true;
		if (delay <= 0) {
			radius = Math.min(maxRadius, radius + growth);
			if (radius >= maxRadius * 0.8)
				growth = Math.max(0.02f, growth * 0.95f);

			width = (maxRadius-radius)/(maxRadius-startWidth)*startWidth;
		}
		return false;
	}

	@Override
	public void render(Graphics g) {
		g.setColor(color);
		Graphics2D g2d = (Graphics2D)g;
		g2d.setStroke(new BasicStroke(Math.max(0.1f,Renderer.toPixel(Math.min(radius, width)))));
		if (radius > 0)
			Renderer.drawCircle(g2d,x, y, radius);
	}
	
}
