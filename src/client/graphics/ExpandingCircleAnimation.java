package client.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import client.gui.GameWindow;

/**
 * Represent an animation of an circle expanding until it reaches the max size and stops.
 */
public class ExpandingCircleAnimation extends BasicAnimation {
	
	private double maxRadius;
	private double x;
	private double y;
	private double radius;
	private double startWidth;
	private double width;
	
	private double growth;
	private Color color;

	/**
	 * Create an expanding circle animation with the default colour (WHITE).
	 * 
	 * @param x
	 *            The x coordinate of the centre of the animation.
	 * @param y
	 *            The y coordinate of the centre of the animation.
	 * @param d
	 *            The max size that the circle will reach.
	 * @param life
	 *            The duration of the animation.
	 * @param delay
	 *            The delay before the animation starts playing.
	 */
	public ExpandingCircleAnimation(double x, double y, double d, long life, long delay) {
		this(x, y, d, life, delay, Renderer.DEFAULT_COLOR);
	}

	/**
	 * Create an expanding circle animation with a custom colour.
	 * 
	 * @param x
	 *            The x coordinate of the centre of the animation.
	 * @param y
	 *            The y coordinate of the centre of the animation.
	 * @param d
	 *            The max size that the circle will reach.
	 * @param life
	 *            The duration of the animation.
	 * @param delay
	 *            The delay before the animation starts playing.
	 * @param color
	 *            The color of the animation.
	 */
	public ExpandingCircleAnimation(double x, double y, double d, long life, long delay, Color color) {
		super(life, delay);
		this.maxRadius = d;
		this.x = x;
		this.y = y;
		this.startWidth = Math.max(0.3f,0.25f*maxRadius);
		double startGrowth = maxRadius / (life * 3 / 4) * GameWindow.MS_PER_UPDATE;
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
	public void render(Graphics2D g2D) {
		g2D.setColor(color);
		g2D.setStroke(new BasicStroke(Math.max(0.1f,Renderer.toPixel(Math.min(radius, width)))));
		if (radius > 0)
			Renderer.drawCircle(g2D,x, y, radius);
	}
	
}
