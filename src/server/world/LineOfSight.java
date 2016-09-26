package server.world;

import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import client.graphics.Renderer;
import network.FullCharacterData;
import server.character.Character;

/**
 * Used for the logic behind our line of sight.
 * 
 * @author Anh Pham
 */
public class LineOfSight {
	
	private int lastX = Integer.MAX_VALUE;
	private int lastY = Integer.MAX_VALUE;
	private int lastRange = Integer.MAX_VALUE;

	//private static final Node DEAD = new Node();
	private List<Node> edges = null;
	private Rectangle2D bb = null;

	/**
	 * Generates Line of Sight for the Character.
	 * 
	 * @param p
	 *            represents the data pertaining to the particular Character.
	 * @param a
	 *            represents the Arena where the fight is taking place.
	 * @return Area an area representing the Line of Sight.
	 */
	public Area generateLoS(final FullCharacterData p, final Arena a) {
		return genLOSAreaPixel(p.x, p.y, p.viewRange, p.viewAngle, p.direction, a);
	}

	/**
	 * Generates Line of Sight for the Character.
	 * 
	 * @param px
	 *            x coordinate of the position of the source
	 * @param py
	 *            y coordinate of the position of the source
	 * @param effectRange
	 *            view range of the Line of Sight
	 * @param viewAngle
	 *            view angle of the Line of Sight
	 * @param direction
	 *            direction of the Line of Sight
	 * @param a
	 *            represents the Arena where the fight is taking place.
	 * @return Area an area representing the Line of Sight
	 */
	public Area genLOSAreaMeter(final double px, final double py,
			double viewRange, double viewAngle, double dir, final Arena a) {
		List<Point2D> points = getLOSPoints(px,py,viewRange,viewAngle,dir,a);
		
		GeneralPath losBoxy = new GeneralPath();
		
		losBoxy.moveTo(points.get(0).getX(),points.get(0).getY());
		for (Point2D p:points) {
			losBoxy.lineTo(p.getX(),p.getY());
		}
		
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
		double ts = Tile.tileSize;
		int range = (int)(viewRange / ts + 0.5);
		int pX = (int)(px / ts);
		int pY = (int)(py / ts);

		int x1 = Math.max(0, pX - range);
		int y1 = Math.max(0, pY - range);
		int x2 = Math.min(a.getWidth() - 1, pX + range);
		int y2 = Math.min(a.getHeight() - 1, pY + range);

		if (lastX != pX || lastY != pY || range!=lastRange) {
			edges = new LinkedList<Node>();
			bb = new Rectangle2D.Double((x1 - 1) * ts, (y1 - 1) * ts, (x2 - x1 + 3) * ts, (y2 - y1 + 3) * ts);
			lastX = pX;
			lastY = pY;
			lastRange = range;
			
			double xa, ya, xb, yb;

			for (int x = x1; x <= x2; x++) {
				for (int y = y1; y <= y2; y++) {
					if (!a.get(x, y).isTransparent()) {
						// Horizontal Edges
						// above player & below that is an empty tile
						if (y < pY && a.get(x, y + 1).isTransparent()) {
							xa = x * ts;
							ya = (y + 1) * ts;
							xb = xa + ts;
							yb = ya;
							if ((bb.contains(xa, ya) || bb.contains(xb, yb))) {
									edges.add(new Node(xa, ya, xb, yb));
							}
						}

						// below player & above that is an empty tile
						else if (y > pY && a.get(x, y - 1).isTransparent()) {
							xa = (x + 1) * ts;
							ya = y * ts;
							xb = xa - ts;
							yb = ya;
							if (bb.contains(xa, ya) || bb.contains(xb, yb))
								edges.add(new Node(xa, ya, xb, yb));
						}

						// vertical edges
						// to the left of player && to the right of that is an
						// empty tile
						if (x < pX && a.get(x + 1, y).isTransparent()) {
							xa = (x + 1) * ts;
							ya = (y + 1) * ts;
							xb = xa;
							yb = ya - ts;
							if (bb.contains(xa, ya) || bb.contains(xb, yb))
								edges.add(new Node(xa, ya, xb, yb));
						}

						// to the right of player && to the left of that is an
						// empty tile
						if (x > pX && a.get(x - 1, y).isTransparent()) {
							xa = x * ts;
							ya = y * ts;
							xb = xa;
							yb = ya + ts;
							if (bb.contains(xa, ya) || bb.contains(xb, yb))
								edges.add(new Node(xa, ya, xb, yb));
						}
					}
					// System.out.println("processing edge for tile "+x+","+y);
				}
			}

			// add border bound
			for (int l = 0; l < x2 - x1 + 3; l++) {
				// top border
				edges.add(new Node((x1 - 1 + l) * ts, (y1 - 1) * ts, (x1 + l) * ts, (y1 - 1) * ts));

				// bottom border
				edges.add(new Node((x2 + 2 - l) * ts, (y2 + 2) * ts, (x2 + 1 - l) * ts, (y2 + 2) * ts));
			}

			// add border bound
			for (int l = 0; l < y2 - y1 + 3; l++) {
				// right border
				edges.add(new Node((x2 + 2) * ts, (y1 - 1 + l) * ts, (x2 + 2) * ts, (y1 + l) * ts));

				// left border
				edges.add(new Node((x1 - 1) * ts, (y2 + 2 - l) * ts, (x1 - 1) * ts, (y2 + 1 - l) * ts));
			}

			Collections.sort(edges, new Comparator<Node>() {
				@Override
				public int compare(Node arg0, Node arg1) {
					double d1 = arg0.edge.ptSegDist(px, py);
					double d2 = arg1.edge.ptSegDist(px, py);
					return Double.compare(d1, d2);
				}
			});
		}

		// copying edges
		List<Node> pedges = new LinkedList<Node>();
		List<Node> nedges = new LinkedList<Node>();

		for (Node nx : edges) {
			Node n = new Node(nx.edge.getX1(), nx.edge.getY1(), nx.edge.getX2(), nx.edge.getY2());
			nedges.add(n);
		}

		// linking
		for (Node n1 : nedges) {
			for (Node n2 : nedges) {
				if (n1.edge.getP2().equals(n2.edge.getP1())) {
					n1.next = n2;
					n2.prev = n1;
					break;
				}
			}
		}
		
		// projecting
		Node start = nedges.get(0);
		
		while (!nedges.isEmpty()) {
			Node n = nedges.remove(0);
			
			if (n.next == null) {
				double vx = n.edge.getX2() - px;
				double vy = n.edge.getY2() - py;
				Point2D infEnd = new Point2D.Double();
				infEnd.setLocation(n.edge.getP2());

				while (bb.contains(infEnd)) {
					infEnd.setLocation(infEnd.getX() + vx, infEnd.getY() + vy);
				}
				assert ((int) infEnd.getX() != (int) n.edge.getX2() || (int) infEnd.getY() != (int) n.edge.getY2());

				// projection
				Line2D pj = new Line2D.Double(n.edge.getP2(), infEnd);
				for (Node n2 : nedges) {
					if (n2 != n) {
						Point2D intersect = Geometry.intersection(pj, n2.edge);
						if (intersect != null) {
							if (n2.prev != null) {
								Node repNode = new Node(n2.edge.getP1(),intersect);
								n2.prev.next = repNode;
								repNode.prev = n2.prev;
							}
							
							// n2.edge.getP1().setLocation(intersect);
							n2.edge.setLine(intersect, n2.edge.getP2());
							Node pjNode = new Node(n.edge.getX2(), n.edge.getY2(), intersect.getX(), intersect.getY());

							n.next = pjNode;
							pjNode.prev = n;
							
							pjNode.next = n2;
							n2.prev = pjNode;
							
							pjNode.isProjection = true;
							pedges.add(pjNode);
							break;
						}
					}
				}
			}

			if (n.prev == null) {
				double vx = n.edge.getX1() - px;
				double vy = n.edge.getY1() - py;
				Point2D infEnd = new Point2D.Double();
				infEnd.setLocation(n.edge.getP1());

				while (bb.contains(infEnd)) {
					infEnd.setLocation(infEnd.getX() + vx, infEnd.getY() + vy);
				}
				assert (infEnd.getX() != n.edge.getX2() || infEnd.getY() != n.edge.getY2());

				// projection
				Line2D pj = new Line2D.Double(infEnd, n.edge.getP1());
				for (Node n2 : nedges) {
					if (n2 != n) {
						Point2D intersect = Geometry.intersection(pj, n2.edge);
						if (intersect != null) {
							if (n2.next != null) {
								Node repNode = new Node(intersect,n2.edge.getP2());
								n2.next.prev = repNode;
								repNode.next = n2.next;
							}
							
							n2.edge.setLine(n2.edge.getP1(), intersect);
							Node pjNode = new Node(intersect.getX(), intersect.getY(), n.edge.getX1(), n.edge.getY1());
							n.prev = pjNode;
							pjNode.next = n;

							pjNode.prev = n2;
							pjNode.isProjection = true;

							n2.next = pjNode;
							pedges.add(pjNode);
							break;
						}
					}
				}
			}
		}
		
		List<Point2D> points = new LinkedList<Point2D>();
		points.add(new Point2D.Double(px,py));
		points.add(start.edge.getP1());
		points.add(start.edge.getP2());
		Node current = start.next;
		int i = 0;
		
		while (current != start && current != null && i < 1000) {
			points.add(current.edge.getP2());
			current = current.next;
			i++;
		}

		return points;
	}
	
	/*
	private Area addEdgeToShadow(Area a, Line2D e, Point2D p, double range) {
		Vector2D v = new Vector2D(e.getX1() - p.getX(),e.getY1() - p.getY());
		double rSqr = range*range;
		while (v.magSqr()<rSqr)
			v.mult(2);
		Vector2D v2 = new Vector2D(e.getX2() - p.getX(),e.getY2() - p.getY());
		while (v2.magSqr()<rSqr)
			v2.mult(2);
		Path2D path = new Path2D.Double();
		path.moveTo(p.getX(),p.getY());
		path.lineTo(p.getX()+v.x,p.getY()+v.y);
		path.lineTo(p.getX()+v2.x,p.getY()+v2.y);
		path.closePath();
		Area ret = new Area(path);
		ret.add(a);
		return ret;
	}
	*/
	
	/**
	 * Represents the edges of the Line of Sight on the map.
	 */
	public static class Node {
		boolean isProjection = false;
		Line2D edge;

		Node next;
		Node prev;

		public Node(double d, double e, double f, double g) {
			edge = new Line2D.Double(d, e, f, g);
			this.next = null;
			this.prev = null;
		}

		public Node() {}

		public Node(Point2D point1, Point2D point2) {
			this(point1.getX(),point1.getY(),point2.getX(),point2.getY());
		}
	}
	
}
