package passive;

import character.ControlledCharacter;
import core.World;

public class Assault extends Passive {
	public static final double AS_BODYINSTA = -0.2;
	public Assault(ControlledCharacter self) {
		super(self);
	}

	@Override
	protected void onDeactivate(World w) {
		self().setInstaF(self().getInstaF() - AS_BODYINSTA);
	}

	@Override
	protected void onActivate(World w) {
		self().setInstaF(self().getInstaF() + AS_BODYINSTA);
	}

	@Override
	protected boolean trigger() {
		return self().getDx()!=0 || self().getDy()!=0;
	}

	@Override
	protected void onUpdate(World w) {
		// TODO Auto-generated method stub

	}

}
