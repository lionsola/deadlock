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
import java.util.List;

import server.ai.PathFinder;
import server.ai.PathFinder.Path;
import server.character.InputControlledEntity;
import server.world.Arena;
import shared.network.GameDataPackets.InputPacket;

/** ExecutionAction class created from MMPM action Move. */
public class Move extends jbt.execution.task.leaf.action.ExecutionAction {
	private static final double COORD_THRESHOLD = 0.05;
	private static final double PATH_THRESHOLD = 0.1;
	/**
	 * Value of the parameter "moveTarget" in case its value is specified at
	 * construction time. null otherwise.
	 */
	private float[] moveTarget;
	/**
	 * Location, in the context, of the parameter "moveTarget" in case its value
	 * is not specified at construction time. null otherwise.
	 */
	private java.lang.String moveTargetLoc;

	private List<Point2D> path;
	
	/**
	 * Constructor. Constructs an instance of Move that is able to run a
	 * server.ai.jbt.actions.Move.
	 * 
	 * @param moveTarget
	 *            value of the parameter "moveTarget", or null in case it should
	 *            be read from the context. If null,
	 *            <code>moveTargetLoc<code> cannot be null.
	 * @param moveTargetLoc
	 *            in case <code>moveTarget</code> is null, this variable
	 *            represents the place in the context where the parameter's
	 *            value will be retrieved from.
	 */
	public Move(server.ai.jbt.actions.Move modelTask,
			jbt.execution.core.BTExecutor executor,
			jbt.execution.core.ExecutionTask parent, float[] moveTarget,
			java.lang.String moveTargetLoc) {
		super(modelTask, executor, parent);

		this.moveTarget = moveTarget;
		this.moveTargetLoc = moveTargetLoc;
	}

	/**
	 * Returns the value of the parameter "moveTarget", or null in case it has
	 * not been specified or it cannot be found in the context.
	 */
	public float[] getMoveTarget() {
		if (this.moveTarget != null) {
			return this.moveTarget;
		} else {
			return (float[]) this.getContext().getVariable(this.moveTargetLoc);
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
		InputControlledEntity pc = (InputControlledEntity)getContext().getVariable("Character");
		PathFinder pf = new PathFinder((Arena)getContext().getVariable("Arena"));
		float[] target = getMoveTarget();
		Path p = pf.findPath(pc.getX(), pc.getY(), target[0], target[1]);
		path = p.path;
	}

	protected jbt.execution.core.ExecutionTask.Status internalTick() {
		/*
		 * TODO: this method's implementation must be completed. This function
		 * should only return Status.SUCCESS, Status.FAILURE or Status.RUNNING.
		 * No other values are allowed.
		 */
		InputControlledEntity character = (InputControlledEntity)getContext().getVariable("Character");
		double distToCheckPoint = path.get(0).distance(character.getPosition());
		// if alr close to one checkpoint, move on
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
			return Status.SUCCESS;
		} else {
			Point2D next = path.get(0);
			if (character.getX() + COORD_THRESHOLD < next.getX()) {
				input.right = true;
			} else
				input.right = false;
			if (character.getX() - COORD_THRESHOLD > next.getX()) {
				input.left = true;
	
			} else
				input.left = false;
			if (character.getY() + COORD_THRESHOLD < next.getY()) {
				input.down = true;
			} else
				input.down = false;
			if (character.getY() - COORD_THRESHOLD > next.getY()) {
				input.up = true;
			} else
				input.up = false;
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