package server.passive;

import server.character.InputControlledEntity;
import server.world.Geometry;
import server.world.World;

public class Assault extends Passive {
	public static final double AS_BODYINSTA = -0.2;
	public static final double AS_SPEED = 0.2;
	public static final double AS_RECOIL = -0.5;
	public static final double AS_MAXANGLE = Math.PI*2/3;
	public static final double AS_MINANGLE = Math.PI/3;
	public static final double AS_TRANS = 300;
	
	private double increasedSpeed;
	private double increasedInsta;
	private double increasedRecoil;
	
	public Assault(InputControlledEntity self) {
		super(self);
	}

	@Override
	protected void onUpdate(World w) {
		double ratio = getActivationLevel();
		double targetSpeed = AS_SPEED*ratio;
		double targetInsta = AS_BODYINSTA*ratio;
		double targetRecoil = AS_RECOIL*ratio;
		
		self().addSpeedMod(targetSpeed-increasedSpeed);
		self().addInstaMod(targetInsta-increasedInsta);
		self().getWeapon().addRecoilMod(targetRecoil-increasedRecoil);
		increasedSpeed = targetSpeed;
		increasedInsta = targetInsta;
		increasedRecoil = targetRecoil;
	}

	@Override
	protected double calculateActivationLevel(World w) {
		if (self().isMoving()) {
			double diffAngle = Math.abs(Geometry.wrapAngle(self().getMovingDirection()-self().getDirection()));
			double level = 0;
			if (diffAngle<=AS_MINANGLE) {
				level = 1;
			} else if (diffAngle>=AS_MAXANGLE) {
				level = 0;
			} else {
				level = 1-(diffAngle-AS_MINANGLE)/(AS_MAXANGLE-AS_MINANGLE);
			}
			return level;
		} else {
			return 0;
		}
	}
}
