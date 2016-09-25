package passive;

import character.Armor;
import character.ControlledCharacter;
import core.World;

public class Shield extends Passive {

	public Shield(ControlledCharacter self) {
		super(self);
		self.setArmor(new Armor(self,-Math.PI*0.1,Math.PI*0.4));
	}

	@Override
	protected boolean trigger() {
		return true;
	}

	@Override
	protected void onUpdate(World w) {
	}
}
