package server.passive;

import server.character.Armor;
import server.character.PlayerCharacter;
import server.world.World;

public class Shield extends Passive {

	public Shield(PlayerCharacter self) {
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
