// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                       DO NOT MODIFY                     
//                                                         
// Generated on 12/23/2016 11:00:28
// ******************************************************* 
package server.ai.jbt.conditions;

/** ModelCondition class created from MMPM condition WeaponReady. */
public class WeaponReady extends jbt.model.task.leaf.condition.ModelCondition {

	/** Constructor. Constructs an instance of WeaponReady. */
	public WeaponReady(jbt.model.core.ModelTask guard) {
		super(guard);
	}

	/**
	 * Returns a server.ai.jbt.conditions.execution.WeaponReady task that is
	 * able to run this task.
	 */
	public jbt.execution.core.ExecutionTask createExecutor(
			jbt.execution.core.BTExecutor executor,
			jbt.execution.core.ExecutionTask parent) {
		return new server.ai.jbt.conditions.execution.WeaponReady(this,
				executor, parent);
	}
}