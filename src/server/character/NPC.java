package server.character;

import editor.SpawnPoint.Behaviour;
import server.ai.NPCBrain;
import server.ai.PathFinder;
import server.world.Arena;
import server.world.World;
import shared.network.NPCData;
import shared.network.event.GameEvent;

public class NPC extends InputControlledEntity {
	public NPCBrain brain;
	
	public NPC(int id, int team, ClassStats cs, Arena a, Behaviour behaviour) {
		super(id, team, cs);
		brain = new NPCBrain(behaviour);
		brain.init(a, this, new PathFinder(a));
	}
	
	@Override
	public void update(World w) {
		super.update(w);
		if (!isDead()) {
			for (GameEvent e:getPerception().events) {
				brain.processEvent(e);
			}
			brain.update(getPerception());
		}
	}
	
	@Override
	public NPCData generatePartial() {
		NPCData data = new NPCData(this);
		return data;
	}
	
	public static NPC createNPC(int id, int team, int setup, int behaviour, int level) {
		return null;
	}
}
