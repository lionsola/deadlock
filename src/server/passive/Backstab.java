package server.passive;

import server.character.PlayerCharacter;
import server.world.Geometry;
import server.world.World;
import shared.network.CharData;

public class Backstab extends Passive {
	public static final double BS_MIN_ANGLE = Math.PI/10;
	public static final double BS_RECOIL = -0.5;
	public static final double BS_NOISE = -0.3;

	public Backstab(PlayerCharacter self) {
		super(self);
	}

	@Override
	protected void onDeactivate(World w) {
		self().addNoiseMod(-BS_NOISE);
		self().getWeapon().addRecoilMod(- self().getWeapon().type.instability*BS_RECOIL);
	}

	@Override
	protected void onActivate(World w) {
		self().addNoiseMod(+BS_NOISE);
		self().getWeapon().addRecoilMod(+ self().getWeapon().type.instability*BS_RECOIL);
	}

	@Override
	protected double calculateActivationLevel(World w) {
		double max = 0;
		for (CharData c:self().getPerception().characters) {
			double dir = Math.abs(Geometry.wrapAngle(c.direction - Math.atan2(c.y-self().getY(),c.x-self().getX())));
			double activation = (BS_MIN_ANGLE - dir)/BS_MIN_ANGLE;
			if (activation > max) {
				max = activation;
			}
		}
		return max;
	}
}
