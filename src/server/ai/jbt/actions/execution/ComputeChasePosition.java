// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                MUST BE CAREFULLY COMPLETED              
//                                                         
//           ABSTRACT METHODS MUST BE IMPLEMENTED          
//                                                         
// Generated on 01/18/2017 01:44:56
// ******************************************************* 
package server.ai.jbt.actions.execution;

import java.awt.geom.Point2D;
import java.util.List;

import server.ai.NPCBrain;
import server.ai.Searcher;
import server.character.InputControlledEntity;
import server.world.Arena;
import shared.network.CharData;

/** ExecutionAction class created from MMPM action ComputeChasePosition. */
public class ComputeChasePosition extends
		jbt.execution.task.leaf.action.ExecutionAction {

	/**
	 * Constructor. Constructs an instance of ComputeChasePosition that is able
	 * to run a server.ai.jbt.actions.ComputeChasePosition.
	 */
	public ComputeChasePosition(
			server.ai.jbt.actions.ComputeChasePosition modelTask,
			jbt.execution.core.BTExecutor executor,
			jbt.execution.core.ExecutionTask parent) {
		super(modelTask, executor, parent);
	}

	protected void internalSpawn() {
		/*
		 * Do not remove this first line unless you know what it does and you
		 * need not do it.
		 */
		this.getExecutor().requestInsertionIntoList(
				jbt.execution.core.BTExecutor.BTExecutorList.TICKABLE, this);
		// this method's implementation must be completed.
		System.out.println(this.getClass().getCanonicalName() + " spawned");
		
	}

	protected jbt.execution.core.ExecutionTask.Status internalTick() {
		List<CharData> enemies = (List<CharData>) getContext().getVariable("Enemies");
		InputControlledEntity player = (InputControlledEntity)getContext().getVariable("Character");
		Arena arena = (Arena)getContext().getVariable("Arena");
		
		// choose an enemy
		CharData e = enemies.get(0);
		double pen = NPCBrain.estimateMaxPen(player.getWeapon().type.getProjectileSpeed());
		double range = 5;
		
		float[] d = new float[2];
		if (player.getWeapon().type.weaponType==3) {
			d[0] = e.x;
			d[1] = e.y;
			getContext().setVariable("chaseTarget", d);
			return jbt.execution.core.ExecutionTask.Status.SUCCESS;
		} else {
			Point2D dest = Searcher.searchAttackStandPoint(arena, player.getPosition(), new Point2D.Float(e.x,e.y), range, pen);
			if (dest!=null) {
				d[0] = (float)dest.getX();
				d[1] = (float)dest.getY();
				getContext().setVariable("chaseTarget", d);
				return jbt.execution.core.ExecutionTask.Status.SUCCESS;
			} else {
				return jbt.execution.core.ExecutionTask.Status.FAILURE;
			}
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