package editor;

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;
import server.world.Tile;

public class DataManager {
	public static void saveTileList(Collection<Tile> tileList) {
		ObjectOutputStream oos = null;
		FileOutputStream fout = null;
		try{
		    fout = new FileOutputStream("resource/tile/tileList", true);
		    oos = new ObjectOutputStream(new BufferedOutputStream(fout));
		    oos.writeObject(tileList);
		} catch (Exception e) {
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
	
	public static Collection<Tile> loadTileList() throws FileNotFoundException, IOException, ClassNotFoundException {
		ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream("resource/tile/tileList")));
		Collection<Tile> tileList = (Collection<Tile>)in.readObject();
		in.close();
		return tileList;
	}
	
	public static Collection<Tile> loadTileListOld() throws FileNotFoundException, IOException, ClassNotFoundException {
		List<Tile> tiles = new LinkedList<Tile>();
		try {
			FileInputStream fileInputStream = new FileInputStream("resource/tile/tiles");
			Scanner fileSc = new Scanner(fileInputStream);
			// load tile information
			for (int i = 0; fileSc.hasNext(); i++) {
				String line = fileSc.nextLine();
				Scanner sc = new Scanner(line);
				int id = sc.nextInt(16); // reads the hex image
				Color c = new Color(sc.nextInt(16));
				String tileName = sc.next(); // reads the light tile image
				String filename = sc.next(); // reads the light tile image
				boolean walkable = sc.nextBoolean(); // reads the walkable bool
				boolean transparent = sc.nextBoolean(); // reads the transparent bool
				int protection = sc.nextInt();
				sc.close();
				Tile t = new Tile(id,walkable,transparent);
				t.setCoverType(protection);
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
	
	public static void loadTileGraphics(Collection<Tile> tiles) throws FileNotFoundException, IOException {
		for (Tile t:tiles) {
			t.setImage(ImageIO.read(new FileInputStream("resource/tile/" + t.getImageName())));
		}
	}
	
	public static HashMap<Integer,Tile> getTileMap(Collection<Tile> tiles) {
		HashMap<Integer,Tile> tileMap = new HashMap<Integer,Tile>(tiles.size());
		for (Tile t:tiles) {
			tileMap.put(t.getId(),t);
		}
		return tileMap;
	}
}
