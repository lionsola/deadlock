package server.world;

import java.io.Serializable;

public class Light implements Serializable {
	private static final long serialVersionUID = 1755777083887764779L;
	
	private transient boolean active;
	private int x;
	private int y;
	private int range;
	private int color;
	
	public Light(int tx, int ty, int color, int range) {
		x = tx;
		y = ty;
		this.color = color;
		this.range = range;
	}

	public int getX() {return x;}
	
	public int getY() {return y;}
	
	public int getRange() {return range;}
	
	public int getColor() {return color;}
	
	public void setActive(boolean active) {
		this.active = active;
	}
	
	public boolean isActive() {
		return active;
	}
}
