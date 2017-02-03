// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                       DO NOT MODIFY                     
//                                                         
// Generated on 01/31/2017 09:23:28
// ******************************************************* 
package server.ai.jbt.conditions;

/** ModelCondition class created from MMPM condition TargetOutOfRange. */
public class TargetOutOfRange extends
		jbt.model.task.leaf.condition.ModelCondition {
	/**
	 * Value of the parameter "target" in case its value is specified at
	 * construction time. null otherwise.
	 */
	private java.lang.Object target;
	/**
	 * Location, in the context, of the parameter "target" in case its value is
	 * not specified at construction time. null otherwise.
	 */
	private java.lang.String targetLoc;
	/**
	 * Value of the parameter "tileDistance" in case its value is specified at
	 * construction time. null otherwise.
	 */
	private java.lang.Integer tileDistance;
	/**
	 * Location, in the context, of the parameter "tileDistance" in case its
	 * value is not specified at construction time. null otherwise.
	 */
	private java.lang.String tileDistanceLoc;

	/**
	 * Constructor. Constructs an instance of TargetOutOfRange.
	 * 
	 * @param target
	 *            value of the parameter "target", or null in case it should be
	 *            read from the context. If null, <code>targetLoc</code> cannot
	 *            be null.
	 * @param targetLoc
	 *            in case <code>target</code> is null, this variable represents
	 *            the place in the context where the parameter's value will be
	 *            retrieved from.
	 * @param tileDistance
	 *            value of the parameter "tileDistance", or null in case it
	 *            should be read from the context. If null,
	 *            <code>tileDistanceLoc</code> cannot be null.
	 * @param tileDistanceLoc
	 *            in case <code>tileDistance</code> is null, this variable
	 *            represents the place in the context where the parameter's
	 *            value will be retrieved from.
	 */
	public TargetOutOfRange(jbt.model.core.ModelTask guard,
			java.lang.Object target, java.lang.String targetLoc,
			java.lang.Integer tileDistance, java.lang.String tileDistanceLoc) {
		super(guard);
		this.target = target;
		this.targetLoc = targetLoc;
		this.tileDistance = tileDistance;
		this.tileDistanceLoc = tileDistanceLoc;
	}

	/**
	 * Returns a server.ai.jbt.conditions.execution.TargetOutOfRange task that
	 * is able to run this task.
	 */
	public jbt.execution.core.ExecutionTask createExecutor(
			jbt.execution.core.BTExecutor executor,
			jbt.execution.core.ExecutionTask parent) {
		return new server.ai.jbt.conditions.execution.TargetOutOfRange(this,
				executor, parent, this.target, this.targetLoc,
				this.tileDistance, this.tileDistanceLoc);
	}
}