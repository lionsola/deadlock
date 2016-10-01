package server.ability;

import client.gui.GameWindow;
import server.character.ControlledCharacter;
import server.world.World;

public class HearingAmplifier extends ToggleAbility {
	public static final double HEAR_RANGE = -0.8;
	public static final double HEAR_SPEED = -0.5;
	public static final double HEAR_HEAR = 0.5;
	public static final double HEAR_TRANS = 1000;
	public static final long COOLDOWN = 4000;
	
	private long transElapsed = Long.MAX_VALUE;
	private final double rangeInc;
	private final double hearInc;
	private final double speedInc;
	
	public HearingAmplifier(ControlledCharacter self) {
		super(self,COOLDOWN);
		rangeInc = (HEAR_RANGE)*GameWindow.MS_PER_UPDATE/HEAR_TRANS;
		hearInc = (HEAR_HEAR)*GameWindow.MS_PER_UPDATE/HEAR_TRANS;
		speedInc = self.cs.getSpeedF()*(HEAR_SPEED)*GameWindow.MS_PER_UPDATE/HEAR_TRANS;
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
				self().addFovRangeMod(+rangeInc);
				self().addHearMod(+hearInc);
				self().addSpeedMod(+speedInc);
			} else {
				self().addFovRangeMod(-rangeInc);
				self().addHearMod(-hearInc);
				self().addSpeedMod(-speedInc);
			}
			transElapsed += GameWindow.MS_PER_UPDATE;
		}
	}
}
