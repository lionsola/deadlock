package network;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import client.gui.GameWindow;
import network.GameDataPackets.InputPacket;
import network.GameDataPackets.WorldStatePacket;
import network.GameEvent.GameEndEvent;
import network.GameEvent.GameEventListener;
import network.GameEvent.PlayerDieEvent;
import network.GameEvent.ScoreChangedEvent;
import server.ai.AIPlayer;
import server.ai.PathFinder;
import server.character.CharacterFactory;
import server.character.ClassStats;
import server.character.ControlledCharacter;
import server.weapon.WeaponFactory;
import server.world.Arena;
import server.world.World;

/**
 * Is the game server.
 * 
 * @author Anh Pham
 */
public class MatchServer implements Runnable, GameEventListener {
	
	private World world;
	private boolean tick = true;
	private boolean playing = false;
	private boolean scoreChanged = false;
	private List<ServerPlayer> players;
	private List<GameEvent> events;
	private List<String> chatTexts = new LinkedList<String>();
	
	private int scoreThreshold = 30;
	private int timeThreshold = 600000;
	private int team1Score = 0;
	private int team2Score = 0;
	private int gameTimeCounter;

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
	public MatchServer(List<ServerPlayer> players, String arenaName) throws FileNotFoundException, IOException {
		this.players = players;
		
		
		events = new LinkedList<GameEvent>();
		
		setUp(players,arenaName);
		new Thread(this).start();
	}

	private void setUp(List<ServerPlayer> players, String arenaName) throws FileNotFoundException, IOException{
		WeaponFactory.initWeapons();
		ClassStats.initClassStats();
		
		Arena arena = new Arena(arenaName, false);
		
		world = new World(arena, this);
		
		PathFinder pathFinder = new PathFinder(arena);
		for (ServerPlayer p : players) {
			if (p instanceof AIPlayer) {
				((AIPlayer) p).init(arena, pathFinder);
			}
			ControlledCharacter character = CharacterFactory.newCharacter(p.id, p.team, p.type);
			world.addPlayer(character);
			p.setCharacter(character);
			new Thread(new InputReceiver(p, character)).start();
		}
	}
	
	private boolean checkEndGame() {
		return gameTimeCounter > timeThreshold || team1Score >= scoreThreshold || team2Score >= scoreThreshold;
	}
	
	@Override
	public void run() {
		int delayCount = 0;

		while (!playing) {
			sendState();
			delayCount += 30;
			if (delayCount >= 3000)
				playing = true;
			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		gameTimeCounter = 0;
		while (playing) {
			long last = System.currentTimeMillis();
			if (checkEndGame()) {
				events.add(new GameEndEvent());
				// world.update();
				sendState();
				events.add(new GameEndEvent());
				sendState();
				playing = false;
				break;
			}
			gameTimeCounter += GameWindow.MS_PER_UPDATE;
			// simulate the world
			world.update();

			// send world state out every 2 frames
			if (tick) {
				sendState();
			}
			tick = !tick;

			long wait = GameWindow.MS_PER_UPDATE - (System.currentTimeMillis() - last);

			if (wait > 0) {
				try {
					Thread.sleep(wait);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Sends the state of the server
	 */
	private void sendState() {
		WorldStatePacket wsp = world.generateState();
		wsp.time = System.currentTimeMillis();
		if (scoreChanged) {
			// generate teams' new score
			team1Score = 0;
			team2Score = 0;
			for (ServerPlayer p : players) {
				if (p.team == 0) {
					team2Score += p.deaths;
				} else {
					team1Score += p.deaths;
				}
			}
			events.add(new ScoreChangedEvent(team1Score, team2Score));
			scoreChanged = false;
		}
		wsp.events = events;
		wsp.chatTexts = chatTexts;
		// Send world data to players
		for (ServerPlayer p : players) {
			WorldStatePacket per = p.character.getPerception();
			per.chatTexts  = chatTexts;
			per.events.addAll(wsp.events);
			per.time = wsp.time;
			per.player = p.character.generate();
			p.sendData(per);
			per.events.clear();
		}
		events = new LinkedList<GameEvent>();
		chatTexts = new LinkedList<String>();
	}

	private ServerPlayer findPlayer(int id) {
		for (ServerPlayer p : players) {
			if (p.id == id) {
				return p;
			}
		}
		return null;
	}

	@Override
	public void onEventReceived(GameEvent event) {
		if (event instanceof PlayerDieEvent) {
			PlayerDieEvent e = (PlayerDieEvent) event;
			ServerPlayer killer = findPlayer(e.killerID);
			ServerPlayer killed = findPlayer(e.killedID);

			killed.deaths++;
			if (killer.team != killed.team) {
				killer.kills++;
			} else {
				killer.kills--;
			}
			scoreChanged = true;
			events.add(event);
		} else {
			events.add(event);
		}
	}

	/**
	 * Receive & process input data from players in another thread to avoid blockings the server.
	 */
	private class InputReceiver implements Runnable {
		private ServerPlayer player;
		private ControlledCharacter character;

		public InputReceiver(ServerPlayer player, ControlledCharacter character) {
			this.player = player;
			this.character = character;
		}

		@Override
		public void run() {
			while (!playing) {
				player.getInput();
			}
			while (playing) {
				InputPacket input = player.getInput();
				character.setInput(input);
				if (input.chatText != null)
					synchronized (chatTexts) {
						chatTexts.add(player.name + ": " + input.chatText);
					}
			}

			try {
				player.socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
