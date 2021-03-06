package server.world;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.Serializable;

import editor.CellRenderable;
import editor.Identifiable;
import editor.ImageLoadable;

/**
 * Represent the properties and data of one type of tile. Note that there is only one tile object
 * for each type, the tile map in Arena just keep reference to them. The server-side does not need the
 * images, so a network-side tile only use the property fields.
 * 
 * @author Anh Pham
 */
public class Terrain implements CellRenderable, Identifiable, Serializable, ImageLoadable {
	private static final long serialVersionUID = 2914169403817715835L;
	
	public static final double tileSize = 1.6; // default
	public static final int BLEND = 1;

	protected int id;
	protected String name;
	protected Color color;
	protected transient BufferedImage tileImage;
	
	protected int soundId;
	protected double volume;

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
		//Terrain.class.getDeclaredFields()[0];
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
	
	/**
	 * @return the soundId
	 */
	public int getSoundId() {
		return soundId;
	}

	/**
	 * @param soundId the soundId to set
	 */
	public void setSoundId(int soundId) {
		this.soundId = soundId;
	}

	/**
	 * @return the volume
	 */
	public double getVolume() {
		return volume;
	}

	/**
	 * @param volume the volume to set
	 */
	public void setVolume(double volume) {
		this.volume = volume;
	}

	public String toString() {
		return getName();
	}
}
