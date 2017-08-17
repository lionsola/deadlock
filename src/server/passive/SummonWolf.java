package server.passive;

import client.gui.GameWindow;
import editor.SpawnPoint.Behaviour;
import editor.SpawnPoint.CharType;
import server.ai.NPCBrain;
import server.character.InputControlledEntity;
import server.world.World;

public class SummonWolf extends Passive {
	public SummonWolf(InputControlledEntity self) {
		super(self);
	}

	@Override
	protected double calculateActivationLevel(World w) {
		if (getActivationLevel()<1) {
			return getActivationLevel() + GameWindow.MS_PER_UPDATE/30000.0;
		} else {
			return getActivationLevel()-1;
		}
	}

	@Override
	protected void onUpdate(World w) {
		if (getActivationLevel()>=1) {
			InputControlledEntity e = new InputControlledEntity(w.generateUniqueID(),self().team,CharType.Wolf.id);
			InputControlledEntity.initAbilitySet(e, -1, -1);
			e.brain = new NPCBrain(Behaviour.Custom, w.getArena(), e);
			e.setPosition(w, self().getX(), self().getY());
			w.addCharacter(e);
		}
	}
}
