// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                MUST BE CAREFULLY COMPLETED              
//                                                         
//           ABSTRACT METHODS MUST BE IMPLEMENTED          
//                                                         
// Generated on 12/24/2016 13:40:18
// ******************************************************* 
package server.ai.jbt.actions.execution;

import java.awt.geom.Point2D;
import java.util.List;

import server.character.InputControlledEntity;
import server.world.Arena;
import server.world.Geometry;
import server.world.Utils;
import shared.network.CharData;

/** ExecutionAction class created from MMPM action ComputeClosestSafePosition. */
public class ComputeClosestSafePosition extends
		jbt.execution.task.leaf.action.ExecutionAction {

	/**
	 * Constructor. Constructs an instance of ComputeClosestSafePosition that is
	 * able to run a server.ai.jbt.actions.ComputeClosestSafePosition.
	 */
	public ComputeClosestSafePosition(
			server.ai.jbt.actions.ComputeClosestSafePosition modelTask,
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
		/* TODO: this method's implementation must be completed. */
		System.out.println(this.getClass().getCanonicalName() + " spawned");
		
		List<CharData> enemies = (List<CharData>) getContext().getVariable("Enemies");
		Arena a = (Arena) getContext().getVariable("Arena");
		InputControlledEntity p = (InputControlledEntity) getContext().getVariable("Character");
		int tx = Utils.meterToTile(p.getX());
		int ty = Utils.meterToTile(p.getY());
		Point2D best = null;
		double bestPoint = 0;
		for (int x=tx-5;x<tx+5;x++) {
			for (int y=ty-5;y<ty+5;y++) {
				Point2D meter = Utils.tileToMeter(x, y);
				int coverCount = 0;
				//boolean 
				for (CharData e:enemies) {
					// cast a line from this position to the enemy
					List<Point2D> points = Geometry.getLineSamples(e.x,e.y,meter.getX(),meter.getY(),1);
					for (Point2D castPoint:points) {
						coverCount += a.getTileAt(castPoint.getX(), castPoint.getY()).getCoverType();
					}
				}
				
				// take the one that's most secluded?
			}
		}
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