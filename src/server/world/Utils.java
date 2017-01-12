package server.world;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Random;
/**
 * Utility methods used in the server.world package.
 * @author Anh Pham
 */
public class Utils {
	
	private static final Random randomizer = new Random();

	/*
	public static Point tileToPixel(Point tileCoord) {
		return new Point(tileCoord.x * Tile.tileSize + Tile.tileSize / 2, tileCoord.y * Tile.tileSize + Tile.tileSize / 2);
	}
	*/

	public static Point2D tileToMeter(Point tileCoord) {
		return tileToMeter(tileCoord.x,tileCoord.y);
	}
	
	public static Point2D tileToMeter(int tx, int ty) {
		return new Point2D.Double((tx+0.5)*Terrain.tileSize, (ty+0.5)*Terrain.tileSize);
	}
	
	public static Random random() {
		return Utils.randomizer;
	}

	public static int meterToTile(double m) {
		return (int)(m/Terrain.tileSize);
	}
	
	public static double getVolumeAtDistance(double volume, double distance, double hearF) {
		double reducedByDistance = World.DISTANCE_VOLUME_DROP_RATE * distance / hearF;
		return (volume - reducedByDistance);
	}

	public static Rectangle2D getBoundingBox(double cx, double cy, double r) {
		return new Rectangle2D.Double(cx-r,cy-r,r*2,r*2);
	}
	
	public Point2D castSight(Arena a, Point2D start, Point2D end) {
		List<Point2D> points = Geometry.getLineSamples(start, end, 0.5);
		for (Point2D p:points) {
			if (!a.getTileAt(p.getX(), p.getY()).isClear()) {
				return p;
			}
		}
		return null;
	}
}
