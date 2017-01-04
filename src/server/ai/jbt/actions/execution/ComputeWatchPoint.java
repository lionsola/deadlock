// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                MUST BE CAREFULLY COMPLETED              
//                                                         
//           ABSTRACT METHODS MUST BE IMPLEMENTED          
//                                                         
// Generated on 01/01/2017 01:23:59
// ******************************************************* 
package server.ai.jbt.actions.execution;

import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

import server.character.Entity;
import server.world.Arena;
import server.world.Terrain;
import server.world.Utils;
import server.world.Visibility;

/** ExecutionAction class created from MMPM action ComputeWatchPoint. */
public class ComputeWatchPoint extends
		jbt.execution.task.leaf.action.ExecutionAction {

	/**
	 * Constructor. Constructs an instance of ComputeWatchPoint that is able to
	 * run a server.ai.jbt.actions.ComputeWatchPoint.
	 */
	public ComputeWatchPoint(server.ai.jbt.actions.ComputeWatchPoint modelTask,
			jbt.execution.core.BTExecutor executor,
			jbt.execution.core.ExecutionTask parent) {
		super(modelTask, executor, parent);

	}

	protected void internalSpawn() {
		/*
		 * Do not remove this first line unless you know what it does and you
		 * need not do it.
		 */
		this.getExecutor().requestInsertionIntoList(
				jbt.execution.core.BTExecutor.BTExecutorList.TICKABLE, this);
		System.out.println(this.getClass().getCanonicalName() + " spawned");
		Arena a = (Arena)getContext().getVariable("Arena");
		Entity e = (Entity)getContext().getVariable("Character");
		
		Area los = new Visibility().genLOSAreaMeter(e.getX(), e.getY(), e.getFovRange(), Math.PI*2, e.getDirection(), a);
		int range = (int) (e.getFovRange()/Terrain.tileSize);
		int tx = (int)(e.getX()/Terrain.tileSize);
		int ty = (int)(e.getY()/Terrain.tileSize);
		List<Point2D> targets = new LinkedList<Point2D>();
		for (int x = tx-range; x<= tx+range; x++) {
			for (int y = ty-range; y<= ty+range; y++) {
				Point2D p = new Point2D.Double(x*Terrain.tileSize, y*Terrain.tileSize);
				if (los.contains(p)) {
					targets.add(p);
				}
			}
		}
		Point2D t = targets.get(Utils.random().nextInt(targets.size()));
		float[] target = {(float)t.getX(),(float)t.getY()};
		getContext().setVariable("watchPoint", target);
	}

	protected jbt.execution.core.ExecutionTask.Status internalTick() {
		/*
		 * TODO: this method's implementation must be completed. This function
		 * should only return Status.SUCCESS, Status.FAILURE or Status.RUNNING.
		 * No other values are allowed.
		 */
		return jbt.execution.core.ExecutionTask.Status.SUCCESS;
	}

	protected void internalTerminate() {
		/* TODO: this method's implementation must be completed. */
	}

	protected void restoreState(jbt.execution.core.ITaskState state) {
		/* TODO: this method's implementation must be completed. */
	}

	protected jbt.execution.core.ITaskState storeState() {
		/* TODO: this method's implementation must be completed. */
		return null;
	}

	protected jbt.execution.core.ITaskState storeTerminationState() {
		/* TODO: this method's implementation must be completed. */
		return null;
	}
}