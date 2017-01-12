// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                       DO NOT MODIFY                     
//                                                         
// Generated on 01/07/2017 11:40:01
// ******************************************************* 
package server.network.btmodel;

/** ModelCondition class created from MMPM condition ReachTarget. */
public class ReachTarget extends jbt.model.task.leaf.condition.ModelCondition {
	/**
	 * Value of the parameter "targetLocation" in case its value is specified at
	 * construction time. null otherwise.
	 */
	private float[] targetLocation;
	/**
	 * Location, in the context, of the parameter "targetLocation" in case its
	 * value is not specified at construction time. null otherwise.
	 */
	private java.lang.String targetLocationLoc;

	/**
	 * Constructor. Constructs an instance of ReachTarget.
	 * 
	 * @param targetLocation
	 *            value of the parameter "targetLocation", or null in case it
	 *            should be read from the context. If null,
	 *            <code>targetLocationLoc</code> cannot be null.
	 * @param targetLocationLoc
	 *            in case <code>targetLocation</code> is null, this variable
	 *            represents the place in the context where the parameter's
	 *            value will be retrieved from.
	 */
	public ReachTarget(jbt.model.core.ModelTask guard, float[] targetLocation,
			java.lang.String targetLocationLoc) {
		super(guard);
		this.targetLocation = targetLocation;
		this.targetLocationLoc = targetLocationLoc;
	}

	/**
	 * Returns a server.network.btexecution.ReachTarget task that is able to run
	 * this task.
	 */
	public jbt.execution.core.ExecutionTask createExecutor(
			jbt.execution.core.BTExecutor executor,
			jbt.execution.core.ExecutionTask parent) {
		return new server.network.btexecution.ReachTarget(this, executor,
				parent, this.targetLocation, this.targetLocationLoc);
	}
}