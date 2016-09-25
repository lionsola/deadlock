package passive;

import character.ControlledCharacter;
import core.World;
import game.Game;
import network.PartialCharacterData;
import network.GameEvent.EnemyInfoEvent;

public class Mark extends Passive {
	public static final double MARK_SPEED = 0.1;
	public static final long MARK_COOLDOWN = 1000;
	private long lastBroadcast;
	public Mark(ControlledCharacter self) {
		super(self);
	}

	@Override
	protected boolean trigger() {
		return !self().getPerception().characters.isEmpty();
	}

	
	@Override
	protected void onUpdate(World w) {
		if (lastBroadcast<=0) {
			for (PartialCharacterData c:self().getPerception().characters) {
				w.getEventListener().onEventReceived(new EnemyInfoEvent(c.x,c.y,c.id));
			}
			lastBroadcast = MARK_COOLDOWN;
		}
		else {
			lastBroadcast -= Game.MS_PER_UPDATE;
		}
	}

	@Override
	protected void onDeactivate(World w) {
		self().addSpeedMod(-self().cs.getSpeedF()*MARK_SPEED);
	}

	@Override
	protected void onActivate(World w) {
		lastBroadcast = 0;
		self().addSpeedMod(+self().cs.getSpeedF()*MARK_SPEED);
	}
}
