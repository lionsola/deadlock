// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                MUST BE CAREFULLY COMPLETED              
//                                                         
//           ABSTRACT METHODS MUST BE IMPLEMENTED          
//                                                         
// Generated on 01/18/2017 14:49:56
// ******************************************************* 
package server.ai.jbt.conditions.execution;

/** ExecutionCondition class created from MMPM condition SomethingNeedChecking. */
public class SomethingNeedChecking extends
		jbt.execution.task.leaf.condition.ExecutionCondition {

	/**
	 * Constructor. Constructs an instance of SomethingNeedChecking that is able
	 * to run a server.ai.jbt.conditions.SomethingNeedChecking.
	 */
	public SomethingNeedChecking(
			server.ai.jbt.conditions.SomethingNeedChecking modelTask,
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
	}

	protected jbt.execution.core.ExecutionTask.Status internalTick() {
		/*
		 * TODO: this method's implementation must be completed. This function
		 * should only return Status.SUCCESS, Status.FAILURE or Status.RUNNING.
		 * No other values are allowed.
		 */
		if (getContext().getVariable("interest")!=null) {
			System.out.println("Something needs to get checked!");
			return jbt.execution.core.ExecutionTask.Status.SUCCESS;
		} else {
			return jbt.execution.core.ExecutionTask.Status.FAILURE;
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