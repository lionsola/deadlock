package server.passive;

import server.character.ControlledCharacter;
import server.world.Geometry;
import server.world.World;

public class Assault extends Passive {
	public static final double AS_BODYINSTA = -0.2;
	public static final double AS_SPEED = 0.2;
	public static final double AS_MAXANGLE = Math.PI/2;
	public static final double AS_TRANS = 300;
	private double diffAngle = 0;
	
	private double targetSpeed;
	private double targetInsta;
	
	private double increasedSpeed;
	private double increasedInsta;
	
	public Assault(ControlledCharacter self) {
		super(self);
	}

	@Override
	protected boolean trigger() {
		return diffAngle<AS_MAXANGLE;
	}

	@Override
	protected void onUpdate(World w) {
		diffAngle = Math.abs(Geometry.wrapAngle(self().getMovingDirection()-self().getDirection()));
		if (isActive()) {
			double ratio = (AS_MAXANGLE - diffAngle)/AS_MAXANGLE;
			targetSpeed = AS_SPEED*ratio;
			targetInsta = AS_BODYINSTA*ratio;
		}
		else {
			targetSpeed = 0;
			targetInsta = 0;
		}
		
		self().addSpeedMod(targetSpeed-increasedSpeed);
		self().addInstaMod(targetInsta-increasedInsta);
		increasedSpeed = targetSpeed;
		increasedInsta = targetInsta;
	}
}
