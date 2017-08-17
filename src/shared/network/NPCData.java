package shared.network;

import server.character.InputControlledEntity;

public class NPCData extends CharData {
	private static final long serialVersionUID = -5921571142439143525L;

	public float alertness;
	
	public NPCData(InputControlledEntity e) {
		super(e);
		alertness = (float) e.brain.getAlertness();
	}
}
