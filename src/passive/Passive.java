package passive;

import character.ControlledCharacter;
import core.World;

public abstract class Passive {
	private ControlledCharacter self;
	private boolean isActive;
	public Passive(ControlledCharacter self) {
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
	
	protected abstract void onDeactivate(World w);

	protected abstract void onActivate(World w);

	protected ControlledCharacter self() {
		return self;
	}
	
	protected abstract boolean trigger();
	
	
	protected abstract void onUpdate(World w);
	
	public boolean isActive () {
		return isActive;
	}
}
