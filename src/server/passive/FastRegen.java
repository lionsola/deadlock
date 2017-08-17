package server.passive;

import server.character.InputControlledEntity;
import server.world.World;

public class FastRegen extends Passive {

	public FastRegen(InputControlledEntity self) {
		super(self);
		
	}

	@Override
	protected double calculateActivationLevel(World w) {
		return 0;
	}

	@Override
	public void onUpdate(World w) {
		self().setHealthPoints(self().getHealthPoints() + self().getMaxHP()/60);
	}
}
