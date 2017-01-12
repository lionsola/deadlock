package client.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import shared.core.Vector2D;

/**
 * Draw a line for the specified time.
 */
public class LineAnimation extends BasicAnimation {
	private Vector2D dest;
	private Vector2D source;
	private double size;
	private Color color;
	private transient int alpha;
	
	public LineAnimation(long life, double x1, double y1, double x2, double y2, double size, Color color) {
		super(life);
		this.color = color;
		this.dest = new Vector2D(x1,y1);
		this.source = new Vector2D(x2,y2);
		this.size = size;
	}
	
	public LineAnimation(long life, double x1, double y1, double x2, double y2, double size) {
		this(life, x1, y1, x2, y2, size, Color.WHITE);
	}
	
	@Override
	public void update(AnimationSystem as) {
		super.update(as);
		alpha = (int)Math.max(0,(color.getAlpha()*life/duration));
	}
	
	@Override
	public void render(Graphics2D g2D) {
		if (life>0) {
			//double rat = (1.0*life/duration);
			//Renderer.drawLine(g2D, dest.x, dest.y, dest.x+(source.x-dest.x)*rat, dest.y+(source.y-dest.y)*rat);
			g2D.setStroke(new BasicStroke((float)(Renderer.getPPM()*size),BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND));
			//int rgb = (int)((255.0 * life)/duration);
			//g2D.setColor(new Color(rgb,rgb,rgb));
			try {
				g2D.setColor(new Color(color.getRed(),color.getGreen(),color.getBlue(),alpha));
				Renderer.drawLine(g2D, dest.x, dest.y, source.x, source.y);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
