// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                       DO NOT MODIFY                     
//                                                         
// Generated on 01/01/2017 01:23:59
// ******************************************************* 
package server.ai.jbt.actions;

/** ModelAction class created from MMPM action ComputeWatchPoint. */
public class ComputeWatchPoint extends jbt.model.task.leaf.action.ModelAction {

	/** Constructor. Constructs an instance of ComputeWatchPoint. */
	public ComputeWatchPoint(jbt.model.core.ModelTask guard) {
		super(guard);
	}

	/**
	 * Returns a server.ai.jbt.actions.execution.ComputeWatchPoint task that is
	 * able to run this task.
	 */
	public jbt.execution.core.ExecutionTask createExecutor(
			jbt.execution.core.BTExecutor executor,
			jbt.execution.core.ExecutionTask parent) {
		return new server.ai.jbt.actions.execution.ComputeWatchPoint(this,
				executor, parent);
	}
}