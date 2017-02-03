package server.passive;

import client.gui.GameWindow;
import server.character.InputControlledEntity;
import server.world.World;
import shared.network.CharData;
import shared.network.event.GameEvent.EnemyInfoEvent;

public class Mark extends Passive {
	public static final double MARK_SPEED = 0.1;
	public static final long MARK_COOLDOWN = 1000;
	private long lastBroadcast;
	public Mark(InputControlledEntity self) {
		super(self);
	}

	@Override
	protected double calculateActivationLevel(World w) {
		return !self().getPerception().characters.isEmpty()?1:0;
	}
	
	@Override
	protected void onUpdate(World w) {
		if (lastBroadcast<=0) {
			for (CharData c:self().getPerception().characters) {
				if (c.team!=self().team && c.healthPoints>0) {
					for (InputControlledEntity pc:w.getCharacters()) {
						if (pc.team==self().team) {
							pc.getPerception().events.add(new EnemyInfoEvent(c.x, c.y, c.id));
						}
					}
				}
			}
			lastBroadcast = MARK_COOLDOWN;
		}
		else {
			lastBroadcast -= GameWindow.MS_PER_UPDATE;
		}
	}

	@Override
	protected void onDeactivate(World w) {
		self().addSpeedMod(-MARK_SPEED);
	}

	@Override
	protected void onActivate(World w) {
		lastBroadcast = 0;
		self().addSpeedMod(+MARK_SPEED);
	}
}
