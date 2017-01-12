package client.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import client.gui.GameWindow;

/**
 * Represent an animation of an circle expanding until it reaches the max size and stops.
 */
public class CircleAnimation extends BasicAnimation {
	private static final long serialVersionUID = -7037935330702338643L;
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
	public CircleAnimation(double x, double y, double d, long life, long delay) {
		this(x, y, d, life, delay, Renderer.DEFAULT_COLOR);
	}
	
	/**
	 * Create an expanding circle animation with a custom colour.
	 * 
	 * @param x
	 *            The x coordinate of the centre of the animation.
	 * @param y
	 *            The y coordinate of the centre of the animation.
	 * @param radius
	 *            The max size that the circle will reach.
	 * @param life
	 *            The duration of the animation.
	 * @param delay
	 *            The delay before the animation starts playing.
	 * @param color
	 *            The color of the animation.
	 */
	public CircleAnimation(double x, double y, double radius, long life, long delay, Color color) {
		super(life, delay);
		this.maxRadius = radius;
		this.x = x;
		this.y = y;
		this.startWidth = Math.max(0.3f,0.25f*maxRadius);
		this.width = startWidth;
		this.growth = maxRadius / (life * 3 / 4) * GameWindow.MS_PER_UPDATE;
		this.color = color;
	}
	
	@Override
	public void update(AnimationSystem as) {
		super.update(as);
		if (delay <= 0) {
			radius = Math.min(maxRadius, radius + growth);
			if (radius >= maxRadius * 0.8)
				growth = Math.max(0.02f, growth * 0.95f);

			width = (maxRadius-radius)/(maxRadius-startWidth)*startWidth;
		}
	}
	
	@Override
	public void render(Graphics2D g2D) {
		g2D.setColor(new Color(color.getRed(),color.getGreen(), color.getBlue(), (int)Math.max(0,255*this.life/this.duration)));
		g2D.setStroke(new BasicStroke(Math.max(0.1f,Renderer.toPixel(Math.min(radius, width)))));
		if (radius > 0)
			Renderer.drawCircle(g2D,x, y, radius);
	}
	
}
