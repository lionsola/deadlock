package server.world;
import java.awt.Point;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import server.character.Character;
import shared.network.GameEvent;

public abstract class Trigger implements Serializable {
	private static final long serialVersionUID = 8439427870770047350L;

	abstract public void update(World w);
	abstract public void onCharacterEnter(Character character, World w);
	abstract public void onCharacterTouch(Character character, World w);
	abstract public void onCharacterUntouch(Character character, World w);
	abstract public void onCharacterLeave(Character character, World w);
	abstract public boolean isActive();
	
	public static class TileSwitchTrigger extends Trigger{
		private static final long serialVersionUID = 7898439365371577468L;
		int tx,ty;
		final int presetID;
		
		transient TriggerPreset tp;
		
		List<Character> characters = new LinkedList<Character>();
		
		public TileSwitchTrigger(int presetID) {
			this.presetID = presetID;
		}
		
		@Override
		public void onCharacterEnter(Character c, World w) {
			
		}
		
		@Override
		public boolean isActive() {
			return !characters.isEmpty();
		}

		@Override
		public void update(World w) {
			
		}

		@Override
		public void onCharacterTouch(Character c, World w) {
			// register the character
			characters.add(c);
			
			// if this is the first person to enter the door
			if (characters.size()==1) {
				// switch the object
				w.getArena().get(tx, ty).setThing(tp.getSwitchThing());
				
				// tell the clients to switch it too
				w.addEvent(new GameEvent.TileChanged(tx,ty,tp.getSwitchThingID()));
				
				w.addSound(tp.getSoundID(), tp.getSoundVolume(), (tx+0.5)*Terrain.tileSize, (ty+0.5)*Terrain.tileSize);
			}
		}
		
		public void onCharacterUntouch(Character character, World w) {
			characters.remove(character);
		
			if (!isActive()) {
				// switch the original object back
				w.getArena().get(tx, ty).setThing(tp.getOriginalThing());
				
				// send the event to clients too
				w.addEvent(new GameEvent.TileChanged(tx,ty,tp.getOriginalThingID()));
			}
		}
		
		public void onCharacterLeave(Character character, World w) {
			// TODO Auto-generated method stub
			
		}

		public Thing getSwitchThing() {
			return tp.getSwitchThing();
		}
		
		public Point getTargetTile() {
			return new Point(tx,ty);
		}
		
		public void setTileX(int tx) {
			this.tx = tx;
		}
		
		public void setTileY(int ty) {
			this.ty = ty;
		}
		
		public void setTargetTile(Point p) {
			this.tx = p.x;
			this.ty = p.y;
		}
		
		public void setPreset(TriggerPreset tp) {
			this.tp = tp;
		}
	}
}