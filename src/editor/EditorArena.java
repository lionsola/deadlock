package editor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import server.world.Arena;
import server.world.Light;
import server.world.Misc;
import server.world.Terrain;
import server.world.Thing;
import server.world.Tile;
import server.world.trigger.TileSwitchPreset;

public class EditorArena extends Arena {
	Light[][] lights;
	public EditorArena(ArenaData ad, HashMap<Integer, Terrain> tileTable, HashMap<Integer, Thing> objectTable,
			HashMap<Integer,TileSwitchPreset> triggerTable, HashMap<Integer,Misc> miscTable) {
		super(ad, tileTable, objectTable, triggerTable, miscTable);
		lights = new Light[getWidth()][getHeight()];
		for (Light l:lightList) {
			lights[l.getX()][l.getY()] = l;
		}
	}

	public EditorArena(String n, int w, int h) {
		super(n,w,h);
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

	@Override
	public void generateLightMap() {
		List<Light> newLightList = new LinkedList<Light>();
		for (int x=0;x<getWidth();x++) {
			for (int y=0;y<getHeight();y++) {
				if (get(x,y).getThing()!=null && get(x,y).getThing().getLight()!=null) {
					Light l = get(x,y).getThing().getLight();
					newLightList.add(new Light(x,y,l.getColor(),l.getRange()));
				}
				if (get(x,y).getMisc()!=null && get(x,y).getMisc().getLight()!=null) {
					Light l = get(x,y).getMisc().getLight();
					newLightList.add(new Light(x,y,l.getColor(),l.getRange()));
				}
			}
		}
		lightList = newLightList;
		super.generateLightMap();
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
