// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                MUST BE CAREFULLY COMPLETED              
//                                                         
//           ABSTRACT METHODS MUST BE IMPLEMENTED          
//                                                         
// Generated on 12/23/2016 11:00:27
// ******************************************************* 
package server.ai.jbt.actions.execution;

import server.character.InputControlledEntity;
import shared.network.GameDataPackets.InputPacket;

/** ExecutionAction class created from MMPM action FireWeapon. */
public class FireWeapon extends jbt.execution.task.leaf.action.ExecutionAction {

	/**
	 * Constructor. Constructs an instance of FireWeapon that is able to run a
	 * server.ai.jbt.actions.FireWeapon.
	 */
	public FireWeapon(server.ai.jbt.actions.FireWeapon modelTask,
			jbt.execution.core.BTExecutor executor,
			jbt.execution.core.ExecutionTask parent) {
		super(modelTask, executor, parent);

	}

	protected void internalSpawn() {
		this.getExecutor().requestInsertionIntoList(
				jbt.execution.core.BTExecutor.BTExecutorList.TICKABLE, this);
		/* TODO: this method's implementation must be completed. */
		System.out.println(this.getClass().getCanonicalName() + " spawned");
	}

	protected jbt.execution.core.ExecutionTask.Status internalTick() {
		InputControlledEntity pc = (InputControlledEntity)getContext().getVariable("Character");
		if (pc.getWeapon().isReady()) {
			((InputPacket)getContext().getVariable("Input")).fire1 = true;
			return Status.RUNNING;
		} else {
			((InputPacket)getContext().getVariable("Input")).fire1 = false;
			if (getStatus()==Status.RUNNING) {
				return Status.SUCCESS;
			} else {
				return Status.FAILURE;
			}
		}
	}

	protected void internalTerminate() {
		((InputPacket)getContext().getVariable("Input")).fire1 = false;
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