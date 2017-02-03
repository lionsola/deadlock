package server.world.trigger;

import java.awt.Point;
import java.io.Serializable;

import server.world.Terrain;
import server.world.Thing;
import server.world.Tile;
import server.world.World;
import shared.network.event.GameEvent;

public interface TriggerEffect extends Serializable {
	public void activate(World w);
	
	public static class TileSwitch implements TriggerEffect {
		private static final long serialVersionUID = -544862884053974706L;
		Point triggerLocation;
		int tx,ty;
		public final int presetID;
		
		transient TileSwitchPreset tp;
		
		public TileSwitch(int presetID) {
			this.presetID = presetID;
		}

		@Override
		public void activate(World w) {
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
