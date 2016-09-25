package character;

import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;

import core.Geometry;
import core.Projectile;
import core.Utils;

public class Armor {
	private double startAngle;
	private double extend;
	//private double width;
	private Character self;
	
	public Armor (Character c, double start, double angle) {
		self = c;
		startAngle = start;
		extend = angle;
	}
	
	public void applyArmor(Projectile p, double oldX, double oldY) {
		if (checkCollision(p,oldX,oldY)) {
			applyEffect(p);
		}
	}
	
	protected boolean checkCollision (Projectile p,double oldX, double oldY) {
		Arc2D arc = new Arc2D.Double(Utils.getBoundingBox(self().getX(),self().getY(),self().getRadius()),
				Math.toDegrees(self().getDirection()+startAngle),Math.toDegrees(extend),Arc2D.CHORD);
		if (arc.contains(p.getX(), p.getY()))
			return true;
		Point2D i = Geometry.intersection(arc.getStartPoint(),arc.getEndPoint(),
				new Point2D.Double(p.getX(),p.getY()),new Point2D.Double(oldX,oldY));
		return i!=null;
	}
	
	protected void applyEffect (Projectile p) {
		p.setSpeed(0);
	}
	
	public double getStart() {
		return startAngle;
	}
	
	public double getAngle() {
		return extend;
	}
	
	public void setStart(double startAngle) {
		this.startAngle = startAngle;
	}
	
	public void setAngle(double angle) {
		this.extend = angle;
	}
	
	protected Character self() {
		return self;
	}
}
