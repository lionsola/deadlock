package client.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import client.gui.ClientPlayer;
import client.gui.GUIFactory;
import client.image.OverlayComposite;
import editor.DataManager;
import editor.EditorArena;
import editor.SpawnPoint;
import server.character.ClassStats;
import server.character.Entity;
import server.world.Arena;
import server.world.Geometry;
import server.world.Light;
import server.world.SpriteConfig;
import server.world.Terrain;
import server.world.Thing;
import server.world.Tile;
import server.world.Utils;
import server.world.trigger.Trigger;
import server.world.trigger.Trigger.SwitchOnTouchSide;
import server.world.trigger.TriggerEffect;
import shared.network.FullCharacterData;
import shared.network.NPCData;
import shared.network.CharData;
import shared.network.ProjectileData;

/**
 * This class provides all the rendering methods
 * needed to draw things. It converts values in meter
 * to pixel before drawing. 
 */
public class Renderer {
	public static final int CURSOR_BMP_SIZE = 33;
	public static final int CURSOR_SIZE = 5;
	public static final float CHARACTER_WIDTH = 0.08f;
	public static final float HEALTHBAR_WIDTH = 0.25f;
	
	public static final Color DEFAULT_COLOR = new Color(0xb6b6b6);
	public static final Color BACKGROUND_COLOR = new Color(0x1a1a1a);
	//public static final Color[] teamColors = {new Color(0x32ff32),new Color(0xff3232)};
	//public static final Color[] teamColors2 = {new Color(0x0f4f0f),new Color(0x4f0f0f)};
	public static final Color[] teamColors = {new Color(0x999999),new Color(0xff4040)};
	public static final Color[] teamColors2 = {new Color(0x4d4d4d),new Color(0x4d1313)};
	
	private BufferedImage arenaImage;
	private BufferedImage darkArenaImage;
	private BufferedImage lightArenaImage;
	private BufferedImage lightMap;
	
	public BufferedImage lowImage;
	public BufferedImage highImage;
	public BufferedImage bloodImage;
	
	//private HashMap<Integer,CharData> oldData = new HashMap<Integer,CharData>();
	private HashMap<Integer,Float> oldBodyDir = new HashMap<Integer,Float>();
	
	public static final float DEFAULT_PPM = 20f;
	private static float ppm = Renderer.DEFAULT_PPM;
	
	final float dash1[] = {10.0f,30.0f};
    final BasicStroke dashed =
        new BasicStroke(0.5f,
                        BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_MITER,
                        10.0f, dash1, 0.0f);
    
    public Renderer() {
    	
    }
	
    public void saveCharData(FullCharacterData main, List<CharData> others) {
    	HashMap<Integer,Float> newData = new HashMap<Integer,Float>();
    	List<CharData> chars = new LinkedList<CharData>(others);
    	chars.add(main);
    	for (CharData data:chars) {
    		float targetBodyDir = data.bodyDir;
    		float faceBodyDelta = Geometry.wrapAngle(data.faceDir-targetBodyDir);
    		if (Math.abs(faceBodyDelta)>Math.PI*0.75) {
    			targetBodyDir += Math.PI - Math.copySign(0.001,faceBodyDelta);
			}
    		
    		if (oldBodyDir.containsKey(data.id)) {
    			float oldDir = oldBodyDir.get(data.id);
				
    			final float RATE = 0.12f;
    			final float delta = Geometry.wrapAngle(targetBodyDir-oldDir);
    			if (Math.abs(delta)>Math.PI*0.6) {
    				targetBodyDir = (float) (oldDir + Math.copySign(Math.abs(delta)-Math.PI*0.4,delta));
    			}
    			else if (Math.abs(delta)>RATE) {
    				targetBodyDir = oldDir + Math.copySign(RATE,delta);
    			}
    		}
    		newData.put(data.id, targetBodyDir);
    	}
    	oldBodyDir = newData;
    }
    
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
	
	public static void drawThing(Graphics2D g2D, Arena a, int x, int y) {
		double ts = Terrain.tileSize;
		Thing t = a.get(x, y).getThing();
		if (t==null || t.getId()==0)
			return;
		double xS = 0, yS = 0, rot = 0;
		boolean flip = false;
		SpriteConfig config = a.get(x, y).getThingConfig();
		BufferedImage image = t.getImage();
		if (config==null) {
			int w = image.getWidth()/32;
			int h = image.getHeight()/32;
			xS = (x%w)*ts;
			yS = (y%h)*ts;
		} else {
			xS = config.spriteX*ts;
			yS = config.spriteY*ts;
			rot = config.rotation*Math.PI/2;
			flip = config.flip;
			
		}
		
		double wD = ts*t.getSpriteSize();
		double xD = x*ts-(wD-ts)*0.5;
		double yD = y*ts-(wD-ts)*0.5;
		g2D.rotate(-rot,toPixel((x+0.5)*ts),toPixel((y+0.5)*ts));
		if (!flip) {
			drawImage(g2D,image, xS, yS, ts, ts, xD, yD, wD, wD);
		} else {
			drawImage(g2D,image, xS, yS, ts, ts, xD+wD, yD, -wD-1.0/ppm, wD);
		}
		g2D.rotate(rot,toPixel((x+0.5)*ts),toPixel((y+0.5)*ts));
		
		if (t.isBorderDrawn()) {
			double xa = x*ts;
			double ya = y*ts;
			double xb = (x+1)*ts;
			double yb = (y+1)*ts;
			double ww = 0.15;
			//g2D.setStroke(new WobbleStroke(1f, 0.5f, new BasicStroke(toPixel(ww))));
			//g2D.setStroke(new BrushStroke(toPixel(ww), 1,Utils.random().nextLong()));
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
	
	private static void drawTerrain(Graphics2D g2D, Arena a, int x, int y) {
		Terrain t = a.get(x, y).getTerrain();
		if (t==null || t.getId()==0)
			return;
		
		double ts = Terrain.tileSize;
		if (t.getId()==Terrain.BLEND) {
			Terrain[] results = new Terrain[4];
			int i = 0;
			for (int xi=-1;xi<=1;xi+=2) {
				for (int yi=-1;yi<=1;yi+=2) {
					Terrain result = blendVote(a.get(x+xi, y).getTerrain(),
							a.get(x, y+yi).getTerrain(),a.get(x+xi, y+yi).getTerrain());
					results[i] = result;
					
					if (result==null || result.getId()==0)
						continue;
					
					BufferedImage image = result.getImage();
					int w = image.getWidth()/32;
					int h = image.getHeight()/32;
					double xM = (x%w)*ts;
					double yM = (y%h)*ts;
					
					
					drawImage(g2D,image, xM+ts*(xi+1.0)/4, yM+ts*(yi+1.0)/4, ts/2, ts/2,
							x*ts+ts*(xi+1)/4, y*ts+ts*(yi+1)/4, ts/2, ts/2);
					results[i] = result;
					i++;
				}
			}
			// double-check if there's any useless blend spot
			if (results[0]==results[1] && results[1]==results[2] && results[2]==results[3]) {
				g2D.setColor(Color.WHITE);
				fillRect(g2D,x*ts,y*ts,ts,ts);
			}
		} else {
			BufferedImage image = t.getImage();
			int w = image.getWidth()/32;
			int h = image.getHeight()/32;
			double xM = (x%w)*ts;
			double yM = (y%h)*ts;
			
			drawImage(g2D,image, xM, yM, ts, ts, x*ts, y*ts, ts, ts);
		}
	}
	
	private static void drawMisc(Graphics2D g2D, Arena a, int x, int y) {
		Thing m = a.get(x, y).getMisc();
		if (m==null || m.getId()==0)
			return;
		
		double ts = Terrain.tileSize;
		double xS = 0, yS = 0, rot = 0;
		boolean flip = false;
		SpriteConfig config = a.get(x, y).getMiscConfig();
		BufferedImage image = m.getImage();
		if (config==null) {
			int w = image.getWidth()/32;
			int h = image.getHeight()/32;
			xS = (x%w)*ts;
			yS = (y%h)*ts;
		} else {
			xS = config.spriteX*ts;
			yS = config.spriteY*ts;
			rot = config.rotation*Math.PI/2;
			flip = config.flip;
		}
		
		double wD = ts*m.getSpriteSize();
		double xD = x*ts-(wD-ts)*0.5;
		double yD = y*ts-(wD-ts)*0.5;
		g2D.rotate(-rot,toPixel((x+0.5)*ts),toPixel((y+0.5)*ts));
		if (!flip) {
			drawImage(g2D,image, xS, yS, ts, ts, xD, yD, wD, wD);
		}
		else {
			drawImage(g2D,image, xS, yS, ts, ts, xD+wD, yD, -wD-1.0/ppm, wD);
		}
		g2D.rotate(rot,toPixel((x+0.5)*ts),toPixel((y+0.5)*ts));	}
	
	public void initArenaImages(Arena arena) {
		BufferedImage lightArenaImage;
		try {
			lightArenaImage = DataManager.loadImage("/map/"+arena.getName()+"_plain.png");
		} catch (IOException e) {
			System.err.println("Error while reading pre-rendered map image");
			e.printStackTrace();
			lightArenaImage = ImageBlender.drawArena(arena);
		}
		
		try {
			arenaImage = DataManager.loadImage("/map/"+arena.getName()+"_mid.png");
		} catch (IOException e) {
			System.err.println("Error while reading pre-rendered map image");
			e.printStackTrace();
			arenaImage = ImageBlender.applyMiddlegroundEffect(lightArenaImage);
		}
		
		try {
			darkArenaImage = DataManager.loadImage("/map/"+arena.getName()+"_dark.png");
		} catch (IOException e) {
			System.err.println("Error while reading pre-rendered dark image");
			e.printStackTrace();
			darkArenaImage = ImageBlender.applyBackgroundEffect(lightArenaImage);
		}
		
		try {
			lightMap = DataManager.loadImage("/map/"+arena.getName()+"_lightmap.png");
		} catch (IOException e) {
			System.err.println("Error while reading pre-rendered light map");
			e.printStackTrace();
			lightMap = ImageBlender.drawLightImage(arena);
		}

		BufferedImage[] layerImages = new BufferedImage[4];
		for (int layer=0;layer<4;layer++) {
			try {
				layerImages[layer] = DataManager.loadImage("/map/"+arena.getName()+"_layer"+layer+".png");
			} catch (IOException e) {
				System.err.println("Error while reading pre-rendered light map");
				e.printStackTrace();
				layerImages[layer] = ImageBlender.drawArena(arena, layer);
			}
		}
		
		layerImages[0].getGraphics().drawImage(layerImages[1],0,0,null);
		layerImages[2].getGraphics().drawImage(layerImages[3],0,0,null);
		lowImage = layerImages[0];
		highImage = layerImages[2];
		bloodImage = ImageBlender.ge.createCompatibleImage(lowImage.getWidth(),lowImage.getHeight(),Transparency.TRANSLUCENT);
	}
	
	public void dispose() {
		darkArenaImage = null;
		arenaImage = null;
		lightArenaImage = null;
	}
	
	public void renderMainCharacter(Graphics2D g2D, FullCharacterData player, ClientPlayer playerInfo) {
		// render the character
		renderGun(g2D,player.x,player.y,player.radius,player.gunDir,playerInfo.weaponId,playerInfo.team);
		renderCharacter(g2D, player);
		renderArmor(g2D,player.x, player.y, player.radius,player.gunDir+player.armorStart,player.armorAngle,playerInfo.team);
		
	}

	public void renderCharacterUI(Graphics2D g2D, FullCharacterData p) {
		// render the health bar
		/*
		g2D.setStroke(new BasicStroke(toPixel(HEALTHBAR_WIDTH)));
		g2D.setColor(new Color(255, 50, 50));
		double length = (0.2*p.healthPoints/Renderer.ppm);
		double topy = (p.y - p.radius - HEALTHBAR_WIDTH*2);
		drawLine(g2D, p.x - length / 2, topy, p.x + length / 2, topy);
		*/
		// render the highlight circle
	    final BasicStroke dashedHighlight =
		        new BasicStroke(5f,
		                        BasicStroke.CAP_BUTT,
		                        BasicStroke.JOIN_MITER,
		                        10.0f);
		    
	    g2D.setStroke(dashedHighlight);
	    //double FPHASE = 1500;
	    //double t = Math.abs(FPHASE-(System.currentTimeMillis()%(FPHASE*2)))/FPHASE;
		//int alpha = (int)(40 + t*40);
	    int alpha = getAlpha();
	    Color c = GUIFactory.UICOLOR;
		g2D.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha));
		
		double phase = Math.PI*2/p.maxAmmo;
	    for (int a=0;a<p.ammo;a++) {
			Renderer.drawArc(g2D, p.x, p.y, p.radius*2, phase*a, phase*0.7, Arc2D.OPEN);
	    }
	    g2D.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha/4));
	    for (int a=p.ammo;a<p.maxAmmo;a++) {
	    	Renderer.drawArc(g2D, p.x, p.y, p.radius*2, phase*a, phase*0.7, Arc2D.OPEN);
	    }
		
		
		
		// render direction line
		/*
		if (p.viewRange>p.radius*5) {
			g2D.setColor(new Color(200, 200, 200));
			g2D.setStroke(dashed);
			Point2D p3 = Geometry.PolarToCartesian(p.viewRange, p.direction);
			Point2D p1 = Geometry.PolarToCartesian(p.radius*5, p.direction);
			drawLine(g2D,p.x+p3.getX(),p.y-p3.getY(),p.x+p1.getX(),p.y-p1.getY());
		}
		*/
	}
	
	public void renderOtherCharacter(Graphics2D g2D, CharData c) {
		if (c.healthPoints>0) {
			renderGun(g2D,c.x,c.y,c.radius,c.gunDir,c.weapon,c.team);
			renderCharacter(g2D,c);
			renderArmor(g2D,c.x,c.y,c.radius,c.gunDir+c.armorStart,c.armorAngle,c.team);
		} else {
			renderDeadCharacter(g2D,c.x,c.y,c.gunDir,c.radius,c.team);
		}
	}
	
	public void renderNPC(Graphics2D g2D, NPCData npc) {
		renderOtherCharacter(g2D,npc);
		g2D.setStroke(new BasicStroke(toPixel(HEALTHBAR_WIDTH)));
		g2D.setColor(new Color(255, 50, 50));
		double length = (npc.radius*npc.alertness);
		double topy = (npc.y - npc.radius - HEALTHBAR_WIDTH*2);
		drawLine(g2D, npc.x - length / 2, topy, npc.x + length / 2, topy);
	}
	
	private static void renderArmor(Graphics2D g2D, double cx, double cy, double cr, double start, double extent,int team) {
		g2D.setStroke(new BasicStroke((float) (CHARACTER_WIDTH*2*ppm)));
		
		g2D.setColor(teamColors[team].brighter());
		drawArc(g2D,cx,cy,cr,start,extent,Arc2D.OPEN);
	}
	
	private static void renderGun(Graphics2D g2D, double x, double y, double r, double gunDirection, int typeId, int team) {
		BufferedImage gun = Sprite.guns.get(typeId);
		if (gun!=null) {
	 		BufferedImage gunImage = ImageBlender.deepCopy(gun);
			Graphics2D gunG = (Graphics2D)gunImage.getGraphics();
			gunG.setComposite(new OverlayComposite(1.0f));
			gunG.setColor(teamColors[team].darker());
			gunG.fillRect(0, 0, gunImage.getWidth(), gunImage.getHeight());
			gunG.dispose();
			
			double gunW = gunImage.getHeight()/DEFAULT_PPM;
			double gunL = gunImage.getWidth()/DEFAULT_PPM;
			g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			
			g2D.rotate(-gunDirection,x*ppm,y*ppm);
			Renderer.drawImage(g2D,gunImage, x+r*0.6, y-gunW/2,gunL,gunW);
			g2D.rotate(gunDirection,x*ppm,y*ppm);
		}
	}
	
	private void renderCharacter(Graphics2D g2D, CharData d) {
		double x = d.x;
		double y = d.y;
		double faceDir = d.faceDir;
		double r = d.radius;
		int type = d.typeId;
		int team = d.team;
		boolean invi = d.invi;
		
		int body = ClassStats.classStats.get(type).getBodyId();
		double sizeF = r/Entity.BASE_RADIUS;
		if (invi) {
			float dash1[] = {10.0f};
		    BasicStroke dashed = new BasicStroke(CHARACTER_WIDTH*ppm,
		    		BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER,10.0f, dash1, 0.0f);
			g2D.setStroke(dashed);
			g2D.setColor(teamColors[team]);
			drawCircle(g2D,x,y,r);
			return;
		} else {
			g2D.setStroke(new BasicStroke(CHARACTER_WIDTH*ppm));
		}
		
		double bodyDir = oldBodyDir.containsKey(d.id)?oldBodyDir.get(d.id):d.bodyDir;
		
		g2D.rotate(-bodyDir,x*ppm,y*ppm);
		g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		switch (body) {
			case 0:
				g2D.setColor(teamColors[team].darker());
				fillPolygon(g2D,x,y,r*1.2,5);
				g2D.setColor(teamColors2[team]);
				drawPolygon(g2D,x,y,r*1.2,5);
				break;
			case 1:
				g2D.setColor(teamColors[team].darker());
				fillPolygon(g2D,x,y,r*1.4,3);
				g2D.setColor(teamColors2[team]);
				drawPolygon(g2D,x,y,r*1.4,3);
				break;
			case 2:
				g2D.setColor(teamColors[team].darker());
				fillPolygon(g2D,x,y,r*1.1,7);
				break;
			default:
				break;
		}
		g2D.rotate(bodyDir,x*ppm,y*ppm);
		
		// draw head
		Point2D h = Geometry.PolarToCartesian(r*0.3, faceDir);
		double hx = x+h.getX();
		double hy = y-h.getY();
		//g2D.setColor(teamColors[team].brighter());
		//fillCircle(g2D,x+h.getX(),y-h.getY(),hr);
		
		
		BufferedImage headImage = ImageBlender.deepCopy(Sprite.heads.get(type));
		Graphics2D gunG = (Graphics2D)headImage.getGraphics();
		gunG.setComposite(new OverlayComposite(1.0f));
		gunG.setColor(teamColors[team].darker());
		gunG.fillRect(0, 0, headImage.getWidth(), headImage.getHeight());
		gunG.dispose();
		
		double hW = sizeF*headImage.getWidth()/DEFAULT_PPM;
		double hH = sizeF*headImage.getHeight()/DEFAULT_PPM;
		g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		
		g2D.rotate(-faceDir,hx*ppm,hy*ppm);
		Renderer.drawImage(g2D,headImage, hx-hW/2, hy-hH/2,hW,hH);
		g2D.rotate(faceDir,hx*ppm,hy*ppm);
		
		g2D.setStroke(new BasicStroke(1));
	}



	private static void renderDeadCharacter(Graphics2D g2D, double x, double y, double direction, double r, int team) {
		g2D.setStroke(new BasicStroke(CHARACTER_WIDTH*ppm));
		g2D.setColor(teamColors2[team]);
		fillCircle(g2D,x,y,r);
		g2D.setColor(teamColors[team]);
		drawCircle(g2D,x,y,r);
		
		// draw head
		g2D.setColor(teamColors[team]);
		drawLine(g2D,x-r/3,y-r/5,x+r/3,y-r/5);
		drawLine(g2D,x,y-r/2,x,y+r/2);
		
		g2D.setStroke(new BasicStroke(1));
	}
	
	public static void renderProjectile(Graphics2D g2D, ProjectileData pd) {
		g2D.setColor(Color.WHITE);
		if (pd.size < 50) {
			g2D.setStroke(new BasicStroke((float)(getPPM()*pd.size/300)));
			drawLine(g2D, pd.x, pd.y,pd.prevX,pd.prevY);
		}
		else if (pd.size >= 50) {
			g2D.setStroke(new BasicStroke(1));
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

	
	
	public static void renderCrosshair(Graphics2D g2D, float cxFloat, float cyFloat, float crosshairSize_m, float width) {
		int cx = toPixel(cxFloat), cy = toPixel(cyFloat);
		g2D.setStroke(new BasicStroke(width));
		int chS = (int)Math.ceil(toPixel(crosshairSize_m));
		if (chS%2!=0) {
			chS -= 1;
		}
		g2D.drawOval(Math.round(cx-chS),Math.round(cy-chS),
				Math.round(chS*2+1),Math.round(chS*2+1));
		
		final int chL = Math.max(5,chS/3);
		g2D.drawLine(cx, cy+chS-chL,cx, cy+chS+chL);
		g2D.drawLine(cx, cy-chS-chL,cx, cy-chS+chL);
		g2D.drawLine(cx+chS-chL, cy,cx+chS+chL, cy);
		g2D.drawLine(cx-chS-chL, cy,cx-chS+chL, cy);
	}
	
	public static void renderArenaBGFlat(Graphics2D g2D, Arena a, Rectangle2D window) {
		g2D.setColor(BACKGROUND_COLOR);
		fillRect(g2D,window.getX(),window.getY(), window.getWidth(),window.getHeight());
		
		double ts = Terrain.tileSize;
		int x1 = Math.max(0, (int) (window.getX() / ts));
		int y1 = Math.max(0, (int) (window.getY() / ts));
		int x2 = Math.min(a.getWidth() - 1, x1 + (int) (window.getWidth() / ts) + 1);
		int y2 = Math.min(a.getHeight() - 1, y1 + (int) (window.getHeight() / ts) + 1);
		
		
		for (int x = x1; x <= x2; x++) {
			for (int y = y1; y <= y2; y++) {
				drawTerrain(g2D,a,x,y);
			}
		}
	}
	
	private static Terrain blendVote(Terrain main1, Terrain main2, Terrain tieBreaker) {
		if (main1!=null && main1.getId()==Terrain.BLEND) {
			if (main2!=null && main2.getId()==Terrain.BLEND) {
				return tieBreaker;
			} else {
				return main2;
			}
		} else {
			if (main2!=null && main2.getId()==Terrain.BLEND) {
				return main1;
			} else if (tieBreaker==main1 || tieBreaker==main2) {
				return tieBreaker;
			} else {
				return main1;
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
				drawThing(g2D,a,x,y);
			}
		}
	}
	
	public static void renderArenaMiscs(Graphics2D g2D, Arena a, Rectangle2D window) {
		double ts = Terrain.tileSize;
		int x1 = Math.max(0, (int) (window.getX() / ts));
		int y1 = Math.max(0, (int) (window.getY() / ts));
		int x2 = Math.min(a.getWidth() - 1, x1 + (int) (window.getWidth() / ts) + 1);
		int y2 = Math.min(a.getHeight() - 1, y1 + (int) (window.getHeight() / ts) + 1);
		
		for (int x = x1; x <= x2; x++) {
			for (int y = y1; y <= y2; y++) {
				drawMisc(g2D,a,x,y);
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
		int x2 = Math.min(a.getWidth(), x1 + (int) (window.getWidth() / ts) + 1);
		int y2 = Math.min(a.getHeight(), y1 + (int) (window.getHeight() / ts) + 1);
		
		for (int x = x1; x <= x2; x++) {
			Renderer.drawLine(g2D, x*ts, y1*ts, x*ts, y2*ts);
		}
		
		for (int y = y1; y <= y2; y++) {
			Renderer.drawLine(g2D, x1*ts, y*ts, x2*ts, y*ts);
		}
	}
	
	public static void renderSpriteConfig(Graphics2D g2D, EditorArena a, Rectangle2D window, boolean misc) {
		double ts = Terrain.tileSize;
		int x1 = Math.max(0, (int) (window.getX() / ts));
		int y1 = Math.max(0, (int) (window.getY() / ts));
		int x2 = Math.min(a.getWidth() - 1, x1 + (int) (window.getWidth() / ts) + 1);
		int y2 = Math.min(a.getHeight() - 1, y1 + (int) (window.getHeight() / ts) + 1);
		
		//double MARGIN = (1.0-SIZE)/2.0;
		
		g2D.setStroke(new BasicStroke(1));
		for (int x = x1; x <= x2; x++) {
			for (int y = y1; y <= y2; y++) {
				SpriteConfig sc;
				if (misc) {
					sc = a.get(x, y).getMiscConfig();
				} else {
					sc = a.get(x, y).getThingConfig();
				}
				if (sc!=null) {
					if (sc.flip) {
						Renderer.drawLine(g2D, (x+0.8)*ts, (y+0.2)*ts, (x+0.8)*ts, (y+0.8)*ts);
						Renderer.fillRect(g2D, (x+0.65)*ts, (y+0.3)*ts, 0.15*ts, 0.4*ts);
						Renderer.drawRect(g2D, (x+0.8)*ts, (y+0.3)*ts, 0.15*ts, 0.4*ts);
					}
					if (sc.rotation>0) {
						Renderer.fillArc(g2D, (x+0.3)*ts, (y+0.3)*ts, 0.2*ts, 0, Math.PI*sc.rotation/2, Arc2D.PIE);
					}
					Renderer.drawString(g2D, sc.spriteX+","+sc.spriteY, x*ts, (y+0.9)*ts);
				}
			}
		}	
	}
	
	
	
	public static void renderTrigger(Graphics2D g2D, EditorArena a, Rectangle2D window) {
		double ts = Terrain.tileSize;
		int x1 = Math.max(0, (int) (window.getX() / ts));
		int y1 = Math.max(0, (int) (window.getY() / ts));
		int x2 = Math.min(a.getWidth() - 1, x1 + (int) (window.getWidth() / ts) + 1);
		int y2 = Math.min(a.getHeight() - 1, y1 + (int) (window.getHeight() / ts) + 1);
		
		g2D.setStroke(new BasicStroke(1.5f));
		for (int x = x1; x <= x2; x++) {
			for (int y = y1; y <= y2; y++) {
				Trigger tr = a.get(x, y).getTrigger();
				if (tr!=null) {
					if (tr instanceof Trigger.SwitchOnTouchSide) {
						SwitchOnTouchSide sots = (SwitchOnTouchSide) tr;
						g2D.setColor(GUIFactory.UICOLOR);
						Point2D p = Geometry.PolarToCartesian(Terrain.tileSize/2, Math.PI*sots.getSide()/2);
						Point2D c = Utils.tileToMeter(x, y);
						Renderer.drawLine(g2D, c.getX(), c.getY(), c.getX()+p.getX(),c.getY()+p.getY());
					}
					g2D.setColor(GUIFactory.UICOLOR);
					for (TriggerEffect effect:tr.getEffects()) {
						if (effect instanceof TriggerEffect.TileSwitch) {
							TriggerEffect.TileSwitch tileSwitch = (TriggerEffect.TileSwitch) effect;
							Point p = tileSwitch.getTargetTile();
							if (x != p.x || y != p.y){
								drawLine(g2D,Utils.tileToMeter(x, y),Utils.tileToMeter(tileSwitch.getTargetTile()));
							} else {
								Point2D p2d = Utils.tileToMeter(p);
								drawCircle(g2D,p2d.getX(),p2d.getY(),Terrain.tileSize/4);
							}
							if (tileSwitch.getPreset().getSwitchThing()!=null) {
								drawImage(g2D,tileSwitch.getPreset().getSwitchThing().getImage(),0,0,ts,ts,p.x*ts,p.y*ts,ts/2,ts/2);
							}
						} else if (effect instanceof TriggerEffect.GiveData) {
							TriggerEffect.GiveData data = (TriggerEffect.GiveData) effect;
							drawString(g2D, String.valueOf(data.getDataId()), (x+0.5)*ts, (y+0.5)*ts);
						}
					}
				}
			}
		}
	}

	public static void renderData(Graphics2D g2D, Arena arena, Rectangle2D window) {
		double ts = Terrain.tileSize;
		int x1 = Math.max(0, (int) (window.getX() / ts));
		int y1 = Math.max(0, (int) (window.getY() / ts));
		int x2 = Math.min(arena.getWidth() - 1, x1 + (int) (window.getWidth() / ts) + 1);
		int y2 = Math.min(arena.getHeight() - 1, y1 + (int) (window.getHeight() / ts) + 1);
		
		g2D.setStroke(new BasicStroke(2f));
		Color c = Color.WHITE;
		int alpha = getAlpha();
		
		for (int x = x1; x <= x2; x++) {
			for (int y = y1; y <= y2; y++) {
				Trigger tr = arena.get(x, y).getTrigger();
				if (tr!=null) {
					for (TriggerEffect effect:tr.getEffects()) {
						if (effect instanceof TriggerEffect.GiveData) {
							TriggerEffect.GiveData data = (TriggerEffect.GiveData) effect;
							if (!arena.dataObtained(data.getDataId())) {
								g2D.setColor(new Color(c.getRed(),c.getGreen(),c.getBlue(),alpha));
								drawRect(g2D,x*ts,y*ts,ts,ts);
								//drawCenteredString(g2D, "?", GUIFactory.font_s_bold, (x+0.5)*ts, (y+0.5)*ts);
								drawCenteredString(g2D, ""+data.getDataId(), GUIFactory.font_s_bold, (x+0.5)*ts, (y+0.5)*ts);
							} else {
								g2D.setColor(new Color(c.getRed(),c.getGreen(),c.getBlue(),alpha/4));
								drawRect(g2D,x*ts,y*ts,ts,ts);
							}
						}
					}
				}
			}
		}
	}
	
	public void renderSpawnLocations(Graphics2D g2D, EditorArena a, Rectangle2D window, final int players) {
		double ts = Terrain.tileSize;
		int x1 = Math.max(0, (int) (window.getX() / ts));
		int y1 = Math.max(0, (int) (window.getY() / ts));
		int x2 = Math.min(a.getWidth() - 1, x1 + (int) (window.getWidth() / ts) + 1);
		int y2 = Math.min(a.getHeight() - 1, y1 + (int) (window.getHeight() / ts) + 1);
		
		for (int x = x1; x <= x2; x++) {
			for (int y = y1; y <= y2; y++) {
				SpawnPoint sp = a.spawns[x][y];
				if (sp != null && sp.players<=players) {
					Renderer.renderSpawnPoint(g2D,sp);
				}
			}
		}
	}
	
	private static void renderSpawnPoint(Graphics2D g2D, SpawnPoint sp) {
		Point2D p = Utils.tileToMeter(sp.x, sp.y);
		int typeId = sp.setups.get(0).id;
		double x = p.getX();
		double y = p.getY();
		double direction = 0;
		double r = Entity.BASE_RADIUS*ClassStats.classStats.get(typeId).getSize();
		int type = sp.setups.get(0).id;
		int team = sp.team;
		boolean invi = false;
		
		int body = ClassStats.classStats.get(type).getBodyId();
		double sizeF = r/Entity.BASE_RADIUS;
		if (invi) {
			float dash1[] = {10.0f};
		    BasicStroke dashed = new BasicStroke(CHARACTER_WIDTH*ppm,
		    		BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER,10.0f, dash1, 0.0f);
			g2D.setStroke(dashed);
			g2D.setColor(teamColors[team]);
			drawCircle(g2D,x,y,r);
			return;
		} else {
			g2D.setStroke(new BasicStroke(CHARACTER_WIDTH*ppm));
		}
		//fillCircle(g2D,x, y,r);
		g2D.rotate(-direction,x*ppm,y*ppm);
		g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		double openArc;
		switch (body) {
			case 0:
				openArc = 2;
				g2D.setColor(teamColors2[team].darker());
				//fillArc(g2D,x,y,r, openArc/2, Math.PI*2 - openArc, Arc2D.CHORD);
				g2D.setColor(teamColors[team].darker());
				//fillArc(g2D,x,y,r, openArc/2, Math.PI*2 - openArc, Arc2D.CHORD);
				//fillRect(g2D,x-r*0.8,y-r*0.8,r*1.6,r*1.6);
				fillPolygon(g2D,x,y,r*1.2,5);
				//drawArc(g2D,x,y,r, openArc/2, Math.PI*2 - openArc, Arc2D.OPEN);
				break;
			case 1:
				openArc = 2;
				g2D.setColor(teamColors2[team].darker());
				fillArc(g2D,x,y,r,r*0.9, openArc/2, Math.PI*2 - openArc, Arc2D.CHORD);
				g2D.setColor(teamColors[team].darker());
				drawArc(g2D,x,y,r,r*0.9, openArc/2, Math.PI*2 - openArc, Arc2D.OPEN);
				break;
			case 2:
				openArc = 2;
				g2D.setColor(teamColors2[team].darker());
				fillArc(g2D,x,y,r,r*1.1, openArc/2, Math.PI*2 - openArc, Arc2D.CHORD);
				g2D.setColor(teamColors[team].darker());
				drawArc(g2D,x,y,r,r*1.1, openArc/2, Math.PI*2 - openArc, Arc2D.OPEN);
				break;
			default:
				break;
		}
		g2D.rotate(direction,x*ppm,y*ppm);
		
		// draw head
		Point2D h = Geometry.PolarToCartesian(r*0.35, direction);
		double hx = x+h.getX();
		double hy = y-h.getY();
		//g2D.setColor(teamColors[team].brighter());
		//fillCircle(g2D,x+h.getX(),y-h.getY(),hr);
		
		
		BufferedImage headImage = ImageBlender.deepCopy(Sprite.heads.get(type));
		Graphics2D gunG = (Graphics2D)headImage.getGraphics();
		gunG.setComposite(new OverlayComposite(1.0f));
		gunG.setColor(teamColors[team].darker());
		gunG.fillRect(0, 0, headImage.getWidth(), headImage.getHeight());
		gunG.dispose();
		
		double hW = sizeF*headImage.getWidth()/DEFAULT_PPM;
		double hH = sizeF*headImage.getHeight()/DEFAULT_PPM;
		g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		
		g2D.rotate(-direction,hx*ppm,hy*ppm);
		Renderer.drawImage(g2D,headImage, hx-hW/2, hy-hH/2,hW,hH);
		g2D.rotate(direction,hx*ppm,hy*ppm);
		
		g2D.setStroke(new BasicStroke(1));
	}
	
	public static void renderEditorLightSource(Graphics2D g2D, EditorArena a, Rectangle2D window) {
		double ts = Terrain.tileSize;
		for (Light l:a.getLightList()) {
			g2D.setColor(new Color(l.getColor()));
			fillRect(g2D,l.getX()-ts/4,l.getY()-ts/4,ts/2,ts/2);
			g2D.setStroke(new BasicStroke(1));
			Renderer.drawCircle(g2D, l.getX(), l.getY(), l.getRange()*ts);
		}
	}
	
	public static void renderEditorParticleSource(Graphics2D g2D, EditorArena a, Rectangle2D window) {
		double ts = Terrain.tileSize;
		for (int x=0;x<a.pss.length;x++) {
			for (int y=0;y<a.pss[0].length;y++) {
				ParticleSource ps = a.pss[x][y];
				if (ps!=null) {
					g2D.setColor(ps.getBaseParticle().getColor());
					fillRect(g2D,(x+0.25)*ts,(y+0.25)*ts,ts/2,ts/2);
					g2D.setStroke(new BasicStroke(2));
					double sx = ps.spread.x;
					double sy = ps.spread.y;
					Renderer.drawRect(g2D, (x+0.5)*ts-sx,(y+0.5)*ts-sy,sx*2,sy*2);
				}
			}
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
	
	public static void renderHardLight(Graphics2D g2D, int[][] lightMap, Rectangle2D window) {
		double ts = Terrain.tileSize;
		double ts2 = ts/2;
		int x1 = Math.max(0, (int) (window.getX() / ts));
		int y1 = Math.max(0, (int) (window.getY() / ts));
		int x2 = Math.min(lightMap.length/2 - 1, x1 + (int) (window.getWidth() / ts) + 1);
		int y2 = Math.min(lightMap[0].length/2 - 1, y1 + (int) (window.getHeight() / ts) + 1);
		for (int x = x1*2; x <= x2*2; x++) {
			for (int y = y1*2; y <= y2*2; y++) {
				g2D.setColor(new Color(lightMap[x][y],false));
				fillRect(g2D,x*ts2,y*ts2, ts2, ts2);
			}
		}
	}
	
	public static void renderMatrixHardLight(Graphics2D g2D, int[][] lightMap, Rectangle2D window) {
		double ts = Terrain.tileSize;
		double ts2 = ts/2;
		int x1 = Math.max(0, (int) (window.getX() / ts));
		int y1 = Math.max(0, (int) (window.getY() / ts));
		int x2 = Math.min(lightMap.length/2 - 1, x1 + (int) (window.getWidth() / ts) + 1);
		int y2 = Math.min(lightMap[0].length/2 - 1, y1 + (int) (window.getHeight() / ts) + 1);
		
		BufferedImage softShadow = ImageBlender.createImage(toPixel(ts2), toPixel(ts2), Transparency.TRANSLUCENT);
		Graphics2D s1G = softShadow.createGraphics();
		s1G.setColor(GUIFactory.UICOLOR);
		s1G.setStroke(new BasicStroke(1.1f,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_BEVEL));
		drawLine(s1G,0,0,ts2,ts2);
		s1G.dispose();
		
		BufferedImage hardShadow = ImageBlender.createImage(toPixel(ts2), toPixel(ts2), Transparency.TRANSLUCENT);
		Graphics2D s2G = hardShadow.createGraphics();
		s2G.setColor(GUIFactory.UICOLOR);
		s2G.setStroke(new BasicStroke(1f,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_BEVEL));
		drawLine(s2G,0,ts2,ts2,0);
		drawLine(s2G,0,0,ts2,ts2);
		s2G.dispose();
		
		// draw 
		for (int x = x1*2; x <= x2*2; x++) {
			for (int y = y1*2; y <= y2*2; y++) {
				int max = getMaxComponent(lightMap[x][y]);
				if (max<255) {
					if (max<255/2) {
						Renderer.drawImage(g2D, hardShadow, x*ts2, y*ts2, ts2, ts2);
					} else {
						Renderer.drawImage(g2D, softShadow, x*ts2, y*ts2, ts2, ts2);
					}
				}
			}
		}
	}
	
	public static int getMaxComponent(int colorCode) {
		Color c = new Color(colorCode);
		return Math.max(c.getRed(), Math.max(c.getGreen(), c.getBlue()));
	}
	
	public static void drawString(Graphics2D g2D, String s, double x, double y) {
		g2D.drawString(s, toPixel(x), toPixel(y));
	}
	
	public static void drawCenteredString(Graphics2D g2D, String s, Font f, double x, double y) {
	    // Get the FontMetrics
	    FontMetrics metrics = g2D.getFontMetrics(f);
	    // Determine the X coordinate for the text
	    int px = toPixel(x) - metrics.stringWidth(s) / 2;
	    // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
	    int py = toPixel(y) - metrics.getHeight() / 2 + metrics.getAscent();
	    
	    // Set the font
	    g2D.setFont(f);
	    
	    // Draw the String
	    g2D.drawString(s, px, py);
	}
	
	public static void drawLine(Graphics2D g2D, double x1, double y1, double x2, double y2) {
		g2D.draw(new Line2D.Double(x1*ppm,y1*ppm,x2*ppm,y2*ppm));
	}
	
	public static void drawLine(Graphics2D g2D, Point2D p1, Point2D p2) {
		drawLine(g2D,p1.getX(),p1.getY(),p2.getX(),p2.getY());
	}
	
	private static void drawArc(Graphics2D g2D, double cx, double cy, double cr, double start, double extent, int type) {
		g2D.draw(new Arc2D.Double((cx-cr)*ppm,(cy-cr)*ppm,(cr*2)*ppm,(cr*2)*ppm,Math.toDegrees(start),Math.toDegrees(extent),type));
	}
	
	private static void fillArc(Graphics2D g2D, double cx, double cy, double cr, double start, double extent, int type) {
		g2D.fill(new Arc2D.Double((cx-cr)*ppm,(cy-cr)*ppm,(cr*2)*ppm,(cr*2)*ppm,Math.toDegrees(start),Math.toDegrees(extent),type));
	}
	
	private static void drawArc(Graphics2D g2D, double cx, double cy, double cw, double ch, double start, double extent, int type) {
		g2D.draw(new Arc2D.Double((cx-cw)*ppm,(cy-ch)*ppm,(cw*2)*ppm,(ch*2)*ppm,Math.toDegrees(start),Math.toDegrees(extent),type));
	}
	
	private static void fillArc(Graphics2D g2D, double cx, double cy, double cw, double ch, double start, double extent, int type) {
		g2D.fill(new Arc2D.Double((cx-cw)*ppm,(cy-ch)*ppm,(cw*2)*ppm,(ch*2)*ppm,Math.toDegrees(start),Math.toDegrees(extent),type));
	}
	
	static void drawCircle(Graphics2D g2D, double x, double y, double radius) {
		//g2D.drawOval(toPixel(x-radius), toPixel(y-radius), toPixel(radius*2), toPixel(radius*2));
		g2D.draw(new Ellipse2D.Double((x-radius)*ppm, (y-radius)*ppm, radius*2*ppm, radius*2*ppm));
	}
	
	static void fillCircle(Graphics2D g2D, double x, double y, double radius) {
		//g2D.fillOval(toPixel(x-radius), toPixel(y-radius), toPixel(radius*2), toPixel(radius*2));
		g2D.fill(new Ellipse2D.Double((x-radius)*ppm, (y-radius)*ppm, radius*2*ppm, radius*2*ppm));
	}
	
	public static void drawArenaImage(Graphics2D g2D, Image image, Rectangle2D window) {
		drawImage(g2D, image, window.getX(),window.getY(),window.getWidth(),window.getHeight(),
				window.getX(),window.getY(),window.getWidth(),window.getHeight());
	}
	
	static void drawImage(Graphics2D g2D, BufferedImage image, double x, double y, double w, double h) {
		AffineTransform originalTransform = g2D.getTransform();
		
        g2D.translate(x*ppm, y*ppm);
        g2D.scale(w*ppm/image.getWidth(), h*ppm/image.getHeight());
        
        g2D.drawImage(image, 0, 0, null);
	    g2D.setTransform(originalTransform);
		
		/*
		int screenX = toPixel(x);
		int screenY = toPixel(y);
		int screenW = toPixel(w+x-Renderer.toMeter(screenX));
		int screenH = toPixel(h+y-Renderer.toMeter(screenY));
		g2D.drawImage(image, screenX, screenY, screenW, screenH, null);
		*/
	}
	
	static void drawImage(Graphics2D g2D, Image image, double sx, double sy, double sw, double sh, double x, double y, double w, double h) {
		int screenX = toPixel(x);
		int screenY = toPixel(y);
		int screenW = toPixel(w+x-Renderer.toMeter(screenX));
		int screenH = toPixel(h+y-Renderer.toMeter(screenY));
		try {
			g2D.drawImage(image, screenX, screenY, screenW+screenX, screenH+screenY,toPixelDefault(sx), toPixelDefault(sy),
					toPixelDefault(sx+sw), toPixelDefault(sy+sh),
					null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	static void fillRect(Graphics2D g2D, double x, double y, double w, double h) {
		g2D.fill(new Rectangle2D.Double(x*ppm,y*ppm,w*ppm,h*ppm));
	}
	
	static void fillPolygon(Graphics2D g2D, Point2D[] points) {
		Polygon polygon = new Polygon();
		for (Point2D point:points) {
			polygon.addPoint(toPixel(point.getX()),toPixel(point.getY()));
		}
		g2D.fillPolygon(polygon);
	}
	
	static void fillPolygon(Graphics2D g2D, double x, double y, double r, int sides) {
		Polygon polygon = new Polygon();
		for (int s=0;s<sides;s++) {
			double angle = s*Math.PI*2/sides;
			Point2D p = Geometry.PolarToCartesian(r, angle);
			polygon.addPoint(toPixel(x+p.getX()),toPixel(y+p.getY()));
		}
		g2D.fillPolygon(polygon);
	}
	
	static void drawPolygon(Graphics2D g2D, double x, double y, double r, int sides) {
		Polygon polygon = new Polygon();
		for (int s=0;s<sides;s++) {
			double angle = s*Math.PI*2/sides;
			Point2D p = Geometry.PolarToCartesian(r, angle);
			polygon.addPoint(toPixel(x+p.getX()),toPixel(y+p.getY()));
		}
		g2D.drawPolygon(polygon);
	}
	
	static void drawRect(Graphics2D g2D, double x, double y, double w, double h) {
		//g2D.drawRect(toPixel(x), toPixel(y), toPixel(w), toPixel(h));
		g2D.draw(new Rectangle2D.Double(x*ppm,y*ppm,w*ppm,h*ppm));
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
		Renderer.ppm = (float)ppm;
	}
	
	public static float getPPM() {
		return Renderer.ppm;
	}
	
	public static int toPixel(double value) {
		return (int)(value*Renderer.ppm+0.5);
	}
	
	public static int toPixelDefault(double value) {
		return (int)(value*Renderer.DEFAULT_PPM+0.5);
	}
	
	public static float toMeter(int pixel) {
		return pixel/Renderer.ppm;
	}
	
	public static void renderProtection(Graphics2D g2D, int tileX, int tileY, int coverType) {
		if (coverType>0) {
			double w = 0.7;
			double x = (tileX+(1-w)/2)*Terrain.tileSize;
			double y = (tileY+(1-w)/2)*Terrain.tileSize;
			w = w*Terrain.tileSize;
			drawImage(g2D,Sprite.COVER[coverType-1],x,y,w,w);
		}
	}

	public void redrawArenaImage(Arena a, int tx, int ty, int layer) {
		if (layer<2) {
			BufferedImage newLowImage = ImageBlender.drawArena(a,0);
			Renderer.drawArenaLayer(newLowImage.createGraphics(), a, 1, true, true, true);
			lowImage = newLowImage;
		} else {
			BufferedImage newHighImage = ImageBlender.drawArena(a,2);
			Renderer.drawArenaLayer(newHighImage.createGraphics(), a, 3, true, true, true);
			highImage = newHighImage;
		}
	}

	public void redrawLightImage(Arena arena) {
		Graphics2D g2D = (Graphics2D) lightMap.getGraphics();
		Renderer.renderHardLight(g2D, arena.getLightmap(), new Rectangle2D.Double(0, 0, arena.getWidthMeter(), arena.getHeightMeter()));
	}
	
	public static void drawArenaLayer(Graphics2D g2D, Arena a, int layer, boolean terrain, boolean thing, boolean misc) {
		if (terrain && layer==0) {
			Renderer.renderArenaBGFlat(g2D, a, new Rectangle2D.Double(0,0,a.getWidthMeter(),a.getHeightMeter()));
		}
		for (int x=0;x<a.getWidth();x++) {
			for (int y=0;y<a.getHeight();y++) {
				Tile t = a.get(x, y);
				
				Thing th = t.getThing();
				if (thing && th!=null && th.getLayer()==layer) {
					drawThing(g2D,a,x,y);
				}
				Thing mi = t.getMisc();
				if (misc && mi != null) {
					int tlayer = th==null?0:th.getLayer();
					int mlayer = mi.getLayer();
					if (tlayer==mlayer) {
						mlayer = Math.min(3, mlayer+1);
					} else {
						mlayer = Math.max(tlayer, mlayer);
					}
					if (mlayer==layer) {
						drawMisc(g2D,a,x,y);
					}
				}
			}
		}
	}
	
	public static void drawArenaMarks(Graphics2D g2D, Arena a, Rectangle2D window) {
		
	}
	
	public static int getAlpha() {
		double FPHASE = 1500;
	    double t = Math.abs(FPHASE-(System.currentTimeMillis()%(FPHASE*2)))/FPHASE;
		int alpha = (int)(80 + t*80);
		return alpha;
	}
}