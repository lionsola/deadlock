package server.status;

import client.gui.GameWindow;
import server.character.Entity;
import server.world.World;

public class Blinded extends StatusEffect {
	public static final double RECOVER_TIME = 0.5;
	int phase = 0;
	double originalFovRange;
	double fovRangeInc;
	double fovRangeIncreased;
	public Blinded(Entity self, long duration) {
		super(self, duration);
		originalFovRange = getSelf().getFovRangeF();
		fovRangeIncreased = 0;
		fovRangeInc = originalFovRange*GameWindow.MS_PER_UPDATE/(duration*RECOVER_TIME);
	}

	@Override
	public void onFinish(World w) {
		getSelf().addFovRangeMod(+(originalFovRange-fovRangeIncreased));
	}

	@Override
	public void update(World w) {
		if (phase==0 && 1.0*getElapsed()/getDuration() >= 1-RECOVER_TIME) {
			phase = 1;
		} else if (phase==1) {
			getSelf().addFovRangeMod(+fovRangeInc);
			fovRangeIncreased += fovRangeInc;
		}
		super.update(w);
	}

	@Override
	public void start() {
		getSelf().addFovRangeMod(-originalFovRange);
	}
}
