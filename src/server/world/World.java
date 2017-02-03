package server.world;


import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import client.gui.GameWindow;
import editor.SpawnPoint;
import server.character.Entity;
import server.character.InputControlledEntity;
import server.world.trigger.Trigger;
import shared.network.CharData;
import shared.network.ProjectileData;
import shared.network.GameDataPackets.WorldStatePacket;
import shared.network.event.AnimationEvent;
import shared.network.event.GameEvent;
import shared.network.event.GameEvent.Listener;

/**
 * The physical world inside a match, which handles everything including characters, projectiles and
 * collision.
 */
public class World {
	public static final double DISTANCE_VOLUME_DROP_RATE = 2.5;
	
	private final List<InputControlledEntity> characters = new ArrayList<InputControlledEntity>();
	
	private final List<Projectile> projectiles = new LinkedList<Projectile>();
	private final List<Projectile> newProjectiles = new LinkedList<Projectile>();
	
	private final List<Light> dynamicLights = new LinkedList<Light>();
	
	private final List<Trigger> activeTriggers = new LinkedList<Trigger>();
	
	private long timeCounter = 0;
	
	private Arena arena;
	private Listener listener;
	
	/**
	 * To circle the spawn positions
	 */
	int[] curSpawn = new int[2];

	/**
	 * Creates the World which is used in GameScreen to play the actual game
	 * 
	 * @param arena
	 *            The arena used in the game
	 * @param listener
	 */
	public World(Arena arena, Listener listener) {
		this.arena = arena;
		this.listener = listener;
	}

	/**
	 * Adds a player to the World
	 * 
	 * @param p
	 *            the player to be added
	 * @param spawnPoint 
	 */
	public void addCharacter(InputControlledEntity p, SpawnPoint spawnPoint) {
		Point2D spawn = Utils.tileToMeter(spawnPoint.x,spawnPoint.y);
		p.setPosition(this,spawn.getX(),spawn.getY());
		characters.add(p);
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

	public void addSound(int id, double volume, double x, double y) {
		for (Entity ch:characters) {
			ch.filterSound(id, volume, x, y);
		}
	}
	
	public void addAnimation(int id, double x, double y, double direction) {
		for (Entity ch:characters) {
			ch.filterVisualAnimation(id, x, y, direction);
		}
	}
	
	public void addGlobalAnimation(int id, double x, double y, double rot) {
		for (Entity ch:characters) {
			ch.getPerception().events.add(new AnimationEvent(id,x,y,rot));
		}
	}
	
	public void addGlobalAnimation(AnimationEvent e) {
		for (Entity ch:characters) {
			ch.getPerception().events.add(e);
		}
	}
	
	public void addEvent(GameEvent event) {
		for (Entity ch:characters) {
			ch.getPerception().events.add(event);
		}
	}
	
	/**
	 * Update the world, and all the characters and projectiles in the world. This happens every
	 * frame.
	 */
	public void update() {
		// update characters
		for (InputControlledEntity p : characters) {
			p.update(this);
		}
		
		// update projectiles
		projectiles.addAll(newProjectiles);
		newProjectiles.clear();
		
		List<Projectile> consumed = new LinkedList<Projectile>();
		for (Projectile p : projectiles) {
			if (p.isConsumed()) {
				consumed.add(p);
			}
			else {
				p.update(this);
			}
		}
		
		projectiles.removeAll(consumed);
		
		// update triggers and remove the inactive ones
		List<Trigger> inactive = new LinkedList<Trigger>();
		for (Trigger tr : activeTriggers) {
			if (!tr.isActive()) {
				inactive.add(tr);
			} else {
				tr.update(this);
			}
		}
		activeTriggers.removeAll(inactive);
		
		for (int x=0;x<arena.getWidth();x++) {
			for (int y=0;y<arena.getHeight();y++) {
				Thing th = arena.get(x, y).getThing();
				if (th!=null) {
					SoundSource ss = th.getSound();
					if (ss!=null) {
						if (ss.isRandom()) {
							double chance = 1.0*GameWindow.MS_PER_UPDATE/ss.getFrequency();
							if (Utils.random().nextDouble()<chance) {
								Point2D p = Utils.tileToMeter(x,y);
								addSound(ss.getSoundId(), ss.getSoundVolume(),p.getX(),p.getY());
							}
						} else {
							if (timeCounter%ss.getFrequency()<GameWindow.MS_PER_UPDATE) {
								Point2D p = Utils.tileToMeter(x,y);
								addSound(ss.getSoundId(), ss.getSoundVolume(),p.getX(),p.getY());
							}
						}
					}
				}
			}
		}
		
		if (!dynamicLights.isEmpty()) {
			arena.updateLightMap(dynamicLights);
		}
		
		timeCounter += GameWindow.MS_PER_UPDATE;
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
	 * Get the characters visible to one particular characters. Used to give the AIs information
	 * about what's around them, but only what's visible. Similarly to the filter method actually,
	 * but this one is more about communicating with the AI while filter deals about packets sent to
	 * clients (i.e human players). Can actually merge them into one.
	 * 
	 * @param ch
	 *            The server.character that will get the resulting list.
	 * @return A list of characters in the vision field of the given server.character.
	 */
	public List<Entity> generateVisibleCharacters(Entity ch) {
		// Shape los = LineOfSight.generateLoS(ch.getIntX(),ch.getIntY(),
		// ch.getViewRange(),ch.getViewAngle(),ch.getDirection(), arena);
		List<Entity> list = new LinkedList<Entity>();
		for (InputControlledEntity p : characters) {
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
	public Listener getEventListener() {
		return listener;
	}
	
	public List<InputControlledEntity> getCharacters() {
		return new LinkedList<InputControlledEntity>(characters);
	}

	public List<Projectile> getProjectiles() {
		return projectiles;
	}

	public List<Light> getDynamicLights() {
		return dynamicLights;
	}
	
	public void addDynamicLight(Light light) {
		dynamicLights.add(light);
	}
	
	public void removeLight(Light light) {
		dynamicLights.remove(light);
	}
	
	public void addActiveTrigger(Trigger t) {
		if (!activeTriggers.contains(t)) {
			activeTriggers.add(t);
		}
	}
}