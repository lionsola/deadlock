package server.world;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;

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
	private Tile[][] tileMap; // represents each tile and their position, X and Y
	private List<Point> t1Spawns; // spawn points of team 1
	private List<Point> t2Spawns; // spawn points of team 2
	private List<Point> lightList;
	private Area lightMap;
	
	public Arena(File file, boolean loadGraphics) {
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			Hashtable<Integer, Tile> tileTable = new Hashtable<Integer, Tile>();
			Scanner fileSc = new Scanner(fileInputStream);
			this.name = fileSc.nextLine();

			// load number of tiles
			int n = fileSc.nextInt();
			fileSc.nextLine();
			// load tile information
			for (int i = 0; i < n; i++) {
				String line = fileSc.nextLine();
				Scanner sc = new Scanner(line);
				int color = sc.nextInt(16); // reads the hex image
				Color c = new Color(sc.nextInt(16));
				String filename = sc.next(); // reads the light tile image
				boolean walkable = sc.nextBoolean(); // reads the walkable bool
				boolean transparent = sc.nextBoolean(); // reads the transparent bool
				double protection = sc.nextDouble();
				sc.close();
				BufferedImage tileImage = null;
				Tile t;
				if (loadGraphics) {
					tileImage = ImageIO.read(new FileInputStream("resource/tile/" + filename));
					t = new Tile(walkable, transparent, tileImage,c);
				} else {
					t = new Tile(walkable, transparent);
				}
				t.setProtection(protection);
				tileTable.put(color,t);
			}

			fileSc.close(); // after all tiles are read, close the scanner
			
			loadTileMap(name, tileTable); // load the tile map
			loadPositionMap(name, loadGraphics); // and load the position map
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
	public Arena(String name, boolean loadGraphics) throws FileNotFoundException, IOException {
		this(new File("resource/map/" + name + ".map"), loadGraphics);
	}

	/**
	 * This function is used to load the tile map.
	 * 
	 * @param name
	 *            the name of the map file from which we will load the tile map.
	 * @param tileTable
	 *            the tileTable maps hex colours to the crucial information.
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private void loadTileMap(String name, Hashtable<Integer, Tile> tileTable) throws IOException, FileNotFoundException {
		// load tile map
		BufferedImage image = ImageIO.read(new FileInputStream("resource/map/" + name + ".bmp"));
		tileMap = new Tile[image.getWidth()][image.getHeight()];

		for (int xPixel = 0; xPixel < image.getWidth(); xPixel++) {
			for (int yPixel = 0; yPixel < image.getHeight(); yPixel++) {
				Tile tile = tileTable.get(image.getRGB(xPixel, yPixel) & 0xFFFFFF);

				if (tile == null) {
					System.out.println("Invalid color " + String.format("%x", (image.getRGB(xPixel, yPixel) & 0xFFFFFF)) + " in tile " + xPixel
							+ ", " + yPixel);
					System.exit(-1);
				}

				tileMap[xPixel][yPixel] = tile;
			}
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
	private void loadPositionMap(String name, boolean loadGraphics) throws IOException, FileNotFoundException {
		// load position map
		BufferedImage image = ImageIO.read(new FileInputStream("resource/map/" + name + "_x.bmp"));

		t1Spawns = new ArrayList<Point>();
		t2Spawns = new ArrayList<Point>();
		if (loadGraphics) {
			lightMap = new Area();
			lightList = new LinkedList<Point>();
		}

		for (int xPixel = 0; xPixel < image.getWidth(); xPixel++) {
			for (int yPixel = 0; yPixel < image.getHeight(); yPixel++) {
				int color = image.getRGB(xPixel, yPixel) & 0xFFFFFF;
				if (color == T1_COLOR) {
					t1Spawns.add(new Point(xPixel, yPixel));
				} else if (color == T2_COLOR) {
					t2Spawns.add(new Point(xPixel, yPixel));
				} else if (loadGraphics && color == LIGHT_COLOR) {
					int x = (int) ((xPixel + 0.5) * Tile.tileSize);
					int y = (int) ((yPixel + 0.5) * Tile.tileSize);
					LineOfSight los = new LineOfSight();
					Area light = new Area(los.genLOSAreaMeter(x, y, LIGHT_RANGE, Math.PI*2, 0, this));
					lightMap.add(light);
					lightList.add(new Point(x, y));
				}
			}
		}
	}

	/**
	 * Return the spawn list for a given team.
	 * @param team The team to get the spawn list for.
	 */
	public List<Point> getSpawn(int team) {
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
		return get((int)(x/Tile.tileSize),(int)(y/Tile.tileSize));
	}
	
	/**
	 * Used to return a specific Tile.
	 * 
	 * @param p
	 *            The point with x and y coordinate of the tile.
	 * 
	 * @return the tile located at given point.
	 */
	public Tile get(Point p) {
		return get(p.x, p.y);
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
		return getWidth() * Tile.tileSize;
	}

	/**
	 * Returns the height of the tilemap in pixels.
	 * 
	 * @return the height of the tilemap in pixels.
	 */
	public double getHeightMeter() {
		return getHeight() * Tile.tileSize;
	}



	/**
	 * Returns the name of the arena.
	 * 
	 * @return the name of the arena.
	 */
	public String getName() {
		return name;
	}
	
	public Area getLightMap() {
		return lightMap;
	}
	
	public List<Point> getLightList() {
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
