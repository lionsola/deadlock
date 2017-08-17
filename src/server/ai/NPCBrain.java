package server.ai;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

import editor.SpawnPoint.Behaviour;
import editor.SpawnPoint.CharType;
import jbt.execution.core.BTExecutorFactory;
import jbt.execution.core.ContextFactory;
import jbt.execution.core.IBTExecutor;
import jbt.execution.core.IBTLibrary;
import jbt.execution.core.IContext;
import jbt.model.core.ModelTask;
import server.ai.InterestPoint.Type;
import server.ai.jbt.library.NPCBTLib;
import server.character.Entity;
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

public class NPCBrain {
	int current = 0;
	int mode;
	IContext context;
	IBTExecutor btExecutor;
	List<CharData> enemies = new LinkedList<CharData>();
	List<CharData> allies = new LinkedList<CharData>();
	double alertness;
	
	InputControlledEntity character;
	protected long time;
	
	protected Arena arena;
	private Behaviour behaviour;
	
	
	public NPCBrain(Behaviour behaviour, Arena a, InputControlledEntity e) {
		this.behaviour = behaviour;
		if (behaviour!=Behaviour.Dummy) {
			/* First of all, we create the BT library. */
			IBTLibrary btLibrary = new NPCBTLib();
			/* Then we create the initial context that the tree will use. */
			context = ContextFactory.createContext(btLibrary);
			
			/* Now we get the Model BT to run. */
			ModelTask bt = null;
			if (behaviour==Behaviour.Custom) {
				bt = btLibrary.getBT("NPC"+CharType.valueOf(e.typeId).name());
			} else {
				bt = btLibrary.getBT(behaviour.name());
			}
			/* Then we create the BT Executor to run the tree. */
			btExecutor = BTExecutorFactory.createBTExecutor(bt, context);
		}
		this.arena = a;
		this.character = e;
		if (context!=null) {
			initContext();
		}
	}
	
	private void initContext() {
		context.setVariable("Arena", arena);
		context.setVariable("Character", character);
		context.setVariable("Input", character.getInput());
		context.setVariable("Enemies", enemies);
		context.setVariable("Allies", allies);
	}
	
	protected void identifyCharacters(WorldStatePacket wsp) {
		enemies.clear();
		allies.clear();
		
		// identify an enemy in sight
		boolean enemyInSight = false;
		for (CharData c : wsp.characters) {
			if (c.healthPoints>0) {
				if (c.team != character.team) {
					// calculate relative exposure
					double ex = 0;
					List<Point2D> checkPoints = Entity.getCheckPoints(c.x, c.y, c.radius, c.gunDir, c.radius);
					for (Point2D p:checkPoints) {
						if (character.getLoS().contains(p)) {
							Color l = new Color(arena.getLightAt(p));
							float br = Math.max(l.getRed(), Math.max(l.getGreen(), l.getBlue()))/255.0f;
							ex += Math.pow(br,1.2)/checkPoints.size();
						}
					}
					System.out.println(ex);
					if ((ex + alertness)>=1 && ex>0) {
						enemies.add(c);
						alertness = Math.min(1, alertness + ex*0.04);
						InterestPoint ip = new InterestPoint();
						ip.setLocation(new Point2D.Float(c.x,c.y));
						ip.setTime(time);
						ip.setType(Type.ENEMY);
						setNewIp(ip);
						alertness = 1;
					} else {
						alertness = Math.min(1, alertness + ex*0.02);
					}
					enemyInSight = true;
				} else if (c.team == character.team) {
					allies.add(c);
				}
			}
		}
		
		if (!enemyInSight) {
			alertness = Math.max(0, alertness - 0.003);
		}
	}
	
	public Behaviour getBehaviour() {
		return behaviour;
	}
	
	public double getAlertness() {
		return alertness;
	}
	
	public void setAlertness(double alertness) {
		this.alertness = alertness;
	}
	
	public void addAlertness(double alertness) {
		this.alertness += alertness;
	}
	
	public void update(WorldStatePacket wsp) {
		if (behaviour!=Behaviour.Dummy) {
			for (GameEvent e:wsp.events) {
				processEvent(e);
			}
			identifyCharacters(wsp);
			if (btExecutor!=null) {
				btExecutor.tick();
			}
		}
	}
	
	public void setPatrolLocations(List<Point2D> patrolLocations) {
		if (context!=null) {
			context.setVariable("patrolLocations", patrolLocations);
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
				double d = Point2D.distance(e.x, e.y, character.getX(), character.getY());
				if (d<1) {
					return;
				}
				for (CharData c:allies) {
					// or if it's an ally
					if (Point2D.distance(c.x,c.y,e.x,e.y)<1) {
						// no sweat
						return;
					}
				}
				
				double effect = Math.max(0.01,e.volume/50.0);
				// else
				if (e.volume>5 && alertness+effect >= 1) {
					ip = new InterestPoint();
					ip.setLocation(new Point2D.Float(e.x,e.y));
					ip.setTime(time);
					ip.setType(Type.ENEMY);
					alertness = 1;
				} else {
					// increase alertness
					alertness += effect;
					// register the event as an enemy location
				}
			} else if (e.id == SoundEvent.PING_SOUND_ID) {
				ip = new InterestPoint();
				ip.setLocation(new Point2D.Float(e.x,e.y));
				ip.setTime(time);
				ip.setType(Type.PING);
			}
		}
		if (ip!=null) {
			setNewIp(ip);
		}
	}
	
	public void setNewIp(InterestPoint ip) {
		List<Point2D> path = Searcher.searchPath(arena,character.getPosition(),ip.getLocation());
		if (path!=null) {
			Object curIp = context.getVariable("interest");
			// if it's even better than the current interest point
			if (curIp==null || compare(ip, (InterestPoint)curIp)>1) {
				// well, investigate it!
				context.setVariable("interest", ip);
				System.out.println("Set new interest at " + ip.getLocation() + ", "+ip.getType());
			}
		}
	}
	
	public static boolean isGunshotSound(int id) {
		return id>0 && id<30;
	}
	
	public static double estimateMaxPen(double bulletSpeed) {
		return bulletSpeed/0.25;
	}
	
	public static void moveTo(Point2D self, Point2D dest, InputPacket input) {
		final double COORD_THRESHOLD = 0.05;
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
	
	public static void moveCursorTo(Point2D dest, InputPacket input) {
		double d = Point2D.distance(input.cx, input.cy, dest.getX(), dest.getY());
		final double CURSOR_MOVE_RATE = 0.5;
		double ratio = Math.min(1,CURSOR_MOVE_RATE/d);
		if (ratio<1) {
			input.cx = (float) (input.cx + (dest.getX()-input.cx)*ratio);
			input.cy = (float) (input.cy + (dest.getY()-input.cy)*ratio);
		} else {
			input.cx = (float) dest.getX();
			input.cy = (float) dest.getY();
		}
	}	
	
	public static Point2D getClearAim(InputControlledEntity character, List<CharData> allies, List<CharData> enemies, Arena arena) {
		for (CharData enemy:enemies) {
			boolean blocked = false;
			Vector2D n = new Vector2D(enemy.y-character.getY(),character.getX()-enemy.x);
			n.setMagnitude(enemy.radius/2);
			
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
					double pen = estimateMaxPen(character.getWeapon().type.getProjectileSpeed());
					double CAST_DIST = 0.5;
					List<Point2D> samples = Geometry.getLineSamples(character.getX(),character.getY(),target.x,target.y, CAST_DIST);
					int blockCount = 0;
					for (Point2D point:samples) {
						Tile t = arena.getTileAt(point.getX(),point.getY());
						if (t.getCoverType()>0) {
							blockCount += t.getCoverType()*CAST_DIST/Terrain.tileSize;
						}
						if (blockCount>pen) {
							blocked = true;
							break;
						}
					}
				}
				
				if (!blocked) {
					return new Point2D.Double(target.x,target.y);
				}
			}
		}
		return null;
	}
	
	private static Point2D randomIntr(Arena arena, double x, double y, double direction, double distance) {
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
	
	public int compare(InterestPoint ip0, InterestPoint ip1) {
		double timeWeight = 0.001;
		double typeWeight = 10;
		double distanceWeight = 1;
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
		double distance0 = Searcher.getMoveDistance(arena,character.getPosition(),ip0.getLocation());
		double distance1 = Searcher.getMoveDistance(arena,character.getPosition(),ip1.getLocation());
		double dd = distance0 - distance1;
		double dty = ip0.getType().priority - ip1.getType().priority;
		
		return (int)Math.round(timeWeight*dt + distanceWeight*dd + typeWeight*dty);
	}

	public void reset() {
		context.clear();
		initContext();
		alertness = 0;
	}
}
