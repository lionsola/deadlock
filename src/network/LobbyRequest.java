package network;

import gui.ClientPlayer;

import java.io.Serializable;

/**
 * Models a request from lobby.
 * 
 * @author Anh Pham
 */
public class LobbyRequest implements Serializable {
	
	private static final long serialVersionUID = 5389259171461883295L;

	public static class SwitchTeamRequest extends LobbyRequest {
		private static final long serialVersionUID = -2501388485453266685L;
		public final int playerId;
		public final int desTeam;

		public SwitchTeamRequest(int id, int to) {
			playerId = id;
			desTeam = to;
		}
	}

	public static class ChangeCharacterRequest extends LobbyRequest {
		private static final long serialVersionUID = -3235244044264803979L;
		public final int playerId;
		public final int typeId;

		public ChangeCharacterRequest(int playerId, int typeId) {
			this.playerId = playerId;
			this.typeId = typeId;
		}
	}

	/**
	 * Every time a client connects, the network sends this packet to it to initialize the lobby
	 * gui.
	 */
	public static class LobbyInformationPacket extends LobbyRequest {
		private static final long serialVersionUID = 4996573810361822207L;
		public int id;
		public ClientPlayer[] clientPlayers;
		public GameConfig gameConfig;
	}

	public static class NewPlayerRequest extends LobbyRequest {
		private static final long serialVersionUID = -8392883135040841162L;
		public final ClientPlayer newPlayer;

		public NewPlayerRequest(ClientPlayer newPlayer) {
			this.newPlayer = newPlayer;
		}
	}

	public static class PlayerLeaveRequest extends LobbyRequest {
		private static final long serialVersionUID = -3183562839276734099L;
		public final int id;

		public PlayerLeaveRequest(int id) {
			this.id = id;
		}
	}

	public static class ChatRequest extends LobbyRequest {
		private static final long serialVersionUID = -5223271755845479843L;
		public final int id;
		public final String line;

		public ChatRequest(int id, String line) {
			this.id = id;
			this.line = line;
		}
	}

	/**
	 * 
	 */
	public static class GameConfig implements Serializable {
		private static final long serialVersionUID = 3557083906903628498L;
		public String arena;
		// other information about the game mode
	}

	public static class ToggleReadyRequest extends LobbyRequest {
		private static final long serialVersionUID = 2714262233256790428L;
		public final int id;
		public final boolean ready;

		public ToggleReadyRequest(int id, boolean ready) {
			this.id = id;
			this.ready = ready;
		}
	}

	public static class StartGameRequest extends LobbyRequest {
		private static final long serialVersionUID = -8866955166004741417L;
	}

	public static class ChangeArenaRequest extends LobbyRequest {
		private static final long serialVersionUID = -5417944547018933203L;
		public final String arenaName;

		public ChangeArenaRequest(String arena) {
			arenaName = arena;
		}
	}
	
}
