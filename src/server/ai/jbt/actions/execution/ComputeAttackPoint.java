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

import server.ai.NPCBrain;
import server.character.InputControlledEntity;
import server.world.Arena;
import shared.network.CharData;

/** ExecutionAction class created from MMPM action ComputeAttackPoint. */
public class ComputeAttackPoint extends
		jbt.execution.task.leaf.action.ExecutionAction {

	/**
	 * Constructor. Constructs an instance of ComputeAttackPoint that is able to
	 * run a server.ai.jbt.actions.ComputeAttackPoint.
	 */
	public ComputeAttackPoint(
			server.ai.jbt.actions.ComputeAttackPoint modelTask,
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
	}

	@SuppressWarnings("unchecked")
	protected jbt.execution.core.ExecutionTask.Status internalTick() {
		/*
		 * TODO: this method's implementation must be completed. This function
		 * should only return Status.SUCCESS, Status.FAILURE or Status.RUNNING.
		 * No other values are allowed.
		 */
		List<CharData> allies = (List<CharData>) getContext().getVariable("Allies");
		List<CharData> enemies = (List<CharData>) getContext().getVariable("Enemies");
		Arena arena = (Arena) getContext().getVariable("Arena");
		InputControlledEntity character = (InputControlledEntity) getContext().getVariable("Character");
		Point2D aim = NPCBrain.getClearAim(character, allies, enemies, arena);
		if (aim!=null) {
			float[] point = {(float)aim.getX(),(float)aim.getY()};
			getContext().setVariable("Aim", point);
			return Status.SUCCESS;
		} else {
			return Status.FAILURE;
		}
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