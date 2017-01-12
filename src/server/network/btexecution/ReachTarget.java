// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                MUST BE CAREFULLY COMPLETED              
//                                                         
//           ABSTRACT METHODS MUST BE IMPLEMENTED          
//                                                         
// Generated on 01/07/2017 11:40:02
// ******************************************************* 
package server.network.btexecution;

import java.awt.geom.Point2D;

import server.network.MissionServer;
import server.network.ServerPlayer;
import server.world.Utils;

/** ExecutionCondition class created from MMPM condition ReachTarget. */
public class ReachTarget extends
		jbt.execution.task.leaf.condition.ExecutionCondition {
	/**
	 * Value of the parameter "targetLocation" in case its value is specified at
	 * construction time. null otherwise.
	 */
	private float[] targetLocation;
	/**
	 * Location, in the context, of the parameter "targetLocation" in case its
	 * value is not specified at construction time. null otherwise.
	 */
	private java.lang.String targetLocationLoc;

	/**
	 * Constructor. Constructs an instance of ReachTarget that is able to run a
	 * server.network.btmodel.ReachTarget.
	 * 
	 * @param targetLocation
	 *            value of the parameter "targetLocation", or null in case it
	 *            should be read from the context. If null,
	 *            <code>targetLocationLoc<code> cannot be null.
	 * @param targetLocationLoc
	 *            in case <code>targetLocation</code> is null, this variable
	 *            represents the place in the context where the parameter's
	 *            value will be retrieved from.
	 */
	public ReachTarget(server.network.btmodel.ReachTarget modelTask,
			jbt.execution.core.BTExecutor executor,
			jbt.execution.core.ExecutionTask parent, float[] targetLocation,
			java.lang.String targetLocationLoc) {
		super(modelTask, executor, parent);

		this.targetLocation = targetLocation;
		this.targetLocationLoc = targetLocationLoc;
	}

	/**
	 * Returns the value of the parameter "targetLocation", or null in case it
	 * has not been specified or it cannot be found in the context.
	 */
	public float[] getTargetLocation() {
		if (this.targetLocation != null) {
			return this.targetLocation;
		} else {
			return (float[]) this.getContext().getVariable(
					this.targetLocationLoc);
		}
	}

	protected void internalSpawn() {
		/*
		 * Do not remove this first line unless you know what it does and you
		 * need not do it.
		 */
		this.getExecutor().requestInsertionIntoList(
				jbt.execution.core.BTExecutor.BTExecutorList.TICKABLE, this);
		/* TODO: this method's implementation must be completed. */
		System.out.println(this.getClass().getCanonicalName() + " spawned");
	}

	protected jbt.execution.core.ExecutionTask.Status internalTick() {
		MissionServer server = (MissionServer)getContext().getVariable("MissionServer");
		float[] t = getTargetLocation();
		for (ServerPlayer sp:server.getPlayers()) {
			Point2D p = sp.character.getPosition();
			if (sp.team==0 && (t[0]!=Utils.meterToTile(p.getX()) || t[1]!=Utils.meterToTile(p.getY()))) {
				return Status.RUNNING;
			}
		}
		return jbt.execution.core.ExecutionTask.Status.SUCCESS;
	}

	protected void internalTerminate() {
		/* TODO: this method's implementation must be completed. */
	}

	protected void restoreState(jbt.execution.core.ITaskState state) {
		/* TODO: this method's implementation must be completed. */
	}

	protected jbt.execution.core.ITaskState storeState() {
		/* TODO: this method's implementation must be completed. */
		return null;
	}

	protected jbt.execution.core.ITaskState storeTerminationState() {
		/* TODO: this method's implementation must be completed. */
		return null;
	}
}