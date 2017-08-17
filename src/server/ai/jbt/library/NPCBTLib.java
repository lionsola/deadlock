// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                       DO NOT MODIFY                     
//                                                         
// Generated on 06/04/2017 20:56:09
// ******************************************************* 
package server.ai.jbt.library;

/**
 * BT library that includes the trees read from the following files:
 * <ul>
 * <li>D:/java/Deadlock/\resource/bt/Fire.xbt</li>
 * <li>D:/java/Deadlock/\resource/bt/RunAway.xbt</li>
 * <li>D:/java/Deadlock/\resource/bt/CheckInterest.xbt</li>
 * <li>D:/java/Deadlock/\resource/bt/LookRandomly.xbt</li>
 * <li>D:/java/Deadlock/\resource/bt/NPCPatroller.xbt</li>
 * <li>D:/java/Deadlock/\resource/bt/NPCAttacker.xbt</li>
 * <li>D:/java/Deadlock/\resource/bt/NPCWatcher.xbt</li>
 * <li>D:/java/Deadlock/\resource/bt/NPCRandom.xbt</li>
 * <li>D:/java/Deadlock/\resource/bt/NPCWerewolf.xbt</li>
 * <li>D:/java/Deadlock/\resource/bt/NPCWolf.xbt</li>
 * </ul>
 */
public class NPCBTLib implements jbt.execution.core.IBTLibrary {
	/** Tree generated from file D:/java/Deadlock/\resource/bt/Fire.xbt. */
	private static jbt.model.core.ModelTask Fire;
	/** Tree generated from file D:/java/Deadlock/\resource/bt/RunAway.xbt. */
	private static jbt.model.core.ModelTask RunAway;
	/**
	 * Tree generated from file D:/java/Deadlock/\resource/bt/CheckInterest.xbt.
	 */
	private static jbt.model.core.ModelTask CheckInterest;
	/** Tree generated from file D:/java/Deadlock/\resource/bt/LookRandomly.xbt. */
	private static jbt.model.core.ModelTask LookRandomly;
	/** Tree generated from file D:/java/Deadlock/\resource/bt/NPCPatroller.xbt. */
	private static jbt.model.core.ModelTask NPCPatroller;
	/** Tree generated from file D:/java/Deadlock/\resource/bt/NPCAttacker.xbt. */
	private static jbt.model.core.ModelTask NPCAttacker;
	/** Tree generated from file D:/java/Deadlock/\resource/bt/NPCWatcher.xbt. */
	private static jbt.model.core.ModelTask NPCWatcher;
	/** Tree generated from file D:/java/Deadlock/\resource/bt/NPCRandom.xbt. */
	private static jbt.model.core.ModelTask NPCRandom;
	/** Tree generated from file D:/java/Deadlock/\resource/bt/NPCWerewolf.xbt. */
	private static jbt.model.core.ModelTask NPCWerewolf;
	/** Tree generated from file D:/java/Deadlock/\resource/bt/NPCWolf.xbt. */
	private static jbt.model.core.ModelTask NPCWolf;

	/* Static initialization of all the trees. */
	static {
		Fire = new jbt.model.task.composite.ModelSequence(
				null,
				new server.ai.jbt.actions.ComputeAttackPoint(null),
				new jbt.model.task.decorator.ModelSucceeder(
						null,
						new server.ai.jbt.actions.PointCursor(null, null, "Aim")),
				new server.ai.jbt.conditions.EnemyInWeaponRange(null),
				new server.ai.jbt.actions.FireWeapon(
						new server.ai.jbt.conditions.EnemyInWeaponRange(null)));

		RunAway = new jbt.model.task.composite.ModelSelector(
				null,
				new jbt.model.task.composite.ModelSequence(null,
						new server.ai.jbt.actions.ComputeEscapePosition(null),
						new server.ai.jbt.actions.Run(null, null,
								"escapeTarget", (boolean) true, null)),
				new jbt.model.task.composite.ModelSequence(
						null,
						new server.ai.jbt.actions.ComputeRandomNearbyPoint(null),
						new server.ai.jbt.actions.Run(null, null,
								"randomPoint", (boolean) true, null)));

		CheckInterest = new jbt.model.task.composite.ModelSequence(
				null,
				new server.ai.jbt.actions.ComputeInterestPoint(null),
				new server.ai.jbt.actions.ClearVariable(null, "interest", null),
				new server.ai.jbt.actions.Run(null, null, "interestStandPoint",
						(boolean) true, null),
				new jbt.model.task.leaf.ModelWait(null, 500),
				new server.ai.jbt.actions.ComputeWatchPoint(null),
				new server.ai.jbt.actions.PointCursor(null, null, "watchPoint"),
				new jbt.model.task.leaf.ModelWait(null, 500));

		LookRandomly = new jbt.model.task.composite.ModelSequence(
				null,
				new server.ai.jbt.actions.ComputeWatchPoint(null),
				new server.ai.jbt.actions.PointCursor(null, null, "watchPoint"),
				new server.ai.jbt.actions.ClearVariable(null, "watchPoint",
						null), new jbt.model.task.leaf.ModelWait(null, 1000));

		NPCPatroller = new jbt.model.task.decorator.ModelRepeat(
				null,
				new jbt.model.task.composite.ModelDynamicPriorityList(
						null,
						new jbt.model.task.decorator.ModelRepeat(
								new server.ai.jbt.conditions.EnemyInSight(null),
								new jbt.model.task.composite.ModelParallel(
										null,
										jbt.model.task.composite.ModelParallel.ParallelPolicy.SELECTOR_POLICY,
										new jbt.model.task.decorator.ModelRepeat(
												null,
												new jbt.model.task.leaf.ModelSubtreeLookup(
														null, "Fire")),
										new jbt.model.task.composite.ModelSequence(
												null,
												new server.ai.jbt.actions.ComputeChasePosition(
														null),
												new server.ai.jbt.actions.Run(
														null, null,
														"chaseTarget",
														(boolean) false, null)))),
						new jbt.model.task.leaf.ModelSubtreeLookup(
								new server.ai.jbt.conditions.SomethingNeedChecking(
										null), "CheckInterest"),
						new jbt.model.task.decorator.ModelRepeat(
								null,
								new jbt.model.task.composite.ModelSequence(
										null,
										new server.ai.jbt.actions.ComputePatrolPoint(
												null, null, "patrolLocations",
												(boolean) false, null),
										new server.ai.jbt.actions.Sneak(null,
												null, "patrolLocation",
												(boolean) true, null),
										new jbt.model.task.leaf.ModelWait(null,
												500),
										new jbt.model.task.leaf.ModelSubtreeLookup(
												null, "LookRandomly"),
										new jbt.model.task.leaf.ModelSubtreeLookup(
												null, "LookRandomly")))));

		NPCAttacker = new jbt.model.task.decorator.ModelRepeat(
				null,
				new jbt.model.task.composite.ModelDynamicPriorityList(
						null,
						new jbt.model.task.composite.ModelParallel(
								new server.ai.jbt.conditions.EnemyInSight(null),
								jbt.model.task.composite.ModelParallel.ParallelPolicy.SEQUENCE_POLICY,
								new jbt.model.task.decorator.ModelSucceeder(
										null,
										new jbt.model.task.leaf.ModelSubtreeLookup(
												null, "Fire")),
								new jbt.model.task.composite.ModelSequence(
										null,
										new server.ai.jbt.actions.ComputeChasePosition(
												null),
										new server.ai.jbt.actions.Run(null,
												null, "chaseTarget",
												(boolean) false, null))),
						new jbt.model.task.leaf.ModelSubtreeLookup(
								new server.ai.jbt.conditions.SomethingNeedChecking(
										null), "CheckInterest"),
						new jbt.model.task.decorator.ModelRepeat(
								null,
								new jbt.model.task.composite.ModelSequence(
										null,
										new server.ai.jbt.actions.ComputePatrolPoint(
												null, null, "patrolLocations",
												(boolean) true, null),
										new server.ai.jbt.actions.Run(null,
												null, "patrolLocation",
												(boolean) true, null),
										new jbt.model.task.leaf.ModelWait(null,
												500),
										new jbt.model.task.leaf.ModelSubtreeLookup(
												null, "LookRandomly")))));

		NPCWatcher = new jbt.model.task.decorator.ModelRepeat(
				null,
				new jbt.model.task.composite.ModelDynamicPriorityList(
						null,
						new jbt.model.task.leaf.ModelSubtreeLookup(
								new server.ai.jbt.conditions.EnemyInSight(null),
								"Fire"),
						new jbt.model.task.composite.ModelSequence(null,
								new server.ai.jbt.actions.ComputeWatchPoint(
										null),
								new server.ai.jbt.actions.PointCursor(null,
										null, "watchPoint"),
								new jbt.model.task.leaf.ModelWait(null, 2000))));

		NPCRandom = new jbt.model.task.decorator.ModelRepeat(
				null,
				new jbt.model.task.composite.ModelDynamicPriorityList(
						null,
						new jbt.model.task.decorator.ModelRepeat(
								new server.ai.jbt.conditions.EnemyInSight(null),
								new jbt.model.task.composite.ModelParallel(
										null,
										jbt.model.task.composite.ModelParallel.ParallelPolicy.SELECTOR_POLICY,
										new jbt.model.task.decorator.ModelRepeat(
												null,
												new jbt.model.task.leaf.ModelSubtreeLookup(
														null, "Fire")),
										new jbt.model.task.decorator.ModelRepeat(
												null,
												new jbt.model.task.composite.ModelSelector(
														null,
														new jbt.model.task.composite.ModelSequence(
																null,
																new server.ai.jbt.actions.ComputeChasePosition(
																		null),
																new server.ai.jbt.actions.Run(
																		null,
																		null,
																		"chaseTarget",
																		(boolean) false,
																		null)),
														new jbt.model.task.composite.ModelSequence(
																null,
																new server.ai.jbt.actions.ComputeEscapePosition(
																		null),
																new server.ai.jbt.actions.Run(
																		null,
																		null,
																		"escapeTarget",
																		(boolean) true,
																		null)),
														new jbt.model.task.composite.ModelSequence(
																null,
																new server.ai.jbt.actions.ComputeRandomNearbyPoint(
																		null),
																new server.ai.jbt.actions.Run(
																		null,
																		null,
																		"randomPoint",
																		(boolean) false,
																		null)))))),
						new jbt.model.task.leaf.ModelSubtreeLookup(
								new server.ai.jbt.conditions.SomethingNeedChecking(
										null), "CheckInterest"),
						new jbt.model.task.decorator.ModelRepeat(
								null,
								new jbt.model.task.composite.ModelSequence(
										null,
										new server.ai.jbt.actions.ComputeRandomNearbyPoint(
												null),
										new server.ai.jbt.actions.Sneak(null,
												null, "randomPoint",
												(boolean) true, null),
										new jbt.model.task.leaf.ModelWait(null,
												500),
										new jbt.model.task.leaf.ModelSubtreeLookup(
												null, "LookRandomly")))));

		NPCWerewolf = new jbt.model.task.decorator.ModelRepeat(
				null,
				new jbt.model.task.composite.ModelDynamicPriorityList(
						null,
						new jbt.model.task.decorator.ModelRepeat(
								new server.ai.jbt.conditions.LowHp(null),
								new jbt.model.task.leaf.ModelSubtreeLookup(
										null, "RunAway")),
						new jbt.model.task.decorator.ModelRepeat(
								new server.ai.jbt.conditions.EnemyInSight(null),
								new jbt.model.task.composite.ModelParallel(
										null,
										jbt.model.task.composite.ModelParallel.ParallelPolicy.SELECTOR_POLICY,
										new jbt.model.task.decorator.ModelRepeat(
												null,
												new jbt.model.task.leaf.ModelSubtreeLookup(
														null, "Fire")),
										new jbt.model.task.decorator.ModelRepeat(
												null,
												new server.ai.jbt.actions.UseAbility(
														null)),
										new jbt.model.task.decorator.ModelRepeat(
												null,
												new jbt.model.task.composite.ModelSelector(
														null,
														new jbt.model.task.composite.ModelSequence(
																null,
																new server.ai.jbt.actions.ComputeChasePosition(
																		null),
																new server.ai.jbt.actions.Run(
																		null,
																		null,
																		"chaseTarget",
																		(boolean) false,
																		null)),
														new jbt.model.task.composite.ModelSequence(
																null,
																new server.ai.jbt.actions.ComputeEscapePosition(
																		null),
																new server.ai.jbt.actions.Run(
																		null,
																		null,
																		"escapeTarget",
																		(boolean) true,
																		null)),
														new jbt.model.task.composite.ModelSequence(
																null,
																new server.ai.jbt.actions.ComputeRandomNearbyPoint(
																		null),
																new server.ai.jbt.actions.Run(
																		null,
																		null,
																		"randomPoint",
																		(boolean) false,
																		null)))))),
						new jbt.model.task.leaf.ModelSubtreeLookup(
								new server.ai.jbt.conditions.SomethingNeedChecking(
										null), "CheckInterest"),
						new jbt.model.task.decorator.ModelRepeat(
								null,
								new jbt.model.task.composite.ModelSequence(
										null,
										new server.ai.jbt.actions.ComputeRandomNearbyPoint(
												null),
										new server.ai.jbt.actions.Sneak(null,
												null, "randomPoint",
												(boolean) true, null),
										new jbt.model.task.leaf.ModelWait(null,
												500),
										new jbt.model.task.leaf.ModelSubtreeLookup(
												null, "LookRandomly")))));

		NPCWolf = new jbt.model.task.decorator.ModelRepeat(
				null,
				new jbt.model.task.composite.ModelDynamicPriorityList(
						null,
						new jbt.model.task.decorator.ModelRepeat(
								new server.ai.jbt.conditions.EnemyInSight(null),
								new jbt.model.task.composite.ModelParallel(
										null,
										jbt.model.task.composite.ModelParallel.ParallelPolicy.SELECTOR_POLICY,
										new jbt.model.task.decorator.ModelRepeat(
												null,
												new jbt.model.task.composite.ModelSequence(
														null,
														new server.ai.jbt.actions.ComputeAttackPoint(
																null),
														new jbt.model.task.decorator.ModelSucceeder(
																null,
																new server.ai.jbt.actions.PointCursor(
																		null,
																		null,
																		"Aim")),
														new jbt.model.task.composite.ModelSelector(
																null,
																new jbt.model.task.composite.ModelSequence(
																		null,
																		new server.ai.jbt.conditions.EnemyInRange(
																				null,
																				(float) 5,
																				null),
																		new server.ai.jbt.actions.UseAbility(
																				null)),
																new jbt.model.task.composite.ModelSequence(
																		null,
																		new server.ai.jbt.conditions.EnemyInWeaponRange(
																				null),
																		new server.ai.jbt.actions.FireWeapon(
																				null))))),
										new jbt.model.task.decorator.ModelRepeat(
												null,
												new jbt.model.task.composite.ModelSelector(
														null,
														new jbt.model.task.composite.ModelSequence(
																null,
																new jbt.model.task.decorator.ModelInverter(
																		null,
																		new server.ai.jbt.conditions.WeaponReady(
																				null)),
																new server.ai.jbt.actions.ComputeRandomNearbyPoint(
																		null),
																new server.ai.jbt.actions.Run(
																		null,
																		null,
																		"randomPoint",
																		(boolean) false,
																		null)),
														new jbt.model.task.composite.ModelSequence(
																null,
																new server.ai.jbt.actions.ComputeChasePosition(
																		null),
																new server.ai.jbt.actions.Run(
																		null,
																		null,
																		"chaseTarget",
																		(boolean) false,
																		null)),
														new jbt.model.task.composite.ModelSequence(
																null,
																new server.ai.jbt.actions.ComputeEscapePosition(
																		null),
																new server.ai.jbt.actions.Run(
																		null,
																		null,
																		"escapeTarget",
																		(boolean) true,
																		null)),
														new jbt.model.task.composite.ModelSequence(
																null,
																new server.ai.jbt.actions.ComputeRandomNearbyPoint(
																		null),
																new server.ai.jbt.actions.Run(
																		null,
																		null,
																		"randomPoint",
																		(boolean) false,
																		null)))))),
						new jbt.model.task.leaf.ModelSubtreeLookup(
								new server.ai.jbt.conditions.SomethingNeedChecking(
										null), "CheckInterest"),
						new jbt.model.task.decorator.ModelRepeat(
								null,
								new jbt.model.task.composite.ModelSequence(
										null,
										new server.ai.jbt.actions.ComputeRandomNearbyPoint(
												null),
										new server.ai.jbt.actions.Sneak(null,
												null, "randomPoint",
												(boolean) true, null),
										new jbt.model.task.leaf.ModelWait(null,
												500),
										new jbt.model.task.leaf.ModelSubtreeLookup(
												null, "LookRandomly")))));

	}

	/**
	 * Returns a behaviour tree by its name, or null in case it cannot be found.
	 * It must be noted that the trees that are retrieved belong to the class,
	 * not to the instance (that is, the trees are static members of the class),
	 * so they are shared among all the instances of this class.
	 */
	public jbt.model.core.ModelTask getBT(java.lang.String name) {
		if (name.equals("Fire")) {
			return Fire;
		}
		if (name.equals("RunAway")) {
			return RunAway;
		}
		if (name.equals("CheckInterest")) {
			return CheckInterest;
		}
		if (name.equals("LookRandomly")) {
			return LookRandomly;
		}
		if (name.equals("NPCPatroller")) {
			return NPCPatroller;
		}
		if (name.equals("NPCAttacker")) {
			return NPCAttacker;
		}
		if (name.equals("NPCWatcher")) {
			return NPCWatcher;
		}
		if (name.equals("NPCRandom")) {
			return NPCRandom;
		}
		if (name.equals("NPCWerewolf")) {
			return NPCWerewolf;
		}
		if (name.equals("NPCWolf")) {
			return NPCWolf;
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
		static final long numTrees = 10;
		long currentTree = 0;

		public boolean hasNext() {
			return this.currentTree < numTrees;
		}

		public jbt.util.Pair<java.lang.String, jbt.model.core.ModelTask> next() {
			this.currentTree++;

			if ((this.currentTree - 1) == 0) {
				return new jbt.util.Pair<java.lang.String, jbt.model.core.ModelTask>(
						"Fire", Fire);
			}

			if ((this.currentTree - 1) == 1) {
				return new jbt.util.Pair<java.lang.String, jbt.model.core.ModelTask>(
						"RunAway", RunAway);
			}

			if ((this.currentTree - 1) == 2) {
				return new jbt.util.Pair<java.lang.String, jbt.model.core.ModelTask>(
						"CheckInterest", CheckInterest);
			}

			if ((this.currentTree - 1) == 3) {
				return new jbt.util.Pair<java.lang.String, jbt.model.core.ModelTask>(
						"LookRandomly", LookRandomly);
			}

			if ((this.currentTree - 1) == 4) {
				return new jbt.util.Pair<java.lang.String, jbt.model.core.ModelTask>(
						"NPCPatroller", NPCPatroller);
			}

			if ((this.currentTree - 1) == 5) {
				return new jbt.util.Pair<java.lang.String, jbt.model.core.ModelTask>(
						"NPCAttacker", NPCAttacker);
			}

			if ((this.currentTree - 1) == 6) {
				return new jbt.util.Pair<java.lang.String, jbt.model.core.ModelTask>(
						"NPCWatcher", NPCWatcher);
			}

			if ((this.currentTree - 1) == 7) {
				return new jbt.util.Pair<java.lang.String, jbt.model.core.ModelTask>(
						"NPCRandom", NPCRandom);
			}

			if ((this.currentTree - 1) == 8) {
				return new jbt.util.Pair<java.lang.String, jbt.model.core.ModelTask>(
						"NPCWerewolf", NPCWerewolf);
			}

			if ((this.currentTree - 1) == 9) {
				return new jbt.util.Pair<java.lang.String, jbt.model.core.ModelTask>(
						"NPCWolf", NPCWolf);
			}

			throw new java.util.NoSuchElementException();
		}

		public void remove() {
			throw new java.lang.UnsupportedOperationException();
		}
	}
}
