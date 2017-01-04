// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                MUST BE CAREFULLY COMPLETED              
//                                                         
//           ABSTRACT METHODS MUST BE IMPLEMENTED          
//                                                         
// Generated on 12/23/2016 11:00:28
// ******************************************************* 
package server.ai.jbt.actions.execution;

import java.awt.geom.Point2D;

import server.character.InputControlledEntity;
import shared.network.GameDataPackets.InputPacket;

/** ExecutionAction class created from MMPM action PointCursor. */
public class PointCursor extends jbt.execution.task.leaf.action.ExecutionAction {
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
	 * Constructor. Constructs an instance of PointCursor that is able to run a
	 * server.ai.jbt.actions.PointCursor.
	 * 
	 * @param cursorTarget
	 *            value of the parameter "cursorTarget", or null in case it
	 *            should be read from the context. If null,
	 *            <code>cursorTargetLoc<code> cannot be null.
	 * @param cursorTargetLoc
	 *            in case <code>cursorTarget</code> is null, this variable
	 *            represents the place in the context where the parameter's
	 *            value will be retrieved from.
	 */
	public PointCursor(server.ai.jbt.actions.PointCursor modelTask,
			jbt.execution.core.BTExecutor executor,
			jbt.execution.core.ExecutionTask parent, float[] cursorTarget,
			java.lang.String cursorTargetLoc) {
		super(modelTask, executor, parent);

		this.cursorTarget = cursorTarget;
		this.cursorTargetLoc = cursorTargetLoc;
	}

	/**
	 * Returns the value of the parameter "cursorTarget", or null in case it has
	 * not been specified or it cannot be found in the context.
	 */
	public float[] getCursorTarget() {
		if (this.cursorTarget != null) {
			return this.cursorTarget;
		} else {
			return (float[]) this.getContext()
					.getVariable(this.cursorTargetLoc);
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
		// because you can't fail to move the cursor!
		InputPacket input =((InputControlledEntity)getContext().getVariable("Character")).getInput();
		double d = Point2D.distance(input.cx, input.cy, getCursorTarget()[0], getCursorTarget()[1]);
		final double CURSOR_MOVE_RATE = 0.5;
		double ratio = Math.min(1,CURSOR_MOVE_RATE/d);
		if (ratio<1) {
			input.cx = (float) (input.cx + (getCursorTarget()[0]-input.cx)*ratio);
			input.cy = (float) (input.cy + (getCursorTarget()[1]-input.cy)*ratio);
			return jbt.execution.core.ExecutionTask.Status.RUNNING;
		} else {
			input.cx = getCursorTarget()[0];
			input.cy = getCursorTarget()[1];
			return jbt.execution.core.ExecutionTask.Status.SUCCESS;
		}
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