// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                MUST BE CAREFULLY COMPLETED              
//                                                         
//           ABSTRACT METHODS MUST BE IMPLEMENTED          
//                                                         
// Generated on 01/18/2017 15:54:41
// ******************************************************* 
package server.ai.jbt.actions.execution;

import java.awt.geom.Point2D;

import server.ai.InterestPoint;
import server.ai.Searcher;
import server.character.InputControlledEntity;
import server.world.Arena;

/** ExecutionAction class created from MMPM action ComputeInterestPoint. */
public class ComputeInterestPoint extends
		jbt.execution.task.leaf.action.ExecutionAction {

	/**
	 * Constructor. Constructs an instance of ComputeInterestPoint that is able
	 * to run a server.ai.jbt.actions.ComputeInterestPoint.
	 */
	public ComputeInterestPoint(
			server.ai.jbt.actions.ComputeInterestPoint modelTask,
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
		InterestPoint ip = (InterestPoint)getContext().getVariable("interest");
		float[] ipCoord = {(float)ip.getLocation().getX(),(float)ip.getLocation().getY()};
		getContext().setVariable("interestPoint", ipCoord);
		
		InputControlledEntity player = (InputControlledEntity)getContext().getVariable("Character");
		Arena arena = (Arena)getContext().getVariable("Arena");
		Point2D dest = Searcher.searchCheckStandPoint(arena, player.getPosition(), ip.getLocation(), 3);
		float[] d = new float[2];
		if (dest!=null) {
			d[0] = (float)dest.getX();
			d[1] = (float)dest.getY();
		} else {
			d = ipCoord;
		}
		getContext().setVariable("interestStandPoint", d);
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