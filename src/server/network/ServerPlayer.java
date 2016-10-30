package server.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import server.character.PlayerCharacter;
import shared.network.GameDataPackets.InputPacket;
import shared.network.GameDataPackets.WorldStatePacket;

/**
 * ServerPlayer acts as an intermediate layer for the Server to communicate
 * with the Client. In other words, it represents a player - the server reads
 * input from it, and sends output through it.
 * 
 * @author Anh Pham
 */
public class ServerPlayer {
    public final int id;
    public int team;
    public String name;
    public boolean active = false;
    public int type = 0;
    public int kills = 0;
    public int deaths = 0;
    public Socket socket;
    public PlayerCharacter character;
	public MatchServer.InputReceiver inputReceiver;
	public ObjectOutputStream oos;
	public ObjectInputStream ois;
    
    /**
     * Constructor.
     * @param id The player ID of this player.
     * @param team The team of this player.
     * @param name This player's name.
     * @param socket The socket to communicate with the client.
     */
    public ServerPlayer (int id, int team, String name, Socket socket) {
        this.id = id;
        this.team = team;
        this.name = name;
        this.socket = socket;
    }
    
    /**
     * Get input from the player. Note that this method should block/wait to read input,
     * else it will burn CPU cycles.
     * @return The input packet of this player.
     */
    public InputPacket getInput () {
        while (true) {
            try {
            	if (socket.getInputStream().available()>0) {
	                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
	                Object message = ois.readObject();
	                if (message instanceof InputPacket) {
	                    InputPacket input = ((InputPacket)message);
	                    return input;
	                }
            	}
            } catch (ClassNotFoundException | IOException e) {
                System.out.println("Error while receiving input from player "+id);
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Send world state to a player.
     * @param wsp The state to be sent, already filtered for this player.
     */
    public void sendData(WorldStatePacket wsp) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(wsp);
        } catch (IOException e) {
            System.out.println("Exception when trying to send data to player " + socket.getRemoteSocketAddress());
            e.printStackTrace();
        }
    }
    
    public void setCharacter(PlayerCharacter c) {
    	this.character = c;
    }
}