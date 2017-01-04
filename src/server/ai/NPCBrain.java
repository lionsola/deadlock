package server.ai;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import editor.SpawnPoint.Behaviour;
import jbt.execution.core.BTExecutorFactory;
import jbt.execution.core.ContextFactory;
import jbt.execution.core.IBTExecutor;
import jbt.execution.core.IBTLibrary;
import jbt.execution.core.IContext;
import jbt.model.core.ModelTask;
import server.ai.InterestPoint.Type;
import server.ai.jbt.library.StandardBTLibrary;
import server.character.InputControlledEntity;
import server.world.Arena;
import server.world.Geometry;
import server.world.Tile;
import shared.core.Vector2D;
import shared.network.CharData;
import shared.network.GameDataPackets.WorldStatePacket;

public class NPCBrain extends Brain {
	float PATROL_THRESHOLD = 0.1f;
	List<Point2D> patrolLocations = new ArrayList<Point2D>();
	int current = 0;
	int mode;
	IContext context;
	IBTExecutor btExecutor;
	List<CharData> enemies = new LinkedList<CharData>();
	List<CharData> allies = new LinkedList<CharData>();
	double alertness;
	
	public NPCBrain(Behaviour behaviour) {
		if (behaviour!=Behaviour.Dummy) {
			/* First of all, we create the BT library. */
			IBTLibrary btLibrary = new StandardBTLibrary();
			/* Then we create the initial context that the tree will use. */
			context = ContextFactory.createContext(btLibrary);
			
			/* Now we get the Model BT to run. */
			ModelTask patrolTree = btLibrary.getBT("NPCPatroller");
			/* Then we create the BT Executor to run the tree. */
			btExecutor = BTExecutorFactory.createBTExecutor(patrolTree, context);
		}
	}
	
	@Override
	public void init(Arena arena, InputControlledEntity pc, PathFinder pathFinder) {
		super.init(arena, pc, pathFinder);
		if (context!=null) {
			context.setVariable("Arena", arena);
			context.setVariable("Character", pc);
			context.setVariable("Input", pc.getInput());
			context.setVariable("Enemies", enemies);
			context.setVariable("Allies", allies);
		}
	}
	
	protected void identifyCharacters(WorldStatePacket wsp) {
		enemies.clear();
		allies.clear();
		
		// identify an enemy in sight
		boolean enemyInSight = false;
		for (CharData c : wsp.characters) {
			if (c.healthPoints>0) {
				if (c.team != character.team) {
					if ((c.exposure + alertness)>=1) {
						enemies.add(c);
						alertness = Math.min(1, alertness + c.exposure*0.02);
					} else {
						alertness = Math.min(1, alertness + c.exposure*0.02);
					}
					enemyInSight = true;
				} else if (c.team == character.team) {
					allies.add(c);
				}
			}
		}
		
		if (!enemyInSight) {
			alertness = Math.max(0, alertness - 0.01);
		}
	}
	
	public double getAlertness() {
		return alertness;
	}
	
	@Override
	public void update(WorldStatePacket wsp) {
		identifyCharacters(wsp);
		if (btExecutor!=null) {
			btExecutor.tick();
		}
	}
	
	@Override
	protected AIState decideAction() {
		AIState state = super.decideAction();
		if (state==AIState.EXPLORING && !patrolLocations.isEmpty()) {
			state = AIState.PATROLING;
		}
		return state;
	}

	public void setPatrolLocations(List<Point2D> patrolLocations) {
		this.patrolLocations = patrolLocations;
		if (context!=null) {
			context.setVariable("patrolLocations", patrolLocations);
		}
	}
	
	public static Point2D getClearAim(InputControlledEntity character, List<CharData> allies, List<CharData> enemies, Arena arena) {
		for (CharData enemy:enemies) {
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
					int maxBlock = (int)(character.getWeapon().type.projectileSpeed/0.1);
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
		}
		return null;
	}
}
