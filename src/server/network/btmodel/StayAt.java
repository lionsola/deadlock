// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                       DO NOT MODIFY                     
//                                                         
// Generated on 01/07/2017 11:40:02
// ******************************************************* 
package server.network.btmodel;

/** ModelCondition class created from MMPM condition StayAt. */
public class StayAt extends jbt.model.task.leaf.condition.ModelCondition {
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
	 * Constructor. Constructs an instance of StayAt.
	 * 
	 * @param location
	 *            value of the parameter "location", or null in case it should
	 *            be read from the context. If null, <code>locationLoc</code>
	 *            cannot be null.
	 * @param locationLoc
	 *            in case <code>location</code> is null, this variable
	 *            represents the place in the context where the parameter's
	 *            value will be retrieved from.
	 */
	public StayAt(jbt.model.core.ModelTask guard, float[] location,
			java.lang.String locationLoc) {
		super(guard);
		this.location = location;
		this.locationLoc = locationLoc;
	}

	/**
	 * Returns a server.network.btexecution.StayAt task that is able to run this
	 * task.
	 */
	public jbt.execution.core.ExecutionTask createExecutor(
			jbt.execution.core.BTExecutor executor,
			jbt.execution.core.ExecutionTask parent) {
		return new server.network.btexecution.StayAt(this, executor, parent,
				this.location, this.locationLoc);
	}
}