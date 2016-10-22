package editor;

import java.awt.geom.Point2D;
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
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;

import client.graphics.ImageBlender;
import client.graphics.ParticleEmitter;
import server.world.Arena;
import server.world.Tile;
import server.world.TileBG;

public class DataManager {
	public static void exportImages(Arena a) throws IOException {
		BufferedImage arenaImage = ImageBlender.drawArena(a);
		
		ImageIO.write(arenaImage, "png", new File("resource/map/"+a.getName()+"_plain.png"));
		
		BufferedImage midImage = ImageBlender.applyMiddlegroundEffect(arenaImage);
		ImageIO.write(midImage, "png", new File("resource/map/"+a.getName()+"_mid.png"));
		
		BufferedImage darkImage = ImageBlender.applyBackgroundEffect(arenaImage);
		ImageIO.write(darkImage, "png", new File("resource/map/"+a.getName()+"_dark.png"));
		
		BufferedImage lightImage = ImageBlender.applyForegroundEffect(arenaImage);
		ImageIO.write(lightImage, "png", new File("resource/map/"+a.getName()+"_light.png"));
		
		BufferedImage lightMap = ImageBlender.drawLightImage(a);
		ImageIO.write(lightMap, "png", new File("resource/map/"+a.getName()+"_lightmap.png"));
		
		BufferedImage wholeMap = ImageBlender.blendLightImage(lightImage, lightMap);
		ImageIO.write(wholeMap, "png", new File("resource/map/"+a.getName()+".png"));
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
	
	public static Collection<TileBG> loadTileListOld() {
		List<TileBG> tiles = new LinkedList<TileBG>();
		try {
			FileInputStream fileInputStream = new FileInputStream("resource/tile/tiles");
			Scanner fileSc = new Scanner(fileInputStream);
			// load tile information
			while (fileSc.hasNext()) {
				String line = fileSc.nextLine();
				Scanner sc = new Scanner(line);
				int id = sc.nextInt(16); // reads the hex image
				String tileName = sc.next(); // reads the light tile image
				String filename = sc.next(); // reads the light tile image
				sc.close();
				TileBG t = new TileBG(id);
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
	
	public static void loadTileGraphics(Collection<TileBG> tiles) throws FileNotFoundException, IOException {
		for (TileBG t:tiles) {
			t.setImage(ImageIO.read(new FileInputStream("resource/tile/" + t.getImageName())));
		}
	}
	
	public static void loadObjectGraphics(Collection<Tile> tiles) throws FileNotFoundException, IOException {
		for (Tile t:tiles) {
			t.setImage(ImageIO.read(new FileInputStream("resource/tile/" + t.getImageName())));
		}
	}
	
	public static HashMap<Integer,TileBG> getTileMap(Collection<TileBG> tiles) {
		HashMap<Integer,TileBG> tileMap = new HashMap<Integer,TileBG>(tiles.size());
		for (TileBG t:tiles) {
			tileMap.put(t.getId(),t);
		}
		return tileMap;
	}

	public static Collection<Tile> loadObjectListOld() {
		List<Tile> objects = new LinkedList<Tile>();
		try {
			FileInputStream fileInputStream = new FileInputStream("resource/tile/objects");
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
				
				Tile o = new Tile(id);
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

	public static HashMap<Integer, Tile> getObjectMap(Collection<Tile> objects) {
		HashMap<Integer,Tile> objectMap = new HashMap<Integer,Tile>(objects.size());
		for (Tile t:objects) {
			objectMap.put(t.getId(),t);
		}
		return objectMap;
	}
	
	public static class ArenaData implements Serializable {
		private static final long serialVersionUID = -5816600310705383411L;
		public String name;
		public int[][] tileMap;
		public int[][] objectMap;
		public List<Point2D> spawn1;
		public List<Point2D> spawn2;
		public int[][] lightMap;
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
		for (int x=0;x<a.getWidth();x++) {
			for (int y=0;y<a.getHeight();y++) {
				tileIDMap[x][y] = a.getTile(x, y).getId();
				objectIDMap[x][y] = a.get(x, y).getId();
			}
		}
		ArenaData ad = new ArenaData();
		ad.name = a.getName();
		ad.tileMap = tileIDMap;
		ad.objectMap = objectIDMap;
		ad.spawn1 = a.getSpawn(0);
		ad.spawn2 = a.getSpawn(1);
		ad.lightMap = a.getLightmap();
		saveObject(ad,"resource/map/"+a.getName()+".arena");
	}
}
