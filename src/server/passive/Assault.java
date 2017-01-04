package server.passive;

import server.character.InputControlledEntity;
import server.world.Geometry;
import server.world.World;

public class Assault extends Passive {
	public static final double AS_BODYINSTA = -0.2;
	public static final double AS_SPEED = 0.2;
	public static final double AS_MAXANGLE = Math.PI/2;
	public static final double AS_TRANS = 300;
	
	private double targetSpeed;
	private double targetInsta;
	
	private double increasedSpeed;
	private double increasedInsta;
	
	public Assault(InputControlledEntity self) {
		super(self);
	}

	@Override
	protected void onUpdate(World w) {
		double ratio = getActivationLevel();
		targetSpeed = AS_SPEED*ratio;
		targetInsta = AS_BODYINSTA*ratio;
		
		self().addSpeedMod(targetSpeed-increasedSpeed);
		self().addInstaMod(targetInsta-increasedInsta);
		increasedSpeed = targetSpeed;
		increasedInsta = targetInsta;
	}

	@Override
	protected double calculateActivationLevel(World w) {
		if (self().isMoving()) {
			double diffAngle = Math.abs(Geometry.wrapAngle(self().getMovingDirection()-self().getDirection()));
			double level = Math.max(0,(AS_MAXANGLE - diffAngle)/AS_MAXANGLE);
			return level;
		} else {
			return 0;
		}
	}
}
