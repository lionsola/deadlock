// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                MUST BE CAREFULLY COMPLETED              
//                                                         
//           ABSTRACT METHODS MUST BE IMPLEMENTED          
//                                                         
// Generated on 02/09/2017 10:40:43
// ******************************************************* 
package server.ai.jbt.conditions.execution;

import java.util.List;

import server.character.InputControlledEntity;
import server.weapon.WeaponType;
import shared.network.CharData;

/** ExecutionCondition class created from MMPM condition EnemyInWeaponRange. */
public class EnemyInWeaponRange extends
		jbt.execution.task.leaf.condition.ExecutionCondition {

	/**
	 * Constructor. Constructs an instance of EnemyInWeaponRange that is able to
	 * run a server.ai.jbt.conditions.EnemyInWeaponRange.
	 */
	public EnemyInWeaponRange(
			server.ai.jbt.conditions.EnemyInWeaponRange modelTask,
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
		/* TODO: this method's implementation must be completed. */
		System.out.println(this.getClass().getCanonicalName() + " spawned");
	}

	@SuppressWarnings("unchecked")
	protected jbt.execution.core.ExecutionTask.Status internalTick() {
		/*
		 * TODO: this method's implementation must be completed. This function
		 * should only return Status.SUCCESS, Status.FAILURE or Status.RUNNING.
		 * No other values are allowed.
		 */
		List<CharData> enemies = (List<CharData>)getContext().getVariable("Enemies");
		InputControlledEntity character = (InputControlledEntity)getContext().getVariable("Character");
		WeaponType type = character.getWeapon().type;
		double range = type.weaponType==3?type.length:Double.MAX_VALUE; 
		for (CharData e:enemies) {
			if (e.healthPoints>0 && character.getPosition().distance(e.x, e.y)-e.radius<range) {
				return Status.SUCCESS;
			}
		}
		return jbt.execution.core.ExecutionTask.Status.FAILURE;
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