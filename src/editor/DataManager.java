package editor;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashMap;
import javax.imageio.ImageIO;

import client.graphics.ImageBlender;
import client.graphics.ParticleSource;
import server.world.Arena;
import server.world.Thing;
import server.world.trigger.TileSwitchPreset;
import server.world.Terrain;

public class DataManager {
	public static final String FILE_TILES = "/tile/tiles";
	public static final String FILE_OBJECTS = "/tile/objects";
	public static final String DIR_MAP = "resource/map/";
	public static final String FILE_TRIGGERS = "/tile/triggers";	
	
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
	
	public static Object loadInternalObject(String path) {
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(DataManager.class.getResourceAsStream(path));
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
	
	public static void loadImages(Collection<? extends ImageLoadable> items) {
		try {
			for (ImageLoadable i:items) {
				i.setImage(DataManager.loadImage("/tile/" + i.getImageName()));
			}
		} catch (IOException e) {
			System.err.println("Error while loading tile images");
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public static HashMap<Integer,Terrain> getTileMap(Collection<Terrain> tiles) {
		HashMap<Integer,Terrain> tileMap = new HashMap<Integer,Terrain>(tiles.size());
		for (Terrain t:tiles) {
			tileMap.put(t.getId(),t);
		}
		return tileMap;
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
	
	/*
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
	*/
	
	public static void exportArenaData(EditorArena a) {
		saveObject(new Arena.ArenaData(a),"resource/map/"+a.getName()+"_copy.arena");
		saveObject(new Arena.ArenaData(a),"resource/map/"+a.getName()+".arena");
	}

	public static void updateParticleSource(Collection<Thing> values) {
		for (Thing thing:values) {
			ParticleSource ps = thing.getParticleSource();
			if (ps!=null) {
				ParticleSource preset = ParticleSource.presets.get(ps.name);
				if (preset!=null) {
					thing.setParticleSource(preset);
				}
			}
		}
	}

	public static BufferedImage loadImage(String name) throws IOException {
		InputStream is = DataManager.class.getResourceAsStream(name);
		BufferedImage image = ImageIO.read(DataManager.class.getResourceAsStream(name));
		is.close();
		return image;
	}
}
