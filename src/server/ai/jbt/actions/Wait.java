// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                       DO NOT MODIFY                     
//                                                         
// Generated on 12/24/2016 11:57:23
// ******************************************************* 
package server.ai.jbt.actions;

/** ModelAction class created from MMPM action Wait. */
public class Wait extends jbt.model.task.leaf.action.ModelAction {
	/**
	 * Value of the parameter "milisecs" in case its value is specified at
	 * construction time. null otherwise.
	 */
	private java.lang.Integer milisecs;
	/**
	 * Location, in the context, of the parameter "milisecs" in case its value
	 * is not specified at construction time. null otherwise.
	 */
	private java.lang.String milisecsLoc;

	/**
	 * Constructor. Constructs an instance of Wait.
	 * 
	 * @param milisecs
	 *            value of the parameter "milisecs", or null in case it should
	 *            be read from the context. If null, <code>milisecsLoc</code>
	 *            cannot be null.
	 * @param milisecsLoc
	 *            in case <code>milisecs</code> is null, this variable
	 *            represents the place in the context where the parameter's
	 *            value will be retrieved from.
	 */
	public Wait(jbt.model.core.ModelTask guard, java.lang.Integer milisecs,
			java.lang.String milisecsLoc) {
		super(guard);
		this.milisecs = milisecs;
		this.milisecsLoc = milisecsLoc;
	}

	/**
	 * Returns a server.ai.jbt.actions.execution.Wait task that is able to run
	 * this task.
	 */
	public jbt.execution.core.ExecutionTask createExecutor(
			jbt.execution.core.BTExecutor executor,
			jbt.execution.core.ExecutionTask parent) {
		return new server.ai.jbt.actions.execution.Wait(this, executor, parent,
				this.milisecs, this.milisecsLoc);
	}
}