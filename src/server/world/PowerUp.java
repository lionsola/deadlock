package server.world;
/*package server.world;

import java.awt.Image;
import java.awt.Point;
import java.util.List;

import network.GameEvent.PowerUpPickedUpEvent;
import server.character.ControlledCharacter;

*//**
 * This class handles power ups.
 *//*
public class PowerUp {

	private int x;
	private int y;
	private int type;
	private boolean display;
	private ControlledCharacter picker;
	private long timestamp;

	*//**
	 * Creates a power-up.
	 * @param x
	 *            the power up x coordinate
	 * @param y
	 *            the power up y coordinate
	 * @param type
	 *            the power up type
	 * @param icon
	 *            the power up icon
	 *//*
	public PowerUp(int x, int y, int type, Image icon) {
		this.x = x;
		this.y = y;
		this.type = type;
		this.display = true;
		this.picker = null;
		this.timestamp = 0;
	}

	*//**
	 * Returns the x coordinate of the power-up.
	 * @return the x coordinate
	 *//*
	public int getX() {
		return x;
	}

	*//**
	 * Returns the x coordinate of the power-up.
	 * @return the y coordinate
	 *//*
	public int getY() {
		return y;
	}

	*//**
	 * Returns the type of the power-up.
	 * @return the type
	 *//*
	public int getType() {
		return type;
	}

	*//**
	 * Returns a boolean stating if the power-up is displayed or not.
	 * @return whether power up is displayed or not
	 *//*
	public boolean isDisplayed() {
		return display;
	}

	*//**
	 * Gets the id of the server.character who picks the power-up.
	 * @return the id of the picker otherwise -1
	 *//*
	public int getPickerId() {
		if (picker != null) {
			return picker.id;
		} else {
			return -1;
		}
	}

	*//**
	 * Sets the x and y coordinates of the power-up.
	 * @param x
	 *            the x coordinate to set
	 * @param y
	 *            the y coordinate to set
	 *//*
	public void setCoord(int x, int y) {
		this.x = x;
		this.y = y;
	}

	*//**
	 * Updates the power-ups on the arena.
	 * @param world
	 *            game world
	 * @param powerUps
	 *            power ups array
	 * @param arena
	 *            game arena
	 * @param characters
	 *            server.character list
	 *//*
	public void update(World world, PowerUp[] powerUps, Arena arena, List<ControlledCharacter> characters) {
		if (display) {
			picker = findTouching(characters);
			if (picker != null) {
				display = false;
				timestamp = System.currentTimeMillis();
				activate(world);
			}
			return;
		}

		long timeDifference = System.currentTimeMillis() - timestamp;
		if (picker != null && timeDifference > PowerUpFactory.PERIODS[type] * 1000) {
			timestamp = System.currentTimeMillis();
			deactivate();
			picker = null;
		} else if (timeDifference > PowerUpFactory.REGENERATE_PERIOD * 1000) {
			Point coord = PowerUpFactory.getValidCoord(powerUps, arena, characters);
			int x = coord.x;
			int y = coord.y;
			setCoord(x, y);
			display = true;
		}
	}

	*//**
	 * Returns the server.character that picks up the power up.
	 * @param characters
	 *            server.character list
	 * @return server.character touching power up
	 *//*
	private ControlledCharacter findTouching(List<ControlledCharacter> characters) {
		double realX = getX() * Tile.tileSize + PowerUpFactory.TILE_OFFSET;
		double realY = getY() * Tile.tileSize + PowerUpFactory.TILE_OFFSET;

		for (ControlledCharacter server.character : characters) {
			double deltaX = Math.abs(server.character.getX() - realX);
			double deltaY = Math.abs(server.character.getY() - realY);
			double d = server.character.getRadius() + PowerUpFactory.ICON_SIZE;
			if (deltaX <= d &&
					deltaY <= d) {
				return server.character;
			}
		}

		return null;
	}

	*//**
	 * Activates power up.
	 *//*
	private void activate(World world) {
		world.getEventListener().onEventReceived(new PowerUpPickedUpEvent(x, y, picker.id));

		switch (type) {
			case PowerUpFactory.HEALTH_KIT:
				picker.resetStats();
				break;

			case PowerUpFactory.NOISE_REDUCER:
				picker.setNoiseF(picker.getNoiseF()+0.1);
				break;

			default:
				break;
		}
	}

	*//**
	 * Deactivates power up.
	 *//*
	private void deactivate() {
		switch (type) {
			case PowerUpFactory.HEALTH_KIT:
				// doesn't expire
				break;

			case PowerUpFactory.NOISE_REDUCER:
				picker.setNoiseF(picker.getNoiseF()-0.1);
				break;

			default:
				break;
		}
	}

}
*/