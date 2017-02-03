package server.character;

import editor.SpawnPoint.Behaviour;
import server.ai.NPCBrain;
import server.world.Arena;
import server.world.World;
import shared.network.NPCData;

public class NPC extends InputControlledEntity {
	public NPC(int id, int team, int typeId) {
		super(id, team, typeId);
	}

	public NPCBrain brain;
	
	public void init(Arena a, Behaviour b) {
		brain = new NPCBrain(b);
		brain.init(a, this); 
	}
	
	@Override
	public void update(World w) {
		super.update(w);
		if (!isDead()) {
			brain.update(getPerception());
			getPerception().events.clear();
		}
	}
	
	@Override
	public NPCData generatePartial() {
		NPCData data = new NPCData(this);
		return data;
	}
}
