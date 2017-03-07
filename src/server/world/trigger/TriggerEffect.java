package server.world.trigger;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.Serializable;

import server.character.Entity;
import server.world.Terrain;
import server.world.Thing;
import server.world.Tile;
import server.world.World;
import shared.network.event.GameEvent;

public interface TriggerEffect extends Serializable {
	public void activate(World w, Trigger trigger, Entity e);
	
	public static class GiveData implements TriggerEffect {
		private static final long serialVersionUID = 4940952075726566696L;
		private final int dataId;
		
		public GiveData (int dataId) {
			this.dataId = dataId;
		}
		
		@Override
		public void activate(World w, Trigger trigger, Entity e) {
			if (!w.getArena().dataObtained(dataId)) {
				w.getArena().setData(dataId);
				Point2D p = e.getPosition();
				w.addEvent(new GameEvent.DataObtained(dataId,p.getX(),p.getY(),e.typeId));
			}
		}
		
		public int getDataId() {
			return dataId;
		}
	}
	
	public static class TileSwitch implements TriggerEffect {
		private static final long serialVersionUID = -544862884053974706L;
		private Point triggerLocation;
		private int tx,ty;
		public final int presetID;
		
		transient TileSwitchPreset tp;
		
		public TileSwitch(int presetID) {
			this.presetID = presetID;
		}

		@Override
		public void activate(World w, Trigger trigger, Entity e) {
			Tile t = w.getArena().get(tx, ty);
			Thing s = null;
			if ((tp.getItemType()==TileSwitchPreset.THING && t.getThing()==tp.getOriginalThing()) ||
					tp.getItemType()==TileSwitchPreset.MISC && t.getMisc()==tp.getOriginalThing()) {
				s = tp.getSwitchThing();
			} else if ((tp.getItemType()==TileSwitchPreset.THING && t.getThing()==tp.getSwitchThing()) ||
					tp.getItemType()==TileSwitchPreset.MISC && t.getMisc()==tp.getSwitchThing()) {
				s = tp.getOriginalThing();
			}
			if (tp.getItemType()==TileSwitchPreset.THING) {
				t.setThing(s);
			} else if (tp.getItemType()==TileSwitchPreset.MISC) {
				t.setMisc(s);
			}
			w.getArena().recalculateStaticLights();
			w.getArena().updateLightMap(w.getDynamicLights());
			w.addEvent(new GameEvent.TileChanged(tx,ty,s.getId(),tp.getItemType()));
			w.addSound(tp.getSoundID(), tp.getSoundVolume(), (tx+0.5)*Terrain.tileSize, (ty+0.5)*Terrain.tileSize);
		}
		
		public Point getTargetTile() {
			return new Point(tx,ty);
		}
		
		public void setTriggerTile(Point p) {
			triggerLocation = p;
		}
		
		public Point getTriggerTile() {
			return triggerLocation;
		}
		
		public void setTargetTile(Point p) {
			this.tx = p.x;
			this.ty = p.y;
		}
		
		public void setPreset(TileSwitchPreset tp) {
			this.tp = tp;
		}
		
		public TileSwitchPreset getPreset() {
			return tp;
		}
	}
}
