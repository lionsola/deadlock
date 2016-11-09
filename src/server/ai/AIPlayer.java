package server.ai;


import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

import server.ai.PathFinder.Path;
import server.network.ServerPlayer;
import server.world.Arena;
import server.world.Geometry;
import server.world.Terrain;
import server.world.Thing;
import server.world.Utils;
import shared.network.GameEvent;
import shared.network.CharData;
import shared.core.Vector2D;
import shared.network.GameDataPackets.InputPacket;
import shared.network.GameDataPackets.WorldStatePacket;
import shared.network.GameEvent.SoundEvent;
import shared.network.ProjectileData;

/**
 * Used to model the behaviour of an AI player.
 * 
 * @author Anh Pham
 */
public class AIPlayer extends ServerPlayer {
	
	private static enum AIState {
		IDLING, EXPLORING, ATTACKING, RETREATING, DEFENDING
	}

	private static final double PATH_THRESHOLD = Terrain.tileSize/5;
	private static final double OUTOFRANGE_THRESHOLD = Terrain.tileSize * 2;
	private static final double COORD_THRESHOLD = Terrain.tileSize / 20;
	private static final double EXPLORE_DISTANCE = Terrain.tileSize * 15;
	private static final double RETREAT_DISTANCE = Terrain.tileSize * 5;
	private PathFinder pathFinder;

	private InputPacket input = new InputPacket();
	private WorldStatePacket wsp;
	private List<CharData> enemies;
	private List<CharData> allies;
	private List<GameEvent> events = new LinkedList<GameEvent>();
	private Path nextPath;
	private List<Point2D> curPath;
	private List<InterestPoint> ips;
	private Point2D curIntr;
	private Arena arena;
	private AIState state = AIState.IDLING;
	
	//private float[][] playerProb;

	/**
	 * Creates a new AI Player on the server
	 * 
	 * @param id
	 *            The id of the AI Player
	 * @param team
	 *            The team of the AI player
	 */
	public AIPlayer(int id, int team) {
		super(id, team, "BOT" + id, null);
	}

	/**
	 * Initialise the AI Player
	 * 
	 * @param arena
	 * @param pathFinder
	 */
	public void init(Arena arena, PathFinder pathFinder) {
		this.arena = arena;
		this.pathFinder = pathFinder;
	}

	@Override
	public void sendData(WorldStatePacket wsp) {
		this.wsp = wsp;
		events.addAll(wsp.events);
	}

	/**
	 * This method can and should block for an (arbitrary) amount of time, similarly to how reading
	 * networked input blocks.
	 */
	@Override
	public InputPacket getInput() {
		// waiting for input
		while (wsp == null) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		// delay output a bit
		WorldStatePacket wsp = this.wsp;
		try {
			Thread.sleep(50);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		if (character.isDead())
			return new InputPacket();
		
		Point2D self = new Point2D.Double(wsp.player.x, wsp.player.y);
		
		enemies = new LinkedList<CharData>();
		allies = new LinkedList<CharData>();
		// identify an enemy in sight
		for (CharData c : wsp.characters) {
			if (c.team != team && c.healthPoints>0) {
				enemies.add(c);
			} else if (c.team == team) {
				allies.add(c);
			}
		}
		
		// if I'm exploring but there's something more important
		if (true) {
			AIState newState = decideAction();
			if (newState!=state) {
				state = newState;
				// well, do it
				this.execute(state);
			}
		}

		// if I was calculating a path and a path appeared
		// take it
		if (nextPath!=null && nextPath.path != null) {
			curPath = nextPath.path;
			nextPath.path = null;
		}

		// follow current path
		if (curPath != null && !curPath.isEmpty()) {
			float distToCheckPoint = Geometry.diagonalDistance(self, curPath.get(0));

			// in case of death
			if (distToCheckPoint > OUTOFRANGE_THRESHOLD) {
				setNewIntr(self, randomIntr(self.getX(), self.getY(), Math.PI * 2 * server.world.Utils.random().nextDouble(), EXPLORE_DISTANCE));
			} else {
				// if alr close to one checkpoint, move on
				if (distToCheckPoint < PATH_THRESHOLD) {
					//System.out.println("AI " + id + " arrived at " + curPath.get(0));
					curPath.remove(0);
				}

				// follow the next
				if (!curPath.isEmpty()) {
					moveToward(self, curPath.get(0));
					if (state != AIState.RETREATING && state != AIState.ATTACKING) {
						if (curPath.size()>1)
							pointCursorAt(curPath.get(1));
						else 
							pointCursorAt(curPath.get(0));
					}
				}
			}
		} else {
			setNewIntr(self, randomIntr(self.getX(), self.getY(), Math.PI * 2 * server.world.Utils.random().nextDouble(), EXPLORE_DISTANCE));
		}
		//wsp = null;
		events.clear();
		return input;
		
	}

	private Point2D checkForIntr(WorldStatePacket wsp) {
		Point2D newIntr = null;
		double min = Double.MAX_VALUE;
		for (GameEvent event : wsp.events) {
			// listen for gun shots
			if (event instanceof SoundEvent) {
				SoundEvent e = (SoundEvent) event;
				float eventDist = Geometry.diagonalDistance(e.x, e.y, wsp.player.x, wsp.player.y);
				// if this one is closer than last one
				if (eventDist < min && AIPlayer.isGunshotSound(e.id)) {
					// take this one instead
					newIntr = new Point2D.Double(e.x, e.y);
					min = eventDist;
				}
			}
		}
		return newIntr;
	}

	/**
	 * Returns a random point of interest for an AI player.
	 * 
	 * @param x
	 *            x coordinate of point of interest
	 * @param y
	 *            y coordinate of point of interest
	 * @param direction
	 *            the direction of the point of interest
	 * @param distance
	 *            how far the point of interest is away
	 * 
	 * @return a new point of interest in the given direction, or none if none
	 */
	private Point2D randomIntr(double x, double y, double direction, double distance) {
		double newX = x + distance * Math.cos(direction);
		double newY = y - distance * Math.sin(direction);
		int tileX = (int) (newX / Terrain.tileSize);
		int tileY = (int) (newY / Terrain.tileSize);
		if (tileX < 0)
			tileX = Math.abs(tileX);
		else if (tileX > arena.getWidth())
			tileX = arena.getWidth() * 2 - tileX;
		if (tileY < 0)
			tileY = Math.abs(tileY);
		else if (tileY > arena.getHeight())
			tileY = arena.getHeight() * 2 - tileY;

		int n = 5;
		int tileX0 = Math.max(0, tileX - n);
		int tileX1 = Math.min(arena.getWidth(), tileX + n);
		int tileY0 = Math.max(0, tileY - n);
		int tileY1 = Math.min(arena.getHeight(), tileY + n);

		List<Point> emptyTiles = new LinkedList<Point>();
		for (int i = tileX0; i <= tileX1; i++) {
			for (int j = tileY0; j <= tileY1; j++) {
				if (arena.get(i, j).isTraversable())
					emptyTiles.add(new Point(i, j));
			}
		}
		if (emptyTiles.isEmpty()) {
			System.out.println("Can't find an empty tile from " + new Point(tileX0, tileY0) + " to " + new Point(tileX1, tileY1));
			return null;
		} else {
			return Utils.tileToMeter(emptyTiles.get(Utils.random().nextInt(emptyTiles.size())));
		}
	}

	private AIState decideAction() {
		AIState state = AIState.EXPLORING;
		// if there is an enemy
		if (!enemies.isEmpty()) {
			if (wsp.player.reloadPercent >= 1 && enemies.size()<=2) {
				// ATTACK
				state = AIState.ATTACKING;
			} else if (state != AIState.RETREATING) {
				// RETREAT
				state = AIState.RETREATING;
			}
		} else {
			state = AIState.EXPLORING;
		}
		return state;
	}
	
	private void execute(AIState state) {
		Point2D self = new Point2D.Float(wsp.player.x,wsp.player.y);
		switch (state) {
			case ATTACKING:
				// if weapon is ready
				// decide on target
				
				double minHp = Double.MAX_VALUE;
				CharData target = null;
				for (CharData e:enemies) {
					// do raycasting first to decide which one can actually be hit
					boolean blocked = false;
					for (CharData ally:allies) {
						if (Line2D.ptLineDist(wsp.player.x,wsp.player.y,e.x,e.y,ally.x,ally.y)<ally.radius) {
							blocked = true;
							break;
						}
					}
					List<Point2D> samples = Geometry.getLineSamples(wsp.player.x,wsp.player.y,e.x,e.y, 0.5);
					for (Point2D point:samples) {
						Thing t = arena.getTileAt(point.getX(),point.getY()).getThing();
						if (t!=null && t.getCoverType()>1) {
							blocked = true;
							break;
						}
					}
					
					// ATM just target the one with lowest hp
					if (!blocked) {
						if (e.healthPoints < minHp) {
							target = e;
							minHp = e.healthPoints;
						}
					}
				}
				if (target==null) {
					System.err.println("AI "+this.id+" trying to attack but no target?!");
				} else {
					pointCursorAt(target.x,target.y);
				}
				
				// decide whether to shoot it or wait or run somewhere else
				// ATM just shoot it
				input.fire1 = true;
				break;
			case RETREATING:
				// decide where to retreat to
				input.fire1 = false;
				
				Vector2D evasionVector = new Vector2D();
	            for (CharData e:enemies){
	                Vector2D v = new Vector2D(new Point2D.Float(e.x,e.y),self);
	                //v.multiply(calculateEffect(getSelf(),a));
	                evasionVector.add(v);
	            }
				double evadeDirection = 0;
	            if (evasionVector.magSqr()!=0) {
	            	evadeDirection = evasionVector.getDirection();
	            }
	            
				// calculate the path
	            setNewIntr(self,randomIntr(self.getX(), self.getY(), evadeDirection, RETREAT_DISTANCE));
				// follow the path
				break;
			case EXPLORING:
				input.fire1 = false;
				
				// explore locations of interests (only gun shots atm)
				Point2D newIntr = checkForIntr(wsp);

				if (newIntr != null) {
					if (curIntr!=null) {
						float newDist = Geometry.diagonalDistance(newIntr, self);
						float curDist = Geometry.diagonalDistance(curIntr, self);
						if (newDist < curDist) {
							setNewIntr(self, newIntr);
						}
					} else {
						setNewIntr(self, newIntr);
						state = AIState.EXPLORING;
					}
				}

				if (curIntr == null || Geometry.diagonalDistance(curIntr, self) < PATH_THRESHOLD) {
					// generate a random destination to explore
					setNewIntr(self, randomizeExploreDest());
				}
				// decide where to explore
				break;
			default:
				break;	
		}
	}
	
	private Point2D randomizeExploreDest() {
		return randomIntr(wsp.player.x, wsp.player.y, Math.PI * 2 * server.world.Utils.random().nextDouble(), EXPLORE_DISTANCE);
	}
	
	/**
	 * Sets a new point of interest
	 * 
	 * @param self
	 *            where the server.character is currently
	 * @param point2d
	 *            the new point of interest
	 */
	private void setNewIntr(Point2D self, Point2D point2d) {
		curIntr = point2d;
		nextPath = pathFinder.findPath(self, point2d);
		System.out.println("Set new interest at " + point2d);
		// world.getEventListener().onEventReceived(new GunShotEvent(newIntr.x,newIntr.y,0,0));
	}

	/**
	 * Moves the AI Player from one point to another
	 * 
	 * @param self
	 *            The point where the AI Player starts
	 * @param dest
	 *            The destination where the AI Player is going to
	 */
	private void moveToward(Point2D self, Point2D dest) {
		if (self.getX() + COORD_THRESHOLD < dest.getX()) {
			input.right = true;
		} else
			input.right = false;
		if (self.getX() - COORD_THRESHOLD > dest.getX()) {
			input.left = true;

		} else
			input.left = false;
		if (self.getY() + COORD_THRESHOLD < dest.getY()) {
			input.down = true;
		} else
			input.down = false;
		if (self.getY() - COORD_THRESHOLD > dest.getY()) {
			input.up = true;
		} else
			input.up = false;
	}

	private void pointCursorAt(Point2D point2d) {
		pointCursorAt(point2d.getX(), point2d.getY());
	}

	private void pointCursorAt(double x, double y) {
		input.cx = (float)x;
		input.cy = (float)y;
	}

	public static boolean isGunshotSound(int id) {
		return id>0 && id<30;
	}
	
}
