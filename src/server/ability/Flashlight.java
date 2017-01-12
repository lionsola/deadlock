package server.ability;

import server.character.InputControlledEntity;
import server.world.Light;
import server.world.World;

public class Flashlight extends ToggleAbility {
	private final Light light = new Light(0xffffff,2);
	public Flashlight(InputControlledEntity self) {
		super(Ability.LIGHT_ID, self, 200);
	}

	@Override
	protected void onUpdate(World w, InputControlledEntity c) {
		if (isActive()) {
			light.setX(c.getInput().cx);
			light.setY(c.getInput().cy);
		}
	}

	@Override
	protected void onActivate(World w, InputControlledEntity c) {
		w.addDynamicLight(light);
	}

	@Override
	protected void onDeactivate(World w, InputControlledEntity c) {
		w.removeLight(light);
	}

}
