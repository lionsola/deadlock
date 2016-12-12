package server.world;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.Serializable;

import editor.CellRenderable;
import editor.Identifiable;
import editor.ImageLoadable;
import server.world.trigger.TileSwitchPreset.Switchable;

public class Thing implements Switchable, CellRenderable, Identifiable, Serializable, ImageLoadable {
	private static final long serialVersionUID = 5048693025792883019L;

	public Thing(int id) {
		this.id = id;
	}
	
	protected int id;
	protected boolean walkable;
	protected boolean transparent;
	protected int coverType;
	protected int layer;
	protected Color color;
	protected Light light;
	protected String name;
	protected String imageName;
	
	protected boolean border;
	
	protected double spriteSize; // in meters
	protected transient BufferedImage objectImage;
	
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
	public boolean isClear() {
		return transparent;
	}
	/**
	 * @param transparent the transparent to set
	 */
	public void setClear(boolean transparent) {
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
	
	public boolean isBorderDrawn() {
		return border;
	}
	
	public void setBorder(boolean border) {
		this.border = border;
	}
	
	@Override
	public String toString() {
		return getName();
	}

	/**
	 * @return the light
	 */
	public Light getLight() {
		return light;
	}

	/**
	 * @param light the light to set
	 */
	public void setLight(Light light) {
		this.light = light;
	}

	public int getLayer() {
		return layer;
	}
	
	public void setLayer(int layer) {
		this.layer = layer;
	}
}
