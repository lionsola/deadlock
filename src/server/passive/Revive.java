package server.passive;

import server.character.Entity;
import server.character.InputControlledEntity;
import server.world.World;

public class Revive extends Passive {
	Entity target;
	double progress;
	public Revive(InputControlledEntity self) {
		super(self);
		
	}

	@Override
	protected double calculateActivationLevel(World w) {
		return progress;
	}

	@Override
	public void update(World w) {
		if (target==null) {
			for (InputControlledEntity e :w.getCharacters()) {
				if (e.isDead() && e.team==self().team &&
						e.getPosition().distance(self().getPosition())<e.getRadius()) {
					target = e;
					break;
				}
			}
		}
		else {
			if (target.getPosition().distance(self().getPosition())<target.getRadius()) {
				progress += 0.005;
			} else {
				progress = 0;
			}
		}
		
	}
}
