// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                       DO NOT MODIFY                     
//                                                         
// Generated on 12/23/2016 11:00:28
// ******************************************************* 
package server.ai.jbt.actions;

/** ModelAction class created from MMPM action ComputeNextPatrolPoint. */
public class ComputeNextPatrolPoint extends
		jbt.model.task.leaf.action.ModelAction {
	/**
	 * Value of the parameter "patrolLocations" in case its value is specified
	 * at construction time. null otherwise.
	 */
	private java.lang.Object patrolLocations;
	/**
	 * Location, in the context, of the parameter "patrolLocations" in case its
	 * value is not specified at construction time. null otherwise.
	 */
	private java.lang.String patrolLocationsLoc;

	/**
	 * Constructor. Constructs an instance of ComputeNextPatrolPoint.
	 * 
	 * @param patrolLocations
	 *            value of the parameter "patrolLocations", or null in case it
	 *            should be read from the context. If null,
	 *            <code>patrolLocationsLoc</code> cannot be null.
	 * @param patrolLocationsLoc
	 *            in case <code>patrolLocations</code> is null, this variable
	 *            represents the place in the context where the parameter's
	 *            value will be retrieved from.
	 */
	public ComputeNextPatrolPoint(jbt.model.core.ModelTask guard,
			java.lang.Object patrolLocations,
			java.lang.String patrolLocationsLoc) {
		super(guard);
		this.patrolLocations = patrolLocations;
		this.patrolLocationsLoc = patrolLocationsLoc;
	}

	/**
	 * Returns a server.ai.jbt.actions.execution.ComputeNextPatrolPoint task
	 * that is able to run this task.
	 */
	public jbt.execution.core.ExecutionTask createExecutor(
			jbt.execution.core.BTExecutor executor,
			jbt.execution.core.ExecutionTask parent) {
		return new server.ai.jbt.actions.execution.ComputeNextPatrolPoint(this,
				executor, parent, this.patrolLocations, this.patrolLocationsLoc);
	}
}