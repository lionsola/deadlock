package server.ability;

import server.character.ControlledCharacter;
import server.world.World;

public abstract class ToggleAbility extends Ability {
	private boolean isActive = false;
	public ToggleAbility(ControlledCharacter self, long cooldown) {
		super(self,cooldown);
	}

	@Override
	public void update(World w) {
		super.update(w);
		if(!isActive && self().getInput().fire2 && isReady()) {
			onActivate(w,self());
			isActive = true;
			startCooldown();
		} else if (isActive) {
			if (self().getInput().fire2 && isReady()) {
				onDeactivate(w,self());
				isActive = false;
				startCooldown();
			}
		}
		onUpdate(w,self());
	}

	public boolean isActive() {
		return isActive;
	}
	
	protected abstract void onUpdate(World w, ControlledCharacter c);
	protected abstract void onActivate(World w, ControlledCharacter c);
	protected abstract void onDeactivate(World w, ControlledCharacter c);
}
