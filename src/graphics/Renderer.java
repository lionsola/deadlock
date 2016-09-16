package graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import network.FullCharacterData;
import character.CharacterFactory;
import core.Arena;
import core.Geometry;
import core.Tile;
import gui.GameScreen;

/**
 * Supposed to be the graphics "engine" of the game, this class provides all the rendering methods
 * needed to draw things on the gui.
 * 
 * @author Anh Pham
 * @author Madyan Al-Jazaeri
 */
public class Renderer {
	public static final int CURSOR_BMP_SIZE = 33;
	public static final int CURSOR_SIZE = 5;
	private static BufferedImage light;
	private static Color defaultColor = Color.WHITE;
	public static void init() {
		try {
			light = ImageIO.read(new FileInputStream("resource/light.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void renderMainCharacter(Graphics2D g2D, FullCharacterData player, int typeId) {
		// render the character
		renderCharacter(g2D, player.x, player.y, player.direction, player.radius, typeId, true);

		// render the health bar
		g2D.setStroke(new BasicStroke(3));
		g2D.setColor(new Color(255, 50, 50));
		int length = (int) (0.2*player.healthPoints/GameScreen.ppm);
		int topy = (int) (player.y - player.radius - 6);
		
		drawLine(g2D, player.x - length / 2, topy, player.x + length / 2, topy);
		// render the reload bar
		if (player.reloadPercent < 1) {
			topy += 3;
			g2D.setStroke(new BasicStroke(2));
			g2D.setColor(Color.WHITE);
			double x = player.x - player.radius;
			drawLine(g2D, x, topy, x + player.radius * 2 * player.reloadPercent, topy);
		}
	}

	public static void renderCharacter(Graphics2D g2D, double x, double y, double direction, double r, int typeId, boolean friendly) {
		g2D.setStroke(new BasicStroke(2));
		g2D.setColor(Color.BLACK);
		fillCircle(g2D,x, y,r);
		int color;
		if (friendly) {
			g2D.setColor(Color.GREEN);
			color = 1;
		} else {
			g2D.setColor(Color.RED);
			color = 0;
		}
		drawCircle(g2D,x,y,r);
		
		// draw gun
		Point2D p1 = Geometry.PolarToCartesian(r*0.7, direction);
		Point2D p2 = Geometry.PolarToCartesian(r*1.5, direction);
		drawLine(g2D,x+p1.getX(),y-p1.getY(),x+p2.getX(),y-p2.getY());
		
		int sqrt = (int) (r / Math.sqrt(2) - 1);
		drawImage(g2D,CharacterFactory.getImage(typeId, color), x - sqrt, y - sqrt, 2 * sqrt, 2 * sqrt);
		g2D.setStroke(new BasicStroke(1));
	}

	public static void renderProjectile(Graphics2D g2D, double px, double py,
			double direction, double speed) {
		g2D.setColor(Color.WHITE);
		g2D.setStroke(new BasicStroke(2));
		double dx = Math.cos(direction)*speed;
		double dy = -Math.sin(direction)*speed;
		drawLine(g2D, px, py,px - dx,py - dy);
	}

	public static void renderPowerUp(Graphics2D g2D, int x, int y, int type) {
		//g2D.drawImage(PowerUpFactory.getIcon(type), x, y, null);
	}

	/*
	public static void renderPowerUpRing(Graphics2D g2D, int x, int y, int radius, int type) {
		g2D.setColor(PowerUpFactory.COLORS[type]);
		int ringX = x - radius - 2;
		int ringY = y - radius - 2;
		int size = 2 * radius + 4;
		g2D.drawOval(ringX, ringY, size, size);
	}
	*/

	public static Cursor createCursor() {
		BufferedImage cursor = new BufferedImage(CURSOR_BMP_SIZE,CURSOR_BMP_SIZE, BufferedImage.TYPE_INT_ARGB);
		RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Graphics2D g = cursor.createGraphics();
		g.setRenderingHints(rh);
		g.fillOval((CURSOR_BMP_SIZE-CURSOR_SIZE)/2,(CURSOR_BMP_SIZE-CURSOR_SIZE)/2,CURSOR_SIZE, CURSOR_SIZE);
		Point center = new Point((cursor.getWidth()-1)/2,(cursor.getHeight()-1)/2);
		return Toolkit.getDefaultToolkit().createCustomCursor(cursor, center, "cursor");
	}

	public static void renderCrosshair(Graphics2D g2D, float cxFloat, float cyFloat, float crosshairSize_m) {
		int cx = toPixel(cxFloat), cy = toPixel(cyFloat);
		g2D.setColor(Color.WHITE);
		g2D.setStroke(new BasicStroke(2));
		int chS = (int)Math.ceil(toPixel(crosshairSize_m));
		if (chS%2!=0) {
			chS -= 1;
		}
		g2D.drawOval(Math.round(cx-chS),Math.round(cy-chS),
				Math.round(chS*2+1),Math.round(chS*2+1));
		g2D.drawLine(cx, cy+chS-5,cx, cy+chS+5);
		g2D.drawLine(cx, cy-chS-5,cx, cy-chS+5);
		g2D.drawLine(cx+chS-5, cy,cx+chS+5, cy);
		g2D.drawLine(cx-chS-5, cy,cx-chS+5, cy);
	}
	
	public static void renderDark(Graphics2D g2D, Arena a, Rectangle2D window) {
		double tileSize = Tile.tileSize;
		int x1 = Math.max(0, (int) (window.getX() / tileSize));
		int y1 = Math.max(0, (int) (window.getY() / tileSize));
		int x2 = Math.min(a.getWidth() - 1, x1 + (int) (window.getWidth() / tileSize) + 1);
		int y2 = Math.min(a.getHeight() - 1, y1 + (int) (window.getHeight() / tileSize) + 1);
		for (int x = x1; x <= x2; x++) {
			for (int y = y1; y <= y2; y++) {
				Image image = a.get(x, y).getImageDark();
				drawImage(g2D,image, x * tileSize,y * tileSize,tileSize,tileSize);
			}
		}
	}
	
	public static void render(Graphics2D g2D, Arena a, Rectangle2D window) {
		double tileSize = Tile.tileSize;
		int x1 = Math.max(0, (int) (window.getX() / tileSize));
		int y1 = Math.max(0, (int) (window.getY() / tileSize));
		int x2 = Math.min(a.getWidth() - 1, x1 + (int) (window.getWidth() / tileSize) + 1);
		int y2 = Math.min(a.getHeight() - 1, y1 + (int) (window.getHeight() / tileSize) + 1);
		for (int x = x1; x <= x2; x++) {
			for (int y = y1; y <= y2; y++) {
				Image image = a.get(x, y).getImage();
				drawImage(g2D,image,x * tileSize,y * tileSize,tileSize,tileSize);
			}
		}
		Shape currentClip = g2D.getClip();
		Area tempClip = new Area(currentClip);
		//tempClip.intersect(arena.getLightMap());
		g2D.setClip(tempClip);

		for (int x = x1; x <= x2; x++) {
			for (int y = y1; y <= y2; y++) {
				Image image = a.get(x, y).getImageLight();
				drawImage(g2D,image, x * tileSize, y * tileSize, tileSize, tileSize);
			}
		}
		for (Point l : a.getLightList()) {
			int x0 = l.x - Arena.LIGHT_RANGE;
			int y0 = l.y - Arena.LIGHT_RANGE;
			int d = Arena.LIGHT_RANGE * 2;
			if (window.intersects(x0, y0, d, d)) {
				g2D.drawImage(light, x0, y0, d, d, null);
			}
		}
		g2D.setClip(currentClip);
	}
	
	static void drawLine(Graphics2D g2d, double x1, double y1, double x2, double y2, float strokeWidth) {
		drawLine(g2d,x1,y1,x2,y2);
	}
	
	static void drawLine(Graphics2D g2d, double x1, double y1, double x2, double y2) {
		g2d.drawLine(toPixel(x1), toPixel(y1), toPixel(x2), toPixel(y2));
	}
	
	static void drawCircle(Graphics2D g2d, double x, double y, double radius) {
		g2d.drawOval(toPixel(x-radius), toPixel(y-radius), toPixel(radius*2), toPixel(radius*2));
	}
	
	static void fillCircle(Graphics2D g2d, double x, double y, double radius) {
		g2d.fillOval(toPixel(x-radius), toPixel(y-radius), toPixel(radius*2), toPixel(radius*2));
	}
	
	static void drawImage(Graphics2D g2d, Image image, double x, double y, double w, double h) {
		g2d.drawImage(image, toPixel(x), toPixel(y), toPixel(w), toPixel(h), null);
	}
	
	static void fillRectangle(Graphics2D g2d, double x, double y, double w, double h) {
		Point2D[] points = {new Point2D.Double(x,y),new Point2D.Double(x+w,y),
				new Point2D.Double(x,y+h),new Point2D.Double(x+w,y+h)};
		fillPolygon(g2d,points);
	}
	
	static void fillPolygon(Graphics2D g2d, Point2D[] points) {
		Polygon polygon = new Polygon();
		for (Point2D point:points) {
			polygon.addPoint(toPixel(point.getX()),toPixel(point.getY()));
		}
		g2d.fillPolygon(polygon);
	}
	
	static void drawRectangle(Graphics2D g2d, double x, double y, double w, double h, float strokeWidth) {
		drawRectangle(g2d,x,y,w,h,new BasicStroke(strokeWidth),defaultColor);
	}
	
	static void drawRectangle(Graphics2D g2d, double x, double y, double w, double h, Stroke stroke, Color color) {
		Point2D[] points = {new Point2D.Double(x,y),new Point2D.Double(x+w,y),
				new Point2D.Double(x,y+h),new Point2D.Double(x+w,y+h)};
		drawPolygon(g2d,points,stroke,color);
	}
	
	static void drawPolygon(Graphics2D g2d, Point2D[] points, Stroke stroke, Color color) {
		g2d.setColor(color);
		g2d.setStroke(stroke);
		
		Polygon polygon = new Polygon();
		for (Point2D point:points) {
			polygon.addPoint(toPixel(point.getX()),toPixel(point.getY()));
		}
		g2d.fillPolygon(polygon);
	}
	
	public static double getPPM() {
		return GameScreen.ppm;
	}
	
	public static int toPixel(double value) {
		return (int)(value*GameScreen.ppm+0.5);
	}
	
	public static double toMeter(int pixel) {
		return pixel/GameScreen.ppm;
	}
}