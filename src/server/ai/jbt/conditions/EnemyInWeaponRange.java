// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                       DO NOT MODIFY                     
//                                                         
// Generated on 02/09/2017 10:40:42
// ******************************************************* 
package server.ai.jbt.conditions;

/** ModelCondition class created from MMPM condition EnemyInWeaponRange. */
public class EnemyInWeaponRange extends
		jbt.model.task.leaf.condition.ModelCondition {

	/** Constructor. Constructs an instance of EnemyInWeaponRange. */
	public EnemyInWeaponRange(jbt.model.core.ModelTask guard) {
		super(guard);
	}

	/**
	 * Returns a server.ai.jbt.conditions.execution.EnemyInWeaponRange task that
	 * is able to run this task.
	 */
	public jbt.execution.core.ExecutionTask createExecutor(
			jbt.execution.core.BTExecutor executor,
			jbt.execution.core.ExecutionTask parent) {
		return new server.ai.jbt.conditions.execution.EnemyInWeaponRange(this,
				executor, parent);
	}
}