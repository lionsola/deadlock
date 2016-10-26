package editor;

import java.util.HashMap;

import editor.DataManager.ArenaData;
import server.world.Arena;
import server.world.Light;
import server.world.Terrain;
import server.world.Thing;
import server.world.Tile;

public class EditorArena extends Arena {
	Light[][] lights;
	public EditorArena(ArenaData ad, HashMap<Integer, Terrain> tileTable, HashMap<Integer, Thing> objectTable) {
		super(ad, tileTable, objectTable);
		lights = new Light[getWidth()][getHeight()];
		for (Light l:lightList) {
			lights[l.getX()][l.getY()] = l;
		}
	}

	public EditorArena(String n, int w, int h, HashMap<Integer, Terrain> tileTable, HashMap<Integer, Thing> objectTable) {
		super(n,w,h,tileTable,objectTable);
		lights = new Light[getWidth()][getHeight()];
	}

	public void addLight(Light l) {
		if (lights[l.getX()][l.getY()]!=null) {
			super.lightList.remove(lights[l.getX()][l.getY()]);
		}
		lights[l.getX()][l.getY()] = l;
		super.lightList.add(l);
		super.generateLightMap();
	}
	
	public void clearLight(int x, int y) {
		lightList.remove(lights[x][y]);
		lights[x][y] = null;
		super.generateLightMap();
	}
	
	synchronized public void changeSize(int newWidth, int newHeight, int hDir, int vDir) {
		int w0=Math.min(newWidth, getWidth());
		int h0=Math.min(newHeight, getHeight());
		
		Tile[][] newTMap = new Tile[newWidth][newHeight];
		//Terrain[][] newTileMap = new Terrain[newWidth][newHeight];
		//Thing[][] newObjectMap = new Thing[newWidth][newHeight];
		for (int x=0;x<newWidth;x++) {
			for (int y=0;y<newHeight;y++) {
				newTMap[x][y] = new Tile();
			}
		}
		
		for (int i=0;i<w0;i++) {
			for (int j=0;j<h0;j++) {
				int x,y,nx,ny;
				if (hDir==0) {
					x = i;
					nx = i;
				} else {
					x = getWidth()-1 - i;
					nx = newWidth-1 - i;
				}
				if (vDir==0) {
					y = j;
					ny = j;
				} else {
					y = getHeight()-1 - j;
					ny = newHeight-1 - j;
				}
				newTMap[nx][ny]	= tMap[x][y];
			}
		}
		tMap = newTMap;
	}
}
