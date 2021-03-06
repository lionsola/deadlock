package server.network;

import java.io.IOException;
import client.gui.GameWindow;
import editor.SpawnPoint;
import editor.SpawnPoint.CharType;
import server.character.InputControlledEntity;
import shared.network.Connection;
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
    
    public int weaponMod = -1;
    public int hitMod = -1;
    public int armorMod = -1;
    
    public String name;
    public boolean active = false;
    public CharType type;
    public int kills = 0;
    public int deaths = 0;
    public int headshots = 0;
    
    public int targetIndex;
    
    public SpawnPoint spawnPoint;
    public Connection connection;
    public InputControlledEntity character;
	public MissionServer.InputReceiver inputReceiver;
    
    /**
     * Constructor.
     * @param id The player ID of this player.
     * @param team The team of this player.
     * @param name This player's name.
     * @param socket The socket to communicate with the client.
     */
    public ServerPlayer (int id, int team, String name, Connection connection) {
        this.id = id;
        this.targetIndex = id;
        this.team = team;
        this.name = name;
        this.connection = connection;
    }
    
    /**
     * Get input from the player. Note that this method should block/wait to read input,
     * else it will burn CPU cycles.
     * @return The input packet of this player.
     */
    public InputPacket getInput() {
    	while (true) {
			try {
				Object message = null;
				while (connection.getSocket().getInputStream().available()>200) {
					message = connection.receive();
				}
				if (message!=null && message instanceof InputPacket) {
				    InputPacket input = ((InputPacket)message);
				    return input;
				} else {
					Thread.sleep(GameWindow.MS_PER_UPDATE);
				}
			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
			}
    	}
    }
    
    /**
     * Send world state to a player.
     * @param wsp The state to be sent, already filtered for this player.
     */
    public void sendData(WorldStatePacket wsp) {
        connection.send(wsp);
    }
    
    public void setCharacter(InputControlledEntity c) {
    	this.character = c;
    }
    
    public void setSpawn(SpawnPoint sp) {
    	this.spawnPoint = sp;
    	this.type = sp.setups.get(0);
    	this.team = sp.team;
    }
}