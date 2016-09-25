package core;


import java.awt.Point;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import network.GameDataPackets.WorldStatePacket;
import network.GameEvent;
import network.GameEvent.BulletHitPlayerEvent;
import network.GameEvent.BulletHitWallEvent;
import network.GameEvent.FootStepEvent;
import network.GameEvent.GameEventListener;
import network.GameEvent.GunShotEvent;
import network.GameEvent.PowerUpPickedUpEvent;
import network.PartialCharacterData;
import network.ProjectileData;
import sound.AudioManager;
import character.Character;
import character.ControlledCharacter;

/**
 * The physical world inside a match, which handles everything including characters, projectiles and
 * collision.
 * 
 * @author Anh Pham
 */
public class World {
	
	private Arena arena;
	private List<ControlledCharacter> characters = new ArrayList<ControlledCharacter>();;
	
	private List<Projectile> projectiles = new LinkedList<Projectile>();
	private List<Projectile> newProjectiles = new LinkedList<Projectile>();
	private GameEventListener listener;

	/**
	 * Creates the World which is used in GameScreen to play the actual game
	 * 
	 * @param arena
	 *            The arena used in the game
	 * @param listener
	 */
	public World(Arena arena, GameEventListener listener) {
		this.arena = arena;
		this.listener = listener;
		
		// Hostage h = new Hostage(0);
		// Point spawn = randomizeSpawnPoint(1);
		// h.setX(spawn.x * Tile.tileSize);
		// h.setY(spawn.y * Tile.tileSize);
		// hostages.add(h);
	}

	/**
	 * Adds a player to the World
	 * 
	 * @param p
	 *            the player to be added
	 */
	public void addPlayer(ControlledCharacter p) {
		Point spawn = randomizeSpawnPoint(p.team);
		p.setX((spawn.x+0.5) * Tile.tileSize);
		p.setY((spawn.y+0.5) * Tile.tileSize);
		characters.add(p);
	}



	/**
	 * Randomize the spawn points of a given team
	 * 
	 * @param team
	 *            the team id of the team that wishes its spawn points randomized
	 * @return
	 */
	private Point randomizeSpawnPoint(int team) {
		int spawnIndex = core.Utils.random().nextInt(arena.getSpawn(team).size());
		return arena.getSpawn(team).get(spawnIndex);
	}

	/**
	 * Add a projectile to the world
	 * 
	 * @param p
	 *            the projectile to be added
	 */
	public void addProjectile(Projectile p) {
		newProjectiles.add(p);
	}

	/**
	 * Update the world, and all the characters and projectiles in the world. This happens every
	 * frame.
	 */
	public void update() {
		// update characters
		for (ControlledCharacter p : characters) {
			p.update(this);
		}

		// update projectiles
		List<Projectile> outOfRange = new LinkedList<Projectile>();
		for (Projectile p : projectiles) {
			if (p.isConsumed()) {
				outOfRange.add(p);
			}
			else {
				p.update(this);
			}
		}
		projectiles.removeAll(outOfRange);
		projectiles.addAll(newProjectiles);
		newProjectiles.clear();
	}

	public void onPlayerDeath(ControlledCharacter c) {
		Point spawn = randomizeSpawnPoint(c.team);
		c.setX(spawn.x * Tile.tileSize);
		c.setY(spawn.y * Tile.tileSize);
		c.resetStats();
	}
	
	/**
	 * Returns the arena used by the world
	 * 
	 * @return the arena used by the world
	 */
	public Arena getArena() {
		return arena;
	}

	/**
	 * Generate one snapshot of the world state to send to the clients.
	 * 
	 * @return The snapshot containing data about characters and projectiles.
	 */
	public WorldStatePacket generateState() {
		WorldStatePacket wsp = new WorldStatePacket();

		wsp.characters = new LinkedList<PartialCharacterData>();
		for (ControlledCharacter character : characters) {
			wsp.characters.add(character.generatePartial());
		}

		/*
		wsp.hostages = new LinkedList<PartialCharacterData>();
		for (Hostage hostage : hostages) {
			PartialCharacterData data = new PartialCharacterData();
			data.id = -1;
			data.x = (float) hostage.getX();
			data.y = (float) hostage.getY();
			data.healthPoints = (float) hostage.getHealthPoints();
			data.radius = (byte) hostage.getRadius();
			wsp.hostages.add(data);
		}
*/
		
		wsp.projectiles = new LinkedList<ProjectileData>();
		for (Projectile projectile : projectiles) {
			wsp.projectiles.add(projectile.getData());
		}

		return wsp;
	}

	/**
	 * Filter out the data about entities / events that are outside one character's vision
	 * 
	 * @param globalState
	 *            The global state of the world to be filtered.
	 * @param ch
	 *            The character that will get the resulting state.
	 * @return The filtered world state.
	 */
	public WorldStatePacket filter(WorldStatePacket globalState, int id) {
		ControlledCharacter ch = null;
		for (ControlledCharacter cc : characters) {
			if (cc.id == id) {
				ch = cc;
			}
		}
		if (ch == null) {
			System.out.println("Invalid character Id passed into filter");
			System.exit(-1);
		}
		// create a new copy
		WorldStatePacket localState = new WorldStatePacket();

		Area los = ch.getLoS(arena);

		// copy the characters over if they are in line of sight
		localState.characters = new LinkedList<PartialCharacterData>();
		for (PartialCharacterData data : globalState.characters) {
			// if (Point.distance(ch.getX(),ch.getY(),c.x,c.y)<ch.getViewRange()+10)
			if (data.id!=id && los.intersects(data.x - data.radius, data.y - data.radius, data.radius * 2, data.radius * 2)) {
				localState.characters.add(data);
			}
		}

		/*
		// copy the hostages over if they are in line of sight
		localState.hostages = new LinkedList<PartialCharacterData>();
		for (PartialCharacterData data : globalState.hostages) {
			double distance = Point2D.distance(ch.getX(), ch.getY(), data.x, data.y);
			if (distance < ch.getFovRange() + 10) {
				localState.hostages.add(data);
			}
		}
*/
		// copy the projectiles over if they are in line of sight
		localState.projectiles = new LinkedList<ProjectileData>();
		for (ProjectileData data : globalState.projectiles) {
			if (los.contains(data.x, data.y)) {
				localState.projectiles.add(data);
			}
		}


		// copy the events over if they should be known by the player
		// i.e in view / listen range.
		localState.events = new LinkedList<GameEvent>();
		for (GameEvent event : globalState.events) {
			if (event instanceof BulletHitWallEvent) {
				BulletHitWallEvent e = (BulletHitWallEvent) event;
				double distance = Point2D.distance(e.x, e.y, ch.getX(), ch.getY());
				float volume = core.Utils.getVolumeAtDistance(AudioManager.BULLETWALLNOISE, distance);
				if (distance < ch.getFovRange() || volume >= ch.getHearThres())
					localState.events.add(e);
			} else if (event instanceof BulletHitPlayerEvent) {
				BulletHitPlayerEvent e = (BulletHitPlayerEvent) event;
				double distance = Point2D.distance(e.x, e.y, ch.getX(), ch.getY());
				float volume = core.Utils.getVolumeAtDistance(AudioManager.BULLETHITNOISE, distance);
				if (distance < ch.getFovRange() || volume >= ch.getHearThres())
					localState.events.add(e);
			} else if (event instanceof FootStepEvent) {
				FootStepEvent e = (FootStepEvent) event;
				float volume = core.Utils.getVolumeAtDistance(e.noise, Point2D.distance(e.x, e.y, ch.getX(), ch.getY()));
				if (volume >= ch.getHearThres()) {
					localState.events.add(e);
				}
			} else if (event instanceof PowerUpPickedUpEvent) {
				PowerUpPickedUpEvent e = (PowerUpPickedUpEvent) event;
				if (e.id == id) {
					localState.events.add(e);
				}
			} else if (event instanceof GunShotEvent) {
				GunShotEvent e = (GunShotEvent) event;
				double distance = Point2D.distance(e.x, e.y, ch.getX(), ch.getY());
				float volume = core.Utils.getVolumeAtDistance(AudioManager.BULLETHITNOISE, distance);
				if (volume >= ch.getHearThres())
					localState.events.add(e);
			} else {
				localState.events.add(event);
			}
		}
		localState.time = globalState.time;
		localState.player = ch.generate();
		localState.chatTexts = globalState.chatTexts;
		return localState;
	}

	/**
	 * Get the characters visible to one particular characters. Used to give the AIs information
	 * about what's around them, but only what's visible. Similarly to the filter method actually,
	 * but this one is more about communicating with the AI while filter deals about packets sent to
	 * clients (i.e human players). Can actually merge them into one.
	 * 
	 * @param ch
	 *            The character that will get the resulting list.
	 * @return A list of characters in the vision field of the given character.
	 */
	public List<Character> generateVisibleCharacters(Character ch) {
		// Shape los = LineOfSight.generateLoS(ch.getIntX(),ch.getIntY(),
		// ch.getViewRange(),ch.getViewAngle(),ch.getDirection(), arena);
		List<Character> list = new LinkedList<Character>();
		for (ControlledCharacter p : characters) {
			double x0 = p.getX() - p.getRadius();
			double y0 = p.getY() - p.getRadius();
			// double radius = p.getRadius();
			if (Point2D.distance(x0, y0, ch.getX(), ch.getY()) < ch.getFovRange()) {
				list.add(p);
			}
		}
		return list;
	}

	/**
	 * Get the game event listener, for classes contained in the world to submit game events
	 * themselves.
	 * 
	 * @return The even listener associated with this world.
	 */
	public GameEventListener getEventListener() {
		return listener;
	}
	
	public List<ControlledCharacter> getCharacters() {
		return new LinkedList<ControlledCharacter>(characters);
	}
}
