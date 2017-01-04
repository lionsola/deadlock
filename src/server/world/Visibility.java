package server.world;

import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import client.graphics.Renderer;
import shared.network.FullCharacterData;
import shared.network.Vision;

/** Calculate visible area from a position
 * Re-written in Java based on Haxe code from Red Blob Games
 */
class EndPoint extends Point2D.Float {
	private static final long serialVersionUID = -4666092594884161798L;
	public EndPoint(float x, float y) {
		super(x,y);
	}
	public boolean begin = false;
	public Segment segment = null;
	
	public float angle = 0;
	
	public float dx;
	public float dy;
	
	public boolean visualize = false;
}

class Segment {
	public EndPoint p1;
	public EndPoint p2;
	public float d;
}

/* 2d visibility algorithm, for demo
Usage:
   new Visibility()
Whenever map data changes:
   loadMap
Whenever light source changes:
   setLightLocation
To calculate the area:
   sweep
*/

public class Visibility {
	// Note: DLL is a doubly linked list but an array would be ok too
	// These represent the map and the light location:
	public List<Segment> mapSegments = new LinkedList<Segment>();
	public List<EndPoint> mapEndpoints = new LinkedList<EndPoint>();
	
	public List<Segment> segments = new LinkedList<Segment>();
	public List<EndPoint> endpoints = new LinkedList<EndPoint>();
	public Point2D.Float center = new Point2D.Float();

	// These are currently 'open' line segments, sorted so that the nearest
	// segment is first. It's used only during the sweep algorithm, and exposed
	// as a public field here so that the demo can display it.
	public List<Segment> open = new ArrayList<Segment>();

	// The output is a series of points that forms a visible area polygon
	public List<Point2D> output = new ArrayList<Point2D>();

 // For the demo, keep track of wall intersections
 // public var demo_intersectionsDetected:Array<Array<Point>>;
	
	public Visibility() {
		//loadWholeMap(arena);
	}

	public Area generateLoS(final FullCharacterData p, final Arena a) {
		return genLOSAreaPixel(p.x, p.y, p.viewRange, p.viewAngle, p.direction, a);
	}
	
	public Area generateLoS(final Vision v, final Arena a) {
		return genLOSAreaPixel(v.x, v.y, v.range, v.angle, v.direction, a);
	}
	
	public Area genLOSAreaMeter(final double px, final double py,
			double viewRange, double viewAngle, double dir, final Arena a) {
		List<Point2D> points = getLOSPoints(px,py,viewRange,viewAngle,dir,a);
		
		GeneralPath losBoxy = new GeneralPath();
		
		losBoxy.moveTo(points.get(0).getX(),points.get(0).getY());
		for (Point2D p:points) {
			losBoxy.lineTo(p.getX(),p.getY());
		}
		losBoxy.closePath();
		
		Area area = new Area(losBoxy);
		
		area.intersect(new Area(new Arc2D.Double(px - viewRange,py - viewRange, viewRange * 2, viewRange * 2,
				Math.toDegrees(dir-viewAngle/2), Math.toDegrees(viewAngle), Arc2D.PIE)));
		//System.out.println("calculated LoS in " + (System.nanoTime()-startTime) + " ns");
		
		return area;
	}
	
	public Area genLOSAreaPixel(final double px, final double py,
			double viewRange, double viewAngle, double dir, final Arena a) {
		List<Point2D> points = getLOSPoints(px,py,viewRange,viewAngle,dir,a);
		
		GeneralPath losBoxy = new GeneralPath();
		
		losBoxy.moveTo(Renderer.toPixel(points.get(0).getX()),Renderer.toPixel(points.get(0).getY()));
		for (Point2D p:points) {
			losBoxy.lineTo(Renderer.toPixel(p.getX()),Renderer.toPixel(p.getY()));
		}
		
		Area area = new Area(losBoxy);
		
		area.intersect(new Area(new Arc2D.Double(Renderer.toPixel(px - viewRange),Renderer.toPixel(py - viewRange),
				Renderer.toPixel(viewRange * 2),Renderer.toPixel(viewRange * 2),
				Math.toDegrees(dir-viewAngle/2), Math.toDegrees(viewAngle), Arc2D.PIE)));
		//System.out.println("calculated LoS in " + (System.nanoTime()-startTime) + " ns");
		
		return area;
	}
	
	public List<Point2D> getLOSPoints(final double px, final double py,
			double viewRange, double viewAngle, double dir, final Arena a) {
		//loadEdgeOfMap((float)px,(float)py,(float)viewRange);
		dir = -dir;
		loadMap(a,(float)px,(float)py,(float)viewRange);
		output = new ArrayList<Point2D>();
		output.add(new Point2D.Double(px, py));
		setLightLocation((float)px,(float)py,(float)(dir-viewAngle*0.5));
		sweep((float)viewAngle,(float)(dir-viewAngle/2));
		//setLightLocation((float)px,(float)py);
		//sweep(999.0f);
		return output;
	}
	
	public void loadMap(Arena a, float px, float py, float viewRange) {
	    segments.clear();
	    endpoints.clear();
	    
	    addSegment(px-viewRange,py-viewRange,px-viewRange,py+viewRange);
	    addSegment(px-viewRange,py+viewRange,px+viewRange,py+viewRange);
	    addSegment(px+viewRange,py+viewRange,px+viewRange,py-viewRange);
	    addSegment(px+viewRange,py-viewRange,px-viewRange,py-viewRange);
	    
	    float ts = (float) Terrain.tileSize;
		int range = (int)Math.ceil(viewRange / ts);
		int pX = (int)(px / ts);
		int pY = (int)(py / ts);

		int x1 = Math.max(0, pX - range);
		int y1 = Math.max(0, pY - range);
		int x2 = Math.min(a.getWidth() - 1, pX + range);
		int y2 = Math.min(a.getHeight() - 1, pY + range);
	    

	    final float WALL_DIST = (float)(0.2*ts);
	    Rectangle2D bb = new Rectangle2D.Double((x1 - 1) * ts, (y1 - 1) * ts, (x2 - x1 + 3) * ts, (y2 - y1 + 3) * ts);
	    for (int x = x1; x <= x2; x++) {
			for (int y = y1; y <= y2; y++) {
				if (!a.get(x, y).isClear()) {
					boolean addHSegment = true;
					// add horizontal segment					
					// above player & below that is an empty tile
					float yr=0;
					float aboveWallY = (y+1)*ts-WALL_DIST;
					float belowWallY = y*ts+WALL_DIST;
					if (aboveWallY < py && (a.get(x, y + 1).isClear() || (x==pX && y+1==pY))) {
						yr = aboveWallY;
					}
					// below player & above that is an empty tile
					else if (belowWallY > py && (a.get(x, y - 1).isClear() || (x==pX && y-1==pY))) {
						yr = y * ts + WALL_DIST;
					} else {
						addHSegment = false;
					}
					
					if (addHSegment) {
						float xa = x*ts;
						if (a.get(x-1,y).isClear()) {
							xa += WALL_DIST;
						} else {
							xa -= WALL_DIST;
						}
						float xb = (x+1)*ts;
						if (a.get(x+1,y).isClear()) {
							xb -= WALL_DIST;
						} else {
							xb += WALL_DIST;
						}
						
						if (bb.contains(xa, yr) || bb.contains(xb, yr))
							addSegment(xa, yr, xb, yr);
					}
					boolean addVSegment = true;
					// vertical edges
					// to the left of player && to the right of that is an empty tile
					float xr=0;
					float leftWallX = (x+1)*ts - WALL_DIST;
					float rightWallX = x*ts + WALL_DIST;
					if (leftWallX < px && (a.get(x + 1, y).isClear() || (x+1==pX && y==pY))) {
						xr = (x+1)*ts-WALL_DIST;
					}
					// to the right of player && to the left of that is an empty tile
					else if (rightWallX > px && (a.get(x - 1, y).isClear() || (x-1==pX && y==pY))) {
						xr = x*ts+WALL_DIST;
					} else {
						addVSegment = false;
					}
					
					if (addVSegment) {
						float ya = y*ts;
						if (a.get(x,y-1).isClear()) {
							ya += WALL_DIST;
						} else {
							ya -= WALL_DIST;
						}
						float yb = (y+1)*ts;
						if (a.get(x,y+1).isClear()) {
							yb -= WALL_DIST;
						} else {
							yb += WALL_DIST;
						}
						
						if (bb.contains(xr, ya) || bb.contains(xr, yb))
							addSegment(xr, ya, xr, yb);
					}
				}
			}
		}
	}
	
	// Add a segment, where the first point shows up in the
	// visualization but the second one does not. (Every endpoint is
	// part of two segments, but we want to only show them once.)
	private void addSegment(float x1, float y1, float x2, float y2) {
	    Segment segment = new Segment();
	    EndPoint p1 = new EndPoint(x1,y1);
	    p1.segment = segment;
	    p1.visualize = true;
	    EndPoint p2 = new EndPoint(x2,y2);
	    p2.segment = segment;
	    p2.visualize = false;
	    
	    segment.p1 = p1;
	    segment.p2 = p2;
	    segment.d = 0.0f;
	    segments.add(segment);
	    endpoints.add(p1);
	    endpoints.add(p2);
	}
	
	// Set the light location. Segment and EndPoint data can't be
	// processed until the light location is known.
	public void setLightLocation(float x, float y, float startAngle) {
	    center.x = x;
	    center.y = y;
	    
	    for (Segment segment : segments) {
	        float dx = 0.5f * (segment.p1.x + segment.p2.x) - x;
	        float dy = 0.5f * (segment.p1.y + segment.p2.y) - y;
	        // NOTE: we only use this for comparison so we can use
	        // distance squared instead of distance. However in
	        // practice the sqrt is plenty fast and this doesn't
	        // really help in this situation.
	        segment.d = dx*dx + dy*dy;
	         // NOTE: future optimization: we could record the quadrant
	        // and the y/x or x/y ratio, and sort by (quadrant,
	        // ratio), instead of calling atan2. See
	        // <https://github.com/mikolalysenko/compare-slope> for a
	        // library that does this. Alternatively, calculate the
	        // angles and use bucket sort to get an O(N) sort.
	        segment.p1.angle = (float) Geometry.wrapAngle((Math.atan2(segment.p1.y - y, segment.p1.x - x) - startAngle));
	        if (segment.p1.angle<0) {segment.p1.angle+=Math.PI*2;}
	        segment.p2.angle = (float) Geometry.wrapAngle((Math.atan2(segment.p2.y - y, segment.p2.x - x) - startAngle));
	        if (segment.p2.angle<0) {segment.p2.angle+=Math.PI*2;}
	        float dAngle = (float) Geometry.wrapAngle(segment.p2.angle - segment.p1.angle);
	        //if (dAngle <= -Math.PI) { dAngle += 2*Math.PI; }
	        ///if (dAngle > Math.PI) { dAngle -= 2*Math.PI; }
	        segment.p1.begin = (dAngle > 0.0);
	        segment.p2.begin = !segment.p1.begin;
	    }
	}
	
	// Helper: leftOf(segment, point) returns true if point is "left"
	// of segment treated as a vector. Note that this assumes a 2D
	// coordinate system in which the Y axis grows downwards, which
	// matches common 2D graphics libraries, but is the opposite of
	// the usual convention from mathematics and in 3D graphics
	// libraries.
	static private boolean leftOf(Segment s, Point2D.Float p) {
	    // This is based on a 3d cross product, but we don't need to
	    // use z coordinate inputs (they're 0), and we only need the
	    // sign. If you're annoyed that cross product is only defined
	    // in 3d, see "outer product" in Geometric Algebra.
	    // <http://en.wikipedia.org/wiki/Geometric_algebra>
	    float cross = (s.p2.x - s.p1.x) * (p.y - s.p1.y)
	              - (s.p2.y - s.p1.y) * (p.x - s.p1.x);
	    return cross < 0;
	    // Also note that this is the naive version of the test and
	    // isn't numerically robust. See
	    // <https://github.com/mikolalysenko/robust-arithmetic> for a
	    // demo of how this fails when a point is very close to the
	    // line.
	}
	 // Return p*(1-f) + q*f
	static private Point2D.Float interpolate(Point2D.Float p, Point2D.Float q, float f) {
	    return new Point2D.Float(p.x*(1-f) + q.x*f, p.y*(1-f) + q.y*f);
	}
	
	// Helper: do we know that segment a is in front of b?
	// Implementation not anti-symmetric (that is to say,
	// _segment_in_front_of(a, b) != (!_segment_in_front_of(b, a)).
	// Also note that it only has to work in a restricted set of cases
	// in the visibility algorithm; I don't think it handles all
	// cases. See http://www.redblobgames.com/articles/visibility/segment-sorting.html
	private boolean _segment_in_front_of(Segment a, Segment b, Point2D.Float relativeTo) {
	    // NOTE: we slightly shorten the segments so that
	    // intersections of the endpoints (common) don't count as
	    // intersections in this algorithm
	    boolean A1 = leftOf(a, interpolate(b.p1, b.p2, 0.01f));
	    boolean A2 = leftOf(a, interpolate(b.p2, b.p1, 0.01f));
	    boolean A3 = leftOf(a, relativeTo);
	    boolean B1 = leftOf(b, interpolate(a.p1, a.p2, 0.01f));
	    boolean B2 = leftOf(b, interpolate(a.p2, a.p1, 0.01f));
	    boolean B3 = leftOf(b, relativeTo);
	     // NOTE: this algorithm is probably worthy of a short article
	    // but for now, draw it on paper to see how it works. Consider
	    // the line A1-A2. If both B1 and B2 are on one side and
	    // relativeTo is on the other side, then A is in between the
	    // viewer and B. We can do the same with B1-B2: if A1 and A2
	    // are on one side, and relativeTo is on the other side, then
	    // B is in between the viewer and A.
	    if (B1 == B2 && B2 != B3) return true;
	    if (A1 == A2 && A2 == A3) return true;
	    if (A1 == A2 && A2 != A3) return false;
	    if (B1 == B2 && B2 == B3) return false;
	    
	    // If A1 != A2 and B1 != B2 then we have an intersection.
	    // Expose it for the GUI to show a message. A more robust
	    // implementation would split segments at intersections so
	    // that part of the segment is in front and part is behind.
	    //demo_intersectionsDetected.push([a.p1, a.p2, b.p1, b.p2]);
	    return false;
	    // NOTE: previous implementation was a.d < b.d. That's simpler
	    // but trouble when the segments are of dissimilar sizes. If
	    // you're on a grid and the segments are similarly sized, then
	    // using distance will be a simpler and faster implementation.
	}
	
	// Run the algorithm, sweeping over all or part of the circle to find
	// the visible area, represented as a set of triangles
	public void sweep(float maxAngle, float oriAngle /*= 999.0f*/) {
	    //output = [];  // output set of triangles
	    //demo_intersectionsDetected = [];
	    Collections.sort(endpoints, endpointAngleComparator);
	    
	    float beginAngle = 0;
	    
	    open.clear();
	    // At the beginning of the sweep we want to know which
	    // segments are active. The simplest way to do this is to make
	    // a pass collecting the segments, and make another pass to
	    // both collect and process them. However it would be more
	    // efficient to go through all the segments, figure out which
	    // ones intersect the initial sweep line, and then sort them.
	    for (int pass=0;pass<=1;pass++) {
	        for (EndPoint p : endpoints) {
	            Segment current_old = open.isEmpty()? null : open.get(0);
	            
	            if (p.begin) {
	                // Insert into the right place in the list
	                int node = 0;
	                while (node<open.size() && _segment_in_front_of(p.segment, open.get(node), center)) {
	                    node += 1;
	                }
	                if (node >= open.size()) {
	                    open.add(p.segment);
	                } else {
	                    open.add(node, p.segment);
	                }
	            }
	            else {
	                open.remove(p.segment);
	            }
	            
	            Segment current_new = open.isEmpty()? null : open.get(0);
	            if (current_old != current_new) {
	                if (pass == 1) {
	                    addTriangle(beginAngle+oriAngle, p.angle+oriAngle, current_old);
	                }
	                beginAngle = p.angle;
	                if (pass == 1 && p.angle > maxAngle) {
		                // Early exit for the visualization to show the sweep process
		                break;
		            }
	            }
	        }
	    }
	}	
	
	public Point2D.Float lineIntersection(Point2D.Float p1, Point2D.Float p2,
			Point2D.Float p3, Point2D.Float p4) {
	    // From http://paulbourke.net/geometry/lineline2d/
	    float s = ((p4.x - p3.x) * (p1.y - p3.y) - (p4.y - p3.y) * (p1.x - p3.x))
	        / ((p4.y - p3.y) * (p2.x - p1.x) - (p4.x - p3.x) * (p2.y - p1.y));
	    return new Point2D.Float(p1.x + s * (p2.x - p1.x), p1.y + s * (p2.y - p1.y));
	}
	
	        
	private void addTriangle(float angle1, float angle2, Segment segment) {
	    Point2D.Float p1 = center;
	    Point2D.Float p2 = new Point2D.Float(center.x + (float)Math.cos(angle1),
	    		center.y + (float)Math.sin(angle1));
	    Point2D.Float p3 = new Point2D.Float();
	    Point2D.Float p4 = new Point2D.Float();
	    if (segment != null) {
	        // Stop the triangle at the intersecting segment
	        p3.x = segment.p1.x;
	        p3.y = segment.p1.y;
	        p4.x = segment.p2.x;
	        p4.y = segment.p2.y;
	    } else {
	        // Stop the triangle at a fixed distance; this probably is
	        // not what we want, but it never gets used in the demo
	        p3.x = (float) (center.x + Math.cos(angle1) * 500);
	        p3.y = (float) (center.y + Math.sin(angle1) * 500);
	        p4.x = (float) (center.x + Math.cos(angle2) * 500);
	        p4.y = (float) (center.y + Math.sin(angle2) * 500);
	    }
	
	    Point2D.Float pBegin = lineIntersection(p3, p4, p1, p2);
	    p2.x = (float) (center.x + Math.cos(angle2));
	    p2.y = (float) (center.y + Math.sin(angle2));
	    Point2D.Float pEnd = lineIntersection(p3, p4, p1, p2);
	    output.add(0, pBegin);
	    output.add(0, pEnd);
	}
	
	Comparator<EndPoint> endpointAngleComparator = new Comparator<EndPoint>() {
		@Override
		public int compare(EndPoint a, EndPoint b) {
			// Helper: comparison function for sorting points by angle
		    // Traverse in angle order
		    if (a.angle > b.angle) return 1;
		    if (a.angle < b.angle) return -1;
		    // But for ties (common), we want Begin nodes before End nodes
		    if (!a.begin && b.begin) return 1;
		    if (a.begin && !b.begin) return -1;
		    return 0;
		}
	};
}