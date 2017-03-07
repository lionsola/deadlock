package editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import client.graphics.ParticleSource;
import server.network.MissionVar;
import server.world.Arena;
import server.world.Terrain;
import server.world.Thing;
import server.world.Tile;
import server.world.trigger.TileSwitchPreset;

public class EditorArena extends Arena {
	public SpawnPoint[][] spawns;
	public String objectiveType;
	public List<MissionVar> objectiveData;
	
	public ParticleSource[][] pss;
	private int noData;
	private boolean isReal;
	
	public EditorArena(ArenaData ad, HashMap<Integer, Terrain> tileTable, HashMap<Integer, Thing> objectTable,
			HashMap<Integer,TileSwitchPreset> triggerTable) {
		super(ad, tileTable, objectTable, triggerTable);
		spawns = new SpawnPoint[ad.tMap.length][ad.tMap[0].length];
		pss = new ParticleSource[ad.tMap.length][ad.tMap[0].length];
		objectiveType = ad.objectiveType;
		objectiveData = ad.objectiveData;
		
		if (ad.spawns!=null) {
			for (SpawnPoint sp:ad.spawns) {
				spawns[sp.x][sp.y] = sp;
			}
		}
		if (ad.pss!=null) {
			for (ParticleSource ps:ad.pss) {
				pss[ps.getTx()][ps.getTy()] = ps;
			}
		}
		if (objectiveData==null) {
			objectiveData = new ArrayList<MissionVar>();
		}
		noData = ad.noData;
		isReal = ad.isReal;
	}

	public EditorArena(String n, int w, int h) {
		super(n,w,h);
		spawns = new SpawnPoint[w][h];
		pss = new ParticleSource[w][h];
		objectiveData = new ArrayList<MissionVar> ();
		isReal = true;
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
		ParticleSource[][] newPss = new ParticleSource[newWidth][newHeight];
		SpawnPoint[][] newSpawns = new SpawnPoint[newWidth][newHeight];
		
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
				newPss[nx][ny] = pss[x][y];
				newSpawns[nx][ny] = spawns[x][y];
			}
		}
		tMap = newTMap;
		pss = newPss;
		spawns = newSpawns;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public List<ParticleSource> getParticleSources() {
		List<ParticleSource> pss = new LinkedList<ParticleSource>();
		for (int x=0;x<tMap.length;x++) {
			for (int y=0;y<tMap[0].length;y++) {
				if (this.pss[x][y]!=null) {
					pss.add(this.pss[x][y]);
				}
				Tile tile = get(x,y);
				Thing t = tile.getThing();
				if (t!=null && t.getParticleSource()!=null) {
					ParticleSource ps = t.getParticleSource().clone();
					ps.setLocation(x, y);
					pss.add(ps);
				}
				Thing m = tile.getMisc();
				if (m!=null && m.getParticleSource()!=null) {
					ParticleSource ps = m.getParticleSource().clone();
					ps.setLocation(x, y);
					pss.add(ps);
				}
			}
		}
		return pss;
	}

	public int getNoData() {
		return noData;
	}

	public void setNoData(int noData) {
		this.noData = noData;
	}
	
	public void setReal(boolean isReal) {
		this.isReal = isReal;
	}
	
	public boolean isReal() {
		return isReal;
	}
}
