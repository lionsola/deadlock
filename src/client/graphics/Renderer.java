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
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import client.gui.ClientPlayer;
import editor.EditorArena;
import server.world.Arena;
import server.world.Geometry;
import server.world.Light;
import server.world.SpriteConfig;
import server.world.Terrain;
import server.world.Thing;
import shared.network.FullCharacterData;
import shared.network.PartialCharacterData;
import shared.network.ProjectileData;

/**
 * Supposed to be the client.graphics "engine" of the game, this class provides all the rendering methods
 * needed to draw things on the client.gui.
 */
public class Renderer {
	public static final int CURSOR_BMP_SIZE = 33;
	public static final int CURSOR_SIZE = 5;
	public static final double CHARACTER_WIDTH = 0.1;
	public static final double HEALTHBAR_WIDTH = 0.25;
	//private static Color defaultColor = Color.WHITE;
	private static final Color[] teamColors = {Color.GREEN.darker(),Color.RED.darker()};
	/**
	 * @return the arenaImage
	 */
	public BufferedImage getArenaImage() {
		return arenaImage;
	}

	/**
	 * @return the darkArenaImage
	 */
	public BufferedImage getDarkArenaImage() {
		return darkArenaImage;
	}

	/**
	 * @return the lightArenaImage
	 */
	public BufferedImage getLightArenaImage() {
		return lightArenaImage;
	}

	/**
	 * @return the lightMap
	 */
	public BufferedImage getLightMap() {
		return lightMap;
	}

	private BufferedImage arenaImage;
	private BufferedImage darkArenaImage;
	private BufferedImage lightArenaImage;
	private BufferedImage lightMap;
	public static final double DEFAULT_PPM = 20;
	private static double ppm = Renderer.DEFAULT_PPM;
	
	public void initArenaImages(Arena arena) {
		try {
			arenaImage = ImageIO.read(new FileInputStream("resource/map/"+arena.getName()+"_mid.png"));
		} catch (IOException e) {
			System.err.println("Error while reading pre-rendered map image");
			e.printStackTrace();
			arenaImage = ImageBlender.drawArena(arena);
		}
		
		try {
			darkArenaImage = ImageIO.read(new FileInputStream("resource/map/"+arena.getName()+"_dark.png"));
		} catch (IOException e) {
			System.err.println("Error while reading pre-rendered dark image");
			e.printStackTrace();
			darkArenaImage = ImageBlender.applyBackgroundEffect(arenaImage);
		}
		
		try {
			lightArenaImage = ImageIO.read(new FileInputStream("resource/map/"+arena.getName()+"_light.png"));
		} catch (IOException e) {
			System.err.println("Error while reading pre-rendered map image");
			e.printStackTrace();
			lightArenaImage = ImageBlender.applyForegroundEffect(arenaImage);
		}
		
		try {
			lightMap = ImageIO.read(new FileInputStream("resource/map/"+arena.getName()+"_lightmap.png"));
		} catch (IOException e) {
			System.err.println("Error while reading pre-rendered light map");
			e.printStackTrace();
			lightArenaImage = ImageBlender.drawLightImage(arena);
		}
	}
	
	public void addBloodToArena(double x, double y, double direction) {
		Graphics2D g2D = (Graphics2D) lightArenaImage.getGraphics();
		g2D.rotate(-direction,toPixel(x),toPixel(y));
		double bw = 2;
		drawImage(g2D,Sprite.getBloodImage(),x+bw/4,y-bw/2,bw,bw);
		g2D.rotate(direction,toPixel(x),toPixel(y));
		g2D.dispose();
	}
	
	public void dispose() {
		darkArenaImage = null;
		arenaImage = null;
		lightArenaImage = null;
	}
	
	public static void renderMainCharacter(Graphics2D g2D, FullCharacterData player, ClientPlayer playerInfo) {
		// render the character
		renderCharacter(g2D, player.x, player.y, player.direction, player.radius, playerInfo.type,playerInfo.team);
		renderArmor(g2D,player.x, player.y, player.radius,player.direction+player.armorStart,player.armorAngle,playerInfo.team);
		// render the health bar
		g2D.setStroke(new BasicStroke(toPixel(HEALTHBAR_WIDTH)));
		g2D.setColor(new Color(255, 50, 50));
		double length = (0.2*player.healthPoints/Renderer.ppm);
		double topy = (player.y - player.radius - HEALTHBAR_WIDTH*2);
		
		drawLine(g2D, player.x - length / 2, topy, player.x + length / 2, topy);
		// render the reload bar
		if (player.reloadPercent < 1) {
			topy += HEALTHBAR_WIDTH;
			length = (player.radius*player.reloadPercent);
			g2D.setStroke(new BasicStroke(toPixel(0.15)));
			g2D.setColor(Color.WHITE);
			double x = player.x - length/2;
			drawLine(g2D, x, topy, x + length, topy);
		}
	}

	public static void renderOtherCharacter(Graphics2D g2D, PartialCharacterData c, int typeId) {
		renderArmor(g2D,c.x,c.y,c.radius,c.direction+c.armorStart,c.armorAngle,c.team);
		renderCharacter(g2D,c.x,c.y,c.direction,c.radius,typeId,c.team);
	}
	
	private static void renderArmor(Graphics2D g2D, double cx, double cy, double cr, double start, double extent,int team) {
		g2D.setStroke(new BasicStroke(toPixel(CHARACTER_WIDTH*2)));
		g2D.setColor(teamColors[team]);
		drawArc(g2D,cx,cy,cr,start,extent,Arc2D.OPEN);
	}
	
	private static void renderCharacter(Graphics2D g2D, double x, double y, double direction, double r, int typeId, int team) {
		double GUN_WIDTH = 0.15;
		// draw gun
		g2D.setStroke(new BasicStroke(toPixel(GUN_WIDTH)));
		g2D.setColor(teamColors[team]);
		Point2D p1 = Geometry.PolarToCartesian(r*0.7, direction);
		Point2D p2 = Geometry.PolarToCartesian(r*1.7, direction);
		drawLine(g2D,x+p1.getX(),y-p1.getY(),x+p2.getX(),y-p2.getY());
		
		g2D.setStroke(new BasicStroke(toPixel(CHARACTER_WIDTH)));
		g2D.setColor(Color.BLACK);
		fillCircle(g2D,x, y,r);
		g2D.setColor(teamColors[team]);
		//fillCircle(g2D,x,y,r);
		drawCircle(g2D,x,y,r);
		

		
		// draw head
		Point2D h = Geometry.PolarToCartesian(r*0.4, direction);
		double hr = r*0.4;
		fillCircle(g2D,x+h.getX(),y-h.getY(),hr);
		
		g2D.setStroke(new BasicStroke(1));
	}

	public static void renderProjectile(Graphics2D g2D, ProjectileData pd) {
		g2D.setColor(Color.WHITE);
		g2D.setStroke(new BasicStroke(2));
		drawLine(g2D, pd.x, pd.y,pd.prevX,pd.prevY);
		if (pd.size > 50) {
			drawCircle(g2D,pd.x,pd.y,pd.size/2000);
		}
	}
	
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
	
	public static void renderArenaBG(Graphics2D g2D, Arena a, Rectangle2D window) {
		g2D.setColor(new Color(0x080808));
		fillRect(g2D,0,0, a.getWidthMeter(), a.getHeightMeter());
		
		double ts = Terrain.tileSize;
		int x1 = Math.max(0, (int) (window.getX() / ts));
		int y1 = Math.max(0, (int) (window.getY() / ts));
		int x2 = Math.min(a.getWidth() - 1, x1 + (int) (window.getWidth() / ts) + 1);
		int y2 = Math.min(a.getHeight() - 1, y1 + (int) (window.getHeight() / ts) + 1);
		
		
		for (int x = x1; x <= x2; x++) {
			for (int y = y1; y <= y2; y++) {
				Terrain t = a.get(x, y).getTerrain();
				if (t==null || t.getId()==0)
					continue;
				
				BufferedImage image = t.getImage();
				int w = image.getWidth()/32;
				int h = image.getHeight()/32;
				double xM = (x%w)*ts;
				double yM = (y%h)*ts;
				drawImage(g2D,image, xM, yM, ts, ts, x*ts, y*ts, ts, ts);
			}
		}
	}
	
	public static void renderArenaObjects(Graphics2D g2D, Arena a, Rectangle2D window) {
		double ts = Terrain.tileSize;
		int x1 = Math.max(0, (int) (window.getX() / ts));
		int y1 = Math.max(0, (int) (window.getY() / ts));
		int x2 = Math.min(a.getWidth() - 1, x1 + (int) (window.getWidth() / ts) + 1);
		int y2 = Math.min(a.getHeight() - 1, y1 + (int) (window.getHeight() / ts) + 1);
		
		for (int x = x1; x <= x2; x++) {
			for (int y = y1; y <= y2; y++) {
				Thing t = a.get(x, y).getThing();
				if (t==null || t.getId()==0)
					continue;
				
				double xM = 0, yM = 0, rot = 0;
				SpriteConfig config = a.get(x, y).getSpriteConfig();
				BufferedImage image = t.getImage();
				if (config==null) {
					int w = image.getWidth()/32;
					int h = image.getHeight()/32;
					xM = (x%w)*ts;
					yM = (y%h)*ts;
				} else {
					xM = config.spriteX*ts;
					yM = config.spriteY*ts;
					rot = config.rotation*Math.PI/2;
				}
				
				double sw = ts*t.getSpriteSize();
				g2D.rotate(-rot,toPixel((x+0.5)*ts),toPixel((y+0.5)*ts));
				drawImage(g2D,image, xM, yM, ts, ts, x*ts-(sw-ts)*0.5, y*ts-(sw-ts)*0.5, sw, sw);
				g2D.rotate(rot,toPixel((x+0.5)*ts),toPixel((y+0.5)*ts));
				
				double xa = x*ts;
				double ya = y*ts;
				double xb = (x+1)*ts;
				double yb = (y+1)*ts;
				
				if (t.getCoverType()==3) {
					double ww = 0.15;
					g2D.setStroke(new BasicStroke(toPixel(ww)));
					g2D.setColor(t.getColor());
					if (a.get(x-1,y).getThing()!=t) {
						drawLine(g2D,xa,ya,xa,yb);
					}
					if (a.get(x,y-1).getThing()!=t) {
						drawLine(g2D,xa,ya,xb,ya);
					}
					if (a.get(x+1,y).getThing()!=t) {
						drawLine(g2D,xb,ya,xb,yb);
					}
					if (a.get(x,y+1).getThing()!=t) {
						drawLine(g2D,xa,yb,xb,yb);
					}
				}
			}
		}
	}
	
	public static void renderGrid(Graphics2D g2D, Arena a, Rectangle2D window) {
		//drawImage(g2D, a.image, window.getX(),window.getY(),window.getWidth(),window.getHeight(),window.getX(),window.getY(),window.getWidth(),window.getHeight());
		g2D.setColor(Color.GRAY);
		g2D.setStroke(new BasicStroke(0.5f));
		
		double ts = Terrain.tileSize;
		int x1 = Math.max(0, (int) (window.getX() / ts));
		int y1 = Math.max(0, (int) (window.getY() / ts));
		int x2 = Math.min(a.getWidth() - 1, x1 + (int) (window.getWidth() / ts) + 1);
		int y2 = Math.min(a.getHeight() - 1, y1 + (int) (window.getHeight() / ts) + 1);
		
		for (int x = x1; x <= x2; x++) {
			Renderer.drawLine(g2D, x*ts, y1*ts, x*ts, y2*ts);
		}
		
		for (int y = y1; y <= y2; y++) {
			Renderer.drawLine(g2D, x1*ts, y*ts, x2*ts, y*ts);
		}
	}
	
	public static void renderEditorLightSource(Graphics2D g2D, EditorArena a, Rectangle2D window) {
		double ts = Terrain.tileSize;
		for (Light l:a.getLightList()) {
			g2D.setColor(new Color(l.getColor()));
			fillRect(g2D,(l.getX()+0.25)*ts,(l.getY()+0.25)*ts,ts/2,ts/2);
			g2D.setStroke(new BasicStroke(1));
			Renderer.drawCircle(g2D, (l.getX()+0.5)*ts, (l.getY()+0.5)*ts, (l.getRange()-0.5)*ts);
		}
	}
	
	public static void renderEditorHardLight(Graphics2D g2D, int[][] lightMap, Rectangle2D window) {
		double ts = Terrain.tileSize;
		int x1 = Math.max(0, (int) (window.getX() / ts));
		int y1 = Math.max(0, (int) (window.getY() / ts));
		int x2 = Math.min(lightMap.length - 1, x1 + (int) (window.getWidth() / ts) + 1);
		int y2 = Math.min(lightMap[0].length - 1, y1 + (int) (window.getHeight() / ts) + 1);
		double SIZE = 0.3;
		double MARGIN = (1.0-SIZE)/2.0;
		
		for (int x = x1; x <= x2; x++) {
			for (int y = y1; y <= y2; y++) {
				if (lightMap[x][y] != 0) {
					g2D.setColor(new Color(lightMap[x][y]));
					fillRect(g2D,(x+MARGIN)*ts,(y+MARGIN)*ts, ts*SIZE, ts*SIZE);
					g2D.setColor(Color.WHITE);
					g2D.setStroke(new BasicStroke(1));
					drawRect(g2D,(x+MARGIN)*ts,(y+MARGIN)*ts, ts*SIZE, ts*SIZE);
				}
			}
		}			
	}
	
	public static void renderHardLight(Graphics2D g2D, Arena a, Rectangle2D window) {
		double ts = Terrain.tileSize;
		int x1 = Math.max(0, (int) (window.getX() / ts));
		int y1 = Math.max(0, (int) (window.getY() / ts));
		int x2 = Math.min(a.getWidth() - 1, x1 + (int) (window.getWidth() / ts) + 1);
		int y2 = Math.min(a.getHeight() - 1, y1 + (int) (window.getHeight() / ts) + 1);
		
		for (int x = x1; x <= x2; x++) {
			for (int y = y1; y <= y2; y++) {
				g2D.setColor(new Color(a.getLightmap()[x][y],false));
				fillRect(g2D,x*ts,y*ts, ts, ts);
			}
		}
	}
	
	static void drawLine(Graphics2D g2D, double x1, double y1, double x2, double y2) {
		g2D.drawLine(toPixel(x1), toPixel(y1), toPixel(x2), toPixel(y2));
	}
	
	private static void drawArc(Graphics2D g2D, double cx, double cy, double cr, double start, double extent, int type) {
		g2D.draw(new Arc2D.Double(toPixel(cx-cr),toPixel(cy-cr),toPixel(cr*2),toPixel(cr*2),Math.toDegrees(start),Math.toDegrees(extent),type));
	}
	
	static void drawCircle(Graphics2D g2D, double x, double y, double radius) {
		g2D.drawOval(toPixel(x-radius), toPixel(y-radius), toPixel(radius*2), toPixel(radius*2));
	}
	
	static void fillCircle(Graphics2D g2D, double x, double y, double radius) {
		g2D.fillOval(toPixel(x-radius), toPixel(y-radius), toPixel(radius*2), toPixel(radius*2));
	}
	
	public static void drawArenaImage(Graphics2D g2D, Image image, Rectangle2D window) {
		drawImage(g2D, image, window.getX(),window.getY(),window.getWidth(),window.getHeight(),
				window.getX(),window.getY(),window.getWidth(),window.getHeight());
	}
	
	static void drawImage(Graphics2D g2D, Image image, double x, double y, double w, double h) {
		int screenX = toPixel(x);
		int screenY = toPixel(y);
		int screenW = toPixel(w+x-Renderer.toMeter(screenX));
		int screenH = toPixel(h+y-Renderer.toMeter(screenY));
		g2D.drawImage(image, screenX, screenY, screenW, screenH, null);
	}
	
	static void drawImage(Graphics2D g2D, Image image, double sx, double sy, double sw, double sh, double x, double y, double w, double h) {
		int screenX = toPixel(x);
		int screenY = toPixel(y);
		int screenW = toPixel(w+x-Renderer.toMeter(screenX));
		int screenH = toPixel(h+y-Renderer.toMeter(screenY));
		g2D.drawImage(image, screenX, screenY, screenW+screenX, screenH+screenY,
				toPixelDefault(sx), toPixelDefault(sy), toPixelDefault(sx+sw), toPixelDefault(sy+sh),
				null);
	}
	
	static void fillRect(Graphics2D g2D, double x, double y, double w, double h) {
		g2D.fillRect(toPixel(x), toPixel(y), toPixel(w), toPixel(h));
	}
	
	static void fillPolygon(Graphics2D g2D, Point2D[] points) {
		Polygon polygon = new Polygon();
		for (Point2D point:points) {
			polygon.addPoint(toPixel(point.getX()),toPixel(point.getY()));
		}
		g2D.fillPolygon(polygon);
	}
	
	static void drawRect(Graphics2D g2D, double x, double y, double w, double h) {
		g2D.drawRect(toPixel(x), toPixel(y), toPixel(w), toPixel(h));
	}
	
	static void drawRectangle(Graphics2D g2D, double x, double y, double w, double h, Stroke stroke, Color color) {
		Point2D[] points = {new Point2D.Double(x,y),new Point2D.Double(x+w,y),
				new Point2D.Double(x,y+h),new Point2D.Double(x+w,y+h)};
		drawPolygon(g2D,points,stroke,color);
	}
	
	static void drawPolygon(Graphics2D g2D, Point2D[] points, Stroke stroke, Color color) {
		g2D.setColor(color);
		g2D.setStroke(stroke);
		
		Polygon polygon = new Polygon();
		for (Point2D point:points) {
			polygon.addPoint(toPixel(point.getX()),toPixel(point.getY()));
		}
		g2D.fillPolygon(polygon);
	}
	
	public static void setPPM(double ppm) {
		Renderer.ppm = ppm;
	}
	
	public static double getPPM() {
		return Renderer.ppm;
	}
	
	public static int toPixel(double value) {
		return (int)(value*Renderer.ppm+0.5);
	}
	
	public static int toPixelDefault(double value) {
		return (int)(value*Renderer.DEFAULT_PPM+0.5);
	}
	
	public static double toMeter(int pixel) {
		return pixel/Renderer.ppm;
	}

	public static void renderProtection(Graphics2D g2D, int tileX, int tileY, double protection) {
		double y = (tileY+0.1)*Terrain.tileSize;
		double x = (tileX+0.1)*Terrain.tileSize;
		g2D.setStroke(new BasicStroke(toPixel(0.1)));
		g2D.setColor(Color.WHITE);
		drawLine(g2D,x,y,x+0.8*Terrain.tileSize*protection/3.0,y);
		double w = 0.8;
		x = (tileX+(1-w)/2)*Terrain.tileSize;
		y = (tileY+(1-w)/2)*Terrain.tileSize;
		w = w*Terrain.tileSize;
		drawImage(g2D,Sprite.SHIELD,x,y,w,w);
	}
}