// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                       DO NOT MODIFY                     
//                                                         
// Generated on 01/18/2017 16:59:23
// ******************************************************* 
package server.ai.jbt.actions;

/** ModelAction class created from MMPM action Run. */
public class Run extends jbt.model.task.leaf.action.ModelAction {
	/**
	 * Value of the parameter "runTarget" in case its value is specified at
	 * construction time. null otherwise.
	 */
	private float[] runTarget;
	/**
	 * Location, in the context, of the parameter "runTarget" in case its value
	 * is not specified at construction time. null otherwise.
	 */
	private java.lang.String runTargetLoc;
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
	 * Constructor. Constructs an instance of Run.
	 * 
	 * @param runTarget
	 *            value of the parameter "runTarget", or null in case it should
	 *            be read from the context. If null, <code>runTargetLoc</code>
	 *            cannot be null.
	 * @param runTargetLoc
	 *            in case <code>runTarget</code> is null, this variable
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
	public Run(jbt.model.core.ModelTask guard, float[] runTarget,
			java.lang.String runTargetLoc, java.lang.Boolean controlCursor,
			java.lang.String controlCursorLoc) {
		super(guard);
		this.runTarget = runTarget;
		this.runTargetLoc = runTargetLoc;
		this.controlCursor = controlCursor;
		this.controlCursorLoc = controlCursorLoc;
	}

	/**
	 * Returns a server.ai.jbt.actions.execution.Run task that is able to run
	 * this task.
	 */
	public jbt.execution.core.ExecutionTask createExecutor(
			jbt.execution.core.BTExecutor executor,
			jbt.execution.core.ExecutionTask parent) {
		return new server.ai.jbt.actions.execution.Run(this, executor, parent,
				this.runTarget, this.runTargetLoc, this.controlCursor,
				this.controlCursorLoc);
	}
}