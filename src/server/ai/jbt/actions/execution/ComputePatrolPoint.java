// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                MUST BE CAREFULLY COMPLETED              
//                                                         
//           ABSTRACT METHODS MUST BE IMPLEMENTED          
//                                                         
// Generated on 02/03/2017 16:09:48
// ******************************************************* 
package server.ai.jbt.actions.execution;

import java.awt.geom.Point2D;
import java.util.List;

import server.character.InputControlledEntity;
import server.world.Utils;

/** ExecutionAction class created from MMPM action ComputePatrolPoint. */
public class ComputePatrolPoint extends
		jbt.execution.task.leaf.action.ExecutionAction {
	/**
	 * Value of the parameter "patrolLocations" in case its value is specified
	 * at construction time. null otherwise.
	 */
	private java.lang.Object patrolLocations;
	/**
	 * Location, in the context, of the parameter "patrolLocations" in case its
	 * value is not specified at construction time. null otherwise.
	 */
	private java.lang.String patrolLocationsLoc;
	/**
	 * Value of the parameter "random" in case its value is specified at
	 * construction time. null otherwise.
	 */
	private java.lang.Boolean random;
	/**
	 * Location, in the context, of the parameter "random" in case its value is
	 * not specified at construction time. null otherwise.
	 */
	private java.lang.String randomLoc;

	/**
	 * Constructor. Constructs an instance of ComputePatrolPoint that is able to
	 * run a server.ai.jbt.actions.ComputePatrolPoint.
	 * 
	 * @param patrolLocations
	 *            value of the parameter "patrolLocations", or null in case it
	 *            should be read from the context. If null,
	 *            <code>patrolLocationsLoc<code> cannot be null.
	 * @param patrolLocationsLoc
	 *            in case <code>patrolLocations</code> is null, this variable
	 *            represents the place in the context where the parameter's
	 *            value will be retrieved from.
	 * @param random
	 *            value of the parameter "random", or null in case it should be
	 *            read from the context. If null,
	 *            <code>randomLoc<code> cannot be null.
	 * @param randomLoc
	 *            in case <code>random</code> is null, this variable represents
	 *            the place in the context where the parameter's value will be
	 *            retrieved from.
	 */
	public ComputePatrolPoint(
			server.ai.jbt.actions.ComputePatrolPoint modelTask,
			jbt.execution.core.BTExecutor executor,
			jbt.execution.core.ExecutionTask parent,
			java.lang.Object patrolLocations,
			java.lang.String patrolLocationsLoc, java.lang.Boolean random,
			java.lang.String randomLoc) {
		super(modelTask, executor, parent);

		this.patrolLocations = patrolLocations;
		this.patrolLocationsLoc = patrolLocationsLoc;
		this.random = random;
		this.randomLoc = randomLoc;
	}

	/**
	 * Returns the value of the parameter "patrolLocations", or null in case it
	 * has not been specified or it cannot be found in the context.
	 */
	public java.lang.Object getPatrolLocations() {
		if (this.patrolLocations != null) {
			return this.patrolLocations;
		} else {
			return (java.lang.Object) this.getContext().getVariable(
					this.patrolLocationsLoc);
		}
	}

	/**
	 * Returns the value of the parameter "random", or null in case it has not
	 * been specified or it cannot be found in the context.
	 */
	public java.lang.Boolean getRandom() {
		if (this.random != null) {
			return this.random;
		} else {
			return (java.lang.Boolean) this.getContext().getVariable(
					this.randomLoc);
		}
	}

	@SuppressWarnings("unchecked")
	protected void internalSpawn() {
		/*
		 * Do not remove this first line unless you know what it does and you
		 * need not do it.
		 */
		this.getExecutor().requestInsertionIntoList(
				jbt.execution.core.BTExecutor.BTExecutorList.TICKABLE, this);
		
		System.out.println(this.getClass().getCanonicalName() + " spawned");
		InputControlledEntity pc = (InputControlledEntity)getContext().getVariable("Character");
		if (getPatrolLocations()!=null) {
			List<Point2D> patrolLocations = (List<Point2D>)getPatrolLocations();
		
			Object i = getContext().getVariable("patrolIndex");
			int currentIndex = i==null?0:((int)i);
			Point2D p = patrolLocations.get(currentIndex);
			
			if (p.distance(pc.getPosition())<0.2) {
				if (getRandom()) {
					int tempIndex = Utils.random().nextInt(patrolLocations.size()-1);
					currentIndex = tempIndex<currentIndex?tempIndex:tempIndex+1;
				} else {
					currentIndex = (currentIndex+1)%patrolLocations.size();
				}
				p = patrolLocations.get(currentIndex);
			}
			float[] patrolPoint = {(float)p.getX(),(float)p.getY()};
			getContext().setVariable("patrolLocation", patrolPoint);
			getContext().setVariable("patrolIndex", currentIndex);
		} else {
			float[] patrolPoint = {(float)pc.getX(),(float)pc.getY()};
			getContext().setVariable("patrolLocation", patrolPoint);
		}
	}

	protected jbt.execution.core.ExecutionTask.Status internalTick() {
		/*
		 * TODO: this method's implementation must be completed. This function
		 * should only return Status.SUCCESS, Status.FAILURE or Status.RUNNING.
		 * No other values are allowed.
		 */
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