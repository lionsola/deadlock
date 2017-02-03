// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                MUST BE CAREFULLY COMPLETED              
//                                                         
//           ABSTRACT METHODS MUST BE IMPLEMENTED          
//                                                         
// Generated on 01/31/2017 09:23:28
// ******************************************************* 
package server.ai.jbt.conditions.execution;

/** ExecutionCondition class created from MMPM condition TargetOutOfRange. */
public class TargetOutOfRange extends
		jbt.execution.task.leaf.condition.ExecutionCondition {
	/**
	 * Value of the parameter "target" in case its value is specified at
	 * construction time. null otherwise.
	 */
	private java.lang.Object target;
	/**
	 * Location, in the context, of the parameter "target" in case its value is
	 * not specified at construction time. null otherwise.
	 */
	private java.lang.String targetLoc;
	/**
	 * Value of the parameter "tileDistance" in case its value is specified at
	 * construction time. null otherwise.
	 */
	private java.lang.Integer tileDistance;
	/**
	 * Location, in the context, of the parameter "tileDistance" in case its
	 * value is not specified at construction time. null otherwise.
	 */
	private java.lang.String tileDistanceLoc;

	/**
	 * Constructor. Constructs an instance of TargetOutOfRange that is able to
	 * run a server.ai.jbt.conditions.TargetOutOfRange.
	 * 
	 * @param target
	 *            value of the parameter "target", or null in case it should be
	 *            read from the context. If null,
	 *            <code>targetLoc<code> cannot be null.
	 * @param targetLoc
	 *            in case <code>target</code> is null, this variable represents
	 *            the place in the context where the parameter's value will be
	 *            retrieved from.
	 * @param tileDistance
	 *            value of the parameter "tileDistance", or null in case it
	 *            should be read from the context. If null,
	 *            <code>tileDistanceLoc<code> cannot be null.
	 * @param tileDistanceLoc
	 *            in case <code>tileDistance</code> is null, this variable
	 *            represents the place in the context where the parameter's
	 *            value will be retrieved from.
	 */
	public TargetOutOfRange(
			server.ai.jbt.conditions.TargetOutOfRange modelTask,
			jbt.execution.core.BTExecutor executor,
			jbt.execution.core.ExecutionTask parent, java.lang.Object target,
			java.lang.String targetLoc, java.lang.Integer tileDistance,
			java.lang.String tileDistanceLoc) {
		super(modelTask, executor, parent);

		this.target = target;
		this.targetLoc = targetLoc;
		this.tileDistance = tileDistance;
		this.tileDistanceLoc = tileDistanceLoc;
	}

	/**
	 * Returns the value of the parameter "target", or null in case it has not
	 * been specified or it cannot be found in the context.
	 */
	public java.lang.Object getTarget() {
		if (this.target != null) {
			return this.target;
		} else {
			return (java.lang.Object) this.getContext().getVariable(
					this.targetLoc);
		}
	}

	/**
	 * Returns the value of the parameter "tileDistance", or null in case it has
	 * not been specified or it cannot be found in the context.
	 */
	public java.lang.Integer getTileDistance() {
		if (this.tileDistance != null) {
			return this.tileDistance;
		} else {
			return (java.lang.Integer) this.getContext().getVariable(
					this.tileDistanceLoc);
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