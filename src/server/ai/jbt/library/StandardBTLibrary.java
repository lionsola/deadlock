// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                       DO NOT MODIFY                     
//                                                         
// Generated on 01/02/2017 08:12:36
// ******************************************************* 
package server.ai.jbt.library;

/**
 * BT library that includes the trees read from the following files:
 * <ul>
 * <li>D:/java/Deadlock/\resource/bt/Patrol.xbt</li>
 * <li>D:/java/Deadlock/\resource/bt/Fire.xbt</li>
 * <li>D:/java/Deadlock/\resource/bt/NPCPatroller.xbt</li>
 * </ul>
 */
public class StandardBTLibrary implements jbt.execution.core.IBTLibrary {
	/** Tree generated from file D:/java/Deadlock/\resource/bt/Patrol.xbt. */
	private static jbt.model.core.ModelTask Patrol;
	/** Tree generated from file D:/java/Deadlock/\resource/bt/Fire.xbt. */
	private static jbt.model.core.ModelTask Fire;
	/** Tree generated from file D:/java/Deadlock/\resource/bt/NPCPatroller.xbt. */
	private static jbt.model.core.ModelTask NPCPatroller;

	/* Static initialization of all the trees. */
	static {
		Patrol = new jbt.model.task.decorator.ModelRepeat(
				null,
				new jbt.model.task.composite.ModelSequence(
						null,
						new server.ai.jbt.actions.ComputeNextPatrolPoint(null,
								null, "patrolLocations"),
						new jbt.model.task.composite.ModelParallel(
								null,
								jbt.model.task.composite.ModelParallel.ParallelPolicy.SEQUENCE_POLICY,
								new server.ai.jbt.actions.PointCursor(null,
										null, "patrolLocation"),
								new server.ai.jbt.actions.Move(null, null,
										"patrolLocation")),
						new jbt.model.task.composite.ModelSequence(null,
								new server.ai.jbt.actions.ComputeWatchPoint(
										null),
								new server.ai.jbt.actions.PointCursor(null,
										null, "watchPoint"),
								new jbt.model.task.leaf.ModelWait(null, 1000)),
						new jbt.model.task.composite.ModelSequence(null,
								new server.ai.jbt.actions.ComputeWatchPoint(
										null),
								new server.ai.jbt.actions.PointCursor(null,
										null, "watchPoint"),
								new jbt.model.task.leaf.ModelWait(null, 1000))));

		Fire = new jbt.model.task.composite.ModelSequence(null,
				new server.ai.jbt.actions.ComputeAttackPoint(null),
				new server.ai.jbt.actions.PointCursor(null, null, "Aim"),
				new server.ai.jbt.actions.FireWeapon(null));

		NPCPatroller = new jbt.model.task.decorator.ModelRepeat(
				null,
				new jbt.model.task.composite.ModelDynamicPriorityList(
						null,
						new jbt.model.task.leaf.ModelSubtreeLookup(
								new server.ai.jbt.conditions.EnemyInSight(null),
								"Fire"),
						new jbt.model.task.leaf.ModelSubtreeLookup(null,
								"Patrol")));

	}

	/**
	 * Returns a behaviour tree by its name, or null in case it cannot be found.
	 * It must be noted that the trees that are retrieved belong to the class,
	 * not to the instance (that is, the trees are static members of the class),
	 * so they are shared among all the instances of this class.
	 */
	public jbt.model.core.ModelTask getBT(java.lang.String name) {
		if (name.equals("Patrol")) {
			return Patrol;
		}
		if (name.equals("Fire")) {
			return Fire;
		}
		if (name.equals("NPCPatroller")) {
			return NPCPatroller;
		}
		return null;
	}

	/**
	 * Returns an Iterator that is able to iterate through all the elements in
	 * the library. It must be noted that the iterator does not support the
	 * "remove()" operation. It must be noted that the trees that are retrieved
	 * belong to the class, not to the instance (that is, the trees are static
	 * members of the class), so they are shared among all the instances of this
	 * class.
	 */
	public java.util.Iterator<jbt.util.Pair<java.lang.String, jbt.model.core.ModelTask>> iterator() {
		return new BTLibraryIterator();
	}

	private class BTLibraryIterator
			implements
			java.util.Iterator<jbt.util.Pair<java.lang.String, jbt.model.core.ModelTask>> {
		static final long numTrees = 3;
		long currentTree = 0;

		public boolean hasNext() {
			return this.currentTree < numTrees;
		}

		public jbt.util.Pair<java.lang.String, jbt.model.core.ModelTask> next() {
			this.currentTree++;

			if ((this.currentTree - 1) == 0) {
				return new jbt.util.Pair<java.lang.String, jbt.model.core.ModelTask>(
						"Patrol", Patrol);
			}

			if ((this.currentTree - 1) == 1) {
				return new jbt.util.Pair<java.lang.String, jbt.model.core.ModelTask>(
						"Fire", Fire);
			}

			if ((this.currentTree - 1) == 2) {
				return new jbt.util.Pair<java.lang.String, jbt.model.core.ModelTask>(
						"NPCPatroller", NPCPatroller);
			}

			throw new java.util.NoSuchElementException();
		}

		public void remove() {
			throw new java.lang.UnsupportedOperationException();
		}
	}
}
