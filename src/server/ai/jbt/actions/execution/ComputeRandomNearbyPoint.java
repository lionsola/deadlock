// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                MUST BE CAREFULLY COMPLETED              
//                                                         
//           ABSTRACT METHODS MUST BE IMPLEMENTED          
//                                                         
// Generated on 04/20/2017 22:15:35
// ******************************************************* 
package server.ai.jbt.actions.execution;

import java.awt.geom.Point2D;

import server.ai.Searcher;
import server.character.Entity;
import server.world.Arena;
import server.world.Utils;

/** ExecutionAction class created from MMPM action ComputeRandomNearbyPoint. */
public class ComputeRandomNearbyPoint extends
		jbt.execution.task.leaf.action.ExecutionAction {

	/**
	 * Constructor. Constructs an instance of ComputeRandomNearbyPoint that is
	 * able to run a server.ai.jbt.actions.ComputeRandomNearbyPoint.
	 */
	public ComputeRandomNearbyPoint(
			server.ai.jbt.actions.ComputeRandomNearbyPoint modelTask,
			jbt.execution.core.BTExecutor executor,
			jbt.execution.core.ExecutionTask parent) {
		super(modelTask, executor, parent);

	}

	protected void internalSpawn() {
		this.getExecutor().requestInsertionIntoList(
				jbt.execution.core.BTExecutor.BTExecutorList.TICKABLE, this);
		System.out.println(this.getClass().getCanonicalName() + " spawned");
		Arena arena = (Arena)getContext().getVariable("Arena");
		Point2D self = ((Entity)getContext().getVariable("Character")).getPosition();
		Point2D point = Searcher.searchRandomPoint(arena, self, 5, Utils.random().nextDouble()*Math.PI*2);
		float[] p = {(float) point.getX(),(float) point.getY()};
		getContext().setVariable("randomPoint", p);
	}

	protected jbt.execution.core.ExecutionTask.Status internalTick() {
		return jbt.execution.core.ExecutionTask.Status.SUCCESS;
	}

	protected void internalTerminate() {
	}

	protected void restoreState(jbt.execution.core.ITaskState state) {
	}

	protected jbt.execution.core.ITaskState storeState() {
		return null;
	}

	protected jbt.execution.core.ITaskState storeTerminationState() {
		return null;
	}
}