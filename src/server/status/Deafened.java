package server.status;

import client.gui.GameWindow;
import server.character.Entity;
import server.world.World;

public class Deafened extends StatusEffect {
	public static final double RECOVER_TIME = 0.5;
	int phase = 0;
	double originalHearF;
	double hearFInc;
	double hearFIncreased;
	public Deafened(Entity self, long duration) {
		super(self, duration);
		originalHearF = getSelf().getHearF();
		hearFIncreased = 0;
		hearFInc = originalHearF*GameWindow.MS_PER_UPDATE/(duration*RECOVER_TIME);
	}

	@Override
	public void onFinish(World w) {
		getSelf().addHearMod(+(originalHearF-hearFIncreased));
	}

	@Override
	public void update(World w) {
		if (phase==0 && 1.0*getElapsed()/getDuration() >= 1-RECOVER_TIME) {
			phase = 1;
		} else if (phase==1) {
			getSelf().addHearMod(+hearFInc);
			hearFIncreased += hearFInc;
		}
		super.update(w);
	}

	@Override
	public void start() {
		getSelf().addHearMod(-originalHearF);
	}
}