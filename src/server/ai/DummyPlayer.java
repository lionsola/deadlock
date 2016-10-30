package server.ai;

import server.network.ServerPlayer;
import shared.network.GameDataPackets.InputPacket;
import shared.network.GameDataPackets.WorldStatePacket;

public class DummyPlayer extends ServerPlayer {

	public DummyPlayer(int id, int team) {
		super(id, team, "Dummy"+id, null);
	}

	@Override
	public InputPacket getInput() {
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return new InputPacket();
	}
	
	@Override
	public void sendData(WorldStatePacket wsp) {
	}
}
