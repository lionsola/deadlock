// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                MUST BE CAREFULLY COMPLETED              
//                                                         
//           ABSTRACT METHODS MUST BE IMPLEMENTED          
//                                                         
// Generated on 01/18/2017 16:00:03
// ******************************************************* 
package server.ai.jbt.actions.execution;

/** ExecutionAction class created from MMPM action ClearVariable. */
public class ClearVariable extends
		jbt.execution.task.leaf.action.ExecutionAction {
	/**
	 * Value of the parameter "variableName" in case its value is specified at
	 * construction time. null otherwise.
	 */
	private java.lang.String variableName;
	/**
	 * Location, in the context, of the parameter "variableName" in case its
	 * value is not specified at construction time. null otherwise.
	 */
	private java.lang.String variableNameLoc;

	/**
	 * Constructor. Constructs an instance of ClearVariable that is able to run
	 * a server.ai.jbt.actions.ClearVariable.
	 * 
	 * @param variableName
	 *            value of the parameter "variableName", or null in case it
	 *            should be read from the context. If null,
	 *            <code>variableNameLoc<code> cannot be null.
	 * @param variableNameLoc
	 *            in case <code>variableName</code> is null, this variable
	 *            represents the place in the context where the parameter's
	 *            value will be retrieved from.
	 */
	public ClearVariable(server.ai.jbt.actions.ClearVariable modelTask,
			jbt.execution.core.BTExecutor executor,
			jbt.execution.core.ExecutionTask parent,
			java.lang.String variableName, java.lang.String variableNameLoc) {
		super(modelTask, executor, parent);

		this.variableName = variableName;
		this.variableNameLoc = variableNameLoc;
	}

	/**
	 * Returns the value of the parameter "variableName", or null in case it has
	 * not been specified or it cannot be found in the context.
	 */
	public java.lang.String getVariableName() {
		if (this.variableName != null) {
			return this.variableName;
		} else {
			return (java.lang.String) this.getContext().getVariable(
					this.variableNameLoc);
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
		getContext().clearVariable(getVariableName());
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