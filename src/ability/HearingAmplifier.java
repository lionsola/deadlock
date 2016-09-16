package ability;

import character.ControlledCharacter;
import core.World;
import game.Game;

public class HearingAmplifier extends ToggleAbility {
	public static final double HEAR_RANGE = -0.8;
	public static final double HEAR_SPEED = -0.5;
	public static final double HEAR_THRES = -0.8;
	public static final double HEAR_TRANS = 1000;
	public static final long COOLDOWN = 4000;
	
	private long transElapsed = Long.MAX_VALUE;
	private final double rangeInc;
	private final double thresInc;
	private final double speedInc;
	
	public HearingAmplifier(ControlledCharacter self) {
		super(self,COOLDOWN);
		rangeInc = (HEAR_RANGE)*Game.MS_PER_UPDATE/HEAR_TRANS;
		thresInc = (HEAR_THRES)*Game.MS_PER_UPDATE/HEAR_TRANS;
		speedInc = self.cs.getSpeedF()*(HEAR_SPEED)*Game.MS_PER_UPDATE/HEAR_TRANS;
	}

	@Override
	protected void onActivate(World w, ControlledCharacter c) {
		transElapsed = 0;
	}

	@Override
	protected void onDeactivate(World w, ControlledCharacter c) {
		transElapsed = 0;
	}
	
	@Override
	protected void onUpdate(World w, ControlledCharacter c) {
		if (transElapsed<HEAR_TRANS) {
			if (isActive()) {
				self().setViewRangeF(self().getFovRangeF()+rangeInc);
				self().setHearF(self().getHearF()+thresInc);
				self().setSpeedF(self().getSpeedF()+speedInc);
			} else {
				self().setViewRangeF(self().getFovRangeF()-rangeInc);
				self().setHearF(self().getHearF()-thresInc);
				self().setSpeedF(self().getSpeedF()-speedInc);
			}
			transElapsed += Game.MS_PER_UPDATE;
		}
	}
}
