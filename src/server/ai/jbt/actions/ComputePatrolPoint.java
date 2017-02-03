// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                       DO NOT MODIFY                     
//                                                         
// Generated on 02/03/2017 16:09:46
// ******************************************************* 
package server.ai.jbt.actions;

/** ModelAction class created from MMPM action ComputePatrolPoint. */
public class ComputePatrolPoint extends jbt.model.task.leaf.action.ModelAction {
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
	 * Value of the parameter "random" in case its value is specified at
	 * construction time. null otherwise.
	 */
	private java.lang.Boolean random;
	/**
	 * Location, in the context, of the parameter "random" in case its value is
	 * not specified at construction time. null otherwise.
	 */
	private java.lang.String randomLoc;

	/**
	 * Constructor. Constructs an instance of ComputePatrolPoint.
	 * 
	 * @param patrolLocations
	 *            value of the parameter "patrolLocations", or null in case it
	 *            should be read from the context. If null,
	 *            <code>patrolLocationsLoc</code> cannot be null.
	 * @param patrolLocationsLoc
	 *            in case <code>patrolLocations</code> is null, this variable
	 *            represents the place in the context where the parameter's
	 *            value will be retrieved from.
	 * @param random
	 *            value of the parameter "random", or null in case it should be
	 *            read from the context. If null, <code>randomLoc</code> cannot
	 *            be null.
	 * @param randomLoc
	 *            in case <code>random</code> is null, this variable represents
	 *            the place in the context where the parameter's value will be
	 *            retrieved from.
	 */
	public ComputePatrolPoint(jbt.model.core.ModelTask guard,
			java.lang.Object patrolLocations,
			java.lang.String patrolLocationsLoc, java.lang.Boolean random,
			java.lang.String randomLoc) {
		super(guard);
		this.patrolLocations = patrolLocations;
		this.patrolLocationsLoc = patrolLocationsLoc;
		this.random = random;
		this.randomLoc = randomLoc;
	}

	/**
	 * Returns a server.ai.jbt.actions.execution.ComputePatrolPoint task that is
	 * able to run this task.
	 */
	public jbt.execution.core.ExecutionTask createExecutor(
			jbt.execution.core.BTExecutor executor,
			jbt.execution.core.ExecutionTask parent) {
		return new server.ai.jbt.actions.execution.ComputePatrolPoint(this,
				executor, parent, this.patrolLocations,
				this.patrolLocationsLoc, this.random, this.randomLoc);
	}
}