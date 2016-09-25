package passive;

import character.ControlledCharacter;
import core.World;
import game.Game;

public class Overwatch extends Passive {
	public static final double OW_RANGE = 0.2;
	public static final double OW_RECOIL = -0.5;
	public static final double OW_TRANS = 3000;
	
	private final double rangeInc;
	
	private double rangeIncreased = 0;
	
	public Overwatch(ControlledCharacter self) {
		super(self);
		rangeInc = OW_RANGE*Game.MS_PER_UPDATE/OW_TRANS;
	}
	
	@Override
	protected void onUpdate(World w) {
		if (isActive()) {
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
		self().getPrimary().addRecoilMod(-self().getPrimary().type.instability*OW_RECOIL);
	}

	@Override
	protected void onActivate(World w) {
		self().getPrimary().addRecoilMod(self().getPrimary().type.instability*OW_RECOIL);
	}

	@Override
	protected boolean trigger() {
		return !self().isMoving();
	}
}
