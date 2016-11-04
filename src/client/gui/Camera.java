package client.gui;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import client.graphics.Renderer;
import server.world.Arena;
import shared.network.FullCharacterData;

/**
 * Follows the player as he moves to display the appropriate area of the screen.
 */
public class Camera {
	private Component parent;
	private int x;
	private int y;
	private Arena arena;

	/**
	 * Creates a camera which will follow the player
	 * 
	 * @param arena
	 *            The arena in the game
	 */
	public Camera(Arena arena, Component parent) {
		this.parent = parent;
		this.arena = arena;
		//x = arenaWidthMeter / 2;
		//y = arenaHeightMeter / 2;
	}

	/**
	 * Updates the camera
	 * 
	 * @param parent
	 *            The parent
	 * @param player
	 *            The player which the camera is following
	 */
	public void update(FullCharacterData player) {
		update(player.x,player.y);
	}

	/**
	 * Updates the camera
	 * 
	 * @param parent
	 *            The parent
	 */
	public void update(double px, double py) {
		int arenaWidthPixel = (int) (arena.getWidthMeter()*Renderer.getPPM()+0.5);
		int arenaHeightPixel = (int) (arena.getHeightMeter()*Renderer.getPPM()+0.5);
		
		if (arenaWidthPixel > parent.getWidth()) {
			x = Math.min(Renderer.toPixel(px), (int)(arenaWidthPixel - parent.getWidth() / 2.0));
			x = Math.max(x, (parent.getWidth() / 2));
		} else {
			x = arenaWidthPixel / 2;
		}

		if (arenaHeightPixel > parent.getHeight()) {
			y = Math.min(Renderer.toPixel(py), (int)(arenaHeightPixel - parent.getHeight() / 2.0));
			y = Math.max(y, parent.getHeight() / 2);
		} else {
			y = arenaHeightPixel / 2;
		}
	}
	
	/**
	 * Gets the X coordinate of the camera
	 * 
	 * @return Returns the X coordinate
	 */
	public int getX() {
		return x;
	}

	/**
	 * Gets the Y coordinate of the camera
	 * 
	 * @return Returns the Y coordinate
	 */
	public int getY() {
		return y;
	}

	/**
	 * Get the top left X coordinate of the camera
	 * 
	 * @param parent
	 *            The parent
	 * @return Returns the top left X coordinate of the camera
	 */
	public int getTopLeftXPixel() {
		return x - parent.getWidth() / 2;
	}

	/**
	 * Get the top left X coordinate of the camera
	 * 
	 * @param parent
	 *            The parent
	 * @return Returns the top left X coordinate of the camera
	 */
	public int getTopLeftYPixel() {
		return y - parent.getHeight() / 2;
	}

	/**
	 * Get the top left X coordinate of the camera
	 * 
	 * @param parent
	 *            The parent
	 * @return Returns the top left X coordinate of the camera
	 */
	public double getTopLeftXMeter() {
		return Renderer.toMeter(getTopLeftXPixel());
	}

	/**
	 * Get the top left X coordinate of the camera
	 * 
	 * @param parent
	 *            The parent
	 * @return Returns the top left X coordinate of the camera
	 */
	public double getTopLeftYMeter() {
		return Renderer.toMeter(getTopLeftYPixel());
	}

	public Rectangle2D getDrawArea() {
		return new Rectangle2D.Double(Math.max(0,getTopLeftXMeter()), Math.max(0,getTopLeftYMeter()),
				Math.min(arena.getWidthMeter(),Renderer.toMeter(parent.getWidth())), Math.min(arena.getHeightMeter(),Renderer.toMeter(parent.getHeight())));
	}

	public Rectangle getDrawAreaPixel() {
		return new Rectangle(getTopLeftXPixel(), getTopLeftYPixel(),
				parent.getWidth(), parent.getHeight());
	}
}
