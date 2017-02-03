package server.ability;

import client.gui.GameWindow;
import server.character.InputControlledEntity;
import server.world.World;

public class Optics extends ToggleAbility {
	public final double RANGE;
	public final double ANGLE;
	public final double SPEED;
	
	public static final double TRANS = 500;
	public static final long COOLDOWN = 4000;
	
	private long transElapsed = Long.MAX_VALUE;
	private final double rangeInc;
	private final double angleInc;
	private final double speedInc;
	private final boolean allowWeapon;
	
	public Optics(int id, InputControlledEntity self, double rangeMod, double angleMod, double speedMod, boolean allowWeapon) {
		super(id,self,COOLDOWN);
		this.allowWeapon = allowWeapon;
		RANGE = rangeMod;
		ANGLE = angleMod;
		SPEED = speedMod;
		
		rangeInc = (RANGE)*GameWindow.MS_PER_UPDATE/TRANS;
		angleInc = (ANGLE)*GameWindow.MS_PER_UPDATE/TRANS;
		speedInc = SPEED*GameWindow.MS_PER_UPDATE/TRANS;
	}

	@Override
	protected void onActivate(World w, InputControlledEntity c) {
		if (!allowWeapon) {
			self().getWeapon().setStatus(false);
		}
		transElapsed = 0;
	}

	@Override
	protected void onDeactivate(World w, InputControlledEntity c) {
		if (!allowWeapon) {
			self().getWeapon().setStatus(true);
		}
		transElapsed = 0;
	}
	
	@Override
	protected void onUpdate(World w, InputControlledEntity c) {
		if (transElapsed<TRANS) {
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
	
	public static class Scope extends Optics {
		public static final double SCOPE_RANGE = 0.25;
		public static final double SCOPE_ANGLE = -0.75;
		public static final double SCOPE_SPEED = -0.25;
		
		public Scope(InputControlledEntity self) {
			super(SCOPE_ID,self, SCOPE_RANGE, SCOPE_ANGLE, SCOPE_SPEED, true);
		}
	}
	
	public static class Binocular extends Optics {
		public static final double BINO_RANGE = 0.25;
		public static final double BINO_ANGLE = -0.5;
		public static final double BINO_SPEED = -0.5;
		
		public Binocular(InputControlledEntity self) {
			super(BINO_ID,self, BINO_RANGE, BINO_ANGLE, BINO_SPEED, false);
		}
	}
}
