// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                       DO NOT MODIFY                     
//                                                         
// Generated on 01/18/2017 16:00:02
// ******************************************************* 
package server.ai.jbt.actions;

/** ModelAction class created from MMPM action ClearVariable. */
public class ClearVariable extends jbt.model.task.leaf.action.ModelAction {
	/**
	 * Value of the parameter "variableName" in case its value is specified at
	 * construction time. null otherwise.
	 */
	private java.lang.String variableName;
	/**
	 * Location, in the context, of the parameter "variableName" in case its
	 * value is not specified at construction time. null otherwise.
	 */
	private java.lang.String variableNameLoc;

	/**
	 * Constructor. Constructs an instance of ClearVariable.
	 * 
	 * @param variableName
	 *            value of the parameter "variableName", or null in case it
	 *            should be read from the context. If null,
	 *            <code>variableNameLoc</code> cannot be null.
	 * @param variableNameLoc
	 *            in case <code>variableName</code> is null, this variable
	 *            represents the place in the context where the parameter's
	 *            value will be retrieved from.
	 */
	public ClearVariable(jbt.model.core.ModelTask guard,
			java.lang.String variableName, java.lang.String variableNameLoc) {
		super(guard);
		this.variableName = variableName;
		this.variableNameLoc = variableNameLoc;
	}

	/**
	 * Returns a server.ai.jbt.actions.execution.ClearVariable task that is able
	 * to run this task.
	 */
	public jbt.execution.core.ExecutionTask createExecutor(
			jbt.execution.core.BTExecutor executor,
			jbt.execution.core.ExecutionTask parent) {
		return new server.ai.jbt.actions.execution.ClearVariable(this,
				executor, parent, this.variableName, this.variableNameLoc);
	}
}