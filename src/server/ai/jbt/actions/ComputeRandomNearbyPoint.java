// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                       DO NOT MODIFY                     
//                                                         
// Generated on 04/20/2017 22:15:34
// ******************************************************* 
package server.ai.jbt.actions;

/** ModelAction class created from MMPM action ComputeRandomNearbyPoint. */
public class ComputeRandomNearbyPoint extends
		jbt.model.task.leaf.action.ModelAction {

	/** Constructor. Constructs an instance of ComputeRandomNearbyPoint. */
	public ComputeRandomNearbyPoint(jbt.model.core.ModelTask guard) {
		super(guard);
	}

	/**
	 * Returns a server.ai.jbt.actions.execution.ComputeRandomNearbyPoint task
	 * that is able to run this task.
	 */
	public jbt.execution.core.ExecutionTask createExecutor(
			jbt.execution.core.BTExecutor executor,
			jbt.execution.core.ExecutionTask parent) {
		return new server.ai.jbt.actions.execution.ComputeRandomNearbyPoint(
				this, executor, parent);
	}
}