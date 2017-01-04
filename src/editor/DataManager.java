package editor;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;

import client.graphics.ImageBlender;
import server.world.Arena;
import server.world.Misc;
import server.world.Thing;
import server.world.trigger.TileSwitchPreset;
import server.world.Terrain;

public class DataManager {
	public static final String FILE_TILES = "resource/tile/tiles";
	public static final String FILE_OBJECTS = "resource/tile/objects";
	public static final String FILE_TILES_OLD = "resource/tile/tiles_old";
	public static final String FILE_OBJECTS_OLD = "resource/tile/objects_old";
	public static final String DIR_MAP = "resource/map/";
	public static final String FILE_TRIGGERS = "resource/tile/triggers";
	public static final String FILE_MISC = "resource/tile/misc";
	
	public static void exportImages(Arena a) throws IOException {
		
		BufferedImage arenaImage = ImageBlender.drawArena(a);
		
		ImageIO.write(arenaImage, "png", new File(DIR_MAP+a.getName()+"_plain.png"));
		
		BufferedImage midImage = ImageBlender.applyMiddlegroundEffect(arenaImage);
		ImageIO.write(midImage, "png", new File(DIR_MAP+a.getName()+"_mid.png"));
		
		BufferedImage darkImage = ImageBlender.applyBackgroundEffect(arenaImage);
		ImageIO.write(darkImage, "png", new File(DIR_MAP+a.getName()+"_dark.png"));
		
		//BufferedImage lightImage = ImageBlender.applyForegroundEffect(arenaImage);
		//ImageIO.write(lightImage, "png", new File(DIR_MAP+a.getName()+"_light.png"));
		
		BufferedImage lightMap = ImageBlender.drawLightImage(a);
		ImageIO.write(lightMap, "png", new File(DIR_MAP+a.getName()+"_lightmap.png"));
		
		BufferedImage wholeMap = ImageBlender.blendLightImage(arenaImage, lightMap);
		ImageIO.write(wholeMap, "png", new File(DIR_MAP+a.getName()+".png"));
		
		for (int layer=0;layer<4;layer++) {
			BufferedImage layerImage = ImageBlender.drawArena(a,layer);
			ImageIO.write(layerImage, "png", new File(DIR_MAP+a.getName()+"_layer"+layer+".png"));
		}
	}
	
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
	
	public static void loadTileGraphics(Collection<Terrain> tiles){
		try {
			for (Terrain t:tiles) {
				t.setImage(ImageIO.read(new FileInputStream("resource/tile/" + t.getImageName())));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void loadObjectGraphics(Collection<Thing> tiles) {
		try {
			for (Thing t:tiles) {
				t.setImage(ImageIO.read(new FileInputStream("resource/tile/" + t.getImageName())));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void loadMiscGraphics(Collection<Misc> miscs) {
		try {
			for (Misc m:miscs) {
				m.setImage(ImageIO.read(new FileInputStream("resource/tile/" + m.getImageName())));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void loadImage(Collection<? extends ImageLoadable> items) {
		try {
			for (ImageLoadable i:items) {
				i.setImage(ImageIO.read(new FileInputStream("resource/tile/" + i.getImageName())));
			}
		} catch (Exception e) {
			e.printStackTrace();
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
				o.setClear(transparent);
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
				wr.print(tile.isClear()+" ");
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
	
	public static HashMap<Integer, TileSwitchPreset> getTriggerMap(Collection<TileSwitchPreset> triggers) {
		HashMap<Integer,TileSwitchPreset> objectMap = new HashMap<Integer,TileSwitchPreset>(triggers.size());
		for (TileSwitchPreset t:triggers) {
			objectMap.put(t.getId(),t);
		}
		return objectMap;
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
	
	public static void exportArenaData(EditorArena a) {
		saveObject(new Arena.ArenaData(a),"resource/map/"+a.getName()+"_copy.arena");
		saveObject(new Arena.ArenaData(a),"resource/map/"+a.getName()+".arena");
	}
}
