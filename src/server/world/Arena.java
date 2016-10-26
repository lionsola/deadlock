package server.world;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

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

	private String name; // the name of the map
	private transient List<Point2D> t1Spawns; // spawn points of team 1
	private transient List<Point2D> t2Spawns; // spawn points of team 2
	protected List<Light> lightList;
	
	int[][] lightMap;
	
	protected Tile[][] tMap;
	
	
	public Arena(File file, HashMap<Integer,Terrain> tileTable, HashMap<Integer,Thing> objectTable) {
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			
			Scanner fileSc = new Scanner(fileInputStream);
			this.name = fileSc.nextLine();
			
			fileSc.close();
			ArenaData ad = (ArenaData) DataManager.loadObject("resource/map/"+name+".arena");
			initialize(ad,tileTable,objectTable);
			
			//tileMap = new TileBG[width][height];
			//objectMap = new Tile[width][height];
			//DataManager.loadBitMap(name+"_bg", tileTable, tileMap); // load the tile map
			//DataManager.loadBitMap(name+"_ob", objectTable,objectMap);
			//loadPositionMap(name); // and load the position map
		} catch (Exception e) {
			System.err.println("Error while reading map");
			e.printStackTrace();
		}
	}
	
	/**
	 * Creating a new Arena
	 * 
	 * @param name
	 *            the name of the file that will be read in to generate the arena.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public Arena(String name, HashMap<Integer,Terrain> tileTable, HashMap<Integer,Thing> objectTable) throws FileNotFoundException, IOException {
		this((ArenaData) DataManager.loadObject("resource/map/"+name+".arena"),tileTable,objectTable);
	}
	
	public Arena(ArenaData ad, HashMap<Integer,Terrain> tileTable, HashMap<Integer,Thing> objectTable) {
		initialize(ad,tileTable,objectTable);
	}

	public Arena(String name, int width, int height, HashMap<Integer,Terrain> tileTable, HashMap<Integer,Thing> objectTable) {
		this.name = name;
		this.lightMap = new int[width][height];
		this.tMap = new Tile[width][height];
		for (int x=0;x<width;x++) {
			for (int y=0;y<height;y++) {
				tMap[x][y] = new Tile();
			}
		}
	}
	
	private void initialize(ArenaData ad, HashMap<Integer,Terrain> tileTable, HashMap<Integer,Thing> objectTable) {
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
		int[][] lightMap = new int[getWidth()][getHeight()];
		for (Light l:lightList) {
			int x1 = Math.min(getWidth()-1, Math.max(0,l.getX() - l.getRange()));
			int x2 = Math.min(getWidth()-1, Math.max(0,l.getX() + l.getRange()));
			int y1 = Math.min(getHeight()-1, Math.max(0, l.getY() - l.getRange()));
			int y2 = Math.min(getHeight()-1, Math.max(0, l.getY() + l.getRange()));
			for (int x=x1;x<=x2;x++) {
				for (int y=y1;y<=y2;y++) {
					double d = 1.0-Point.distance(l.getX(), l.getY(), x, y)/l.getRange();
					if (d>0) {
						List<Point2D> points = Geometry.getLineSamples(l.getX()+0.5, l.getY()+0.5, x+0.5, y+0.5, 0.33);
						int blockCount = 0;
						double MAX_BLOCK_COUNT = 3;
						Tile stopWall = null;
						for (Point2D p:points) {
							if (!get((int)p.getX(),(int)p.getY()).isClear()) {
								stopWall = get((int)p.getX(),(int)p.getY());
								blockCount ++;
							}
							if (blockCount>=MAX_BLOCK_COUNT) {
								break;
							}
						}
						if (stopWall!=get(x,y)) {
							if (blockCount>=MAX_BLOCK_COUNT) {
								continue;
							} else {
								d = d*(1-blockCount/MAX_BLOCK_COUNT);
							}
						}
						int sRGB = l.getColor();
						int sR = (sRGB >> 16) & 0xFF;
						int sG = (sRGB >> 8) & 0xFF;
						int sB = (sRGB) & 0xFF;
						
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
				if (get(x,y).getThing().getId()==T1_COLOR) {
					t1Spawns.add(new Point2D.Double((x+0.5)*Terrain.tileSize, (y+0.5)*Terrain.tileSize));
				} else if (get(x,y).getThing().getId()==T2_COLOR) {
					t2Spawns.add(new Point2D.Double((x+0.5)*Terrain.tileSize, (y+0.5)*Terrain.tileSize));
				}
			}
		}
	}
	
	public void setTerrain(int x, int y, Terrain t) {
		if (x>0 && x<getWidth() && y>0 && y<getHeight())
			tMap[x][y].setTerrain(t);
	}

	public void setThing(int x, int y, Thing ob) {
		if (x>0 && x<getWidth() && y>0 && y<getHeight())
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
