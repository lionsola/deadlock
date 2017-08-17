package server.world;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import client.graphics.ParticleSource;
import client.gui.HostScreen;
import editor.DataManager;
import editor.EditorArena;
import editor.SpawnPoint;
import server.world.trigger.Trigger;
import server.world.trigger.TriggerEffect;
import server.network.MissionVar;
import server.world.trigger.TileSwitchPreset;

/**
 * Class used to model an Arena - the Arena is what the characters will fight in and what they will
 * move around in.
 * 
 * The class uses 3 files in order to generate a map, the map.bmp file, the map_x.bmp file (spawn
 * points) and the map.map file which contains the information for mapping pixel colours to specific
 * textures from the map.bmp file.
 */
public class Arena {
	public static final int T1_COLOR = 0x00ff00; // red spawn colour
	public static final int T2_COLOR = 0xff0000; // green spawn colour

	private ArenaData ad;
	protected String name; // the name of the map
	
	protected transient List<Light> staticLights;
	protected transient int[][] lightMap;
	
	protected Tile[][] tMap;
	protected boolean[] data;
	
	/**
	 * Creating a new Arena
	 * 
	 * @param mId
	 *            the name of the file that will be read in to generate the arena.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public Arena(int mId, HashMap<Integer,Terrain> tileTable, HashMap<Integer,Thing> objectTable,
			HashMap<Integer,TileSwitchPreset> triggerTable) {
		this((ArenaData) DataManager.loadInternalObject("/map/"+HostScreen.MAP_LIST[mId]+".arena"),tileTable,objectTable,triggerTable);
	}
	
	public Arena(ArenaData ad, HashMap<Integer,Terrain> tileTable, HashMap<Integer,Thing> objectTable,
			HashMap<Integer,TileSwitchPreset> triggerTable) {
		initialize(ad,tileTable,objectTable,triggerTable);
	}
	
	public Arena(String name, int width, int height) {
		this.name = name;
		this.lightMap = new int[width][height];
		this.tMap = new Tile[width][height];
		for (int x=0;x<width;x++) {
			for (int y=0;y<height;y++) {
				tMap[x][y] = new Tile();
			}
		}
		this.staticLights = new LinkedList<Light>();
	}
	
	private void initialize(ArenaData ad, HashMap<Integer,Terrain> tileTable, HashMap<Integer,Thing> objectTable,
			HashMap<Integer,TileSwitchPreset> triggerTable) {
		this.ad = ad;
		this.name = ad.name;
		int width = ad.tMap.length;
		int height = ad.tMap[0].length;
		tMap = ad.tMap;
		staticLights = new LinkedList<Light>();
		data = new boolean[ad.noData];
		for (int x=0;x<width;x++) {
			for (int y=0;y<height;y++) {
				tMap[x][y].setTerrain(tileTable.get(ad.idMap[x][y].terrainId));
				tMap[x][y].setThing(objectTable.get(ad.idMap[x][y].thingId));
				tMap[x][y].setMisc(objectTable.get(ad.idMap[x][y].miscId));
				
				Trigger tr = tMap[x][y].getTrigger();
				if (tr!=null) {
					for (TriggerEffect effect:tr.getEffects()) {
						if (effect instanceof TriggerEffect.TileSwitch) {
							TriggerEffect.TileSwitch tst = (TriggerEffect.TileSwitch)effect;
							tst.setPreset(triggerTable.get(tst.presetID));
							if (tst.getPreset()==null) {
								tMap[x][y].setTrigger(null);
							}
						}
					}
				}
			}
		}
		List<ParticleSource> newPss = new LinkedList<ParticleSource>();
		if (ad.pss!=null) {
			for (ParticleSource ps:ad.pss) {
				ParticleSource preset = ParticleSource.presets.get(ps.name);
				if (preset!=null) {
					ParticleSource dup = preset.clone();
					dup.setLocation(ps.getTx(), ps.getTy());
					newPss.add(dup);
				} else {
					newPss.add(ps);
				}
			}
		}
		ad.pss = newPss;
		recalculateStaticLights();
		updateLightMap(null);
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
	/*
	public Thing get(int x, int y) {
		if (x >= getWidth() || y >= getHeight() || x < 0 || y < 0)
			return objectMap[0][0];
		else
			return objectMap[x][y];
	}
	*/
	
	public Tile get(int x, int y) {
		if (x >= getWidth() || y >= getHeight() || x < 0 || y < 0)
			return tMap[0][0];
		else
			return tMap[x][y];
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
		return get((int)(x/Terrain.tileSize),(int)(y/Terrain.tileSize));
	}

	/**
	 * Returns the height of the tilemap.
	 * 
	 * @return the height of the tilemap.
	 */
	public int getHeight() {
		return tMap[0].length;
	}

	/**
	 * Returns the width of the tilemap.
	 * 
	 * @return the width of the tilemap.
	 */
	public int getWidth() {
		return tMap.length;
	}
	

	/**
	 * Returns the width of the tilemap in pixels.
	 * 
	 * @return the width of the tilemap in pixels.
	 */
	public double getWidthMeter() {
		return getWidth() * Terrain.tileSize;
	}

	/**
	 * Returns the height of the tilemap in pixels.
	 * 
	 * @return the height of the tilemap in pixels.
	 */
	public double getHeightMeter() {
		return getHeight() * Terrain.tileSize;
	}

	public void recalculateStaticLights() {
		List<Light> newLightList = new LinkedList<Light>();
		//double ts = Terrain.tileSize;
		for (int x=0;x<getWidth();x++) {
			for (int y=0;y<getHeight();y++) {
				if (get(x,y).getThing()!=null) {
					Light l = get(x,y).getThing().getLight();
					if (l!=null) {
						newLightList.add(new Light(Utils.tileToMeter(x, y),l.getColor(),l.getRange()));
					}
				}
				if (get(x,y).getMisc()!=null) {
					Light l = get(x,y).getMisc().getLight();
					if (l!=null) {
						newLightList.add(new Light(Utils.tileToMeter(x, y),l.getColor(),l.getRange()));
					}
				}
			}
		}
		staticLights = newLightList;
	}
	
	public void updateLightMap(List<Light> dynamicLights) {
		//int INITIAL_LIGHT = 0x00;
		//int INITIAL_LIGHT_RGB = 0x000000;
		
		int[][] lightMap = new int[getWidth()*2][getHeight()*2];
		for (int[] lights:lightMap) {
			Arrays.fill(lights, 0);
		}
		
		List<Light> lights = new LinkedList<Light>();
		lights.addAll(staticLights);
		if (dynamicLights!=null) {
			lights.addAll(dynamicLights);
		}
		double ts = Terrain.tileSize;
		for (Light l:lights) {
			int tx = (int)(l.getX()/ts);
			int ty = (int)(l.getY()/ts);
			int x1 = Math.min(getWidth()-1, Math.max(0,tx - l.getRange() - 1));
			int x2 = Math.min(getWidth()-1, Math.max(0,tx + l.getRange() + 1));
			int y1 = Math.min(getHeight()-1, Math.max(0, ty - l.getRange() - 1));
			int y2 = Math.min(getHeight()-1, Math.max(0, ty + l.getRange() + 1));
			Point2D lightPos = new Point2D.Double(l.getX()/ts,l.getY()/ts);
			for (int x=x1*2;x<=x2*2;x++) {
				for (int y=y1*2;y<=y2*2;y++) {
					Point2D dest = new Point2D.Double((x+0.5)/2.0, (y+0.5)/2.0);
					Point2D minLCDest = null;
					double minDist = Double.MAX_VALUE;
					//if (!get(x/2,y/2).isClear()) {
						for (int i=-1;i<=1;i+=2) {
							for (int j=-1;j<=1;j+=2) {
								Point2D lcDest = new Point2D.Double(dest.getX()+i*0.25,dest.getY()+j*0.25);
								double dist = lcDest.distance(lightPos);
								if (dist<minDist) {
									minDist = dist;
									minLCDest = lcDest;
								}
							}
						}
					//} else {
					//	minLCDest = new Point2D.Double(x/2+0.5,y/2+0.5);
					//}
					
					double d = 1.0-lightPos.distance(minLCDest)/l.getRange();
					if (d>0) {
						int blockCount = 0;
						final double MAX_BLOCK_COUNT = 2;
						final double LINECAST_DISTANCE = 0.25; 
						List<Point2D> points = Geometry.getLineSamples(lightPos,
								minLCDest, LINECAST_DISTANCE);
						for (Point2D p:points) {
							if (((int)p.getX()!=tx || (int)p.getY()!=ty) &&
									p.distance(minLCDest)>0.01 && !get((int)p.getX(),(int)p.getY()).isClear()) {
								blockCount ++;
							}
							if (blockCount>=MAX_BLOCK_COUNT) {
								break;
							}
						}
						if (blockCount>=MAX_BLOCK_COUNT) {
							continue;
						}
						else if (blockCount>0) {
							d = d*(1-blockCount/MAX_BLOCK_COUNT);
						}
						int sRGB = l.getColor();
						int sR = ((sRGB >> 16) & 0xFF) ;
						int sG = ((sRGB >> 8) & 0xFF) ;
						int sB = ((sRGB) & 0xFF) ;
						
						int dRGB = lightMap[x][y];
						int dR = (dRGB >> 16) & 0xFF;
						int dG = (dRGB >> 8) & 0xFF;
						int dB = (dRGB) & 0xFF;
						
						int r = Math.min(255, (int) (sR * d + dR));
						int g = Math.min(255, (int) (sG * d + dG));
						int b = Math.min(255, (int) (sB * d + dB));
						
						lightMap[x][y] = r << 16 | g << 8 | b; 
					}
				}
			}
		}
		
		//final int MIN_ILLU = 0x2f;
		final int LEVELS = 2;
		final float INTERVAL = 255/LEVELS;
		for (int x=0;x<lightMap.length;x++) {
			for (int y=0;y<lightMap[0].length;y++) {
				Color c = new Color(lightMap[x][y]);
				int l = Math.max(c.getRed(), Math.max(c.getGreen(), c.getBlue()));
				if (l==255) {
					int q = 1;
					q++;
					if (q>0)
						;
				}
				int level = (int)Math.min(LEVELS, Math.ceil(l/INTERVAL));
				
				float ratio = l==0?0:((level*255.0f/LEVELS)/l);
				
				int r = clampLight(c.getRed()*ratio);
				int g = clampLight(c.getGreen()*ratio);
				int b = clampLight(c.getBlue()*ratio);

				lightMap[x][y] = r << 16 | g << 8 | b; 
			}
		}
		this.lightMap = lightMap;
	}
	
	private int clampLight(double light) {
		final int MIN_ILLU = 0x2f;
		return (int) Math.round(Math.max(MIN_ILLU, Math.min(255, light)));
	}
	
	public void setTerrain(int x, int y, Terrain t) {
		if (x>=0 && x<getWidth() && y>=0 && y<getHeight())
			tMap[x][y].setTerrain(t);
	}

	public void setThing(int x, int y, Thing ob) {
		if (x>=0 && x<getWidth() && y>=0 && y<getHeight())
			tMap[x][y].setThing(ob);
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

	public int getLightAt(Point2D p) {
		try {
			int lx = (int)(p.getX()*2/Terrain.tileSize);
			int ly = (int)(p.getY()*2/Terrain.tileSize);
			return lightMap[lx][ly];
		} catch (Exception e){
			return 0;
		}
	}
	
	public List<SpawnPoint> getSpawns() {
		return ad.spawns;
	}
	
	
	public int[][] getLightmap() {
		return lightMap;
	}
	
	public List<Light> getLightList() {
		return staticLights;
	}

	public static class ArenaData implements Serializable {
		private static final long serialVersionUID = -3052994148693588749L;
		public final String name;
		public final Tile[][] tMap;
		public final TileData[][] idMap;
		
		public final List<SpawnPoint> spawns = new LinkedList<SpawnPoint>();
		public List<ParticleSource> pss = new LinkedList<ParticleSource>();
		
		public final String objectiveType;
		public final List<MissionVar> objectiveData;
		public final int noData;
		public final boolean isReal;
		
		public ArenaData(EditorArena a) {
			name = a.getName();
			tMap = a.tMap;
			idMap = new TileData[a.getWidth()][a.getHeight()];
			noData = a.getNoData();
			int spCount = 0;
			for (int x=0;x<a.getWidth();x++) {
				for (int y=0;y<a.getHeight();y++) {
					Tile t = a.get(x, y);
					
					idMap[x][y] = new TileData();
					Terrain te = t.getTerrain();
					idMap[x][y].terrainId = te!=null?te.getId():0;
					
					Thing ti = t.getThing();
					idMap[x][y].thingId = ti!=null?ti.getId():0;
					
					Thing mi = t.getMisc();
					idMap[x][y].miscId = mi!=null?mi.getId():0;
					
					if (a.spawns[x][y]!=null) {
						spawns.add(a.spawns[x][y]);
						a.spawns[x][y].setId(spCount++);
					}
					
					if (a.pss[x][y]!=null) {
						pss.add(a.pss[x][y]);
					}
				}
			}
			objectiveType = a.objectiveType;
			objectiveData = a.objectiveData;
			isReal = a.isReal();
		}
	}
	
	public static class TileData implements Serializable {
		private static final long serialVersionUID = 5708858620829910396L;
		int terrainId;
		int thingId;
		int miscId;
	}

	public List<ParticleSource> getParticleSources() {
		List<ParticleSource> pss = new LinkedList<ParticleSource>();
		if (ad.pss!=null) {
			pss.addAll(ad.pss);
		}
		for (int x=0;x<tMap.length;x++) {
			for (int y=0;y<tMap[0].length;y++) {
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

	public Tile getTileAt(Point2D point) {
		return getTileAt(point.getX(),point.getY());
	}

	public void setData(int dataId) {
		if (dataId>=0 && dataId<data.length) {
			data[dataId] = true;
		}
	}
	
	public int getNoData() {
		return ad.noData;
	}

	public boolean dataObtained(int dataId) {
		return data[dataId];
	}
	
	public boolean isReal() {
		return ad.isReal;
	}
	
	public ArenaData getArenaData() {
		return ad;
	}
}
