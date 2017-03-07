// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                       DO NOT MODIFY                     
//                                                         
// Generated on 02/04/2017 13:31:43
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
	 * Value of the parameter "controlCursor" in case its value is specified at
	 * construction time. null otherwise.
	 */
	private java.lang.Boolean controlCursor;
	/**
	 * Location, in the context, of the parameter "controlCursor" in case its
	 * value is not specified at construction time. null otherwise.
	 */
	private java.lang.String controlCursorLoc;

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
	 * @param controlCursor
	 *            value of the parameter "controlCursor", or null in case it
	 *            should be read from the context. If null,
	 *            <code>controlCursorLoc</code> cannot be null.
	 * @param controlCursorLoc
	 *            in case <code>controlCursor</code> is null, this variable
	 *            represents the place in the context where the parameter's
	 *            value will be retrieved from.
	 */
	public Sneak(jbt.model.core.ModelTask guard, float[] sneakTarget,
			java.lang.String sneakTargetLoc, java.lang.Boolean controlCursor,
			java.lang.String controlCursorLoc) {
		super(guard);
		this.sneakTarget = sneakTarget;
		this.sneakTargetLoc = sneakTargetLoc;
		this.controlCursor = controlCursor;
		this.controlCursorLoc = controlCursorLoc;
	}

	/**
	 * Returns a server.ai.jbt.actions.execution.Sneak task that is able to run
	 * this task.
	 */
	public jbt.execution.core.ExecutionTask createExecutor(
			jbt.execution.core.BTExecutor executor,
			jbt.execution.core.ExecutionTask parent) {
		return new server.ai.jbt.actions.execution.Sneak(this, executor,
				parent, this.sneakTarget, this.sneakTargetLoc,
				this.controlCursor, this.controlCursorLoc);
	}
}