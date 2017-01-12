// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                       DO NOT MODIFY                     
//                                                         
// Generated on 01/07/2017 11:40:02
// ******************************************************* 
package server.network.btmodel;

/** ModelCondition class created from MMPM condition Survive. */
public class Survive extends jbt.model.task.leaf.condition.ModelCondition {

	/** Constructor. Constructs an instance of Survive. */
	public Survive(jbt.model.core.ModelTask guard) {
		super(guard);
	}

	/**
	 * Returns a server.network.btexecution.Survive task that is able to run
	 * this task.
	 */
	public jbt.execution.core.ExecutionTask createExecutor(
			jbt.execution.core.BTExecutor executor,
			jbt.execution.core.ExecutionTask parent) {
		return new server.network.btexecution.Survive(this, executor, parent);
	}
}