package server.status;

import server.character.Entity;
import server.world.World;

public class Courage extends StatusEffect {
	public static final double BUFF_SPEED = 0.1;
	public Courage(Entity self, long duration) {
		super(self, duration);
	}

	@Override
	public void onFinish(World w) {
		getSelf().addSpeedMod(-BUFF_SPEED);
	}

	@Override
	public void start() {
		getSelf().addSpeedMod(BUFF_SPEED);
	}
}
