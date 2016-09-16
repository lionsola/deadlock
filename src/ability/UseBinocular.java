package ability;

import character.ControlledCharacter;
import core.World;
import game.Game;

public class UseBinocular extends ToggleAbility {
	public static final double BINO_RANGE = 1.5;
	public static final double BINO_ANGLE = 0.5;
	public static final double BINO_SPEED = 0.5;
	public static final double BINO_TRANS = 500;
	public static final long COOLDOWN = 4000;
	
	private long transElapsed = Long.MAX_VALUE;
	private final double rangeInc;
	private final double angleInc;
	private final double speedInc;
	
	public UseBinocular(ControlledCharacter self) {
		super(self,COOLDOWN);
		rangeInc = (BINO_RANGE-1)*Game.MS_PER_UPDATE/BINO_TRANS;
		angleInc = (BINO_ANGLE-1)*Game.MS_PER_UPDATE/BINO_TRANS;
		speedInc = self.cs.getSpeedF()*(BINO_SPEED-1)*Game.MS_PER_UPDATE/BINO_TRANS;
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
		if (transElapsed<BINO_TRANS) {
			if (isActive()) {
				self().setViewRangeF(self().getFovRangeF()+rangeInc);
				self().setViewAngleF(self().getFovAngleF()+angleInc);
				self().setSpeedF(self().getSpeedF()+speedInc);
			} else {
				self().setViewRangeF(self().getFovRangeF()-rangeInc);
				self().setViewAngleF(self().getFovAngleF()-angleInc);
				self().setSpeedF(self().getSpeedF()-speedInc);
			}
			transElapsed += Game.MS_PER_UPDATE;
		}
	}
}
