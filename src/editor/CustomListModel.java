package editor;

import java.util.List;

import javax.swing.AbstractListModel;

public class CustomListModel<T> extends AbstractListModel<T> {

	private static final long serialVersionUID = 1480642633891355142L;
	private List<T> tiles;

	/**
	 * Creates a new TeamListModel using a list of players
	 * 
	 * @param team
	 *            a list of each team
	 */
	public CustomListModel(List<T> team) {
		this.tiles = team;
	}

	@Override
	public T getElementAt(int index) {
		return tiles.get(index);
	}

	@Override
	public int getSize() {
		return tiles.size();
	}

	/**
	 * Invalidate the list so the it gets redrawn on the screen.
	 */
	public void invalidate() {
		this.fireContentsChanged(this, 0, getSize());
	}
}
