package server.network;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;

import client.gui.GameWindow;
import editor.DataManager;
import server.ai.AIPlayer;
import server.ai.PathFinder;
import server.character.ClassStats;
import server.character.PlayerCharacter;
import server.weapon.WeaponFactory;
import server.world.Arena;
import server.world.Misc;
import server.world.Thing;
import server.world.Terrain;
import server.world.World;
import server.world.trigger.TileSwitchPreset;
import shared.network.GameDataPackets.InputPacket;
import shared.network.GameDataPackets.WorldStatePacket;
import shared.network.event.AnimationEvent;
import shared.network.event.GameEvent;
import shared.network.event.GameEvent.*;
import shared.network.event.SoundEvent;

/**
 * Is the game server.
 */
public class MatchServer implements Runnable, Listener {
	private static final int UNDECIDED = -1;
	private static final int DRAW = -2;
	enum State {PAUSING, PLAYING, DELAYING}
	private State state = State.DELAYING;
	
	protected World world;
	private List<ServerPlayer> players;
	private List<GameEvent> events = new LinkedList<GameEvent>();
	private List<String> chatTexts = new LinkedList<String>();
	
	private int[] score = new int[2];
	private int maxScore = 3;
	
	private long timeThreshold = 600000;
	private long gameTimeCounter;
	private Timer timer;
	private int waitTimeCounter;

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
		for (int i=0;i<players.size();i++) {
			ServerPlayer p = players.get(i);
			p.inputReceiver = new InputReceiver(this,p);
			p.inputReceiver.start();
		}
		initializeWorld(arenaName);
		setUp(world,players);
		new Thread(this).start();
	}

	protected void initializeWorld(String arenaName) {
		HashMap<Integer,Terrain> tileTable = (HashMap<Integer, Terrain>) DataManager.loadObject(DataManager.FILE_TILES);
		
		HashMap<Integer,Thing> objectTable = (HashMap<Integer, Thing>) DataManager.loadObject(DataManager.FILE_OBJECTS);
		
		HashMap<Integer,Misc> miscTable = (HashMap<Integer,Misc>) DataManager.loadObject(DataManager.FILE_MISC);
		
		HashMap<Integer,TileSwitchPreset> triggerTable = (HashMap<Integer, TileSwitchPreset>) DataManager.loadObject(DataManager.FILE_TRIGGERS);
		
		for (TileSwitchPreset tp:triggerTable.values()) {
			if (tp.getItemType()==TileSwitchPreset.THING) {
	        	tp.setSwitchThing(objectTable.get(tp.getSwitchThingID()));
	        	tp.setOriginalThing(objectTable.get(tp.getOriginalThingID()));
			} else if (tp.getItemType()==TileSwitchPreset.MISC) {
				tp.setSwitchThing(miscTable.get(tp.getSwitchThingID()));
	        	tp.setOriginalThing(miscTable.get(tp.getOriginalThingID()));
			}
        }
		
		
		
		Arena arena = new Arena(arenaName, tileTable, objectTable, triggerTable, miscTable);

		this.world = new World(arena, this);
	}
	
	protected void setUp(World world, List<ServerPlayer> players) {
		PathFinder pathFinder = new PathFinder(world.getArena());
		for (int i=0;i<players.size();i++) {
			ServerPlayer p = players.get(i);
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
			p.targetIndex = i;
		}
	}
	
	protected int getRoundWinner() {
		int winner = UNDECIDED;
		int[] remainingPlayers = new int[2];
		for (ServerPlayer p:getPlayers()) {
			if (!p.character.isDead()) {
				remainingPlayers[p.team] ++;
			}
		}
		if (remainingPlayers[0]==0 || remainingPlayers[1]==0) {
			if (remainingPlayers[0]==0 && remainingPlayers[1]>0) {
				winner = 1;
			} else if (remainingPlayers[0]>0 && remainingPlayers[1]==0) {
				winner = 0;
			} else {
				winner = DRAW;
			}
		}
		return winner;
	}
	
	protected void update(World world) {
		
	}
	
	protected boolean checkEndGame() {
		//return score[0]>=maxScore || score[1]>=maxScore;
		return score[0]>=maxScore || score[1]>=maxScore;
	}
	
	
	protected int getWinningTeam() {
		if (score[0]>score[1]) {
			return 0;
		} else if (score[1]>score[0]) {
			return 1;
		} else {
			return -1;
		}
	}
	
	@Override
	public void run() {
		// PRE- COUNTDOWN to get everyone prepared
		// TODO check if everyone is connected to the game already
		final int PRE_COUNTDOWN = 3000;
		waitTimeCounter = PRE_COUNTDOWN;
		while (state==State.DELAYING) {
			sendState();
			waitTimeCounter -= GameWindow.MS_PER_UPDATE;
			if (waitTimeCounter <= 0) {
				state = State.PLAYING;
			} else {
				try {
					Thread.sleep(GameWindow.MS_PER_UPDATE);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		// START THE MATCH		
		gameTimeCounter = 0;
		while (gameTimeCounter < timeThreshold) {
			long wait = 0;
			long last = System.currentTimeMillis();
			if (state!=State.PAUSING) {
				// simulate the world
				world.update();
				
				// send world state out
				sendState();
				
				if (state==State.PLAYING) {
					gameTimeCounter += GameWindow.MS_PER_UPDATE;
					int winner = getRoundWinner();
					if (winner!=UNDECIDED){

						if (winner!=DRAW) {
							score[winner] += 1;
							addEvent(new ScoreChangedEvent(score[0],score[1]));
							addEvent(new GameEvent.RoundEnd(winner));
						}
						
						waitTimeCounter = 5000;
						// delay before going to next round
						state = State.DELAYING;
					}
				} else if (state==State.DELAYING) {
					waitTimeCounter -= GameWindow.MS_PER_UPDATE;
					if (waitTimeCounter <= 0) {
						if (!checkEndGame()) {
							initializeWorld(world.getArena().getName());
							setUp(this.world,getPlayers());
							addEvent(new GameEvent.RoundStart());
							state = State.PLAYING;
						} else {
							break;
						}
					}
				}
			}
			
			
			
			wait = GameWindow.MS_PER_UPDATE - (System.currentTimeMillis() - last);
			
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
		
		events.add(new GameEndEvent(getWinningTeam()));
		sendState();
		events.add(new GameEndEvent(getWinningTeam()));
		sendState();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for (ServerPlayer p:players) {
			try {
				if (p.connection!=null && p.connection.getSocket()!=null)
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
			ServerPlayer target = players.get(p.targetIndex);
			WorldStatePacket per = target.character.getPerception();
			per.chatTexts  = chatTexts;
			if (events.size()>0) {
				per.events.addAll(events);
			}
			per.time = gameTimeCounter;
			per.player = target.character.generate();
			p.sendData(per);
			
			p.character.getPerception().events = new LinkedList<GameEvent>();
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
	public static class InputReceiver extends Thread {
		private ServerPlayer player;
		private MatchServer server;
		private long lastPing;
		
		public boolean active = true;

		public boolean canPing() {
			return System.currentTimeMillis()-lastPing>1000;
		}
		
		public InputReceiver(MatchServer server, ServerPlayer player) {
			this.server = server;
			this.player = player;
		}
		
		@Override
		public void run() {
			while (active) {
				try {
					InputPacket input = player.getInput();
					if (input.chatText != null) {
						synchronized (server.chatTexts) {
							server.chatTexts.add(player.name + ": " + input.chatText);
						}
					}
					if (input.alt && input.fire2 && canPing()) {
						for (ServerPlayer p:server.players) {
							if (p.team==player.team) {
								p.character.getPerception().events.add(new SoundEvent(SoundEvent.PING_SOUND_ID,SoundEvent.PING_SOUND_VOLUME,input.cx,input.cy));
								p.character.getPerception().events.add(new AnimationEvent(AnimationEvent.PING_ANIMATION_ID,player.id,input.cx,input.cy,0,true));
							}
						}
						lastPing = System.currentTimeMillis();
					}
					
					if (player.character.isDead() && input.fire1 && !player.character.getInput().fire1) {
						for (int i=player.targetIndex+1;i!=player.targetIndex;i++) {
							if (i>=server.players.size()) {
								i = 0;
							}
							if (server.players.get(i).team==player.team) {
								player.targetIndex = i;
								break;
							}
						}
					}
					
					if (player.character!=null) {
						player.character.setInput(input);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	
	public void onEventReceived(GameEvent event) {
		if (event instanceof PlayerDieEvent) {
			PlayerDieEvent e = (PlayerDieEvent) event;
			ServerPlayer killer = findPlayer(e.killerId);
			ServerPlayer victim = findPlayer(e.killedId);

			victim.deaths++;
			if (killer.team != victim.team) {
				killer.kills++;
			} else {
				killer.kills--;
			}
		} else if (event instanceof Headshot) {
			Headshot e = (Headshot) event;
			ServerPlayer attacker = findPlayer(e.attacker);
			attacker.headshots++;
		}
		addEvent(event);
	}
}
