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
	private double x;
	private double y;
	private double z;
	private Arena arena;

	/**
	 * Creates a camera which will follow the player
	 * 
	 * @param arena
	 *            The arena in the game
	 */
	public Camera(Arena arena, Component parent, int height) {
		this.parent = parent;
		this.arena = arena;
		z = height;
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
		/*
		double pw = Renderer.toMeter(parent.getWidth());
		double ph = Renderer.toMeter(parent.getHeight());
		
		
		if (arena.getWidthMeter() > pw) {
			x = Math.min(px, arena.getWidthMeter() - pw / 2.0);
			x = Math.max(x, (pw / 2));
		} else {
			x = arena.getWidthMeter() / 2;
		}

		if (arena.getHeightMeter() > ph) {
			y = Math.min(py, arena.getHeightMeter() - ph / 2.0);
			y = Math.max(y, ph / 2);
		} else {
			y = arena.getHeightMeter() / 2;
		}*/
		x = px;
		y = py;
	}
	
	/**
	 * Gets the X coordinate of the camera
	 * 
	 * @return Returns the X coordinate
	 */
	public double getX() {
		return x;
	}

	/**
	 * Gets the Y coordinate of the camera
	 * 
	 * @return Returns the Y coordinate
	 */
	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}
	
	public void setZ(double z) {
		this.z = z;
	}
	
	/**
	 * Get the top left X coordinate of the camera
	 * 
	 * @param parent
	 *            The parent
	 * @return Returns the top left X coordinate of the camera
	 */
	public int getTopLeftXPixel() {
		return Renderer.toPixel(x) - parent.getWidth() / 2;
	}

	/**
	 * Get the top left X coordinate of the camera
	 * 
	 * @param parent
	 *            The parent
	 * @return Returns the top left X coordinate of the camera
	 */
	public int getTopLeftYPixel() {
		return Renderer.toPixel(y) - parent.getHeight() / 2;
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
