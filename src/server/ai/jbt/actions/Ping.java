// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                       DO NOT MODIFY                     
//                                                         
// Generated on 12/23/2016 11:00:28
// ******************************************************* 
package server.ai.jbt.actions;

/** ModelAction class created from MMPM action Ping. */
public class Ping extends jbt.model.task.leaf.action.ModelAction {

	/** Constructor. Constructs an instance of Ping. */
	public Ping(jbt.model.core.ModelTask guard) {
		super(guard);
	}

	/**
	 * Returns a server.ai.jbt.actions.execution.Ping task that is able to run
	 * this task.
	 */
	public jbt.execution.core.ExecutionTask createExecutor(
			jbt.execution.core.BTExecutor executor,
			jbt.execution.core.ExecutionTask parent) {
		return new server.ai.jbt.actions.execution.Ping(this, executor, parent);
	}
}