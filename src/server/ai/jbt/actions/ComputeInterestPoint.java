// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                       DO NOT MODIFY                     
//                                                         
// Generated on 01/18/2017 15:54:41
// ******************************************************* 
package server.ai.jbt.actions;

/** ModelAction class created from MMPM action ComputeInterestPoint. */
public class ComputeInterestPoint extends
		jbt.model.task.leaf.action.ModelAction {

	/** Constructor. Constructs an instance of ComputeInterestPoint. */
	public ComputeInterestPoint(jbt.model.core.ModelTask guard) {
		super(guard);
	}

	/**
	 * Returns a server.ai.jbt.actions.execution.ComputeInterestPoint task that
	 * is able to run this task.
	 */
	public jbt.execution.core.ExecutionTask createExecutor(
			jbt.execution.core.BTExecutor executor,
			jbt.execution.core.ExecutionTask parent) {
		return new server.ai.jbt.actions.execution.ComputeInterestPoint(this,
				executor, parent);
	}
}