package server.world;


import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import server.character.Character;
import server.character.PlayerCharacter;
import shared.network.PartialCharacterData;
import shared.network.ProjectileData;
import shared.network.GameDataPackets.WorldStatePacket;
import shared.network.GameEvent.AnimationEvent;
import shared.network.GameEvent.GameEventListener;

/**
 * The physical world inside a match, which handles everything including characters, projectiles and
 * collision.
 * 
 * @author Anh Pham
 */
public class World {
	
	private Arena arena;
	private List<PlayerCharacter> characters = new ArrayList<PlayerCharacter>();
	private List<Character> npcs = new LinkedList<Character>();
	
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
	}

	static int[] curSpawn = new int[2];
	/**
	 * Adds a player to the World
	 * 
	 * @param p
	 *            the player to be added
	 */
	public void addPlayer(PlayerCharacter p) {
		Point2D spawn = arena.getSpawn(p.team).get(curSpawn[p.team]);
		curSpawn[p.team] = (curSpawn[p.team]+1)%arena.getSpawn(p.team).size();
		p.setX(spawn.getX());
		p.setY(spawn.getY());
		characters.add(p);
	}

	public void addNPC(Character npc) {
		npcs.add(npc);
	}

	/**
	 * Randomize the spawn points of a given team
	 * 
	 * @param team
	 *            the team id of the team that wishes its spawn points randomized
	 * @return
	 */
	private Point2D randomizeSpawnPoint(int team) {
		int spawnIndex = server.world.Utils.random().nextInt(arena.getSpawn(team).size());
		return arena.getSpawn(team).get(spawnIndex);
	}

	/**
	 * Add a projectile to the world
	 * 
	 * @param p
	 *            the projectile to be added
	 */
	public void addDelayedProjectile(Projectile p) {
		newProjectiles.add(p);
	}
	
	public void addProjectile(Projectile p) {
		projectiles.add(p);
	}
	
	public void addSound(Sound s, double x, double y) {
		addSound(s.id,s.volume,x,y);
	}

	public void addSound(int id, double volume, double x, double y) {
		for (Character ch:characters) {
			ch.filterSound(id, volume, x, y);
		}
	}
	
	public void addAnimation(int id, double x, double y, double direction) {
		for (Character ch:characters) {
			ch.filterVisualAnimation(id, x, y, direction);
		}
	}
	
	public void addGlobalAnimation(int id, double x, double y, double rot) {
		for (Character ch:characters) {
			ch.getPerception().events.add(new AnimationEvent(id,x,y,rot));
		}
	}
	
	/**
	 * Update the world, and all the characters and projectiles in the world. This happens every
	 * frame.
	 */
	public void update() {
		
		
		// update characters
		for (PlayerCharacter p : characters) {
			p.update(this);
		}

		// update npcs
		List<Character> expired = new LinkedList<Character>();
		for (Character npc : npcs) {
			//if (npc.)
			npc.update(this);
		}
		npcs.removeAll(expired);
		
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
		for (PlayerCharacter character : characters) {
			wsp.characters.add(character.generatePartial());
		}
		
		wsp.projectiles = new LinkedList<ProjectileData>();
		for (Projectile projectile : projectiles) {
			wsp.projectiles.add(projectile.getData());
		}

		return wsp;
	}

	/**
	 * Get the characters visible to one particular characters. Used to give the AIs information
	 * about what's around them, but only what's visible. Similarly to the filter method actually,
	 * but this one is more about communicating with the AI while filter deals about packets sent to
	 * clients (i.e human players). Can actually merge them into one.
	 * 
	 * @param ch
	 *            The server.character that will get the resulting list.
	 * @return A list of characters in the vision field of the given server.character.
	 */
	public List<Character> generateVisibleCharacters(Character ch) {
		// Shape los = LineOfSight.generateLoS(ch.getIntX(),ch.getIntY(),
		// ch.getViewRange(),ch.getViewAngle(),ch.getDirection(), arena);
		List<Character> list = new LinkedList<Character>();
		for (PlayerCharacter p : characters) {
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
	
	public List<PlayerCharacter> getCharacters() {
		return new LinkedList<PlayerCharacter>(characters);
	}

	public List<Projectile> getProjectiles() {
		return projectiles;
	}
}

