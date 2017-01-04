package server.ai;

import java.awt.geom.Point2D;

public class InterestPoint {
	public enum Type {RANDOM(0), PATROL(0), PING(1),ENEMY(2);
		
		public final int priority;
		Type(int priority) {
			this.priority = priority;
		}
	};
	private Type type;
	private Point2D location;
	private long time;
	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(Type type) {
		this.type = type;
	}
	/**
	 * @return the location
	 */
	public Point2D getLocation() {
		return location;
	}
	/**
	 * @param location the location to set
	 */
	public void setLocation(Point2D location) {
		this.location = location;
	}
	/**
	 * @return the time
	 */
	public long getTime() {
		return time;
	}
	
	/**
	 * @param time the time to set
	 */
	public void setTime(long time) {
		this.time = time;
	}
	
}
