package server.passive;

import server.character.PlayerCharacter;
import server.world.World;

public abstract class Passive {
	private PlayerCharacter self;
	private boolean isActive;
	public Passive(PlayerCharacter self) {
		this.self = self;
	}
	
	public void update(World w) {
		if (!isActive && trigger()) {
			onActivate(w);
			isActive = true;
		} else if (isActive) {
			if (!trigger()) {
				onDeactivate(w);
				isActive = false;
			}
		}
		onUpdate(w);
	}
	
	protected void onDeactivate(World w) {};

	protected void onActivate(World w) {};

	protected PlayerCharacter self() {
		return self;
	}
	
	protected abstract boolean trigger();
	
	
	protected void onUpdate(World w) {};
	
	public boolean isActive () {
		return isActive;
	}
}
