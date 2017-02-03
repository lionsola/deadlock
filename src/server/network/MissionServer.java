package server.network;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import client.gui.GameWindow;
import editor.DataManager;
import editor.SpawnPoint;
import editor.SpawnPoint.SpawnType;
import jbt.execution.core.BTExecutorFactory;
import jbt.execution.core.ContextFactory;
import jbt.execution.core.ExecutionTask.Status;
import jbt.execution.core.IBTExecutor;
import jbt.execution.core.IBTLibrary;
import jbt.execution.core.IContext;
import jbt.model.core.ModelTask;
import server.ai.AIPlayer;
import server.ai.Searcher;
import server.character.ClassStats;
import server.character.InputControlledEntity;
import server.character.NPC;
import server.weapon.Weapon;
import server.weapon.WeaponFactory;
import server.world.Arena;
import server.world.Arena.ArenaData;
import server.world.Thing;
import server.world.Terrain;
import server.world.World;
import server.world.trigger.TileSwitchPreset;
import shared.network.FullCharacterData;
import shared.network.GameDataPackets.InputPacket;
import shared.network.GameDataPackets.WorldStatePacket;
import shared.network.event.AnimationEvent;
import shared.network.event.GameEvent;
import shared.network.event.GameEvent.*;
import shared.network.event.SoundEvent;

/**
 * Is the game server.
 */
public class MissionServer implements Runnable, Listener {
	public static final int UNDECIDED = -1;
	public static final int DRAW = -2;
	enum State {PAUSING, PLAYING, DELAYING}
	private State state = State.DELAYING;
	
	protected World world;
	private List<ServerPlayer> players;
	private List<GameEvent> events = new LinkedList<GameEvent>();
	private List<String> chatTexts = new LinkedList<String>();
	
	private long timeThreshold = 600000;
	private long gameTimeCounter;
	
	IContext context;
	IBTExecutor gamemaster;

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
	public MissionServer(List<ServerPlayer> players, String arenaName) {
		this.players = players;
		WeaponFactory.initWeapons();
		ClassStats.initClassStats();
		for (int i=0;i<players.size();i++) {
			ServerPlayer p = players.get(i);
			p.inputReceiver = new InputReceiver(this,p);
			p.inputReceiver.start();
		}
		initializeMission(arenaName);
		setUp(world,players);
		new Thread(this).start();
	}

	protected void initializeMission(String arenaName) {
		HashMap<Integer,Terrain> tileTable = (HashMap<Integer, Terrain>) DataManager.loadObject(DataManager.FILE_TILES);
		
		HashMap<Integer,Thing> objectTable = (HashMap<Integer, Thing>) DataManager.loadObject(DataManager.FILE_OBJECTS);
		
		HashMap<Integer,TileSwitchPreset> triggerTable = (HashMap<Integer, TileSwitchPreset>) DataManager.loadObject(DataManager.FILE_TRIGGERS);
		
		for (TileSwitchPreset tp:triggerTable.values()) {
        	tp.setSwitchThing(objectTable.get(tp.getSwitchThingID()));
        	tp.setOriginalThing(objectTable.get(tp.getOriginalThingID()));
        }
		ArenaData ad = (ArenaData) DataManager.loadObject("resource/map/"+arenaName+".arena");
		/* First of all, we create the BT library. */
		IBTLibrary btLibrary = new MissionBTLibrary();
		/* Then we create the initial context that the tree will use. */
		context = ContextFactory.createContext(btLibrary);
		context.setVariable("MissionServer", this);
		for (MissionVar var:ad.objectiveData) {
			context.setVariable(var.name, var.getValue());
		}
		/* Now we get the Model BT to run. */
		ModelTask tree = btLibrary.getBT(ad.objectiveType);
		/* Then we create the BT Executor to run the tree. */
		
		if (ad.objectiveType!=null) {
			gamemaster = BTExecutorFactory.createBTExecutor(tree, context);
		}
		
		Arena arena = new Arena(arenaName, tileTable, objectTable, triggerTable);

		this.world = new World(arena, this);
	}
	
	protected void setUp(World world, List<ServerPlayer> players) {
		for (int i=0;i<players.size();i++) {
			ServerPlayer p = players.get(i);
			if (p.character==null) {
				InputControlledEntity character = InputControlledEntity.newCharacter(p.spawnPoint.getId(), p.spawnPoint.team, p.type);
				p.setCharacter(character);
			}  else {
				//p.character.resetStats();
				// At the moment, just create everything new
				InputControlledEntity character = InputControlledEntity.newCharacter(p.spawnPoint.getId(), p.team, p.type);
				p.setCharacter(character);
			}
			if (p instanceof AIPlayer) {
				((AIPlayer) p).init(world.getArena(), p.character);
			}
			
			world.addCharacter(p.character,p.spawnPoint);
			p.targetIndex = i;
		}
		
		for (SpawnPoint p:world.getArena().getSpawns()) {
			if (p.type==SpawnType.NPCOnly) {
				NPC npc = new NPC(p.getId(),p.team, p.setups.get(0).id);
				npc.init(world.getArena(), p.behaviour);
				world.addCharacter(npc,p);
				npc.setWeapon(WeaponFactory.createGun(Weapon.SILENT_PISTOL_ID, npc));
				npc.brain.setPatrolLocations(p.patrolLocations);
			}
		}
	}
	
	protected boolean isWinningPossible() {
		int remainingPlayers = 0;
		
		for (InputControlledEntity e:world.getCharacters()) {
			if (!e.isDead() && e.team==0) {
				remainingPlayers ++;
			}
		}
		return remainingPlayers>0;
	}
	
	@Override
	public void run() {
		// PRE- COUNTDOWN to get everyone prepared
		// TODO check if everyone is connected to the game already
		final int PRE_COUNTDOWN = 3000;
		long waitTimeCounter = PRE_COUNTDOWN;
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
		int winner = UNDECIDED;
		while (gameTimeCounter < timeThreshold) {
			long wait = 0;
			long last = System.currentTimeMillis();
			if (state!=State.PAUSING) {
				// simulate the world
				world.update();
				
				// send world state out
				sendState();
				gamemaster.tick();
				if (state==State.PLAYING) {
					gameTimeCounter += GameWindow.MS_PER_UPDATE;
					
					if (!isWinningPossible() || gamemaster.getStatus()==Status.FAILURE){
						winner = 1;
					} else if (gamemaster.getStatus()==Status.SUCCESS) {
						winner = 0;
					}
					if (winner!=UNDECIDED) {
						addEvent(new GameEvent.RoundEnd(winner));
						waitTimeCounter = 5000;
						// delay before going to next round
						state = State.DELAYING;
					}
				} else if (state==State.DELAYING) {
					waitTimeCounter -= GameWindow.MS_PER_UPDATE;
					if (waitTimeCounter <= 0) {
						break;
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
		
		events.add(new GameEndEvent(winner));
		sendState();
		events.add(new GameEndEvent(winner));
		sendState();
		events.add(new GameEndEvent(winner));
		sendState();
		try {
			Thread.sleep(2000);
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
			per.player = new FullCharacterData(target.character);
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

	public List<ServerPlayer> getPlayers() {
		return players;
	}
	
	/**
	 * Receive & process input data from players in another thread to avoid blockings the server.
	 */
	public static class InputReceiver extends Thread {
		private ServerPlayer player;
		private MissionServer server;
		private long lastPing;
		
		public boolean active = true;

		public boolean canPing() {
			return System.currentTimeMillis()-lastPing>1000;
		}
		
		public InputReceiver(MissionServer server, ServerPlayer player) {
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
					if (input.ping && canPing()) {
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
			
			if (victim!=null) {
				victim.deaths++;
			}
			if (killer!=null) {
				if (victim!=null && killer.team == victim.team) {
					killer.kills--;
				} else {
					killer.kills++;
				}
			}
		} else if (event instanceof Headshot) {
			Headshot e = (Headshot) event;
			ServerPlayer attacker = findPlayer(e.attacker);
			attacker.headshots++;
		}
		addEvent(event);
	}
}
