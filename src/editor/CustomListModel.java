package editor;

import java.awt.Component;
import java.util.List;

import javax.swing.AbstractListModel;

public class CustomListModel<T> extends AbstractListModel<T> {

	private static final long serialVersionUID = 1480642633891355142L;
	private List<T> list;

	/**
	 * Creates a new TeamListModel using a list of players
	 * 
	 * @param team
	 *            a list of each team
	 */
	public CustomListModel(List<T> team) {
		this.list = team;
	}

	public void setList(List<T> list) {
		this.list = list;
	}
	
	@Override
	public T getElementAt(int index) {
		return list.get(index);
	}

	@Override
	public int getSize() {
		return list.size();
	}

	/**
	 * Invalidate the list so the it gets redrawn on the screen.
	 */
	public void invalidate() {
		this.fireContentsChanged(this, 0, list.size()-1);
	}

	public List<T> getList() {
		return list;
	}
}
