// ******************************************************* 
//                   MACHINE GENERATED CODE                
//                       DO NOT MODIFY                     
//                                                         
// Generated on 03/04/2017 14:31:51
// ******************************************************* 
package server.network;

/**
 * BT library that includes the trees read from the following files:
 * <ul>
 * <li>D:/java/Deadlock/\resource/bt/mission/ReachTarget.xbt</li>
 * <li>D:/java/Deadlock/\resource/bt/mission/EliminateAll.xbt</li>
 * </ul>
 */
public class MissionBTLibrary implements jbt.execution.core.IBTLibrary {
	/**
	 * Tree generated from file
	 * D:/java/Deadlock/\resource/bt/mission/ReachTarget.xbt.
	 */
	private static jbt.model.core.ModelTask ReachTarget;
	/**
	 * Tree generated from file
	 * D:/java/Deadlock/\resource/bt/mission/EliminateAll.xbt.
	 */
	private static jbt.model.core.ModelTask EliminateAll;

	/* Static initialization of all the trees. */
	static {
		ReachTarget = new server.network.btmodel.ReachTarget(null, null,
				"targetLocation");

		EliminateAll = new server.network.btmodel.EliminateAll(null);

	}

	/**
	 * Returns a behaviour tree by its name, or null in case it cannot be found.
	 * It must be noted that the trees that are retrieved belong to the class,
	 * not to the instance (that is, the trees are static members of the class),
	 * so they are shared among all the instances of this class.
	 */
	public jbt.model.core.ModelTask getBT(java.lang.String name) {
		if (name.equals("ReachTarget")) {
			return ReachTarget;
		}
		if (name.equals("EliminateAll")) {
			return EliminateAll;
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
		static final long numTrees = 2;
		long currentTree = 0;

		public boolean hasNext() {
			return this.currentTree < numTrees;
		}

		public jbt.util.Pair<java.lang.String, jbt.model.core.ModelTask> next() {
			this.currentTree++;

			if ((this.currentTree - 1) == 0) {
				return new jbt.util.Pair<java.lang.String, jbt.model.core.ModelTask>(
						"ReachTarget", ReachTarget);
			}

			if ((this.currentTree - 1) == 1) {
				return new jbt.util.Pair<java.lang.String, jbt.model.core.ModelTask>(
						"EliminateAll", EliminateAll);
			}

			throw new java.util.NoSuchElementException();
		}

		public void remove() {
			throw new java.lang.UnsupportedOperationException();
		}
	}
}
