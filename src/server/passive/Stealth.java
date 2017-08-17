package server.passive;

import client.gui.GameWindow;
import server.character.InputControlledEntity;
import server.world.World;

public class Stealth extends Passive {
	private final int PHASE;
	private int counter;
	private final int visible;
	public Stealth(InputControlledEntity self, int invi, int visible) {
		super(self);
		this.visible = visible;
		PHASE = invi+visible;
	}

	@Override
	protected double calculateActivationLevel(World w) {
		return Math.min(1, counter/(PHASE-visible));
	}

	@Override
	public void onUpdate(World w) {
		int oldC = counter;
		counter += GameWindow.MS_PER_UPDATE;
		if (counter>PHASE) {
			counter -= PHASE;
			self().setInvi(false);
		} else if (oldC<visible && counter>visible) {
			self().setInvi(true);
		}
	}
}
