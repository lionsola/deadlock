// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                MUST BE CAREFULLY COMPLETED              
//                                                         
//           ABSTRACT METHODS MUST BE IMPLEMENTED          
//                                                         
// Generated on 01/07/2017 11:40:02
// ******************************************************* 
package server.network.btexecution;

/** ExecutionCondition class created from MMPM condition StayAt. */
public class StayAt extends
		jbt.execution.task.leaf.condition.ExecutionCondition {
	/**
	 * Value of the parameter "location" in case its value is specified at
	 * construction time. null otherwise.
	 */
	private float[] location;
	/**
	 * Location, in the context, of the parameter "location" in case its value
	 * is not specified at construction time. null otherwise.
	 */
	private java.lang.String locationLoc;

	/**
	 * Constructor. Constructs an instance of StayAt that is able to run a
	 * server.network.btmodel.StayAt.
	 * 
	 * @param location
	 *            value of the parameter "location", or null in case it should
	 *            be read from the context. If null,
	 *            <code>locationLoc<code> cannot be null.
	 * @param locationLoc
	 *            in case <code>location</code> is null, this variable
	 *            represents the place in the context where the parameter's
	 *            value will be retrieved from.
	 */
	public StayAt(server.network.btmodel.StayAt modelTask,
			jbt.execution.core.BTExecutor executor,
			jbt.execution.core.ExecutionTask parent, float[] location,
			java.lang.String locationLoc) {
		super(modelTask, executor, parent);

		this.location = location;
		this.locationLoc = locationLoc;
	}

	/**
	 * Returns the value of the parameter "location", or null in case it has not
	 * been specified or it cannot be found in the context.
	 */
	public float[] getLocation() {
		if (this.location != null) {
			return this.location;
		} else {
			return (float[]) this.getContext().getVariable(this.locationLoc);
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