package gui;

import java.io.Serializable;

import network.PartialCharacterData;

/**
 * Models a client player.
 * 
 * @author Anh Pham
 */
public class ClientPlayer implements Serializable {

	private static final long serialVersionUID = 3345850157208060523L;
	public int id;
	public int team;
	public String name;
	public boolean active;
	public int type;
	public int kills;
	public int deaths;
	public PartialCharacterData character;

}
