// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                MUST BE CAREFULLY COMPLETED              
//                                                         
//           ABSTRACT METHODS MUST BE IMPLEMENTED          
//                                                         
// Generated on 01/07/2017 11:40:02
// ******************************************************* 
package server.network.btexecution;

/** ExecutionCondition class created from MMPM condition InTime. */
public class InTime extends
		jbt.execution.task.leaf.condition.ExecutionCondition {
	/**
	 * Value of the parameter "time" in case its value is specified at
	 * construction time. null otherwise.
	 */
	private java.lang.Integer time;
	/**
	 * Location, in the context, of the parameter "time" in case its value is
	 * not specified at construction time. null otherwise.
	 */
	private java.lang.String timeLoc;

	/**
	 * Constructor. Constructs an instance of InTime that is able to run a
	 * server.network.btmodel.InTime.
	 * 
	 * @param time
	 *            value of the parameter "time", or null in case it should be
	 *            read from the context. If null,
	 *            <code>timeLoc<code> cannot be null.
	 * @param timeLoc
	 *            in case <code>time</code> is null, this variable represents
	 *            the place in the context where the parameter's value will be
	 *            retrieved from.
	 */
	public InTime(server.network.btmodel.InTime modelTask,
			jbt.execution.core.BTExecutor executor,
			jbt.execution.core.ExecutionTask parent, java.lang.Integer time,
			java.lang.String timeLoc) {
		super(modelTask, executor, parent);

		this.time = time;
		this.timeLoc = timeLoc;
	}

	/**
	 * Returns the value of the parameter "time", or null in case it has not
	 * been specified or it cannot be found in the context.
	 */
	public java.lang.Integer getTime() {
		if (this.time != null) {
			return this.time;
		} else {
			return (java.lang.Integer) this.getContext().getVariable(
					this.timeLoc);
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