package core;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

/**
 * Contains the logic regarding geometry.
 * 
 * @author Anh Pham
 *
 */
public class Geometry {
    private static final double LINE_SAMPLE_THRESHOLD = 4;
    
	public static Point2D intersection(Line2D l1, Line2D l2) {
		Point2D p1 = l1.getP1();
		Point2D p2 = l1.getP2();
		Point2D p3 = l2.getP1();
		Point2D p4 = l2.getP2();

		// From http://paulbourke.net/geometry/lineline2d/
		double d = (p4.getY() - p3.getY()) * (p2.getX() - p1.getX())
				 - (p4.getX() - p3.getX()) * (p2.getY() - p1.getY());
		if (d == 0) {
			return null;
		}

		double s = ((p4.getX() - p3.getX()) * (p1.getY() - p3.getY())
				  - (p4.getY() - p3.getY()) * (p1.getX() - p3.getX())) / d;
		if (s <= 0 || s > 1) {
			return null;
		}

		Point2D p = new Point2D.Double(p1.getX() + s * (p2.getX() - p1.getX()),
				p1.getY() + s * (p2.getY() - p1.getY()));

		if (l1.intersectsLine(l2)) {
			return p;
		} else {
			return null;
		}
	}
	
	public static double wrapAngle(double angle) {
		double pi2 = Math.PI*2;
		angle = angle % pi2; 

		// force it to be the positive remainder, so that 0 <= angle < 360  
		angle = (angle + pi2) % pi2;  

		// force into the minimum absolute value residue class, so that -180 < angle <= 180  
		if (angle > Math.PI)  
		    angle -= pi2;  
		return angle;
	}
	
	// Returns the list of points from (x0, y0) to (x1, y1), with default distance threshold
	public static List<Point2D> getLineSamples(double x0, double y0, double x1, double y1) {
		return getLineSamples(x0,y0,x1,y1,LINE_SAMPLE_THRESHOLD);
	}
		
	public static List<Point2D> getLineSamples(double x0, double y0, double x1, double y1, double threshold) {
	    List<Point2D> result = new LinkedList<Point2D>();
	    double smallestDistance = Point.distance(x0, y0, x1, y1);
	    int fragments = 1;
	    while (smallestDistance>threshold) {
	        fragments*=2;
	        smallestDistance/=2;
	    }
	    double dx = (x1-x0)/fragments;
	    double dy = (y1-y0)/fragments;
	    for (int f=0;f<=fragments;f++) {
	        result.add(new Point2D.Double(x0+dx*f,y0+dy*f));
	    }
	    return result;
	}

	public static boolean checkLineWithinAngle(Line2D l, Point2D origin, double dir, double angle) {
		return checkPointWithinAngle(l.getP1(),origin,dir,angle) || checkPointWithinAngle(l.getP2(),origin,dir,angle);
	}
	
	public static boolean checkPointWithinAngle(Point2D p, Point2D origin, double dir, double angle) {
        double pointDir = Math.atan2(-(p.getY()-origin.getY()),p.getX()-origin.getX());
        return Math.abs(Geometry.wrapAngle(pointDir - dir))<angle/2;
    }
	
	public static Point2D PolarToCartesian(double magnitude, double direction) {
		return new Point2D.Double(magnitude*Math.cos(direction),magnitude*Math.sin(direction));
	}
	
    public static float diagonalDistance(Point2D p1, Point2D p2) {
        return diagonalDistance(p1.getX(),p1.getY(),p2.getX(),p2.getY());
    }
    
    static boolean areClockwise(Point2D v1, Point2D v2) {
    	  return -v1.getX()*v2.getY() + v1.getY()*v2.getX() > 0;
	}
    
    public static float diagonalDistance(double x1, double y1, double x2, double y2) {
        float xdiff = (float) Math.abs(x1-x2);
        float ydiff = (float) Math.abs(y1-y2);
        float straightDist = Math.abs(xdiff-ydiff);
        float diagonalDist = 1.4f*(Math.max(xdiff,ydiff)-straightDist); 
        return straightDist + diagonalDist;
    }
}
