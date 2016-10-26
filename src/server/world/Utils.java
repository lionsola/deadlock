package server.world;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
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
		return new Point2D.Double(tileCoord.x * Terrain.tileSize + Terrain.tileSize / 2, tileCoord.y * Terrain.tileSize + Terrain.tileSize / 2);
	}
	
	public static Random random() {
		return Utils.randomizer;
	}

	public static double getVolumeAtDistance(double volume, double distance, double hearF) {
		double reducedByDistance = Math.max(0,1-hearF) * Sound.DISTANCE_VOLUME_DROP_RATE * distance;
		return (volume - reducedByDistance);
	}

	public static Rectangle2D getBoundingBox(double cx, double cy, double r) {
		return new Rectangle2D.Double(cx-r,cy-r,r*2,r*2);
	}
}
