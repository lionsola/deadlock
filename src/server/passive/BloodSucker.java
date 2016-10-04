package server.passive;

import java.awt.geom.Point2D;

import client.gui.GameWindow;
import server.character.PlayerCharacter;
import server.world.World;
import shared.network.GameEvent.PlayerDieEvent;

public class BloodSucker extends Passive {
	public static final double BLOODSUCK_RATE = 0.05; // hp / ms 

	public BloodSucker(PlayerCharacter self) {
		super(self);
	}

	@Override
	protected boolean trigger() {
		return !self().getPerception().characters.isEmpty();
	}
	
	@Override
	protected void onUpdate(World w) {
		for (PlayerCharacter c:w.getCharacters()) {
			if (c.team!=self().team &&
					Point2D.distance(self().getX(),self().getY(),c.getX(),c.getY())<c.getRadius()) {
				double damage = GameWindow.MS_PER_UPDATE*BLOODSUCK_RATE;
				c.onHit(damage);
				self().onHit(-damage);
				if (c.getHealthPoints()<=0) {
					w.getEventListener().onEventReceived(new PlayerDieEvent(self().id, c.id));
					w.onPlayerDeath(c);
				}
			}
		}
	}
}
