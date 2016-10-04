package server.world;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;

import client.graphics.ImageBlender;

/**
 * Represent the properties and data of one type of tile. Note that there is only one tile object
 * for each type, the tile map in Arena just keep reference to them. The server-side does not need the
 * images, so a network-side tile only use the property fields.
 * 
 * @author Anh Pham
 */
public class Tile {
	
	public static final double tileSize = 1.2; // default

	protected boolean walkable;
	protected boolean transparent;
	protected double protection;
	protected String name;
	protected Color color; 
	protected BufferedImage tileImage;

	/**
	 * Constructor. Note that this constructor should only be used for the client, since the network
	 * does not need the image resources and would waste memory keeping them in the tile.
	 * 
	 * @param walkable
	 *            If true, the characters can move through the tile.
	 * @param transparent
	 *            If true, the characters can see through the tile.
	 * @param tileImage
	 *            The base image of this tile, used to generate different images for the lighting.
	 */
	public Tile(boolean walkable, boolean transparent, BufferedImage tileImage, Color color) {
		this.walkable = walkable;
		this.transparent = transparent;
		this.color = color;
		
		if (transparent) {
			this.tileImage = ImageBlender.darkenImage(tileImage, 1.2f, 2);
		} else {
			// the solid tiles will not have lighting effects on them,
			// so no need to have different tile images.
			this.tileImage = tileImage;//ImageBlender.darkenImage(tileImage, 6, 3);
		}
	}

	/**
	 * Constructor. Note that this constructor should only be used for the network, because it does
	 * not initialize the graphical resource.
	 * 
	 * @param walkable
	 *            If true, the characters can move through the tile.
	 * @param transparent
	 *            If true, the characters can see through the tile.
	 */
	public Tile(boolean walkable, boolean transparent) {
		this.walkable = walkable;
		this.transparent = transparent;
		this.color = null;
		this.tileImage = null;
	}

	/**
	 * Check if this tile is walkable.
	 * 
	 * @return True if the characters can walk through it, false otherwise.
	 */
	public boolean isWalkable() {
		return walkable;
	}

	/**
	 * Check if this tile is walkable.
	 * 
	 * @return True if the characters can walk through it, false otherwise.
	 */
	public boolean isTransparent() {
		return transparent;
	}

	public Color getColor() {
		return color;
	}
	
	/**
	 * Get the standard image for this tile.
	 * 
	 * @return
	 */
	public Image getImage() {
		return tileImage;
	}
	
	public void setProtection(double protection) {
		this.protection = protection;
	}
	
	public String getName() {
		return name;
	}

	public double getProtection() {
		return protection;
	}
}
