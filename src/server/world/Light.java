package server.world;

import java.awt.geom.Point2D;
import java.io.Serializable;

public class Light implements Serializable {
	private static final long serialVersionUID = 1755777083887764779L;
	
	private transient boolean active;
	private int range;
	private int color;
	private double rx;
	private double ry;
	
	public Light(double x, double y, int color, int range) {
		this.rx = x;
		this.ry = y;
		this.color = color;
		this.range = range;
	}
	
	public Light(Point2D location, int color, int range) {
		this.rx = location.getX();
		this.ry = location.getY();
		this.color = color;
		this.range = range;
	}
	
	public Light(int color, int range) {
		this.color = color;
		this.range = range;
	}

	public double getX() {return rx;}
	
	public double getY() {return ry;}
	
	public int getRange() {return range;}
	
	public int getColor() {return color;}
	
	public void setX(double x) {
		this.rx = x;
	}
	
	public void setY(double y) {
		this.ry = y;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}
	
	public boolean isActive() {
		return active;
	}
}
