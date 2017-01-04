// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                       DO NOT MODIFY                     
//                                                         
// Generated on 12/23/2016 11:00:28
// ******************************************************* 
package server.ai.jbt.actions;

/** ModelAction class created from MMPM action Move. */
public class Move extends jbt.model.task.leaf.action.ModelAction {
	/**
	 * Value of the parameter "moveTarget" in case its value is specified at
	 * construction time. null otherwise.
	 */
	private float[] moveTarget;
	/**
	 * Location, in the context, of the parameter "moveTarget" in case its value
	 * is not specified at construction time. null otherwise.
	 */
	private java.lang.String moveTargetLoc;

	/**
	 * Constructor. Constructs an instance of Move.
	 * 
	 * @param moveTarget
	 *            value of the parameter "moveTarget", or null in case it should
	 *            be read from the context. If null, <code>moveTargetLoc</code>
	 *            cannot be null.
	 * @param moveTargetLoc
	 *            in case <code>moveTarget</code> is null, this variable
	 *            represents the place in the context where the parameter's
	 *            value will be retrieved from.
	 */
	public Move(jbt.model.core.ModelTask guard, float[] moveTarget,
			java.lang.String moveTargetLoc) {
		super(guard);
		this.moveTarget = moveTarget;
		this.moveTargetLoc = moveTargetLoc;
	}

	/**
	 * Returns a server.ai.jbt.actions.execution.Move task that is able to run
	 * this task.
	 */
	public jbt.execution.core.ExecutionTask createExecutor(
			jbt.execution.core.BTExecutor executor,
			jbt.execution.core.ExecutionTask parent) {
		return new server.ai.jbt.actions.execution.Move(this, executor, parent,
				this.moveTarget, this.moveTargetLoc);
	}
}