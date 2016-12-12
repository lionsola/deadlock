package server.world.trigger;

import java.awt.Point;
import java.io.Serializable;

import server.world.Misc;
import server.world.Terrain;
import server.world.Thing;
import server.world.Tile;
import server.world.World;
import server.world.trigger.TileSwitchPreset.Switchable;
import shared.network.event.GameEvent;

public interface TriggerEffect extends Serializable {
	public void activate(World w);
	
	public static class TileSwitch implements TriggerEffect {
		private static final long serialVersionUID = -544862884053974706L;
		int tx,ty;
		public final int presetID;
		
		transient TileSwitchPreset tp;
		
		public TileSwitch(int presetID) {
			this.presetID = presetID;
		}

		@Override
		public void activate(World w) {
			Tile t = w.getArena().get(tx, ty);
			Switchable s = null;
			if ((tp.getItemType()==TileSwitchPreset.THING && t.getThing()==tp.getOriginalThing()) ||
					tp.getItemType()==TileSwitchPreset.MISC && t.getMisc()==tp.getOriginalThing()) {
				s = tp.getSwitchThing();
			} else if ((tp.getItemType()==TileSwitchPreset.THING && t.getThing()==tp.getSwitchThing()) ||
					tp.getItemType()==TileSwitchPreset.MISC && t.getMisc()==tp.getSwitchThing()) {
				s = tp.getOriginalThing();
			}
			t.setItem(s);
			w.addEvent(new GameEvent.TileChanged(tx,ty,s.getId(),tp.getItemType()));
			w.addSound(tp.getSoundID(), tp.getSoundVolume(), (tx+0.5)*Terrain.tileSize, (ty+0.5)*Terrain.tileSize);
		}
		
		public Point getTargetTile() {
			return new Point(tx,ty);
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
