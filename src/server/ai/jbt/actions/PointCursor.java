// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                       DO NOT MODIFY                     
//                                                         
// Generated on 12/23/2016 11:00:28
// ******************************************************* 
package server.ai.jbt.actions;

/** ModelAction class created from MMPM action PointCursor. */
public class PointCursor extends jbt.model.task.leaf.action.ModelAction {
	/**
	 * Value of the parameter "cursorTarget" in case its value is specified at
	 * construction time. null otherwise.
	 */
	private float[] cursorTarget;
	/**
	 * Location, in the context, of the parameter "cursorTarget" in case its
	 * value is not specified at construction time. null otherwise.
	 */
	private java.lang.String cursorTargetLoc;

	/**
	 * Constructor. Constructs an instance of PointCursor.
	 * 
	 * @param cursorTarget
	 *            value of the parameter "cursorTarget", or null in case it
	 *            should be read from the context. If null,
	 *            <code>cursorTargetLoc</code> cannot be null.
	 * @param cursorTargetLoc
	 *            in case <code>cursorTarget</code> is null, this variable
	 *            represents the place in the context where the parameter's
	 *            value will be retrieved from.
	 */
	public PointCursor(jbt.model.core.ModelTask guard, float[] cursorTarget,
			java.lang.String cursorTargetLoc) {
		super(guard);
		this.cursorTarget = cursorTarget;
		this.cursorTargetLoc = cursorTargetLoc;
	}

	/**
	 * Returns a server.ai.jbt.actions.execution.PointCursor task that is able
	 * to run this task.
	 */
	public jbt.execution.core.ExecutionTask createExecutor(
			jbt.execution.core.BTExecutor executor,
			jbt.execution.core.ExecutionTask parent) {
		return new server.ai.jbt.actions.execution.PointCursor(this, executor,
				parent, this.cursorTarget, this.cursorTargetLoc);
	}
}