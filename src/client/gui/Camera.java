package client.gui;

import java.awt.Component;
import java.awt.geom.Rectangle2D;

import client.graphics.Renderer;
import server.world.Arena;
import shared.network.FullCharacterData;
import shared.network.GameDataPackets.InputPacket;

/**
 * Follows the player as he moves to display the appropriate area of the screen.
 * 
 * @author Anh Pham
 */
public class Camera {

	private int x;
	private int y;
	private final double arenaWidthMeter;
	private final double arenaHeightMeter;

	/**
	 * Creates a camera which will follow the player
	 * 
	 * @param arena
	 *            The arena in the game
	 */
	public Camera(Arena arena) {
		this.arenaWidthMeter = arena.getWidthMeter();
		this.arenaHeightMeter = arena.getHeightMeter();
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
	public void update(Component parent, FullCharacterData player, InputPacket input) {
		int arenaWidthPixel = (int) (arenaWidthMeter*Renderer.getPPM()+0.5);
		int arenaHeightPixel = (int) (arenaHeightMeter*Renderer.getPPM()+0.5);
		
		if (arenaWidthPixel > parent.getWidth()) {
			x = Math.min(Renderer.toPixel(player.x), arenaWidthPixel - parent.getWidth() / 2);
			x = Math.max(x, parent.getWidth() / 2);
		} else {
			x = arenaWidthPixel / 2;
		}

		if (arenaHeightPixel > parent.getHeight()) {
			y = Math.min(Renderer.toPixel(player.y), arenaHeightPixel - parent.getHeight() / 2);
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
	public int getTopLeftXPixel(Component parent) {
		return x - parent.getWidth() / 2;
	}

	/**
	 * Get the top left X coordinate of the camera
	 * 
	 * @param parent
	 *            The parent
	 * @return Returns the top left X coordinate of the camera
	 */
	public int getTopLeftYPixel(Component parent) {
		return y - parent.getHeight() / 2;
	}

	/**
	 * Get the top left X coordinate of the camera
	 * 
	 * @param parent
	 *            The parent
	 * @return Returns the top left X coordinate of the camera
	 */
	public double getTopLeftXMeter(Component parent) {
		return Renderer.toMeter(getTopLeftXPixel(parent));
	}

	/**
	 * Get the top left X coordinate of the camera
	 * 
	 * @param parent
	 *            The parent
	 * @return Returns the top left X coordinate of the camera
	 */
	public double getTopLeftYMeter(Component parent) {
		return Renderer.toMeter(getTopLeftYPixel(parent));
	}
	
	/**
<<<<<<< .mine
	 * Get the rendering "window" of the game world
	 * @param parent The parent component that is rendered into
	 * @return A rectangle representing the draw area of the game world
=======
	 * Get the draw area of the camera
	 * 
	 * @param parent
	 *            The parent
	 * @return Returns a rectangle of the draw area of the camera
>>>>>>> .r128
	 */
	public Rectangle2D getDrawArea(Component parent) {
		return new Rectangle2D.Double(getTopLeftXMeter(parent), getTopLeftYMeter(parent),
				Renderer.toMeter(parent.getWidth()), Renderer.toMeter(parent.getHeight()));
	}

}
