package server.network;

import server.world.World;
import shared.network.event.GameEvent.Listener;

public abstract class GameMode implements Listener {
	public static final int UNDECIDED = -1;
	public static final int DRAW = -2;
	
	abstract public Team[] getTeams();
	abstract public void setUp(World w);
	abstract public void update();
	abstract public int getWinner();
}

class Team {
	int teamId;
	String teamName;
	
	Group[] groups;
}

class Group {
	String name;
	ServerPlayer[] members;
	
	public Group(int number, String name) {
		this.name = name;
		this.members = new ServerPlayer[number];
	}
	
	public String getName() {return name;} 
	
	public ServerPlayer[] getMembers() {
		return members;
	}
}