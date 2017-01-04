// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                       DO NOT MODIFY                     
//                                                         
// Generated on 12/24/2016 13:40:18
// ******************************************************* 
package server.ai.jbt.actions;

/** ModelAction class created from MMPM action ComputeClosestSafePosition. */
public class ComputeClosestSafePosition extends
		jbt.model.task.leaf.action.ModelAction {

	/** Constructor. Constructs an instance of ComputeClosestSafePosition. */
	public ComputeClosestSafePosition(jbt.model.core.ModelTask guard) {
		super(guard);
	}

	/**
	 * Returns a server.ai.jbt.actions.execution.ComputeClosestSafePosition task
	 * that is able to run this task.
	 */
	public jbt.execution.core.ExecutionTask createExecutor(
			jbt.execution.core.BTExecutor executor,
			jbt.execution.core.ExecutionTask parent) {
		return new server.ai.jbt.actions.execution.ComputeClosestSafePosition(
				this, executor, parent);
	}
}