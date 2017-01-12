package server.ai;

import java.awt.Point;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import server.ai.InterestPoint.Type;
import server.ai.PathFinder.Path;
import server.character.InputControlledEntity;
import server.world.Arena;
import server.world.Geometry;
import server.world.Terrain;
import server.world.Tile;
import server.world.Utils;
import shared.core.Vector2D;
import shared.network.CharData;
import shared.network.GameDataPackets.InputPacket;
import shared.network.GameDataPackets.WorldStatePacket;
import shared.network.event.GameEvent;
import shared.network.event.SoundEvent;

/**
 * Obsolete class for providing AI behaviour, code is being
 * transfered to NPCbrain.
 */
public class Brain {
	private static final double PATH_THRESHOLD = Terrain.tileSize/5;
	private static final double OUTOFRANGE_THRESHOLD = Terrain.tileSize * 2;
	private static final double COORD_THRESHOLD = Terrain.tileSize / 20;
	private static final double EXPLORE_DISTANCE = Terrain.tileSize * 15;
	private static final double RETREAT_DISTANCE = Terrain.tileSize * 5;
	
	protected static enum AIState {
		IDLING,
		PATROLING,
		CHASING,
		RETREATING,
		DEFENDING,
		EXPLORING
	}
	
	private PathFinder pathFinder;

	private InputPacket input = new InputPacket();
	protected List<CharData> enemies = new LinkedList<CharData>();
	protected List<CharData> allies = new LinkedList<CharData>();
	
	protected long time;
	
	private Point2D focus;
	
	protected Arena arena;
	private AIState state = AIState.IDLING;
	
	private Path nextPath;
	private List<Point2D> curPath;
	private List<InterestPoint> ips = new ArrayList<InterestPoint>(20);
	
	private HashMap<Condition,Boolean> conditions;
	private HashMap<Condition,Boolean> lastConditions;
	
	private InterestPoint curIp;
	
	InputControlledEntity character;
	
	/**
	 * Initialise the AI Player
	 * 
	 * @param arena
	 * @param pc 
	 * @param pathFinder
	 */
	public void init(Arena arena, InputControlledEntity pc, PathFinder pathFinder) {
		this.arena = arena;
		this.pathFinder = pathFinder;
		this.character = pc;
	}

	public InputPacket getInput() {
		return input;
	}
	
	protected boolean getCondition(Condition c) {
		if (!conditions.containsKey(c))
			conditions.put(c, c.check(this, character));
		return conditions.get(c);
	}
	
	protected void identifyCharacters(WorldStatePacket wsp) {
		enemies = new LinkedList<CharData>();
		allies = new LinkedList<CharData>();
		
		// identify an enemy in sight
		for (CharData c : wsp.characters) {
			if (c.healthPoints>0) {
				if (c.team != character.team) {
					enemies.add(c);
				} else if (c.team == character.team) {
					allies.add(c);
				}
			}
		}
	}
	
	public void update(WorldStatePacket wsp) {
		if (wsp==null)
			return;
		time = wsp.time;
		lastConditions = conditions;
		conditions = new HashMap<Condition,Boolean>();
		Point2D self = new Point2D.Double(character.getX(), character.getY());
		
		identifyCharacters(wsp);
		
		// if I'm exploring but there's something more important
		AIState newState = decideAction();
		state = newState;
		// well, do it
		execute(state);

		followPath();
	}

	protected void followPath() {
		// if I was calculating a path and a path appeared
		// take it
		if (nextPath!=null && nextPath.path != null) {
			curPath = nextPath.path;
			nextPath.path = null;
		}

		// follow current path
		if (curPath != null && !curPath.isEmpty()) {
			float distToCheckPoint = Geometry.diagonalDistance(character.getPosition(), curPath.get(0));

			// in case of death
			if (distToCheckPoint > OUTOFRANGE_THRESHOLD) {
				setNewIntr(null,false);
				//setNewIntr(self, randomIntr(self.getX(), self.getY(), Math.PI * 2 * server.world.Utils.random().nextDouble(), EXPLORE_DISTANCE));
			} else {
				// if alr close to one checkpoint, move on
				if (distToCheckPoint < PATH_THRESHOLD) {
					//System.out.println("AI " + id + " arrived at " + curPath.get(0));
					curPath.remove(0);
				}

				// follow the next
				if (!curPath.isEmpty()) {
					moveToward(curPath.get(0));
					if (state != AIState.RETREATING && state != AIState.CHASING) {
						if (curPath.size()>1)
							pointCursorAt(curPath.get(1));
						else 
							pointCursorAt(curPath.get(0));
					}
				}
			}
		}
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
	
	public void processEvent(GameEvent event) {
		InterestPoint ip = null;
		if (event instanceof SoundEvent) {
			SoundEvent e = (SoundEvent) event;
			//float eventDist = Geometry.diagonalDistance(e.x, e.y, wsp.player.x, wsp.player.y);
			// if this one is closer than last one
			if (isGunshotSound(e.id) || e.id==SoundEvent.FOOTSTEP_DEFAULT_ID) {
				// if it's my own footsteps
				if (Point2D.distance(e.x, e.y, character.getX(), character.getY())<0.5) {
					return;
				}
				for (CharData c:allies) {
					// or if it's an ally
					if (Point2D.distance(c.x,c.y,e.x,e.y)<0.5) {
						// no sweat
						return;
					}
				}
				// else
				// register the event as an enemy location
				
				ip = new InterestPoint();
				ip.setLocation(new Point2D.Float(e.x,e.y));
				ip.setTime(time);
				ip.setType(Type.ENEMY);
				
			} else if (e.id == SoundEvent.PING_SOUND_ID) {
				ip = new InterestPoint();
				ip.setLocation(new Point2D.Float(e.x,e.y));
				ip.setTime(time);
				ip.setType(Type.PING);
			}
		}
		// double-check if it can actually get to that point
		if (ip==null) {
			return;
		}
		Path path = pathFinder.findPath(character.getPosition(),ip.getLocation());
		final double COMBINE_DISTANCE = 2;
		if (path.path!=null) {
			// if it's even better than the current interest point
			if (curIp==null || comparator.compare(ip, curIp)>1) {
				// well, investigate it!
				setNewIntr(ip,true);
			} else {
				for (InterestPoint ip0:ips) {
					// if there is already an interest point closeby
					if (ip0.getLocation().distance(ip.getLocation())<COMBINE_DISTANCE) {
						// combine them
						if (ip.getType().priority>ip0.getType().priority) {
							ip0.setType(ip.getType());
						}
						if (ip.getTime()>ip0.getTime()) {
							ip0.setLocation(ip.getLocation());
							ip0.setTime(ip.getTime());
						}
						break;
					}
				}
				ips.add(ip);
			}
		}
	}
	
	protected AIState decideAction() {
		AIState state = AIState.EXPLORING;
		// if there is an enemy
		if (getCondition(Condition.ENEMY_IN_SIGHT)) {
			if (getCondition(Condition.WEAPON_READY)) {
				// ATTACK
				state = AIState.CHASING;
			} else if (state != AIState.RETREATING) {
				// RETREAT
				state = AIState.RETREATING;
			}
		} else {
			state = AIState.EXPLORING;
		}
		return state;
	}
	
	protected void execute(AIState state) {
		Point2D self = new Point2D.Double(character.getX(),character.getY());
		input.alt = false;
		input.fire1 = false;
		input.fire2 = false;
		switch (state) {
			case CHASING:
				Point2D target = null;
				// if weapon is ready
				// decide on target
				if (character.getWeapon().isReady()) {
				
					double minHp = Double.MAX_VALUE;
					for (CharData e:enemies) {
						// do raycasting first to decide which one can actually be hit
						Point2D aim = getClearAim(e);
						
						// ATM just target the one with lowest hp
						if (aim!=null) {
							if (e.healthPoints < minHp) {
								target = aim;
								minHp = e.healthPoints;
							}
						}
					}
					if (target!=null) {
						pointCursorAt(target.getX(),target.getY());
						input.fire1 = true;
					}
				}
				InterestPoint ip = new InterestPoint();
				ip.setTime(time);
				ip.setType(Type.ENEMY);
				ip.setLocation(target);
				setNewIntr(ip,false);
				// decide whether to shoot it or wait or run somewhere else
				// ATM just shoot it
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
	            //setNewIntr(self,randomIntr(self.getX(), self.getY(), evadeDirection, RETREAT_DISTANCE));
				// follow the path
				break;
			case EXPLORING:
				input.fire1 = false;
				
				// choose a interest point to explore
				if (curIp==null || character.getLoS().contains(curIp.getLocation())) {
					if (!ips.isEmpty()) {
						Collections.sort(ips, comparator);
						InterestPoint best = ips.get(ips.size()-1);
						setNewIntr(best,false);
					} else {
						setNewIntr(randomizeExploreDest(),false);
					}
				}
				break;
			default:
				break;
			
		}
		
		// ping
		if (!input.fire1 && !input.fire2 && enemies.size()>0){
			CharData enemy = enemies.get(0);
			pointCursorAt(enemy.x,enemy.y);
			input.alt = true;
			input.fire2 = true;
		}
	}
	
	private Point2D getClearAim(CharData enemy) {
		boolean blocked = false;
		Vector2D n = new Vector2D(enemy.y-character.getY(),character.getX()-enemy.x);
		n.mult((enemy.radius/2)/Math.sqrt(n.magSqr()));
		
		Vector2D enemyPos = new Vector2D(enemy.x,enemy.y);
		//enemyPos.sub(n);
		int[] a = {0,1,-1};
		for (int i=0;i<3;i++) {
			blocked = false;
			Vector2D target = enemyPos.clone();
			Vector2D d = n.clone();
			d.mult(a[i]);
			target.add(d);
			
			for (CharData ally:allies) {
				if (Line2D.ptSegDist(character.getX(),character.getY(),enemy.x,enemy.y,ally.x,ally.y)<ally.radius) {
					blocked = true;
					break;
				}
			}
			
			if (!blocked) {
				int maxBlock = (int)(character.getWeapon().type.projectileSpeed/0.2);
				List<Point2D> samples = Geometry.getLineSamples(character.getX(),character.getY(),target.x,target.y, 0.5);
				int blockCount = 0;
				for (Point2D point:samples) {
					Tile t = arena.getTileAt(point.getX(),point.getY());
					if (t.coverType()>0) {
						blockCount += t.coverType();
					}
					if (blockCount>maxBlock) {
						blocked = true;
						break;
					}
				}
			}
			
			if (!blocked) {
				return new Point2D.Double(target.x,target.y);
			}
		}
		return null;
	}
	
	private InterestPoint randomizeExploreDest() {
		Point2D p = randomIntr(character.getX(), character.getY(), Math.PI*2*server.world.Utils.random().nextDouble(), EXPLORE_DISTANCE);
		InterestPoint ip = new InterestPoint();
		ip.setLocation(p);
		ip.setType(Type.RANDOM);
		ip.setTime(time);
		return ip;
	}
	
	/**
	 * Sets a new point of interest
	 * 
	 * @param self
	 *            where the server.character is currently
	 * @param point2d
	 *            the new point of interest
	 */
	protected void setNewIntr(InterestPoint ip, boolean keepOldIp) {
		Point2D self = new Point2D.Double(character.getX(),character.getY());
		if (keepOldIp && curIp!=null) {
			ips.add(curIp);
		}
		
		curIp = ip;
		ips.remove(ip);
		if (ip!=null) {
			nextPath = pathFinder.findPath(self, ip.getLocation());
			System.out.println("Set new interest at " + ip.getLocation() + ", "+ip.getType());
		}
		// world.getEventListener().onEventReceived(new GunShotEvent(newIntr.x,newIntr.y,0,0));
	}
	
	public InterestPoint getCurrentIntr() {
		return curIp;
	}
	
	/**
	 * Moves the AI Player from one point to another
	 * 
	 * @param self
	 *            The point where the AI Player starts
	 */
	private void moveToward(Point2D dest) {
		if (character.getX() + COORD_THRESHOLD < dest.getX()) {
			input.right = true;
		} else
			input.right = false;
		if (character.getX() - COORD_THRESHOLD > dest.getX()) {
			input.left = true;

		} else
			input.left = false;
		if (character.getY() + COORD_THRESHOLD < dest.getY()) {
			input.down = true;
		} else
			input.down = false;
		if (character.getY() - COORD_THRESHOLD > dest.getY()) {
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
	
	private double timeWeight = 0.001;
	private double typeWeight = 10;
	private double distanceWeight = 1;
	public Comparator<InterestPoint> comparator = new Comparator<InterestPoint>() {
		@Override
		public int compare(InterestPoint ip0, InterestPoint ip1) {
			if (ip0.getType()==Type.RANDOM) {
				if (ip1.getType()==Type.RANDOM) {
					return 0;
				} else {
					return -1;
				}
			} else if (ip1.getType()==Type.RANDOM) {
				return 1;
			}
			double dt = ip0.getTime()-ip1.getTime();
			Path p0 = pathFinder.findPath(character.getPosition(),ip0.getLocation());
			Path p1 = pathFinder.findPath(character.getPosition(),ip1.getLocation());
			double distance0 = p0.path!=null?p0.path.size():999999;
			double distance1 = p1.path!=null?p1.path.size():999999;
			double dd = distance0 - distance1;
			double dty = ip0.getType().priority - ip1.getType().priority;
			
			return (int)Math.round(timeWeight*dt + distanceWeight*dd + typeWeight*dty);
		}
	};
}
