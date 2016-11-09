package server.ability;

import server.character.PlayerCharacter;
import server.world.World;

/**
 * Toggle abilities can be turned on and off using the activate button.
 * However, there is a cooldown period for both actions.
 */
public abstract class ToggleAbility extends Ability {
	private boolean isActive = false;
	public ToggleAbility(PlayerCharacter self, long cooldown) {
		super(self,cooldown);
	}

	@Override
	public void update(World w) {
		super.update(w);
		if(!isActive && self().getInput().fire2 && !self().getInput().alt && isReady()) {
			onActivate(w,self());
			isActive = true;
			startCooldown();
		} else if (isActive) {
			if (self().getInput().fire2 && !self().getInput().alt && isReady()) {
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
	
	protected abstract void onUpdate(World w, PlayerCharacter c);
	protected abstract void onActivate(World w, PlayerCharacter c);
	protected abstract void onDeactivate(World w, PlayerCharacter c);
}
