package server.world;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;

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
	public static final int LIGHT_COLOR = 0xffff00; // light colour
	public static final int LIGHT_RANGE = 250;

	private String name; // the name of the map
	private TileBG[][] tileMap; // represents each tile and their position, X and Y
	private Tile[][] objectMap;
	private transient List<Point2D> t1Spawns; // spawn points of team 1
	private transient List<Point2D> t2Spawns; // spawn points of team 2	
	private int[][] lightMap;
	
	public Arena(File file, HashMap<Integer,TileBG> tileTable, HashMap<Integer,Tile> objectTable) {
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			
			Scanner fileSc = new Scanner(fileInputStream);
			this.name = fileSc.nextLine();
			int width = fileSc.nextInt();
			int height = fileSc.nextInt();
			
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
	public Arena(String name, HashMap<Integer,TileBG> tileTable, HashMap<Integer,Tile> objectTable) throws FileNotFoundException, IOException {
		this((ArenaData) DataManager.loadObject("resource/map/"+name+".arena"),tileTable,objectTable);
	}
	
	public Arena(ArenaData ad, HashMap<Integer,TileBG> tileTable, HashMap<Integer,Tile> objectTable) {
		initialize(ad,tileTable,objectTable);
	}

	private void initialize(ArenaData ad, HashMap<Integer,TileBG> tileTable, HashMap<Integer,Tile> objectTable) {
		this.name = ad.name;
		int width = ad.tileMap.length;
		int height = ad.tileMap[0].length;
		tileMap = new TileBG[width][height];
		objectMap = new Tile[width][height];
		DataManager.loadFromTable(ad.tileMap,tileTable,tileMap);
		DataManager.loadFromTable(ad.objectMap,objectTable,objectMap);
		generateSpawnPoints();
		lightMap = ad.lightMap;
		if (t1Spawns==null) {
			t1Spawns = new LinkedList<Point2D>();
			
		}
		if (t1Spawns.size()==0) {
			t1Spawns.add(new Point(3,3));
		}
		if (t2Spawns==null) {
			t2Spawns = new LinkedList<Point2D>();
		}
		if (t2Spawns.size()==0) {
			t2Spawns.add(new Point(width-3,height-3));
		}
		if (lightMap==null) {
			lightMap = new int[width][height];
		}
	}
	
	/**
	 * This function is used to load the position map.
	 * 
	 * @param name
	 *            the name of the map file from which we will load the tile map.
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private void loadPositionMap(String name) throws IOException, FileNotFoundException {
		// load position map
		BufferedImage image = ImageIO.read(new FileInputStream("resource/map/" + name + "_x.bmp"));

		t1Spawns = new ArrayList<Point2D>();
		t2Spawns = new ArrayList<Point2D>();

		for (int xPixel = 0; xPixel < image.getWidth(); xPixel++) {
			for (int yPixel = 0; yPixel < image.getHeight(); yPixel++) {
				int color = image.getRGB(xPixel, yPixel) & 0xFFFFFF;
				if (color == T1_COLOR) {
					t1Spawns.add(new Point2D.Double((xPixel+0.5)*TileBG.tileSize, (yPixel+0.5)*TileBG.tileSize));
				} else if (color == T2_COLOR) {
					t2Spawns.add(new Point2D.Double((xPixel+0.5)*TileBG.tileSize, (yPixel+0.5)*TileBG.tileSize));
				}
			}
		}
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
	public Tile get(int x, int y) {
		if (x >= getWidth() || y >= getHeight() || x < 0 || y < 0)
			return objectMap[0][0];
		else
			return objectMap[x][y];
	}

	public TileBG getTile(int x, int y) {
		if (x >= getWidth() || y >= getHeight() || x < 0 || y < 0)
			return tileMap[0][0];
		else
			return tileMap[x][y];
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
		return get((int)(x/TileBG.tileSize),(int)(y/TileBG.tileSize));
	}

	/**
	 * Returns the height of the tilemap.
	 * 
	 * @return the height of the tilemap.
	 */
	public int getHeight() {
		return tileMap[0].length;
	}

	/**
	 * Returns the width of the tilemap.
	 * 
	 * @return the width of the tilemap.
	 */
	public int getWidth() {
		return tileMap.length;
	}
	

	/**
	 * Returns the width of the tilemap in pixels.
	 * 
	 * @return the width of the tilemap in pixels.
	 */
	public double getWidthMeter() {
		return getWidth() * TileBG.tileSize;
	}

	/**
	 * Returns the height of the tilemap in pixels.
	 * 
	 * @return the height of the tilemap in pixels.
	 */
	public double getHeightMeter() {
		return getHeight() * TileBG.tileSize;
	}

	synchronized private void generateSpawnPoints() {
		t1Spawns = new LinkedList<Point2D>();
		t2Spawns = new LinkedList<Point2D>();
		for (int x=0;x<getWidth();x++) {
			for (int y=0;y<getWidth();y++) {
				if (get(x,y).getId()==T1_COLOR) {
					t1Spawns.add(new Point2D.Double((x+0.5)*TileBG.tileSize, (y+0.5)*TileBG.tileSize));
				} else if (get(x,y).getId()==T2_COLOR) {
					t2Spawns.add(new Point2D.Double((x+0.5)*TileBG.tileSize, (y+0.5)*TileBG.tileSize));
				}
			}
		}
	}
	
	synchronized public void increaseSize(int newWidth, int newHeight, int side) {
		int x0=0,y0=0;
		int z0=0,t0=0;
		int w0=Math.min(newWidth, getWidth());
		int h0=Math.min(newHeight, getHeight());
		
		switch(side) {
			case 0:
			case 1:
				x0 = 0;
				y0 = 0;
				z0 = 0;
				t0 = 0;
				w0 = Math.min(newWidth,getWidth());
				h0 = Math.min(newHeight,getHeight());
				break;
			case 2:
			case 3:
				x0 = 0;
				y0 = 0;
				z0 = newWidth-getWidth();
				t0 = newHeight-getHeight();
				w0 = Math.min(newWidth,getWidth());
				h0 = Math.min(newHeight,getHeight());
				break;
		}
		
		TileBG[][] newTileMap = new TileBG[w0][h0];
		Tile[][] newObjectMap = new Tile[w0][h0];
		int[][] newLightMap   = new int[w0][h0];
		
		for (int x=x0,z=z0,w=0;w<w0;w++) {
			for (int y=y0,t=t0,h=0;h<h0;h++) {
				newTileMap[x][y]	= tileMap[z][t];
				newObjectMap[x][y]	= objectMap[z][t];
				newLightMap[x][y]	= lightMap[z][t];
			}
		}
		
		tileMap = newTileMap;
		objectMap = newObjectMap;
		lightMap = newLightMap;
	}
	
	public void setTile(int x, int y, TileBG t) {
		if (x>0 && x<getWidth() && y>0 && y<getHeight())
			tileMap[x][y] = t;
	}

	public void setTileObject(int x, int y, Tile ob) {
		if (x>0 && x<getWidth() && y>0 && y<getHeight())
			objectMap[x][y] = ob;
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
	
	/**
	 * Render the Arena.
	 * 
	 * @param g
	 * @param window
	 *            the whole game window.
	 */
	
}
