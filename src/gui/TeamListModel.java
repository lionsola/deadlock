package gui;

import java.util.List;

import javax.swing.AbstractListModel;

/**
 * Models a team list.
 * 
 * @author Anh Pham
 */
public class TeamListModel extends AbstractListModel<ClientPlayer> {

	private static final long serialVersionUID = 1480642633891355142L;
	private List<ClientPlayer> team;

	/**
	 * Creates a new TeamListModel using a list of players
	 * 
	 * @param team
	 *            a list of each team
	 */
	public TeamListModel(List<ClientPlayer> team) {
		this.team = team;
	}

	@Override
	public ClientPlayer getElementAt(int index) {
		return team.get(index);
	}

	@Override
	public int getSize() {
		return team.size();
	}

	/**
	 * Invalidate the list so the it gets redrawn on the screen.
	 */
	public void invalidate() {
		this.fireContentsChanged(this, 0, getSize());
	}

}
