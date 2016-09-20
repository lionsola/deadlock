package core;

import graphics.ImageBlender;

import java.awt.Image;
import java.awt.image.BufferedImage;

/**
 * Represent the properties and data of one type of tile. Note that there is only one tile object
 * for each type, the tile map in Arena just keep reference to them. The network does not need the
 * images, so a network-side tile only use the property fields.
 * 
 * @author Anh Pham
 */
public class Tile {
	
	public static final double tileSize = 2; // default

	final boolean walkable;
	final boolean transparent;
	final private BufferedImage tileImage;
	final private BufferedImage tileImageDark;
	final private BufferedImage tileImageLight;

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
	public Tile(boolean walkable, boolean transparent, BufferedImage tileImage) {
		this.walkable = walkable;
		this.transparent = transparent;

		if (transparent) {
			this.tileImage = ImageBlender.darkenImage(tileImage, 1.2f, 2);
			this.tileImageDark = ImageBlender.blurImage(ImageBlender.darkenImage(tileImage, 6, 3));
			this.tileImageLight = ImageBlender.darkenImage(tileImage, 1f, 2);
		} else {
			// the solid tiles will not have lighting effects on them,
			// so no need to have different tile images.
			this.tileImage = tileImage;//ImageBlender.darkenImage(tileImage, 6, 3);
			this.tileImageDark = null;
			this.tileImageLight = null;
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
		this.tileImage = null;
		this.tileImageDark = null;
		this.tileImageLight = null;
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

	/**
	 * Get the standard image for this tile.
	 * 
	 * @return
	 */
	public Image getImage() {
		return tileImage;
	}

	/**
	 * Get a darken image for this tile.
	 * 
	 * @return
	 */
	public Image getImageDark() {
		return tileImageDark != null ? tileImageDark : tileImage;
	}

	/**
	 * Get a lighten image for this tile.
	 * 
	 * @return
	 */
	public Image getImageLight() {
		return tileImageLight != null ? tileImageLight : tileImage;
	}
	
}
