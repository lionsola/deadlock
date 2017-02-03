// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                       DO NOT MODIFY                     
//                                                         
// Generated on 01/31/2017 09:23:28
// ******************************************************* 
package server.ai.jbt.actions;

/** ModelAction class created from MMPM action ComputeTarget. */
public class ComputeTarget extends jbt.model.task.leaf.action.ModelAction {

	/** Constructor. Constructs an instance of ComputeTarget. */
	public ComputeTarget(jbt.model.core.ModelTask guard) {
		super(guard);
	}

	/**
	 * Returns a server.ai.jbt.actions.execution.ComputeTarget task that is able
	 * to run this task.
	 */
	public jbt.execution.core.ExecutionTask createExecutor(
			jbt.execution.core.BTExecutor executor,
			jbt.execution.core.ExecutionTask parent) {
		return new server.ai.jbt.actions.execution.ComputeTarget(this,
				executor, parent);
	}
}