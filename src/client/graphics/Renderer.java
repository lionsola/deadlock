package client.graphics;

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
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import client.gui.GameScreen;
import client.gui.GameWindow;
import network.FullCharacterData;
import network.PartialCharacterData;
import network.ProjectileData;
import server.world.Arena;
import server.world.Geometry;
import server.world.Tile;

/**
 * Supposed to be the client.graphics "engine" of the game, this class provides all the rendering methods
 * needed to draw things on the client.gui.
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
		// render the server.character
		renderCharacter(g2D, player.x, player.y, player.direction, player.radius, typeId, true);
		renderArmor(g2D,player.x, player.y, player.radius,player.direction+player.armorStart,player.armorAngle,true);
		// render the health bar
		g2D.setStroke(new BasicStroke(toPixel(0.25)));
		g2D.setColor(new Color(255, 50, 50));
		double length = (0.2*player.healthPoints/GameScreen.ppm);
		double topy = (player.y - player.radius - 0.5);
		
		drawLine(g2D, player.x - length / 2, topy, player.x + length / 2, topy);
		// render the reload bar
		if (player.reloadPercent < 1) {
			topy += 0.25;
			length = (player.radius*player.reloadPercent);
			g2D.setStroke(new BasicStroke(toPixel(0.15)));
			g2D.setColor(Color.WHITE);
			double x = player.x - length/2;
			drawLine(g2D, x, topy, x + length, topy);
		}
	}

	public static void renderOtherCharacter(Graphics2D g2D, PartialCharacterData c, int typeId, boolean friendly) {
		renderArmor(g2D,c.x,c.y,c.radius,c.direction+c.armorStart,c.armorAngle,friendly);
		renderCharacter(g2D,c.x,c.y,c.direction,c.radius,typeId,friendly);
	}
	
	private static void renderArmor(Graphics2D g2D, double cx, double cy, double cr, double start, double extent, boolean friendly) {
		g2D.setStroke(new BasicStroke(4));
		if (friendly)
			g2D.setColor(Color.GREEN);
		else
			g2D.setColor(Color.RED);
		drawArc(g2D,cx,cy,cr,start,extent,Arc2D.OPEN);
	}
	
	private static void drawArc(Graphics2D g2D, double cx, double cy, double cr, double start, double extent, int type) {
		g2D.draw(new Arc2D.Double(toPixel(cx-cr),toPixel(cy-cr),toPixel(cr*2),toPixel(cr*2),Math.toDegrees(start),Math.toDegrees(extent),type));
	}
	
	private static void renderCharacter(Graphics2D g2D, double x, double y, double direction, double r, int typeId, boolean friendly) {
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
		//fillCircle(g2D,x, y,r);
		drawCircle(g2D,x,y,r);
		
		// draw gun
		Point2D p1 = Geometry.PolarToCartesian(r*0.7, direction);
		Point2D p2 = Geometry.PolarToCartesian(r*1.5, direction);
		drawLine(g2D,x+p1.getX(),y-p1.getY(),x+p2.getX(),y-p2.getY());
		
		double sqrt = (r / 1.4);
		//drawImage(g2D,CharacterFactory.getImage(typeId, color), x - sqrt, y - sqrt, 2 * sqrt, 2 * sqrt);
		//for (ArmorData ad:)
		g2D.setStroke(new BasicStroke(1));
	}

	public static void renderProjectile(Graphics2D g2D, ProjectileData pd) {
		g2D.setColor(Color.WHITE);
		g2D.setStroke(new BasicStroke(2));
		if (pd.speed > 0.05) {
			double dx = Math.cos(pd.direction) * pd.speed * GameWindow.MS_PER_UPDATE;
			double dy = -Math.sin(pd.direction) * pd.speed * GameWindow.MS_PER_UPDATE;
			drawLine(g2D, pd.x, pd.y,pd.x - dx,pd.y - dy);
		}
		if (pd.size > 50) {
			drawCircle(g2D,pd.x,pd.y,pd.size/2000);
		}
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
	
	public static void renderLOS(Graphics2D g2D, Shape los) {
		g2D.setColor(Color.YELLOW);
		g2D.setStroke(new BasicStroke(toPixel(0.15)));
		g2D.draw(los);
	}
	
	public static void renderDark(Graphics2D g2D, Arena a, Rectangle2D window) {
		double ts = Tile.tileSize;
		int x1 = Math.max(0, (int) (window.getX() / ts));
		int y1 = Math.max(0, (int) (window.getY() / ts));
		int x2 = Math.min(a.getWidth() - 1, x1 + (int) (window.getWidth() / ts) + 1);
		int y2 = Math.min(a.getHeight() - 1, y1 + (int) (window.getHeight() / ts) + 1);
		
		double ww = 0.15;
		g2D.setColor(Color.LIGHT_GRAY);
		g2D.setStroke(new BasicStroke(toPixel(ww)));
		
		for (int x = x1; x <= x2; x++) {
			for (int y = y1; y <= y2; y++) {
				//Image image = a.get(x, y).getImageDark();
				//drawImage(g2D,image, x * tileSize,y * tileSize,tileSize,tileSize);
				
				double xa = x*ts;
				double ya = y*ts;
				double xb = (x+1)*ts;
				double yb = (y+1)*ts;
				Tile t = a.get(x, y);
				
				/*
				if (!t.isWalkable()) {
					g2D.setColor(Color.DARK_GRAY);
					fillRectangle(g2D,x*ts,y*ts,ts,ts);
				}
				*/
				g2D.setColor(Color.LIGHT_GRAY);
				
				if (a.get(x-1,y)!=t) {
					drawLine(g2D,xa,ya,xa,yb);
				}
				if (a.get(x,y-1)!=t) {
					drawLine(g2D,xa,ya,xb,ya);
				}
				if (a.get(x+1,y)!=t) {
					drawLine(g2D,xb,ya,xb,yb);
				}
				if (a.get(x,y+1)!=t) {
					drawLine(g2D,xa,yb,xb,yb);
				}
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
		g2d.fillRect(toPixel(x), toPixel(y), toPixel(w), toPixel(h));
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