package client.gui;

import java.io.Serializable;

import shared.network.CharData;

/**
 * Store player data on client side.
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
	public CharData character;
}
