package passive;

import character.ControlledCharacter;
import core.Geometry;
import core.World;
import network.PartialCharacterData;

public class Backstab extends Passive {
	public static final double BS_MIN_ANGLE = 135;
	public static final double BS_INSTA = -0.5;
	public static final double BS_NOISE = -0.3;

	public Backstab(ControlledCharacter self) {
		super(self);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onDeactivate(World w) {
		self().setNoiseF(self().getNoiseF()-BS_NOISE);
		self().getPrimary().setInstability(self().getPrimary().getInstability() - self().getPrimary().type.instability*BS_INSTA);
	}

	@Override
	protected void onActivate(World w) {
		self().setNoiseF(self().getNoiseF()+BS_NOISE);
		self().getPrimary().setInstability(self().getPrimary().getInstability() + self().getPrimary().type.instability*BS_INSTA);
	}

	@Override
	protected boolean trigger() {
		for (PartialCharacterData c:self().getPerception().characters) {
			double dir = Geometry.wrapAngle(c.direction - Math.atan2(c.y-self().getY(),c.x-self().getX()));
			if (Math.abs(dir)<BS_MIN_ANGLE) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected void onUpdate(World w) {
		if (isActive()) {
			
		}
	}

}
