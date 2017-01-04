// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                       DO NOT MODIFY                     
//                                                         
// Generated on 12/23/2016 11:00:27
// ******************************************************* 
package server.ai.jbt.actions;

/** ModelAction class created from MMPM action FireWeapon. */
public class FireWeapon extends jbt.model.task.leaf.action.ModelAction {

	/** Constructor. Constructs an instance of FireWeapon. */
	public FireWeapon(jbt.model.core.ModelTask guard) {
		super(guard);
	}

	/**
	 * Returns a server.ai.jbt.actions.execution.FireWeapon task that is able to
	 * run this task.
	 */
	public jbt.execution.core.ExecutionTask createExecutor(
			jbt.execution.core.BTExecutor executor,
			jbt.execution.core.ExecutionTask parent) {
		return new server.ai.jbt.actions.execution.FireWeapon(this, executor,
				parent);
	}
}