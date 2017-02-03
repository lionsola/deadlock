// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                       DO NOT MODIFY                     
//                                                         
// Generated on 01/18/2017 14:49:56
// ******************************************************* 
package server.ai.jbt.conditions;

/** ModelCondition class created from MMPM condition SomethingNeedChecking. */
public class SomethingNeedChecking extends
		jbt.model.task.leaf.condition.ModelCondition {

	/** Constructor. Constructs an instance of SomethingNeedChecking. */
	public SomethingNeedChecking(jbt.model.core.ModelTask guard) {
		super(guard);
	}

	/**
	 * Returns a server.ai.jbt.conditions.execution.SomethingNeedChecking task
	 * that is able to run this task.
	 */
	public jbt.execution.core.ExecutionTask createExecutor(
			jbt.execution.core.BTExecutor executor,
			jbt.execution.core.ExecutionTask parent) {
		return new server.ai.jbt.conditions.execution.SomethingNeedChecking(
				this, executor, parent);
	}
}