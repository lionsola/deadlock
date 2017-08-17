package server.status;

import server.character.Entity;
import server.world.World;

public class Stunned extends StatusEffect {
	public Stunned(Entity self, long duration) {
		super(self, duration);
	}

	@Override
	public void onFinish(World w) {
		getSelf().setEnabled(true);
	}

	@Override
	public void start() {
		getSelf().setEnabled(false);
	}
}
