package server.world;

import java.awt.Color;
import java.awt.image.BufferedImage;

import editor.CellRenderable;
import editor.Identifiable;

/**
 * Represent the properties and data of one type of tile. Note that there is only one tile object
 * for each type, the tile map in Arena just keep reference to them. The server-side does not need the
 * images, so a network-side tile only use the property fields.
 * 
 * @author Anh Pham
 */
public class Terrain implements CellRenderable, Identifiable {
	public static final int COVER_NONE = 0;
	public static final int COVER_LIGHT = 1;
	public static final int COVER_MEDIUM = 2;
	public static final int COVER_HEAVY = 3;
	
	public static final double tileSize = 1.6; // default

	protected int id;
	protected String name;
	protected Color color;
	protected int textureType;
	protected BufferedImage tileImage;

	protected String imageName;

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
	public Terrain(int id, BufferedImage tileImage, Color color) {
		this.id = id;
		this.color = color;
		this.tileImage = tileImage;
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
	public Terrain(int id) {
		this.id = id;
		this.color = null;
		this.tileImage = null;
	}


	public Color getColor() {
		return color;
	}
	
	/**
	 * Get the standard image for this tile.
	 * 
	 * @return
	 */
	public BufferedImage getImage() {
		return tileImage;
	}
	
	public void setImage(BufferedImage image) {
		this.tileImage = image;
	}
	
	public String getImageName() {
		return imageName;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
	
	public int getId() {
		return id;
	}
	
	public void setTextureType(int type) {
		this.textureType = type;
	}
	
	public int getTextureType() {
		return textureType;
	}
}
