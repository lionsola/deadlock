package core;


import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

import network.GameDataPackets.InputPacket;
import network.GameDataPackets.WorldStatePacket;
import network.GameEvent;
import network.GameEvent.GunShotEvent;
import network.PartialCharacterData;
import network.ServerPlayer;
import core.PathFinder.Path;

/**
 * Used to model the behaviour of an AI player.
 * 
 * @author Anh Pham
 */
public class AIPlayer extends ServerPlayer {
	
	private static enum AIState {
		EXPLORING_POINT, EXPLORING_RANDOMLY, ATTACKING, RETREATING
	}

	private static final double PATH_THRESHOLD = Tile.tileSize / 3;
	private static final double OUTOFRANGE_THRESHOLD = Tile.tileSize * 2;
	private static final double COORD_THRESHOLD = Tile.tileSize / 10;
	private static final double EXPLORE_DISTANCE = Tile.tileSize * 15;
	private static final double RETREAT_DISTANCE = Tile.tileSize * 5;
	private PathFinder pathFinder;

	private InputPacket input = new InputPacket();
	private WorldStatePacket wsp;
	private Path nextPath;
	private List<Point2D> curPath;
	private Point2D curIntr;
	private Arena arena;
	private AIState state = AIState.EXPLORING_RANDOMLY;

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
	public void sendWorldState(WorldStatePacket wsp) {
		this.wsp = wsp;
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
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// delay output a bit to make them less scary
		WorldStatePacket wsp = this.wsp;
		try {
			Thread.sleep(50);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Point2D self = new Point2D.Double(wsp.player.x, wsp.player.y);

		// identify an enemy in sight
		PartialCharacterData enemy = null;
		for (PartialCharacterData c : wsp.characters) {
			if (c.team != team) {
				enemy = c;
				break;
			}
		}

		// if there is an enemy
		if (enemy != null) {
			pointCursorAt(enemy.x, enemy.y);
			if (wsp.player.reloadPercent >= 1) {
				state = AIState.ATTACKING;
				input.fire1 = true;
			} else if (state != AIState.RETREATING) {
				// RETREAT
				state = AIState.RETREATING;
				double enemyDirection = Math.atan2(self.getY() - enemy.y, enemy.x - self.getX());
				setNewIntr(self, randomIntr(self.getX(), self.getY(), enemyDirection + Math.PI, RETREAT_DISTANCE));
			}
		} else { // no enemy in sight
			input.fire1 = false;
			Point2D newIntr = null;
			float min = Float.MAX_VALUE;
			// else, explore locations of interests (only gun shots atm)
			for (GameEvent event : wsp.events) {
				// listen for gun shots
				if (event instanceof GunShotEvent) {
					GunShotEvent e = (GunShotEvent) event;
					float eventDist = Geometry.diagonalDistance(e.x, e.y, self.getX(), self.getY());
					// if this one is closer than last one
					if (eventDist < min) {
						// take this one instead
						newIntr = new Point2D.Double(e.x, e.y);
						min = eventDist;
					}
				}
			}

			if (newIntr != null) {
				if (state == AIState.EXPLORING_POINT) {
					float newDist = Geometry.diagonalDistance(newIntr, self);
					float curDist = Geometry.diagonalDistance(curIntr, self);
					if (newDist < curDist) {
						setNewIntr(self, newIntr);
					}
				} else {
					setNewIntr(self, newIntr);
					state = AIState.EXPLORING_POINT;
				}
			}

			if (curIntr == null || Geometry.diagonalDistance(curIntr, self) < PATH_THRESHOLD) {
				// generate randomly
				state = AIState.EXPLORING_RANDOMLY;
				setNewIntr(self, randomIntr(self.getX(), self.getY(), Math.PI * 2 * core.Utils.random().nextDouble(), EXPLORE_DISTANCE));
			}
		}

		// if was waiting for a path before and a path appeared
		// take it
		if (nextPath.path != null) {
			curPath = nextPath.path;
			nextPath.path = null;
		}

		if (curPath != null && !curPath.isEmpty()) {
			float distToCheckPoint = Geometry.diagonalDistance(self, curPath.get(0));

			// in case of death
			if (distToCheckPoint > OUTOFRANGE_THRESHOLD) {
				state = AIState.EXPLORING_RANDOMLY;
				setNewIntr(self, randomIntr(self.getX(), self.getY(), Math.PI * 2 * core.Utils.random().nextDouble(), EXPLORE_DISTANCE));
			} else {
				// if alr close to one checkpoint, move on
				if (distToCheckPoint < PATH_THRESHOLD) {
					System.out.println("AI " + id + " arrived at " + curPath.get(0));
					curPath.remove(0);
				}

				// follow the next
				if (!curPath.isEmpty()) {
					moveToward(self, curPath.get(0));
					if (state != AIState.RETREATING && state != AIState.ATTACKING) {
						pointCursorAt(curPath.get(0));
					}
				}
			}
		}
		wsp = null;
		return input;
	}

	/**
	 * Returns a random point of interest for an AI player.
	 * 
	 * @param d
	 *            x coordinate of point of interest
	 * @param e
	 *            y coordinate of point of interest
	 * @param direction
	 *            the direction of the point of interest
	 * @param distance
	 *            how far the point of interest is away
	 * 
	 * @return a new point of interest in the given direction, or none if none
	 */
	private Point2D randomIntr(double d, double e, double direction, double distance) {
		double newX = d + distance * Math.cos(direction);
		double newY = e - distance * Math.sin(direction);
		int tileX = (int) (newX / Tile.tileSize);
		int tileY = (int) (newY / Tile.tileSize);
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
				if (arena.get(i, j).walkable)
					emptyTiles.add(new Point(i, j));
			}
		}
		if (emptyTiles.isEmpty()) {
			System.out.println("Can't find an empty tile from " + new Point(tileX0, tileY0) + " to " + new Point(tileX1, tileY1));
			return null;
		} else {
			return core.Utils.tileToMeter(emptyTiles.get(core.Utils.random().nextInt(emptyTiles.size())));
		}
	}

	/**
	 * Sets a new point of interest
	 * 
	 * @param self
	 *            where the character is currently
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
			input.top = true;
		} else
			input.top = false;
	}

	private void pointCursorAt(Point2D point2d) {
		pointCursorAt(point2d.getX(), point2d.getY());
	}

	private void pointCursorAt(double x, double y) {
		input.cx = (float)x;
		input.cy = (float)y;
	}
	
}
