// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                       DO NOT MODIFY                     
//                                                         
// Generated on 12/24/2016 11:57:23
// ******************************************************* 
package server.ai.jbt.actions;

/** ModelAction class created from MMPM action Sneak. */
public class Sneak extends jbt.model.task.leaf.action.ModelAction {
	/**
	 * Value of the parameter "sneakTarget" in case its value is specified at
	 * construction time. null otherwise.
	 */
	private float[] sneakTarget;
	/**
	 * Location, in the context, of the parameter "sneakTarget" in case its
	 * value is not specified at construction time. null otherwise.
	 */
	private java.lang.String sneakTargetLoc;

	/**
	 * Constructor. Constructs an instance of Sneak.
	 * 
	 * @param sneakTarget
	 *            value of the parameter "sneakTarget", or null in case it
	 *            should be read from the context. If null,
	 *            <code>sneakTargetLoc</code> cannot be null.
	 * @param sneakTargetLoc
	 *            in case <code>sneakTarget</code> is null, this variable
	 *            represents the place in the context where the parameter's
	 *            value will be retrieved from.
	 */
	public Sneak(jbt.model.core.ModelTask guard, float[] sneakTarget,
			java.lang.String sneakTargetLoc) {
		super(guard);
		this.sneakTarget = sneakTarget;
		this.sneakTargetLoc = sneakTargetLoc;
	}

	/**
	 * Returns a server.ai.jbt.actions.execution.Sneak task that is able to run
	 * this task.
	 */
	public jbt.execution.core.ExecutionTask createExecutor(
			jbt.execution.core.BTExecutor executor,
			jbt.execution.core.ExecutionTask parent) {
		return new server.ai.jbt.actions.execution.Sneak(this, executor,
				parent, this.sneakTarget, this.sneakTargetLoc);
	}
}