package shared.network;

import server.character.NPC;

public class NPCData extends CharData {
	private static final long serialVersionUID = -5921571142439143525L;

	public float alertness;
	
	public NPCData(NPC e) {
		super(e);
		alertness = (float) e.brain.getAlertness();
	}
}
