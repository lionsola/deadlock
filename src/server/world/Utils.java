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
		return new Point2D.Double(tileCoord.x * Tile.tileSize + Tile.tileSize / 2, tileCoord.y * Tile.tileSize + Tile.tileSize / 2);
	}
	
	public static Random random() {
		return Utils.randomizer;
	}

	public static float getVolumeAtDistance(float originalSound, double distance) {
		// don't increase the volume pass the original volume volume
		// which is based on one-tile distance
		distance = Math.max(Tile.tileSize, distance);

		// in real life it's 20*Math.log10 ... , but because we can't simulate
		// volume loss when a client.sound goes through walls, we have to artificially
		// increase the volume loss over distance to make up for it
		return (float) (originalSound + 20 * Math.log10(Tile.tileSize / distance));
	}

	public static Rectangle2D getBoundingBox(double cx, double cy, double r) {
		return new Rectangle2D.Double(cx-r,cy-r,r*2,r*2);
	}
}
