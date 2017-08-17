// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                MUST BE CAREFULLY COMPLETED              
//                                                         
//           ABSTRACT METHODS MUST BE IMPLEMENTED          
//                                                         
// Generated on 12/23/2016 11:00:28
// ******************************************************* 
package server.ai.jbt.actions.execution;

import server.character.InputControlledEntity;
import shared.network.GameDataPackets.InputPacket;

/** ExecutionAction class created from MMPM action UseAbility. */
public class UseAbility extends jbt.execution.task.leaf.action.ExecutionAction {

	/**
	 * Constructor. Constructs an instance of UseAbility that is able to run a
	 * server.ai.jbt.actions.UseAbility.
	 */
	public UseAbility(server.ai.jbt.actions.UseAbility modelTask,
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

	protected jbt.execution.core.ExecutionTask.Status internalTick() {
		InputControlledEntity pc = (InputControlledEntity)getContext().getVariable("Character");
		InputPacket input = ((InputPacket)getContext().getVariable("Input"));
		if (pc.getAbility().isReady()) {
			input.fire2 = true;
			//System.out.println("UseAbility running");
			return Status.RUNNING;
		} else if (input.fire2) {
			//System.out.println("UseAbility success");
			input.fire2 = false;
			return Status.SUCCESS;
		} else {
			//System.out.println("Fail to use ability");
			return Status.FAILURE;
		}
	}

	protected void internalTerminate() {
		((InputPacket)getContext().getVariable("Input")).fire2 = false;
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