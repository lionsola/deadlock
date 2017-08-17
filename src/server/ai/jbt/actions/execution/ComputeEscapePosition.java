// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                MUST BE CAREFULLY COMPLETED              
//                                                         
//           ABSTRACT METHODS MUST BE IMPLEMENTED          
//                                                         
// Generated on 04/26/2017 11:40:52
// ******************************************************* 
package server.ai.jbt.actions.execution;

import java.awt.geom.Point2D;
import java.util.List;

import server.ai.Searcher;
import server.character.InputControlledEntity;
import server.world.Arena;
import shared.network.CharData;

/** ExecutionAction class created from MMPM action ComputeEscapePosition. */
public class ComputeEscapePosition extends
		jbt.execution.task.leaf.action.ExecutionAction {

	/**
	 * Constructor. Constructs an instance of ComputeEscapePosition that is able
	 * to run a server.ai.jbt.actions.ComputeEscapePosition.
	 */
	public ComputeEscapePosition(
			server.ai.jbt.actions.ComputeEscapePosition modelTask,
			jbt.execution.core.BTExecutor executor,
			jbt.execution.core.ExecutionTask parent) {
		super(modelTask, executor, parent);
	}

	protected void internalSpawn() {
		this.getExecutor().requestInsertionIntoList(
				jbt.execution.core.BTExecutor.BTExecutorList.TICKABLE, this);
		/* TODO: this method's implementation must be completed. */
		System.out.println(this.getClass().getCanonicalName() + " spawned");
	}

	protected jbt.execution.core.ExecutionTask.Status internalTick() {
		List<CharData> enemies = (List<CharData>) getContext().getVariable("Enemies");
		InputControlledEntity player = (InputControlledEntity)getContext().getVariable("Character");
		Arena arena = (Arena)getContext().getVariable("Arena");
		
		if (!enemies.isEmpty()) {
			// choose an enemy
			CharData e = enemies.get(0);
			
			Point2D dest = Searcher.searchEscapePoint(arena, player.getPosition(), new Point2D.Float(e.x,e.y));
			float[] d = new float[2];
			if (dest!=null) {
				d[0] = (float)dest.getX();
				d[1] = (float)dest.getY();
				getContext().setVariable("escapeTarget", d);
				return jbt.execution.core.ExecutionTask.Status.SUCCESS;
			} else {
				return jbt.execution.core.ExecutionTask.Status.FAILURE;
			}
		} else {
			return jbt.execution.core.ExecutionTask.Status.FAILURE;
		}
	}

	protected void internalTerminate() {
	}

	protected void restoreState(jbt.execution.core.ITaskState state) {
	}

	protected jbt.execution.core.ITaskState storeState() {
		return null;
	}

	protected jbt.execution.core.ITaskState storeTerminationState() {
		return null;
	}
}