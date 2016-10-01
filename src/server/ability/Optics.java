package server.ability;

import client.gui.GameWindow;
import server.character.ControlledCharacter;
import server.world.World;

public class Optics extends ToggleAbility {
	public static final double BINO_RANGE = 0.7;
	public static final double BINO_ANGLE = -0.7;
	public static final double BINO_SPEED = -0.5;
	public static final double BINO_TRANS = 500;
	public static final long COOLDOWN = 4000;
	
	private long transElapsed = Long.MAX_VALUE;
	private final double rangeInc;
	private final double angleInc;
	private final double speedInc;
	
	public Optics(ControlledCharacter self) {
		super(self,COOLDOWN);
		rangeInc = (BINO_RANGE)*GameWindow.MS_PER_UPDATE/BINO_TRANS;
		angleInc = (BINO_ANGLE)*GameWindow.MS_PER_UPDATE/BINO_TRANS;
		speedInc = self.cs.getSpeedF()*(BINO_SPEED)*GameWindow.MS_PER_UPDATE/BINO_TRANS;
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
				self().addFovRangeMod(+rangeInc);
				self().addFovAngleMod(+angleInc);
				self().addSpeedMod(+speedInc);
			} else {
				self().addFovRangeMod(-rangeInc);
				self().addFovAngleMod(-angleInc);
				self().addSpeedMod(-speedInc);
			}
			transElapsed += GameWindow.MS_PER_UPDATE;
		}
	}
}
