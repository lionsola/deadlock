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
		List<CharData> enemies = (List<CharData>) getContext().getVariable("Enemies");
		InputControlledEntity player = (InputControlledEntity)getContext().getVariable("Character");
		Arena arena = (Arena)getContext().getVariable("Arena");
		
		// choose an enemy
		CharData e = enemies.get(0);
		double pen = NPCBrain.estimateMaxPen(player.getWeapon().type.projectileSpeed);
		double range = 5;
		if (player.getWeapon().type.weaponType==3) {
			range = player.getWeapon().type.length-0.1;
		}
		Point2D dest = Searcher.searchAttackStandPoint(arena, player.getPosition(), new Point2D.Float(e.x,e.y), range, pen);
		float[] d = new float[2];
		if (dest!=null) {
			d[0] = (float)dest.getX();
			d[1] = (float)dest.getY();
		} else {
			d[0] = e.x;
			d[1] = e.y;
		}
		getContext().setVariable("chaseTarget", d);		
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