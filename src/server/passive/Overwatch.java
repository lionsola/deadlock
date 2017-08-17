package server.passive;

import client.gui.GameWindow;
import server.character.InputControlledEntity;
import server.world.World;

public class Overwatch extends Passive {
	public static final double OW_RANGE = 0.2;
	public static final double OW_RECOIL = -0.5;
	public static final double OW_TRANS = 3000;
	
	private final double rangeInc;
	
	private double rangeIncreased = 0;
	
	public Overwatch(InputControlledEntity self) {
		super(self);
		rangeInc = OW_RANGE*GameWindow.MS_PER_UPDATE/OW_TRANS;
	}
	
	@Override
	protected void onUpdate(World w) {
		if (!self().isMoving()) {
			if (rangeIncreased<OW_RANGE) {
				self().addFovRangeMod(+ rangeInc);
				rangeIncreased += rangeInc;
			}
		} else {
			if (rangeIncreased>0) {
				self().addFovRangeMod(- rangeInc);
				rangeIncreased -= rangeInc;
			}
		}
	}

	@Override
	protected void onDeactivate(World w) {
		self().addRecoilMod(-OW_RECOIL);
	}

	@Override
	protected void onActivate(World w) {
		self().addRecoilMod(OW_RECOIL);
	}

	@Override
	protected double calculateActivationLevel(World w) {
		return 0;
	}
}
