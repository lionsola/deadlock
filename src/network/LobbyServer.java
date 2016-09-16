package network;

import gui.ClientPlayer;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import network.LobbyRequest.ChangeCharacterRequest;
import network.LobbyRequest.ChatRequest;
import network.LobbyRequest.LobbyInformationPacket;
import network.LobbyRequest.PlayerLeaveRequest;
import network.LobbyRequest.StartGameRequest;
import network.LobbyRequest.SwitchTeamRequest;
import network.LobbyRequest.ToggleReadyRequest;
import core.AIPlayer;

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
	private List<ServerPlayer> players;
	private int count = 0;
	// private String arena = "test40";
	private String arena;

	/**
	 * Constructor
	 * @param port The port to open this server on.
	 * @param arena The arena of this game.
	 */
	public LobbyServer(int port, String arena) throws IOException {
		serverSocket = new ServerSocket(port);
		this.arena = arena;
		players = new ArrayList<ServerPlayer>(10);

		// START LISTENING FOR CONNECTIONS
		new Thread(this).start();
	}

	/**
	 * Start the game with the current players in this lobby (called by the host).
	 */
	public void startGame() throws IOException {
		for (ServerPlayer p : players) {
			if (!p.active)
				return;
		}
		running = false;
		sendRequest(new StartGameRequest());
		new GameServer(players, arena);
		serverSocket.close();
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
		for (ServerPlayer p : players) {
			if (p.socket != null) {
				try {
					new ObjectOutputStream(p.socket.getOutputStream()).writeObject(request);
				} catch (IOException e) {
					System.out.println("Error sending " + request.getClass() + " to player " + p.id);
					System.out.println(e.getMessage());
				}
			}
		}
	}

	/**
	 * End the lobby server.
	 */
	public void end() {
		running = false;
		for (ServerPlayer p : players) {
			sendRequest(new PlayerLeaveRequest(p.id));
		}

		try {
			serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Add an AI player to the lobby (called by the host).
	 * @param team The team to create the AI player in.
	 * @param type The type of the AI player.
	 */
	public void addAIPlayer (int team, int type) {
	    AIPlayer p = new AIPlayer(count, team);
	    p.type = type;
	    p.active = true;
	    count++;
	    sendRequest(new LobbyRequest.NewPlayerRequest(generateClientPlayer(p)));
        players.add(p);
	}
	
	/**
	 * Remove a player from the lobby (called by the host).
	 * @param id The ID of the player to be kicked.
	 */
	public void removePlayer (int id) {
	    ServerPlayer p = null;
	    for (ServerPlayer sp:players) {
	        if (sp.id == id) {
	            p=sp;
	            break;
	        }
	    }
	    if (p==null)
	        return;
	    sendRequest(new PlayerLeaveRequest(id));
	    players.remove(p);
	    try {
            p.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	@Override
	public void run() {
		// Lobby gui, waiting for connections
		while (running) {
			try {
				Socket socket = serverSocket.accept();

				// get their name
				String name = (String) new ObjectInputStream(socket.getInputStream()).readObject();

				// generate an ID for a player
				int id = count;
				int team = getTeamWithLeastPlayer();
				count++;
				ServerPlayer p = new ServerPlayer(id, team, name, socket);
				// tell other clients about the newly connected player
				sendRequest(new LobbyRequest.NewPlayerRequest(generateClientPlayer(p)));
				players.add(p);
				// send the lobby information back to them
				new ObjectOutputStream(socket.getOutputStream()).writeObject(generateInformationPacket(p.id));

				// start listening to requests from them
				new Thread(new LobbyRequestReceiver(p)).start();

				System.out.println("ClientPlayer at " + socket.getRemoteSocketAddress() + " connected, id = " + p.id);
			} catch (IOException | ClassNotFoundException e) {
				System.out.println("Exception caught when a client tries to connect.");
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}
	}

	/**
	 * Helper method, to assign a new player to the team with less players.
	 * @return The team number of the smaller team.
	 */
	private int getTeamWithLeastPlayer () {
	    int team1Cnt = 0,team2Cnt = 0;
        for (ServerPlayer p:players) {
            if (p.team==0)
                team1Cnt ++;
            else if (p.team==1)
                team2Cnt ++;
        }
        return team1Cnt<=team2Cnt?0:1;
	}

	/**
	 * Used to receive lobby requests in another thread to avoid blocking other operations.
	 */
	private class LobbyRequestReceiver implements Runnable {
	    private ServerPlayer player;
	    
	    /**
	     * Constructor
	     * @param player The player that this receiver is listening to.
	     */
	    public LobbyRequestReceiver (ServerPlayer player) {
	        this.player = player;
	    }
    	@Override
        public void run() {
            while (isRunning()) {
                try {
                    ObjectInputStream ois = new ObjectInputStream(player.socket.getInputStream());
                    Object message = ois.readObject();
                    if (message instanceof SwitchTeamRequest) {
                        SwitchTeamRequest request = ((SwitchTeamRequest)message);
                        for (ServerPlayer p:players) {
                            if (p.id == request.playerId) {
                                p.team = request.desTeam;
                                break;
                            }
                        }
                        sendRequest(request);
                    } else if (message instanceof ToggleReadyRequest) {
                        ToggleReadyRequest request = ((ToggleReadyRequest)message);
                        for (ServerPlayer p:players) {
                            if (p.id == request.id) {
                                p.active = request.ready;
                                break;
                            }
                        }
                        sendRequest(request);
                    } else if (message instanceof ChangeCharacterRequest) {
                        ChangeCharacterRequest request = ((ChangeCharacterRequest)message);
                        for (ServerPlayer p:players) {
                            if (p.id == request.playerId) {
                                p.type = request.typeId;
                                break;
                            }
                        }
                        sendRequest(request);
                    } else if (message instanceof ChatRequest) {
                    	ChatRequest request = ((ChatRequest)message);
                    	sendRequest(request);
                    }
                } catch (SocketException | EOFException e) {
                    // DISCONNECT
                    removePlayer(player.id);
                    return;
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Error while receiving request from client");
                    e.printStackTrace();
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
        return lobbyPlayer;
	}

	private LobbyInformationPacket generateInformationPacket(int destinationId) {
		LobbyInformationPacket lip = new LobbyInformationPacket();
		ClientPlayer[] players = new ClientPlayer[this.players.size()];
		for (int i = 0; i < players.length; i++) {
			ServerPlayer p = this.players.get(i);
			players[i] = generateClientPlayer(p);
		}
		LobbyRequest.GameConfig config = new LobbyRequest.GameConfig();
		config.arena = arena;
		lip.id = destinationId;
		lip.clientPlayers = players;
		lip.gameConfig = config;
		return lip;
	}

}