package shared.network;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * The game data packets sent through the network.
 * 
 * @author Anh Pham
 */
public class GameDataPackets {
	
	public static class InputPacket implements Serializable {
		private static final long serialVersionUID = -5467864280005368467L;
		public String chatText;
		public long time;
		public float cx;
		public float cy;
		public boolean top;
		public boolean down;
		public boolean left;
		public boolean right;
		public boolean running;
		public boolean sneaking;

		public boolean fire1;
		public boolean fire2;
	}

	/**
	 * 
	 * @author Anh Pham
	 * 
	 *         The thing that server sends to clients every frame to draw their screen.
	 *
	 */
	public static class WorldStatePacket implements Serializable {
		private static final long serialVersionUID = 2388807190734680235L;
		public List<String> chatTexts = new LinkedList<String>();
		public long time;
		public FullCharacterData player;
		public List<PartialCharacterData> characters = new LinkedList<PartialCharacterData>();
		//public List<PartialCharacterData> hostages;
		public List<ProjectileData> projectiles = new LinkedList<ProjectileData>();
		public List<GameEvent> events = new LinkedList<GameEvent>();
		public List<Vision> visions = new LinkedList<Vision>();
	}
	
}
