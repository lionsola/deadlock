// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                MUST BE CAREFULLY COMPLETED              
//                                                         
//           ABSTRACT METHODS MUST BE IMPLEMENTED          
//                                                         
// Generated on 12/24/2016 11:57:23
// ******************************************************* 
package server.ai.jbt.actions.execution;

/** ExecutionAction class created from MMPM action Sneak. */
public class Sneak extends jbt.execution.task.leaf.action.ExecutionAction {
	/**
	 * Value of the parameter "sneakTarget" in case its value is specified at
	 * construction time. null otherwise.
	 */
	private float[] sneakTarget;
	/**
	 * Location, in the context, of the parameter "sneakTarget" in case its
	 * value is not specified at construction time. null otherwise.
	 */
	private java.lang.String sneakTargetLoc;

	/**
	 * Constructor. Constructs an instance of Sneak that is able to run a
	 * server.ai.jbt.actions.Sneak.
	 * 
	 * @param sneakTarget
	 *            value of the parameter "sneakTarget", or null in case it
	 *            should be read from the context. If null,
	 *            <code>sneakTargetLoc<code> cannot be null.
	 * @param sneakTargetLoc
	 *            in case <code>sneakTarget</code> is null, this variable
	 *            represents the place in the context where the parameter's
	 *            value will be retrieved from.
	 */
	public Sneak(server.ai.jbt.actions.Sneak modelTask,
			jbt.execution.core.BTExecutor executor,
			jbt.execution.core.ExecutionTask parent, float[] sneakTarget,
			java.lang.String sneakTargetLoc) {
		super(modelTask, executor, parent);

		this.sneakTarget = sneakTarget;
		this.sneakTargetLoc = sneakTargetLoc;
	}

	/**
	 * Returns the value of the parameter "sneakTarget", or null in case it has
	 * not been specified or it cannot be found in the context.
	 */
	public float[] getSneakTarget() {
		if (this.sneakTarget != null) {
			return this.sneakTarget;
		} else {
			return (float[]) this.getContext().getVariable(this.sneakTargetLoc);
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