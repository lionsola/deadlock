// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                       DO NOT MODIFY                     
//                                                         
// Generated on 06/03/2017 14:31:17
// ******************************************************* 
package server.ai.jbt.conditions;

/** ModelCondition class created from MMPM condition EnemyInRange. */
public class EnemyInRange extends jbt.model.task.leaf.condition.ModelCondition {
	/**
	 * Value of the parameter "range" in case its value is specified at
	 * construction time. null otherwise.
	 */
	private java.lang.Float range;
	/**
	 * Location, in the context, of the parameter "range" in case its value is
	 * not specified at construction time. null otherwise.
	 */
	private java.lang.String rangeLoc;

	/**
	 * Constructor. Constructs an instance of EnemyInRange.
	 * 
	 * @param range
	 *            value of the parameter "range", or null in case it should be
	 *            read from the context. If null, <code>rangeLoc</code> cannot
	 *            be null.
	 * @param rangeLoc
	 *            in case <code>range</code> is null, this variable represents
	 *            the place in the context where the parameter's value will be
	 *            retrieved from.
	 */
	public EnemyInRange(jbt.model.core.ModelTask guard, java.lang.Float range,
			java.lang.String rangeLoc) {
		super(guard);
		this.range = range;
		this.rangeLoc = rangeLoc;
	}

	/**
	 * Returns a server.ai.jbt.conditions.execution.EnemyInRange task that is
	 * able to run this task.
	 */
	public jbt.execution.core.ExecutionTask createExecutor(
			jbt.execution.core.BTExecutor executor,
			jbt.execution.core.ExecutionTask parent) {
		return new server.ai.jbt.conditions.execution.EnemyInRange(this,
				executor, parent, this.range, this.rangeLoc);
	}
}