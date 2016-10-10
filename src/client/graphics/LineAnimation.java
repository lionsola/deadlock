package client.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import shared.core.Vector2D;

public class LineAnimation extends BasicAnimation {
	private Vector2D dest;
	private Vector2D source;
	private double size;
	
	public LineAnimation(long life, double dx, double dy, double prevX, double prevY, double size) {
		super(life);
		this.dest = new Vector2D(dx,dy);
		this.source = new Vector2D(prevX,prevY);
		this.size = size;
	}

	@Override
	public void render(Graphics2D g2D) {
		//double rat = (1.0*life/duration);
		//Renderer.drawLine(g2D, dest.x, dest.y, dest.x+(source.x-dest.x)*rat, dest.y+(source.y-dest.y)*rat);
		g2D.setStroke(new BasicStroke((float)Math.max(1,Renderer.ppm*size/1000)));
		int rgb = (int)((255.0 * life)/duration); 
		g2D.setColor(new Color(rgb,rgb,rgb));
		Renderer.drawLine(g2D, dest.x, dest.y, source.x, source.y);
	}

}
