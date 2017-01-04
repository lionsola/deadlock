package server.ai;


import server.character.InputControlledEntity;
import server.network.ServerPlayer;
import server.world.Arena;
import shared.network.GameDataPackets.InputPacket;
import shared.network.GameDataPackets.WorldStatePacket;
import shared.network.event.GameEvent;

/**
 * Used to model the behaviour of an AI player.
 * 
 * @author Anh Pham
 */
public class AIPlayer extends ServerPlayer implements GameEvent.Listener {
	WorldStatePacket wsp;
	private Brain brain = new Brain();
	public AIPlayer(int id, int team) {
		super(id, team, "BOT"+id, null);
	}

	public void init(Arena a, InputControlledEntity pc, PathFinder p) {
		brain.init(a, pc, p);
	}
	
	@Override
	public void sendData(WorldStatePacket wsp) {
		this.wsp = wsp;
		for (GameEvent e:wsp.events) {
			brain.processEvent(e);
		}
	}
	
	@Override
	public InputPacket getInput() {
		try {
			Thread.sleep(10);
		} catch (Exception e) {
			e.printStackTrace();
		}
		brain.update(wsp);
		return brain.getInput();
	}

	@Override
	public void onEventReceived(GameEvent event) {
		// TODO Auto-generated method stub
		
	}
}
