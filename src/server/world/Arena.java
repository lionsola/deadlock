package server.world;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import editor.DataManager;
import editor.DataManager.ArenaData;

/**
 * Class used to model an Arena - the Arena is what the characters will fight in and what they will
 * move around in.
 * 
 * The class uses 3 files in order to generate a map, the map.bmp file, the map_x.bmp file (spawn
 * points) and the map.map file which contains the information for mapping pixel colours to specific
 * textures from the map.bmp file.
 * 
 * @author Team D1
 * @author Anh D Pham
 */
public class Arena {
	public static final int T1_COLOR = 0x00ff00; // red spawn colour
	public static final int T2_COLOR = 0xff0000; // green spawn colour

	protected String name; // the name of the map
	private transient List<Point2D> t1Spawns; // spawn points of team 1
	private transient List<Point2D> t2Spawns; // spawn points of team 2
	protected List<Light> lightList;
	
	protected int[][] lightMap;
	
	protected Tile[][] tMap;
	
	/**
	 * Creating a new Arena
	 * 
	 * @param name
	 *            the name of the file that will be read in to generate the arena.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public Arena(String name, HashMap<Integer,Terrain> tileTable, HashMap<Integer,Thing> objectTable, HashMap<Integer,TriggerPreset> triggerTable) {
		this((ArenaData) DataManager.loadObject("resource/map/"+name+".arena"),tileTable,objectTable,triggerTable);
	}
	
	public Arena(ArenaData ad, HashMap<Integer,Terrain> tileTable, HashMap<Integer,Thing> objectTable, HashMap<Integer,TriggerPreset> triggerTable) {
		initialize(ad,tileTable,objectTable,triggerTable);
	}

	public Arena(String name, int width, int height) {
		this.name = name;
		this.lightMap = new int[width][height];
		this.tMap = new Tile[width][height];
		for (int x=0;x<width;x++) {
			for (int y=0;y<height;y++) {
				tMap[x][y] = new Tile();
			}
		}
		this.lightList = new LinkedList<Light>();
	}
	
	private void initialize(ArenaData ad, HashMap<Integer,Terrain> tileTable, HashMap<Integer,Thing> objectTable, HashMap<Integer,TriggerPreset> triggerTable) {
		this.name = ad.name;
		int width = ad.tileMap.length;
		int height = ad.tileMap[0].length;
		tMap = new Tile[width][height];
		Terrain[][] tileMap = new Terrain[width][height];
		Thing[][] objectMap = new Thing[width][height];
		DataManager.loadFromTable(ad.tileMap,tileTable,tileMap);
		DataManager.loadFromTable(ad.objectMap,objectTable,objectMap);
		
		for (int x=0;x<width;x++) {
			for (int y=0;y<height;y++) {
				tMap[x][y] = new Tile();
				tMap[x][y].setTerrain(tileMap[x][y]);
				tMap[x][y].setThing(objectMap[x][y]);
				if (ad.configMap!=null) {
					tMap[x][y].setSpriteConfig(ad.configMap[x][y]);
				}
				if (ad.triggerMap!=null) {
					Trigger tr = ad.triggerMap[x][y];
					tMap[x][y].setTrigger(tr);
					if (tr instanceof Trigger.TileSwitchTrigger) {
						Trigger.TileSwitchTrigger tst = (Trigger.TileSwitchTrigger)tr;
						tst.setPreset(triggerTable.get(tst.presetID));
						if (tst.tp==null) {
							tMap[x][y].setTrigger(null);
						}
					}
				}
			}
		}
		generateSpawnPoints();
		lightList = ad.lights;
		if (lightList==null) {
			lightList = new LinkedList<Light>();
		}
		generateLightMap();
	}
	
	/**
	 * Return the spawn list for a given team.
	 * @param team The team to get the spawn list for.
	 */
	public List<Point2D> getSpawn(int team) {
		return (team == 0 ? t1Spawns : t2Spawns);
	}

	/**
	 * Used to return a specific Tile.
	 * 
	 * @param x
	 *            the x coordinate of the tile
	 * @param y
	 *            the y coordinate of the tile
	 * 
	 * @return the tile located at x, y.
	 */
	/*
	public Thing get(int x, int y) {
		if (x >= getWidth() || y >= getHeight() || x < 0 || y < 0)
			return objectMap[0][0];
		else
			return objectMap[x][y];
	}
	*/
	
	public Tile get(int x, int y) {
		if (x >= getWidth() || y >= getHeight() || x < 0 || y < 0)
			return tMap[0][0];
		else
			return tMap[x][y];
	}

	/**
	 * Used to return a specific Tile.
	 * 
	 * @param x
	 *            the x coordinate of the tile
	 * @param y
	 *            the y coordinate of the tile
	 * 
	 * @return the tile located at x, y.
	 */
	public Tile getTileAt(double x, double y) {
		return get((int)(x/Terrain.tileSize),(int)(y/Terrain.tileSize));
	}

	/**
	 * Returns the height of the tilemap.
	 * 
	 * @return the height of the tilemap.
	 */
	public int getHeight() {
		return tMap[0].length;
	}

	/**
	 * Returns the width of the tilemap.
	 * 
	 * @return the width of the tilemap.
	 */
	public int getWidth() {
		return tMap.length;
	}
	

	/**
	 * Returns the width of the tilemap in pixels.
	 * 
	 * @return the width of the tilemap in pixels.
	 */
	public double getWidthMeter() {
		return getWidth() * Terrain.tileSize;
	}

	/**
	 * Returns the height of the tilemap in pixels.
	 * 
	 * @return the height of the tilemap in pixels.
	 */
	public double getHeightMeter() {
		return getHeight() * Terrain.tileSize;
	}

	public void generateLightMap() {
		int INITIAL_LIGHT = 0x0f;
		int INITIAL_LIGHT_RGB = new Color(INITIAL_LIGHT,INITIAL_LIGHT,INITIAL_LIGHT).getRGB();
		
		int[][] lightMap = new int[getWidth()*2][getHeight()*2];
		for (int[] lights:lightMap) {
			Arrays.fill(lights, INITIAL_LIGHT_RGB);
		}
		
		for (Light l:lightList) {
			int x1 = Math.min(getWidth()-1, Math.max(0,l.getX() - l.getRange()));
			int x2 = Math.min(getWidth()-1, Math.max(0,l.getX() + l.getRange()));
			int y1 = Math.min(getHeight()-1, Math.max(0, l.getY() - l.getRange()));
			int y2 = Math.min(getHeight()-1, Math.max(0, l.getY() + l.getRange()));
			Point2D lightPos = new Point2D.Double(l.getX()+0.5,l.getY()+0.5);
			for (int x=x1*2;x<=x2*2;x++) {
				for (int y=y1*2;y<=y2*2;y++) {
					Point2D dest = new Point2D.Double((x+0.5)/2.0, (y+0.5)/2.0);
					Point2D minLCDest = null;
					double minDist = Double.MAX_VALUE;
					if (!get(x/2,y/2).isClear()) {
						for (int i=-1;i<=1;i+=2) {
							for (int j=-1;j<=1;j+=2) {
								Point2D lcDest = new Point2D.Double(dest.getX()+i*0.25,dest.getY()+j*0.25);
								double dist = lcDest.distance(lightPos);
								if (dist<minDist) {
									minDist = dist;
									minLCDest = lcDest;
								}
							}
						}
					} else {
						minLCDest = new Point2D.Double(x/2+0.5,y/2+0.5);
					}
					
					double d = 1.0-lightPos.distance(minLCDest)/l.getRange();
					if (d>0) {
						int blockCount = 0;
						final double MAX_BLOCK_COUNT = 2;
						final double LINECAST_DISTANCE = 0.25; 
						List<Point2D> points = Geometry.getLineSamples(lightPos,
								minLCDest, LINECAST_DISTANCE);
						for (Point2D p:points) {
							if (p.distance(minLCDest)>0.01 && !get((int)p.getX(),(int)p.getY()).isClear()) {
								blockCount ++;
							}
							if (blockCount>=MAX_BLOCK_COUNT) {
								break;
							}
						}
						if (blockCount>=MAX_BLOCK_COUNT) {
							continue;
						}
						else if (blockCount>0) {
							d = d*(1-blockCount/MAX_BLOCK_COUNT);
						}
						int sRGB = l.getColor();
						int sR = ((sRGB >> 16) & 0xFF) *(255-INITIAL_LIGHT)/255;
						int sG = ((sRGB >> 8) & 0xFF) *(255-INITIAL_LIGHT)/255;
						int sB = ((sRGB) & 0xFF) *(255-INITIAL_LIGHT)/255;
						
						int dRGB = lightMap[x][y];
						int dR = (dRGB >> 16) & 0xFF;
						int dG = (dRGB >> 8) & 0xFF;
						int dB = (dRGB) & 0xFF;
						
						int r = Math.min(255, (int) (sR * d + dR));
						int g = Math.min(255, (int) (sG * d + dG));
						int b = Math.min(255, (int) (sB * d + dB));
						
						lightMap[x][y] = r << 16 | g << 8 | b; 
					}
				}
			}
		}
		this.lightMap = lightMap;
	}
	
	synchronized private void generateSpawnPoints() {
		t1Spawns = new LinkedList<Point2D>();
		t2Spawns = new LinkedList<Point2D>();
		for (int x=0;x<getWidth();x++) {
			for (int y=0;y<getWidth();y++) {
				Thing t = get(x,y).getThing();
				if (t!=null) {
					if (t.getId()==T1_COLOR) {
						t1Spawns.add(new Point2D.Double((x+0.5)*Terrain.tileSize, (y+0.5)*Terrain.tileSize));
					} else if (t.getId()==T2_COLOR) {
						t2Spawns.add(new Point2D.Double((x+0.5)*Terrain.tileSize, (y+0.5)*Terrain.tileSize));
					}
				}
			}
		}
	}
	
	public void setTerrain(int x, int y, Terrain t) {
		if (x>=0 && x<getWidth() && y>=0 && y<getHeight())
			tMap[x][y].setTerrain(t);
	}

	public void setThing(int x, int y, Thing ob) {
		if (x>=0 && x<getWidth() && y>=0 && y<getHeight())
			tMap[x][y].setThing(ob);
	}
	
	/**
	 * Returns the name of the arena.
	 * 
	 * @return the name of the arena.
	 */
	public String getName() {
		return name;
	}

	public Tile get(Point top) {
		return get(top.x,top.y);
	}

	public int getLightAt(Point2D p) {
		try {
			return lightMap[(int)(p.getX()*2/Terrain.tileSize)][(int)(p.getY()*2/Terrain.tileSize)];
		} catch (Exception e){
			return 0;
		}
	}
	
	public int[][] getLightmap() {
		return lightMap;
	}
	
	public List<Light> getLightList() {
		return lightList;
	}
	/**
	 * Render the Arena.
	 * 
	 * @param g
	 * @param window
	 *            the whole game window.
	 */
	
}
