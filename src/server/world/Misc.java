package server.world;

import java.awt.image.BufferedImage;
import java.io.Serializable;

import editor.CellRenderable;
import editor.Identifiable;
import editor.ImageLoadable;

public class Misc implements CellRenderable, Identifiable, Serializable, ImageLoadable {
	private static final long serialVersionUID = -7507913680913283436L;

	protected int id;
	
	protected String name;
	protected String imageName;
	
	protected Light light;
	protected int layer;
	protected double spriteSize; // in meters
	protected transient BufferedImage objectImage;
	
	public Misc(int idNumber) {
		id = idNumber;
	}

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

	/**
	 * @return the layer
	 */
	public int getLayer() {
		return layer;
	}

	/**
	 * @param layer the layer to set
	 */
	public void setLayer(int layer) {
		this.layer = layer;
	}
}
