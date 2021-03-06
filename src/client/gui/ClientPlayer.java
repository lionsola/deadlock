package client.gui;

import java.io.Serializable;

import editor.SpawnPoint.CharType;
import shared.network.CharData;

/**
 * Store player data on client side.
 */
public class ClientPlayer implements Serializable {

	private static final long serialVersionUID = 3345850157208060523L;
	public int id;
	public int team;
	public int spawnId;
	public String name;
	public boolean active;
	public CharType type;
	public int weaponId;
	public int abilityId;
	public int passiveId;
	public int kills;
	public int deaths;
	public int headshots;
	public CharData character;
}
