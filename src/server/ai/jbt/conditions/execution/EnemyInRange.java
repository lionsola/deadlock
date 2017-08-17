// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                MUST BE CAREFULLY COMPLETED              
//                                                         
//           ABSTRACT METHODS MUST BE IMPLEMENTED          
//                                                         
// Generated on 06/03/2017 14:31:18
// ******************************************************* 
package server.ai.jbt.conditions.execution;

import java.awt.geom.Point2D;
import java.util.List;

import server.character.InputControlledEntity;
import server.world.Arena;
import server.world.Geometry;
import shared.network.CharData;

/** ExecutionCondition class created from MMPM condition EnemyInRange. */
public class EnemyInRange extends
		jbt.execution.task.leaf.condition.ExecutionCondition {
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
	 * Constructor. Constructs an instance of EnemyInRange that is able to run a
	 * server.ai.jbt.conditions.EnemyInRange.
	 * 
	 * @param range
	 *            value of the parameter "range", or null in case it should be
	 *            read from the context. If null,
	 *            <code>rangeLoc<code> cannot be null.
	 * @param rangeLoc
	 *            in case <code>range</code> is null, this variable represents
	 *            the place in the context where the parameter's value will be
	 *            retrieved from.
	 */
	public EnemyInRange(server.ai.jbt.conditions.EnemyInRange modelTask,
			jbt.execution.core.BTExecutor executor,
			jbt.execution.core.ExecutionTask parent, java.lang.Float range,
			java.lang.String rangeLoc) {
		super(modelTask, executor, parent);

		this.range = range;
		this.rangeLoc = rangeLoc;
	}

	/**
	 * Returns the value of the parameter "range", or null in case it has not
	 * been specified or it cannot be found in the context.
	 */
	public java.lang.Float getRange() {
		if (this.range != null) {
			return this.range;
		} else {
			return (java.lang.Float) this.getContext().getVariable(
					this.rangeLoc);
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
		InputControlledEntity c = (InputControlledEntity) getContext().getVariable("Character");
		List<CharData> enemies = (List<CharData>) getContext().getVariable("Enemies");
		CharData e = enemies.get(0);
		if (c.getPosition().distance(e.x, e.y)>getRange()) {
			return Status.FAILURE;
		}
		Arena arena = (Arena) getContext().getVariable("Arena");
		List<Point2D> samples = Geometry.getLineSamples(c.getX(), c.getY(), e.x, e.y);
		for (Point2D p : samples) {
			if (!arena.getTileAt(p).isTraversable()) {
				return Status.FAILURE;
			}
		}
		
		return Status.SUCCESS;
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