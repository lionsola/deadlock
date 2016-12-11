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
		int tx,ty;
		public final int presetID;
		
		transient TileSwitchPreset tp;
		
		public TileSwitch(int presetID) {
			this.presetID = presetID;
		}

		@Override
		public void activate(World w) {
			Tile t = w.getArena().get(tx, ty);
			if (t.getThing()==tp.getOriginalThing()) {
				// switch the object
				t.setThing(tp.getSwitchThing());
				
				// tell the clients to switch it too
				w.addEvent(new GameEvent.TileChanged(tx,ty,tp.getSwitchThingID()));
				
				w.addSound(tp.getSoundID(), tp.getSoundVolume(), (tx+0.5)*Terrain.tileSize, (ty+0.5)*Terrain.tileSize);
			} else if (t.getThing()==tp.getSwitchThing()) {
				// switch the object
				t.setThing(tp.getOriginalThing());
				
				// tell the clients to switch it too
				w.addEvent(new GameEvent.TileChanged(tx,ty,tp.getOriginalThingID()));
				
				w.addSound(tp.getSoundID(), tp.getSoundVolume(), (tx+0.5)*Terrain.tileSize, (ty+0.5)*Terrain.tileSize);
			}
		}
		
		public Thing getSwitchThing() {
			return tp.getSwitchThing();
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
