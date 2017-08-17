package server.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import client.gui.ClientPlayer;
import client.gui.HostScreen;
import editor.DataManager;
import editor.SpawnPoint;
import editor.SpawnPoint.CharType;
import editor.SpawnPoint.SpawnType;
import server.ai.AIPlayer;
import server.ai.DummyPlayer;
import server.world.Arena.ArenaData;
import shared.network.Connection;
import shared.network.LobbyRequest;
import shared.network.LobbyRequest.ChangeCharSetup;
import shared.network.LobbyRequest.ChangeSpawnRequest;
import shared.network.LobbyRequest.ChatRequest;
import shared.network.LobbyRequest.LobbyInformationPacket;
import shared.network.LobbyRequest.PlayerLeaveRequest;
import shared.network.LobbyRequest.StartGameRequest;
import shared.network.LobbyRequest.SwitchTeamRequest;
import shared.network.LobbyRequest.ToggleReadyRequest;

/**
 * Server for the lobby screen. It receives new connections and handle
 * various lobby requests, like change team, change type, kick, etc.
 * 
 * @author Anh Pham
 *
 */
public class LobbyServer implements Runnable {
	
	private volatile boolean running = true;

	private ServerSocket serverSocket;
	//private List<ServerPlayer> players = new ArrayList<ServerPlayer>(10);
	private HashMap<Integer,ServerPlayer> playerMap = new HashMap<Integer,ServerPlayer>();
	private int count = 0;
	// private String arena = "test40";
	private int mId;
	private List<ChangeSpawnRequest> pendingRequests = new LinkedList<ChangeSpawnRequest>();
	private List<SpawnPoint> spawns;
	private HashMap<Integer,SpawnPoint> spawnMap = new HashMap<Integer,SpawnPoint>();
	private SpawnPoint idle = new SpawnPoint() {
		@Override
		public int getId() {
			return -1;
		}
	};

	/**
	 * Constructor
	 * @param port The port to open this server on.
	 * @param arena The arena of this game.
	 */
	public LobbyServer(int port, int mId) throws IOException {
		serverSocket = new ServerSocket(port);
		this.mId = mId;
		ArenaData ad = (ArenaData) DataManager.loadInternalObject("/map/"+HostScreen.MAP_LIST[mId]+".arena");
		spawns = ad.spawns;
		for (SpawnPoint sp:spawns) {
			spawnMap.put(sp.getId(), sp);
		}
		// START LISTENING FOR CONNECTIONS
		new Thread(this).start();
	}

	/**
	 * Start the game with the current players in this lobby (called by the host).
	 */
	public void startGame() throws IOException {
		for (ServerPlayer p : playerMap.values()) {
			if (!p.active)
				return;
		}
		running = false;
		serverSocket.close();
		
		sendRequest(new StartGameRequest());
		//new MatchServer(players, arena);
		new MissionServer(new ArrayList<ServerPlayer>(playerMap.values()), mId);
	}

	/**
	 * @return The running state of this lobby.
	 */
	public boolean isRunning(){
	    return running;
	}

	/**
	 * Send a request to all connected players in this lobby.
	 * @param request The request to be sent.
	 */
	private void sendRequest(LobbyRequest request) {
		for (ServerPlayer p : playerMap.values()) {
			if (p.connection!=null)
				p.connection.send(request);
		}
	}

	/**
	 * End the lobby server.
	 */
	public void end() {
		running = false;
		for (ServerPlayer p : playerMap.values()) {
			sendRequest(new PlayerLeaveRequest(p.id));
		}

		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Add an AI player to the lobby (called by the host).
	 * @param team The team to create the AI player in.
	 * @param type The type of the AI player.
	 */
	public void addAIPlayer (int team, CharType type) {
	    ServerPlayer p = new AIPlayer(count, team);
		//ServerPlayer p = new DummyPlayer(count, team);
	    p.type = type;
	    p.active = true;
	    count++;
	    sendRequest(new LobbyRequest.NewPlayerRequest(generateClientPlayer(p)));
        playerMap.put(p.id,p);
	}
	
	/**
	 * Add a dummy player to the lobby for testing purpose (called by the host).
	 * @param team The team to create the AI player in.
	 * @param type The type of the AI player.
	 */
	public void addDummyPlayer (int team, CharType type) {
		ServerPlayer p = new DummyPlayer(count, team);
	    p.type = type;
	    p.active = true;
	    count++;
	    sendRequest(new LobbyRequest.NewPlayerRequest(generateClientPlayer(p)));
        playerMap.put(p.id,p);
	}
	
	/**
	 * Remove a player from the lobby (called by the host).
	 * @param id The ID of the player to be kicked.
	 */
	public void removePlayer (int id) {
	    ServerPlayer p = playerMap.get(id);
	    if (p==null)
	        return;
	    sendRequest(new PlayerLeaveRequest(id));
	    playerMap.remove(id);
	    try {
            p.connection.getSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	@Override
	public void run() {
		// Lobby client.gui, waiting for connections
		while (running) {
			try {
				Socket socket = serverSocket.accept();
				Connection connection = new Connection(socket);
				// get their name
				String name = (String) connection.receive();

				// generate an ID for a player
				int id = count;
				int team = -1;
				count++;
				ServerPlayer p = new ServerPlayer(id, team, name, connection);
				p.spawnPoint = idle;
				// tell other clients about the newly connected player
				sendRequest(new LobbyRequest.NewPlayerRequest(generateClientPlayer(p)));
				playerMap.put(p.id, p);
				
				// send the lobby information back to them
				connection.send(generateInformationPacket(p.id));

				// start listening to requests from them
				new LobbyRequestReceiver(this,p).start();

				System.out.println("ClientPlayer at " + socket.getRemoteSocketAddress() + " connected, id = " + p.id);
			} catch (IOException e) {
				//System.out.println("Exception caught when a client tries to connect.");
				System.out.println(e.getMessage());
				//e.printStackTrace();
			}
		}
	}

	/**
	 * Helper method, to assign a new player to the team with less players.
	 * @return The team number of the smaller team.
	 */
	private int getTeamWithLeastPlayer () {
	    int team1Cnt = 0,team2Cnt = 0;
        for (ServerPlayer p:playerMap.values()) {
            if (p.team==0)
                team1Cnt ++;
            else if (p.team==1)
                team2Cnt ++;
        }
        return team1Cnt<=team2Cnt?0:1;
	}

	private void addPending(ChangeSpawnRequest request) {
		
		pendingRequests.add(request);
	}
	
	/**
	 * Used to receive lobby requests in another thread to avoid blocking other operations.
	 */
	private class LobbyRequestReceiver extends Thread {
	    private ServerPlayer player;
	    private LobbyServer server;
	    
	    /**
	     * Constructor
	     * @param player The player that this receiver is listening to.
	     */
	    public LobbyRequestReceiver (LobbyServer server, ServerPlayer player) {
	    	this.server = server;
	        this.player = player;
	    }
	    
    	@Override
        public void run() {
            while (server.isRunning()) {
                Object message = player.connection.receive();
                if (message instanceof SwitchTeamRequest) {
                    SwitchTeamRequest request = ((SwitchTeamRequest)message);
                    player.team = request.desTeam;
                    server.sendRequest(request);
                } else if (message instanceof ToggleReadyRequest) {
                    ToggleReadyRequest request = ((ToggleReadyRequest)message);
                    player.active = true;
                    server.sendRequest(request);
                } else if (message instanceof ChangeCharSetup) {
                    ChangeCharSetup request = ((ChangeCharSetup)message);
                    switch (request.changeType) {
                    	case ChangeCharSetup.CHANGE_CHAR:
                    		player.type = CharType.valueOf(request.changeValue);
                    		break;
                    	case ChangeCharSetup.CHANGE_WMOD:
                    		player.weaponMod = request.changeValue;
                    		break;
                    	case ChangeCharSetup.CHANGE_HMOD:
                    		player.hitMod = request.changeValue;
                    		break;
                		default:
                			break;
                    }
                    
                    server.sendRequest(request);
                } else if (message instanceof ChatRequest) {
                	ChatRequest request = ((ChatRequest)message);
                	server.sendRequest(request);
                } else if (message instanceof ChangeSpawnRequest) {
                	ChangeSpawnRequest request = ((ChangeSpawnRequest)message);
                	// remove previous requests from the same player
                	ChangeSpawnRequest dup = null;
            		for (ChangeSpawnRequest pending:pendingRequests) {
            			if (pending.playerId==request.playerId) {
            				dup = pending;
            				break;
            			}
            		}
            		if (dup!=null) {
            			pendingRequests.remove(dup);
            		}
                	
                	ServerPlayer requestingPlayer = playerMap.get(request.playerId);
                	// if he's leaving the spot and becoming idle
            		// well, let him
            		if (request.spawnId==-1) {
            			requestingPlayer.spawnPoint = idle;
            			requestingPlayer.active = false;
            			request.successful = true;
            			sendRequest(request);
            		} else {
            			boolean occupied = false;
            			for (ServerPlayer player:playerMap.values()) {
            				if (player.spawnPoint.getId()==request.spawnId) {
            					occupied = true;
            					break;
            				}
            			}
            			if (!occupied) {
            				// let him
            				request.successful = true;
            				requestingPlayer.setSpawn(spawnMap.get(request.spawnId));
            				sendRequest(request);
            			}
            			else {
	            			for (ChangeSpawnRequest prev:pendingRequests) {
	            				// if someone requested this spot before
	            				if (prev.spawnId==requestingPlayer.spawnPoint.getId()) {
	            					ServerPlayer pendingPlayer = playerMap.get(prev.playerId);
	            					// and he has the spot I'm wanting
	            					if (request.spawnId==pendingPlayer.spawnPoint.getId()) {
	            						// switch the two
	            						SpawnPoint temp = requestingPlayer.spawnPoint; 
	            						requestingPlayer.setSpawn(pendingPlayer.spawnPoint);
	            						pendingPlayer.setSpawn(temp);
	            						
	            						// tell the world about it
	            						request.successful = true;
	            						prev.successful = true;
	            						sendRequest(request);
	            						sendRequest(prev);
	            					}
	            				}
	            			}
	            			
	            			if (!request.successful) {
	            				pendingRequests.add(request);
	            				sendRequest(request);
	            			}
            			}
            		}
                }
                else {
                	System.err.println("Invalid request received!");
                	System.err.println(message);
                }
            }
        }
	}

	/**
	 * Helper method, used in generating a new information packet everytime a new
	 * player connects to the lobby to tell them about current players.
	 */
	private ClientPlayer generateClientPlayer (ServerPlayer p) {
	    ClientPlayer lobbyPlayer = new ClientPlayer();
        lobbyPlayer.id = p.id;
        lobbyPlayer.name = p.name;
        lobbyPlayer.active = p.active;
        lobbyPlayer.team = p.team;
        lobbyPlayer.type = p.type;
        lobbyPlayer.spawnId = p.spawnPoint!=null?p.spawnPoint.getId():-1;
        return lobbyPlayer;
	}

	private LobbyInformationPacket generateInformationPacket(int destinationId) {
		LobbyInformationPacket lip = new LobbyInformationPacket();
		ArrayList<ClientPlayer> players = new ArrayList<ClientPlayer>(playerMap.size());
		for (ServerPlayer p:playerMap.values()) {
			players.add(generateClientPlayer(p));
		}
		LobbyRequest.GameConfig config = new LobbyRequest.GameConfig();
		config.arena = mId;
		config.playableSpawns = new LinkedList<SpawnPoint>();
		for (SpawnPoint sp:spawns) {
			if (sp.type!=SpawnType.NPCOnly) {
				config.playableSpawns.add(sp);
			}
		}
		
		lip.id = destinationId;
		lip.clientPlayers = players;
		lip.gameConfig = config;
		return lip;
	}

}