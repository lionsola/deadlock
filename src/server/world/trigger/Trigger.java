package server.world.trigger;
import java.awt.Point;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import server.character.Entity;
import server.world.Terrain;
import server.world.World;

public abstract class Trigger implements Serializable {
	private static final long serialVersionUID = 8439427870770047350L;
	private Point location;
	protected List<TriggerEffect> effects = new LinkedList<TriggerEffect>();
	
	public void setLocation(Point p) {
		this.location = p;
	}
	
	public Point getLocation() {
		return location;
	}
	
	public void update(World w) {};
	public void onCharacterEnter(Entity character, World w) {}
	public void onCharacterTouch(Entity character, World w) {}
	public void onCharacterUntouch(Entity character, World w) {}
	public void onCharacterLeave(Entity character, World w) {}
	public boolean isActive() {return false;}
	

	public List<TriggerEffect> getEffects() {
		if (effects==null) {
			effects =  new LinkedList<TriggerEffect>();
		}
		return new LinkedList<TriggerEffect>(effects);
	}
	public void addEffect(TriggerEffect effect) {
		effects.add(effect);
	}
	
	public void removeEffect(TriggerEffect effect) {
		effects.remove(effect);
	}
	
	public static class PressurePlateTouch extends Trigger {
		private static final long serialVersionUID = 7898439365371577468L;
		
		
		List<Entity> characters = new LinkedList<Entity>();

		@Override
		public void onCharacterTouch(Entity c, World w) {
			// register the character
			if (!characters.contains(c)) {
				characters.add(c);
				
				// if this is the first person to enter the door
				if (characters.size()==1) {
					for (TriggerEffect te:effects) {
						te.activate(w,this,c);
					}
				}
			}
		}
		
		public void onCharacterUntouch(Entity character, World w) {
			characters.remove(character);
		
			if (characters.isEmpty()) {
				for (TriggerEffect te:effects) {
					te.activate(w,this,character);
				}
			}
		}
	}

	public static class SwitchOnTouch extends Trigger {
		private static final long serialVersionUID = 5063983553970728297L;
		
		@Override
		public void onCharacterTouch(Entity c, World w) {
			for (TriggerEffect te:effects) {
				te.activate(w,this,c);
			}
		}
	}

	public static class SwitchOnTouchSide extends Trigger {
		private static final long serialVersionUID = -928991321610941534L;
		private int side;
		
		@Override
		public void onCharacterTouch(Entity c, World w) {
			int cx = (int) (c.getX()/Terrain.tileSize);
			int cy = (int) (c.getY()/Terrain.tileSize);
			Point p = getLocation();
			if ((p.y==cy && ((side==0 && cx>p.x) || (side==2 && cx<p.x))) ||
					(p.x==cx && ((side==1 && cy>p.y || (side==3 && cy<p.y))))) {
				for (TriggerEffect te:effects) {
					te.activate(w,this,c);
				}
			}
		}
		
		public void setSide(int side) {
			this.side = side;
		}
		
		public int getSide() {
			return side;
		}
	}
}