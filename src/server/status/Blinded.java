package server.status;

import client.gui.GameWindow;
import server.character.Character;

public class Blinded extends StatusEffect {
	public static final double RECOVER_TIME = 0.5;
	int phase = 0;
	double originalFovRange;
	double fovRangeInc;
	double fovRangeIncreased;
	public Blinded(Character self, long duration) {
		super(self, duration);
		originalFovRange = getSelf().getFovRangeF();
		fovRangeIncreased = 0;
		fovRangeInc = originalFovRange*GameWindow.MS_PER_UPDATE/(duration*RECOVER_TIME);
		
		getSelf().addFovRangeMod(-originalFovRange);
	}

	@Override
	public void onFinish() {
		getSelf().addFovRangeMod(+(originalFovRange-fovRangeIncreased));
	}

	@Override
	public void onUpdate() {
		if (phase==0 && 1.0*getElapsed()/getDuration() >= 1-RECOVER_TIME) {
			phase = 1;
		} else if (phase==1) {
			getSelf().addFovRangeMod(+fovRangeInc);
			fovRangeIncreased += fovRangeInc;
		}
	}
}
