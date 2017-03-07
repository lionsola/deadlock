// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                MUST BE CAREFULLY COMPLETED              
//                                                         
//           ABSTRACT METHODS MUST BE IMPLEMENTED          
//                                                         
// Generated on 02/04/2017 13:31:43
// ******************************************************* 
package server.ai.jbt.actions.execution;

import java.awt.geom.Point2D;
import java.util.List;

import server.ai.NPCBrain;
import server.ai.Searcher;
import server.character.InputControlledEntity;
import server.world.Arena;
import shared.network.GameDataPackets.InputPacket;

/** ExecutionAction class created from MMPM action Sneak. */
public class Sneak extends jbt.execution.task.leaf.action.ExecutionAction {
	/**
	 * Value of the parameter "sneakTarget" in case its value is specified at
	 * construction time. null otherwise.
	 */
	private float[] sneakTarget;
	/**
	 * Location, in the context, of the parameter "sneakTarget" in case its
	 * value is not specified at construction time. null otherwise.
	 */
	private java.lang.String sneakTargetLoc;
	/**
	 * Value of the parameter "controlCursor" in case its value is specified at
	 * construction time. null otherwise.
	 */
	private java.lang.Boolean controlCursor;
	/**
	 * Location, in the context, of the parameter "controlCursor" in case its
	 * value is not specified at construction time. null otherwise.
	 */
	private java.lang.String controlCursorLoc;
	private List<Point2D> path;

	/**
	 * Constructor. Constructs an instance of Sneak that is able to run a
	 * server.ai.jbt.actions.Sneak.
	 * 
	 * @param sneakTarget
	 *            value of the parameter "sneakTarget", or null in case it
	 *            should be read from the context. If null,
	 *            <code>sneakTargetLoc<code> cannot be null.
	 * @param sneakTargetLoc
	 *            in case <code>sneakTarget</code> is null, this variable
	 *            represents the place in the context where the parameter's
	 *            value will be retrieved from.
	 * @param controlCursor
	 *            value of the parameter "controlCursor", or null in case it
	 *            should be read from the context. If null,
	 *            <code>controlCursorLoc<code> cannot be null.
	 * @param controlCursorLoc
	 *            in case <code>controlCursor</code> is null, this variable
	 *            represents the place in the context where the parameter's
	 *            value will be retrieved from.
	 */
	public Sneak(server.ai.jbt.actions.Sneak modelTask,
			jbt.execution.core.BTExecutor executor,
			jbt.execution.core.ExecutionTask parent, float[] sneakTarget,
			java.lang.String sneakTargetLoc, java.lang.Boolean controlCursor,
			java.lang.String controlCursorLoc) {
		super(modelTask, executor, parent);

		this.sneakTarget = sneakTarget;
		this.sneakTargetLoc = sneakTargetLoc;
		this.controlCursor = controlCursor;
		this.controlCursorLoc = controlCursorLoc;
	}

	/**
	 * Returns the value of the parameter "sneakTarget", or null in case it has
	 * not been specified or it cannot be found in the context.
	 */
	public float[] getSneakTarget() {
		if (this.sneakTarget != null) {
			return this.sneakTarget;
		} else {
			return (float[]) this.getContext().getVariable(this.sneakTargetLoc);
		}
	}

	/**
	 * Returns the value of the parameter "controlCursor", or null in case it
	 * has not been specified or it cannot be found in the context.
	 */
	public java.lang.Boolean getControlCursor() {
		if (this.controlCursor != null) {
			return this.controlCursor;
		} else {
			return (java.lang.Boolean) this.getContext().getVariable(
					this.controlCursorLoc);
		}
	}

	protected void internalSpawn() {
		this.getExecutor().requestInsertionIntoList(
				jbt.execution.core.BTExecutor.BTExecutorList.TICKABLE, this);
		/* TODO: this method's implementation must be completed. */
		System.out.println(this.getClass().getCanonicalName() + " spawned");
		InputControlledEntity pc = (InputControlledEntity)getContext().getVariable("Character");
		float[] target = getSneakTarget();
		Arena a = (Arena)getContext().getVariable("Arena");
		path = Searcher.searchPath(a,pc.getX(), pc.getY(), target[0], target[1]);
	}

	protected jbt.execution.core.ExecutionTask.Status internalTick() {
		/*
		 * TODO: this method's implementation must be completed. This function
		 * should only return Status.SUCCESS, Status.FAILURE or Status.RUNNING.
		 * No other values are allowed.
		 */
		InputControlledEntity character = (InputControlledEntity)getContext().getVariable("Character");
		if (path==null) {
			return Status.FAILURE;
		}
		double distToCheckPoint = path.get(0).distance(character.getPosition());
		// if alr close to one checkpoint, move on
		
		final double PATH_THRESHOLD = 0.1;
		if (distToCheckPoint < PATH_THRESHOLD) {
			//System.out.println("AI " + id + " arrived at " + curPath.get(0));
			path.remove(0);
		}
		InputPacket input = character.getInput();
		// follow the next check point on the path
		if (path.isEmpty()) {
			input.up = false;
			input.down = false;
			input.left = false;
			input.right = false;
			input.sneaking = false;
			return Status.SUCCESS;
		} else {
			Point2D next = path.get(0);
			NPCBrain.moveTo(character.getPosition(), next, input);
			input.sneaking = true;
			if (getControlCursor()) {
				Point2D cursorTarget = null;
				if (path.size()>1) {
					cursorTarget = path.get(1);
				} else {
					cursorTarget = path.get(0);
				}
				
				NPCBrain.moveCursorTo(cursorTarget, input);
			}
			return Status.RUNNING;
		}
	}

	protected void internalTerminate() {
		InputControlledEntity character = (InputControlledEntity)getContext().getVariable("Character");
		InputPacket input = character.getInput();
		input.up = false;
		input.down = false;
		input.left = false;
		input.right = false;
		input.sneaking = false;
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