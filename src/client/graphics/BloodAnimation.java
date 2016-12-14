package client.graphics;

import java.awt.Color;
import java.awt.Graphics2D;

import server.world.Terrain;
import shared.core.Vector2D;

public class BloodAnimation extends BasicAnimation {
	static final int BASE_ALPHA = 2;
	
	private Color color;
	private Vector2D loc;
	private Vector2D vel;
	
	public BloodAnimation(double x, double y, double direction, double speed, Color color, long life, long delay) {
		super(life, delay);
		this.color = color;
		this.loc = new Vector2D(x, y);
		double dx = Math.cos(direction) * speed;
		double dy = -Math.sin(direction) * speed;
		this.vel = new Vector2D(dx, dy);
	}
	
	@Override
	public void update() {
		super.update();
		loc.add(vel);
	}

	@Override
	public void render(Graphics2D g2D) {
		double ts = Terrain.tileSize;
		int tx = (int) (loc.x/ts);
		int ty = (int) (loc.y/ts);
		int alpha = (int) Math.min(255,Math.max(1,(BASE_ALPHA+1)*life/duration));;
		g2D.setColor(new Color(color.getRed(),color.getGreen(),color.getBlue(),alpha));;
		Renderer.fillRect(g2D, tx*ts, ty*ts, ts, ts);
	}
}
