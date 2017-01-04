// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                MUST BE CAREFULLY COMPLETED              
//                                                         
//           ABSTRACT METHODS MUST BE IMPLEMENTED          
//                                                         
// Generated on 12/24/2016 11:57:23
// ******************************************************* 
package server.ai.jbt.actions.execution;

/** ExecutionAction class created from MMPM action Wait. */
public class Wait extends jbt.execution.task.leaf.action.ExecutionAction {
	/**
	 * Value of the parameter "milisecs" in case its value is specified at
	 * construction time. null otherwise.
	 */
	private java.lang.Integer milisecs;
	/**
	 * Location, in the context, of the parameter "milisecs" in case its value
	 * is not specified at construction time. null otherwise.
	 */
	private java.lang.String milisecsLoc;

	/**
	 * Constructor. Constructs an instance of Wait that is able to run a
	 * server.ai.jbt.actions.Wait.
	 * 
	 * @param milisecs
	 *            value of the parameter "milisecs", or null in case it should
	 *            be read from the context. If null,
	 *            <code>milisecsLoc<code> cannot be null.
	 * @param milisecsLoc
	 *            in case <code>milisecs</code> is null, this variable
	 *            represents the place in the context where the parameter's
	 *            value will be retrieved from.
	 */
	public Wait(server.ai.jbt.actions.Wait modelTask,
			jbt.execution.core.BTExecutor executor,
			jbt.execution.core.ExecutionTask parent,
			java.lang.Integer milisecs, java.lang.String milisecsLoc) {
		super(modelTask, executor, parent);

		this.milisecs = milisecs;
		this.milisecsLoc = milisecsLoc;
	}

	/**
	 * Returns the value of the parameter "milisecs", or null in case it has not
	 * been specified or it cannot be found in the context.
	 */
	public java.lang.Integer getMilisecs() {
		if (this.milisecs != null) {
			return this.milisecs;
		} else {
			return (java.lang.Integer) this.getContext().getVariable(
					this.milisecsLoc);
		}
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