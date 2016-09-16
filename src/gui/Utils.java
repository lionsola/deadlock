package gui;

import java.awt.Component;
import java.util.List;

/**
 * Utilities class.
 * 
 * @author Anh Pham
 */
public class Utils {

	/**
	 * Set the child to the center of the parent
	 * 
	 * @param child
	 *            the child component
	 * @param parent
	 *            the parent component
	 */
	public static void setLocationCenterOf(Component child, Component parent) {
		int x = parent.getX() + (parent.getWidth() - child.getWidth()) / 2;
		int y = parent.getY() + (parent.getHeight() - child.getHeight()) / 2;
		child.setLocation(x, y);
	}

	/**
	 * Find a player in a list given a specified ID.
	 * 
	 * @param players
	 *            the list of players
	 * @param id
	 *            the id of a specific plaer
	 * 
	 * @return the clientplayer who's ID matches the ID passed
	 */
	public static ClientPlayer findPlayer(List<ClientPlayer> players, int id) {
		for (ClientPlayer player : players) {
			if (player.id == id)
				return player;
		}
		return null;
	}

}
