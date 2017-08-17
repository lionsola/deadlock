// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                       DO NOT MODIFY                     
//                                                         
// Generated on 04/26/2017 11:40:51
// ******************************************************* 
package server.ai.jbt.actions;

/** ModelAction class created from MMPM action ComputeEscapePosition. */
public class ComputeEscapePosition extends
		jbt.model.task.leaf.action.ModelAction {

	/** Constructor. Constructs an instance of ComputeEscapePosition. */
	public ComputeEscapePosition(jbt.model.core.ModelTask guard) {
		super(guard);
	}

	/**
	 * Returns a server.ai.jbt.actions.execution.ComputeEscapePosition task that
	 * is able to run this task.
	 */
	public jbt.execution.core.ExecutionTask createExecutor(
			jbt.execution.core.BTExecutor executor,
			jbt.execution.core.ExecutionTask parent) {
		return new server.ai.jbt.actions.execution.ComputeEscapePosition(this,
				executor, parent);
	}
}