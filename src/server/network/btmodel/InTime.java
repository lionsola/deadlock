// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                       DO NOT MODIFY                     
//                                                         
// Generated on 01/07/2017 11:40:02
// ******************************************************* 
package server.network.btmodel;

/** ModelCondition class created from MMPM condition InTime. */
public class InTime extends jbt.model.task.leaf.condition.ModelCondition {
	/**
	 * Value of the parameter "time" in case its value is specified at
	 * construction time. null otherwise.
	 */
	private java.lang.Integer time;
	/**
	 * Location, in the context, of the parameter "time" in case its value is
	 * not specified at construction time. null otherwise.
	 */
	private java.lang.String timeLoc;

	/**
	 * Constructor. Constructs an instance of InTime.
	 * 
	 * @param time
	 *            value of the parameter "time", or null in case it should be
	 *            read from the context. If null, <code>timeLoc</code> cannot be
	 *            null.
	 * @param timeLoc
	 *            in case <code>time</code> is null, this variable represents
	 *            the place in the context where the parameter's value will be
	 *            retrieved from.
	 */
	public InTime(jbt.model.core.ModelTask guard, java.lang.Integer time,
			java.lang.String timeLoc) {
		super(guard);
		this.time = time;
		this.timeLoc = timeLoc;
	}

	/**
	 * Returns a server.network.btexecution.InTime task that is able to run this
	 * task.
	 */
	public jbt.execution.core.ExecutionTask createExecutor(
			jbt.execution.core.BTExecutor executor,
			jbt.execution.core.ExecutionTask parent) {
		return new server.network.btexecution.InTime(this, executor, parent,
				this.time, this.timeLoc);
	}
}