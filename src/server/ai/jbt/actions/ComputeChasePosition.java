// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                       DO NOT MODIFY                     
//                                                         
// Generated on 01/18/2017 01:44:55
// ******************************************************* 
package server.ai.jbt.actions;

/** ModelAction class created from MMPM action ComputeChasePosition. */
public class ComputeChasePosition extends
		jbt.model.task.leaf.action.ModelAction {

	/** Constructor. Constructs an instance of ComputeChasePosition. */
	public ComputeChasePosition(jbt.model.core.ModelTask guard) {
		super(guard);
	}

	/**
	 * Returns a server.ai.jbt.actions.execution.ComputeChasePosition task that
	 * is able to run this task.
	 */
	public jbt.execution.core.ExecutionTask createExecutor(
			jbt.execution.core.BTExecutor executor,
			jbt.execution.core.ExecutionTask parent) {
		return new server.ai.jbt.actions.execution.ComputeChasePosition(this,
				executor, parent);
	}
}