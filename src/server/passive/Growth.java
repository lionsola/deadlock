package server.passive;

import client.gui.GameWindow;
import editor.SpawnPoint.Behaviour;
import editor.SpawnPoint.CharType;
import server.ai.NPCBrain;
import server.character.InputControlledEntity;
import server.world.World;

public class Growth extends Passive {
	static final double DURATION = 10000;
	
	public Growth(InputControlledEntity self) {
		super(self);
	}
	
	public Growth(InputControlledEntity self, boolean activated) {
		super(self);
	}

	@Override
	protected double calculateActivationLevel(World w) {
		if (self().typeId==CharType.Wolf.id && getActivationLevel()<1) {
			return getActivationLevel() + GameWindow.MS_PER_UPDATE/DURATION;
		} else {
			return getActivationLevel();
		}
	}

	@Override
	public void onUpdate(World w) {
		if (self().typeId==CharType.Wolf.id && getActivationLevel()<1) {
			w.removeCharacter(self());
			InputControlledEntity e = new InputControlledEntity(self().id,self().team,CharType.WolfBig.id);
			InputControlledEntity.initAbilitySet(e, -1, -1);
			e.brain = new NPCBrain(Behaviour.NPCWolf, w.getArena(), e);
			w.addCharacter(e);
		}
	}
}
