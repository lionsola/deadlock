package server.status;

import server.character.Entity;
import server.world.World;

public class Slowed extends StatusEffect {
	final double amount;
	public Slowed(Entity self, long duration, double amount) {
		super(self, duration);
		this.amount = amount;
	}

	@Override
	public void start() {
		getSelf().addSpeedMod(-amount);
	}

	@Override
	public void onFinish(World w) {
		getSelf().addSpeedMod(amount);
	}

}
