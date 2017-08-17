package server.status;

import server.character.Entity;
import server.character.InputControlledEntity;
import server.world.World;

public class Fear extends StatusEffect {
	public static final double FEAR_HEAR = -0.2;
	public static final double FEAR_INSTABILITY = 0.3;
	
	public Fear(Entity self, long duration) {
		super(self, duration);
	}

	@Override
	public void start() {
		getSelf().addHearMod(FEAR_HEAR);
		if (getSelf() instanceof InputControlledEntity) {
			((InputControlledEntity) getSelf()).addInstaMod(FEAR_INSTABILITY);
		}
	}

	@Override
	public void onFinish(World w) {
		getSelf().addHearMod(-FEAR_HEAR);
		if (getSelf() instanceof InputControlledEntity) {
			((InputControlledEntity) getSelf()).addInstaMod(-FEAR_INSTABILITY);
		}
	}

}
