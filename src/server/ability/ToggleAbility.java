package server.ability;

import server.character.InputControlledEntity;
import server.world.World;

/**
 * Toggle abilities can be turned on and off using the activate button.
 * However, there is a cooldown period for both actions.
 */
public abstract class ToggleAbility extends Ability {
	private boolean isActive = false;
	public ToggleAbility(int id, InputControlledEntity self, long cooldown) {
		super(id,self,cooldown);
	}

	@Override
	public void update(World w) {
		super.update(w);
		if (!self().isDead()) {
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
	}

	public boolean isActive() {
		return isActive;
	}
	
	protected abstract void onUpdate(World w, InputControlledEntity c);
	protected abstract void onActivate(World w, InputControlledEntity c);
	protected abstract void onDeactivate(World w, InputControlledEntity c);
}
