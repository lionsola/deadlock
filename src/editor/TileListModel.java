package editor;

import java.util.List;

import javax.swing.AbstractListModel;

import client.gui.ClientPlayer;
import server.world.Tile;

public class TileListModel extends AbstractListModel<Tile> {

	private static final long serialVersionUID = 1480642633891355142L;
	private List<Tile> team;

	/**
	 * Creates a new TeamListModel using a list of players
	 * 
	 * @param team
	 *            a list of each team
	 */
	public TileListModel(List<Tile> team) {
		this.team = team;
	}

	@Override
	public Tile getElementAt(int index) {
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
