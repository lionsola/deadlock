package editor;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;

import client.graphics.ImageBlender;
import client.graphics.ParticleEmitter;
import server.world.Arena;
import server.world.Light;
import server.world.SpriteConfig;
import server.world.Thing;
import server.world.Terrain;

public class DataManager {
	public static final String FILE_TILES = "resource/tile/tiles";
	public static final String FILE_OBJECTS = "resource/tile/objects";
	public static final String FILE_TILES_OLD = "resource/tile/tiles_old";
	public static final String FILE_OBJECTS_OLD = "resource/tile/objects_old";
	public static final String DIR_MAP = "resource/map/";
	
	public static void exportImages(Arena a) throws IOException {
		BufferedImage arenaImage = ImageBlender.drawArena(a);
		
		ImageIO.write(arenaImage, "png", new File(DIR_MAP+a.getName()+"_plain.png"));
		
		BufferedImage midImage = ImageBlender.applyMiddlegroundEffect(arenaImage);
		ImageIO.write(midImage, "png", new File(DIR_MAP+a.getName()+"_mid.png"));
		
		BufferedImage darkImage = ImageBlender.applyBackgroundEffect(arenaImage);
		ImageIO.write(darkImage, "png", new File(DIR_MAP+a.getName()+"_dark.png"));
		
		BufferedImage lightImage = ImageBlender.applyForegroundEffect(arenaImage);
		ImageIO.write(lightImage, "png", new File(DIR_MAP+a.getName()+"_light.png"));
		
		BufferedImage lightMap = ImageBlender.drawLightImage(a);
		ImageIO.write(lightMap, "png", new File(DIR_MAP+a.getName()+"_lightmap.png"));
		
		BufferedImage wholeMap = ImageBlender.blendLightImage(lightImage, lightMap);
		ImageIO.write(wholeMap, "png", new File(DIR_MAP+a.getName()+".png"));
	}
	
	/*
	public static Arena loadArena(String name) {
		
		ArenaData ad = loadObject
	}
	
	public static Arena loadArena(String name, HashMap<Integer,Tile> tileTable, HashMap<Integer,Object> objectTable) {
		ArenaData ad = (ArenaData) loadObject("resource/map/"+name+".arena");
		
	}
	*/
	
	public static void saveObject(Object object, String path) {
		ObjectOutputStream oos = null;
		FileOutputStream fout = null;
		try{
		    fout = new FileOutputStream(path, false);
		    oos = new ObjectOutputStream(new BufferedOutputStream(fout));
		    oos.writeObject(object);
		} catch (Exception e) {
			System.err.println("Error while saving object "+object);
		    e.printStackTrace();
		} finally {
		    if(oos!= null){
		        try {
					oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		    } 
		}
	}
	
	public static Object loadObject(File file) {
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
			Object object = in.readObject();
			return object;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in!=null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	public static Object loadObject(String path) {
		return loadObject(new File(path));
	}
	
	public static Collection<Terrain> loadTileListOld() {
		List<Terrain> tiles = new LinkedList<Terrain>();
		try {
			FileInputStream fileInputStream = new FileInputStream(FILE_TILES_OLD);
			Scanner fileSc = new Scanner(fileInputStream);
			// load tile information
			while (fileSc.hasNext()) {
				String line = fileSc.nextLine();
				Scanner sc = new Scanner(line);
				int id = sc.nextInt(16); // reads the hex image
				String tileName = sc.next(); // reads the light tile image
				String filename = sc.next(); // reads the light tile image
				sc.close();
				Terrain t = new Terrain(id);
				t.setName(tileName);
				t.setImageName(filename);
				tiles.add(t);
			}

			fileSc.close(); // after all tiles are read, close the scanner
		} catch (Exception e) {
			System.err.println("Error while reading map");
			e.printStackTrace();
		}
		return tiles;
	}
	
	public static void saveTileListOld(Collection<Terrain> tileList) {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(FILE_TILES_OLD);
			PrintWriter wr = new PrintWriter(fileOutputStream);
			
			// save tile information
			Iterator<Terrain> i = tileList.iterator();
			while (i.hasNext()) {
				Terrain tile = i.next();
				wr.print(Integer.toHexString(tile.getId())+" ");
				wr.print(tile.getName()+" ");
				wr.print(tile.getImageName());
				if (i.hasNext()) {
					wr.println();
				}
			}
			
			wr.close();
		} catch (Exception e) {
			System.err.println("Error while reading map");
			e.printStackTrace();
		}
	}
	
	public static void loadTileGraphics(Collection<Terrain> tiles) throws FileNotFoundException, IOException {
		for (Terrain t:tiles) {
			t.setImage(ImageIO.read(new FileInputStream("resource/tile/" + t.getImageName())));
		}
	}
	
	public static void loadObjectGraphics(Collection<Thing> tiles) throws FileNotFoundException, IOException {
		for (Thing t:tiles) {
			t.setImage(ImageIO.read(new FileInputStream("resource/tile/" + t.getImageName())));
		}
	}
	
	public static HashMap<Integer,Terrain> getTileMap(Collection<Terrain> tiles) {
		HashMap<Integer,Terrain> tileMap = new HashMap<Integer,Terrain>(tiles.size());
		for (Terrain t:tiles) {
			tileMap.put(t.getId(),t);
		}
		return tileMap;
	}

	public static Collection<Thing> loadObjectListOld() {
		List<Thing> objects = new LinkedList<Thing>();
		try {
			FileInputStream fileInputStream = new FileInputStream(FILE_OBJECTS_OLD);
			Scanner fileSc = new Scanner(fileInputStream);
			// load tile information
			while (fileSc.hasNext()) {
				String line = fileSc.nextLine();
				Scanner sc = new Scanner(line);
				int id = sc.nextInt(16); // reads the hex image
				String objectName = sc.next(); // reads the light tile image
				String filename = sc.next(); // reads the light tile image
				boolean walkable = sc.nextBoolean();
				boolean transparent = sc.nextBoolean();
				int coverType = sc.nextInt();
				double spriteSize = sc.nextDouble();
				sc.close();
				
				Thing o = new Thing(id);
				o.setWalkable(walkable);
				o.setTransparent(transparent);
				o.setCoverType(coverType);
				o.setName(objectName);
				o.setImageName(filename);
				o.setSpriteSize(spriteSize);
				objects.add(o);
			}

			fileSc.close(); // after all tiles are read, close the scanner
		} catch (Exception e) {
			System.err.println("Error while loading object list");
			e.printStackTrace();
		}
		return objects;
	}

	public static void saveObjectListOld(Collection<Thing> tileList) {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(FILE_OBJECTS_OLD);
			PrintWriter wr = new PrintWriter(fileOutputStream);
			
			// save tile information
			Iterator<Thing> i = tileList.iterator();
			while (i.hasNext()) {
				Thing tile = i.next();
				wr.print(Integer.toHexString(tile.getId())+" ");
				wr.print(tile.getName()+" ");
				wr.print(tile.getImageName()+" ");
				
				wr.print(tile.isWalkable()+" ");
				wr.print(tile.isTransparent()+" ");
				wr.print(tile.getCoverType()+" ");
				wr.print(tile.getSpriteSize());
				
				if (i.hasNext()) {
					wr.println();
				}
			}
			
			wr.close();
		} catch (Exception e) {
			System.err.println("Error while reading map");
			e.printStackTrace();
		}
	}
	
	public static HashMap<Integer, Thing> getObjectMap(Collection<Thing> objects) {
		HashMap<Integer,Thing> objectMap = new HashMap<Integer,Thing>(objects.size());
		for (Thing t:objects) {
			objectMap.put(t.getId(),t);
		}
		return objectMap;
	}
	
	public static class ArenaData implements Serializable {
		private static final long serialVersionUID = -5816600310705383411L;
		public String name;
		public int[][] tileMap;
		public int[][] objectMap;
		public SpriteConfig[][] configMap;
		public List<Light> lights;
	}
	
	public static class ArenaGraphicalData implements Serializable {
		private static final long serialVersionUID = 3645894568926110727L; 
		public List<ParticleEmitter> particleEmitters;
	}
	
	public static <T> void loadFromTable(int[][] idArray, HashMap<Integer, T> table, T[][] array) {
		int width = idArray.length;
		int height = idArray[0].length;
		for (int x=0;x<width;x++) {
			for (int y=0;y<height;y++) {
				array[x][y] = table.get(idArray[x][y]);
			}
		}
	}
	
	public static <T> void loadBitMap(String name, HashMap<Integer, T> objectTable, T[][] array) {
		// load tile map
		BufferedImage image = null;
		try {
			image = ImageIO.read(new FileInputStream("resource/map/" + name + ".bmp"));
			for (int xPixel = 0; xPixel < image.getWidth(); xPixel++) {
				for (int yPixel = 0; yPixel < image.getHeight(); yPixel++) {
					T tile = objectTable.get(image.getRGB(xPixel, yPixel) & 0xFFFFFF);
	
					if (tile == null) {
						System.out.println("Invalid color " + String.format("%x", (image.getRGB(xPixel, yPixel) & 0xFFFFFF)) + " in tile " + xPixel
								+ ", " + yPixel);
						//System.exit(-1);
					}
	
					array[xPixel][yPixel] = tile;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void exportArenaData(Arena a) {
		int[][] tileIDMap = new int[a.getWidth()][a.getHeight()];
		int[][] objectIDMap = new int[a.getWidth()][a.getHeight()];
		SpriteConfig[][] configMap = new SpriteConfig[a.getWidth()][a.getHeight()];
		for (int x=0;x<a.getWidth();x++) {
			for (int y=0;y<a.getHeight();y++) {
				Terrain te = a.get(x, y).getTerrain();
				tileIDMap[x][y] = te!=null?te.getId():0;
				Thing ti = a.get(x, y).getThing();
				objectIDMap[x][y] = ti!=null?ti.getId():0;
				SpriteConfig sc = a.get(x, y).getSpriteConfig();
				if (sc!=null && (sc.flip || sc.rotation!=0 || sc.spriteX!=0 || sc.spriteY!=0)) {
					configMap[x][y] = sc;
				}
			}
		}
		ArenaData ad = new ArenaData();
		ad.name = a.getName();
		ad.tileMap = tileIDMap;
		ad.objectMap = objectIDMap;
		ad.configMap = configMap;
		//ad.spawn1 = a.getSpawn(0);
		//ad.spawn2 = a.getSpawn(1);
		//ad.lightMap = a.getLightmap();
		ad.lights = a.getLightList();
		saveObject(ad,"resource/map/"+a.getName()+".arena");
	}
}
