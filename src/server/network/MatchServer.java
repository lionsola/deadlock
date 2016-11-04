package server.network;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import client.gui.GameWindow;
import editor.DataManager;
import server.ai.AIPlayer;
import server.ai.PathFinder;
import server.character.ClassStats;
import server.character.PlayerCharacter;
import server.weapon.WeaponFactory;
import server.world.Arena;
import server.world.Thing;
import server.world.Terrain;
import server.world.World;
import shared.network.GameEvent;
import shared.network.GameDataPackets.InputPacket;
import shared.network.GameDataPackets.WorldStatePacket;
import shared.network.GameEvent.GameEndEvent;
import shared.network.GameEvent.GameEventListener;

/**
 * Is the game server.
 */
public abstract class MatchServer implements Runnable, GameEventListener {
	protected World world;
	protected boolean playing = false;
	private List<ServerPlayer> players;
	private List<GameEvent> events = new LinkedList<GameEvent>();
	private List<String> chatTexts = new LinkedList<String>();
	
	private long timeThreshold = 600000;
	private long gameTimeCounter;

	/**
	 * Creates a new game server, which the host use to run the game
	 * 
	 * @param players
	 *            The list of players in the game
	 * @param arenaName
	 *            The name of the arena to be used
	 * @throws FileNotFoundException
	 *             File not found exception
	 * @throws IOException
	 *             IO exception
	 */
	public MatchServer(List<ServerPlayer> players, String arenaName) {
		this.players = players;
		WeaponFactory.initWeapons();
		ClassStats.initClassStats();
		for (ServerPlayer p:players) {
			p.inputReceiver = new InputReceiver(this,p);
			p.inputReceiver.start();
		}
		initializeWorld(arenaName);
		setUp(world,players);
		new Thread(this).start();
	}

	protected void initializeWorld(String arenaName) {
		HashMap<Integer,Terrain> tileTable = DataManager.getTileMap(DataManager.loadTileListOld());
		HashMap<Integer,Thing> objectTable = DataManager.getObjectMap(DataManager.loadObjectListOld());
		Arena arena = new Arena(arenaName, tileTable, objectTable);

		this.world = new World(arena, this);
	}
	
	protected void setUp(World world, List<ServerPlayer> players) {
		PathFinder pathFinder = new PathFinder(world.getArena());
		for (ServerPlayer p : players) {
			if (p.character==null) {
				PlayerCharacter character = PlayerCharacter.newCharacter(p.id, p.team, p.type);
				p.setCharacter(character);
			}  else {
				//p.character.resetStats();
				// At the moment, just create everything new
				PlayerCharacter character = PlayerCharacter.newCharacter(p.id, p.team, p.type);
				p.setCharacter(character);
			}
			if (p instanceof AIPlayer) {
				((AIPlayer) p).init(world.getArena(), pathFinder);
			}
			
			world.addPlayer(p.character);
		}
	}
	
	protected abstract void update(World world);
	
	protected abstract boolean checkEndGame();
	
	protected abstract int getWinningTeam();
	
	@Override
	public void run() {
		// PRE- COUNTDOWN to get everyone prepared
		// TODO check if everyone is connected to the game already
		final int PRE_COUNTDOWN = 3000;
		gameTimeCounter = -PRE_COUNTDOWN;
		while (!playing) {
			sendState();
			gameTimeCounter += 30;
			if (gameTimeCounter >= 0) {
				playing = true;
			} else {
				try {
					Thread.sleep(30);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		// START THE MATCH		
		gameTimeCounter = 0;
		while (gameTimeCounter < timeThreshold && !checkEndGame()) {
			long wait = 0;
			if (!playing) {
				wait = GameWindow.MS_PER_UPDATE;
			} else {
				long last = System.currentTimeMillis();
				gameTimeCounter += GameWindow.MS_PER_UPDATE;
				// simulate the world
				world.update();
				update(world);
		
				// send world state out
				sendState();
		
				wait = GameWindow.MS_PER_UPDATE - (System.currentTimeMillis() - last);
			}
			if (wait>0) {
				try {
					Thread.sleep(wait);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		for (ServerPlayer p:players) {
			p.inputReceiver.active = false;
			try {
				p.inputReceiver.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		events.add(new GameEndEvent());
		sendState();
		events.add(new GameEndEvent());
		sendState();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for (ServerPlayer p:players) {
			try {
				if (p.connection.getSocket()!=null)
					p.connection.getSocket().close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Sends the state of the server
	 */
	private void sendState() {
		// Send world data to players
		for (ServerPlayer p : players) {
			WorldStatePacket per = p.character.getPerception();
			per.chatTexts  = chatTexts;
			if (events.size()>0) {
				per.events.addAll(events);
			}
			per.time = gameTimeCounter;
			per.player = p.character.generate();
			p.sendData(per);
			per.events.clear();
		}
		events.clear();
		chatTexts.clear();
	}

	protected ServerPlayer findPlayer(int id) {
		for (ServerPlayer p : players) {
			if (p.id == id) {
				return p;
			}
		}
		return null;
	}

	protected void setDuration(long duration) {
		this.timeThreshold = duration;
	}
	
	synchronized protected void addEvent(GameEvent event) {
		events.add(event);
	}

	protected List<ServerPlayer> getPlayers() {
		return players;
	}
	
	/**
	 * Receive & process input data from players in another thread to avoid blockings the server.
	 */
	static class InputReceiver extends Thread {
		private ServerPlayer player;
		private MatchServer server;
		public boolean active = true;

		public InputReceiver(MatchServer server, ServerPlayer player) {
			this.server = server;
			this.player = player;
		}
		
		@Override
		public void run() {
			while (active) {
				InputPacket input = player.getInput();
				if (player.character!=null) {
					player.character.setInput(input);
				}
				if (input.chatText != null)
					synchronized (server.chatTexts) {
						server.chatTexts.add(player.name + ": " + input.chatText);
					}
			}
		}
	}
}
