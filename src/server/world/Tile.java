package server.world;

import java.awt.Color;
import java.awt.image.BufferedImage;

import editor.CellRenderable;
import editor.Identifiable;

public class Tile implements CellRenderable, Identifiable {
	
	public Tile(int id) {
		this.id = id;
	}
	
	protected int id;
	protected boolean walkable;
	protected boolean transparent;
	protected Color color;
	protected int coverType;
	protected String name;
	protected String imageName;
	protected double spriteSize; // in meters
	protected BufferedImage objectImage;
	
	/**
	 * @return the imageName
	 */
	public String getImageName() {
		return imageName;
	}

	/**
	 * @param imageName the imageName to set
	 */
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the walkable
	 */
	public boolean isWalkable() {
		return walkable;
	}
	/**
	 * @param walkable the walkable to set
	 */
	public void setWalkable(boolean walkable) {
		this.walkable = walkable;
	}
	/**
	 * @return the transparent
	 */
	public boolean isTransparent() {
		return transparent;
	}
	/**
	 * @param transparent the transparent to set
	 */
	public void setTransparent(boolean transparent) {
		this.transparent = transparent;
	}
	/**
	 * @return the coverType
	 */
	public int getCoverType() {
		return coverType;
	}
	/**
	 * @param coverType the coverType to set
	 */
	public void setCoverType(int coverType) {
		this.coverType = coverType;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the spriteSize
	 */
	public double getSpriteSize() {
		return spriteSize;
	}
	/**
	 * @param spriteSize the spriteSize to set
	 */
	public void setSpriteSize(double spriteSize) {
		this.spriteSize = spriteSize;
	}
	/**
	 * @return the objectImage
	 */
	public BufferedImage getImage() {
		return objectImage;
	}
	/**
	 * @param objectImage the objectImage to set
	 */
	public void setImage(BufferedImage objectImage) {
		this.objectImage = objectImage;
		color = new Color(objectImage.getRGB(0, 0));
	}
	
	public Color getColor() {
		return color;
	}
}
