package client.graphics;

import java.awt.Color;
import java.awt.Graphics2D;

import client.gui.GameWindow;
import server.world.Terrain;
import shared.core.Vector2D;

public class BloodAnimation extends BasicAnimation {
	private static final long serialVersionUID = 2847226461847442702L;

	static final float BASE_ALPHA = 1.5f;
	
	private Color color;
	private Vector2D loc;
	private Vector2D vel;
	private double carryAlpha;
	
	public BloodAnimation(double x, double y, double direction, double speed, Color color, long life, long delay) {
		super(life, delay);
		this.color = color;
		this.loc = new Vector2D(x, y);
		double dx = Math.cos(direction) * speed;
		double dy = -Math.sin(direction) * speed;
		this.vel = new Vector2D(dx, dy);
	}
	
	@Override
	public void update(AnimationSystem as) {
		super.update(as);
		loc.add(vel);
	}

	@Override
	public void render(Graphics2D g2D) {
		double ts = Terrain.tileSize;
		/*
		double alpha = Math.min(255,Math.max(0,1.0*BASE_ALPHA*life/duration));
		if (alpha + carryAlpha>1) {
			g2D.setColor(new Color(color.getRed(),color.getGreen(),color.getBlue(),(int)(alpha+carryAlpha)));
			Renderer.fillRect(g2D, tx*ts, ty*ts, ts, ts);
		}
		carryAlpha = (alpha + carryAlpha)%1;
		*/
		g2D.setColor(color);
		Renderer.fillRect(g2D, loc.x, loc.y, 0.2, 0.2);
	}
}
