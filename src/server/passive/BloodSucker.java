package server.passive;

import java.awt.geom.Point2D;

import client.gui.GameWindow;
import server.character.InputControlledEntity;
import server.world.World;

public class BloodSucker extends Passive {
	public static final double BLOODSUCK_RATE = 0.05; // hp / ms 

	public BloodSucker(InputControlledEntity self) {
		super(self);
	}
	
	@Override
	protected void onUpdate(World w) {
		for (InputControlledEntity c:w.getCharacters()) {
			if (c.team!=self().team &&
					Point2D.distance(self().getX(),self().getY(),c.getX(),c.getY())<c.getRadius()) {
				double damage = GameWindow.MS_PER_UPDATE*BLOODSUCK_RATE;
				c.onHit(w,damage,self().id);
				self().onHit(w, -damage, self().id);
			}
		}
	}

	@Override
	protected double calculateActivationLevel(World w) {
		return !self().getPerception().characters.isEmpty()?1:0;
	}
}
