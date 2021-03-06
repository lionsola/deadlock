package client.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Shape;
import java.awt.Transparency;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

import client.image.*;
import server.world.Arena;
import server.world.Visibility;
import shared.network.FullCharacterData;

/**
 * Provide image processing methods.
 */
public class ImageBlender {
	
	public static final GraphicsConfiguration ge = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();

	/**
	 * Change the image's brightness and contrast according to given parameters. 1 means not
	 * changing that property.
	 * 
	 * @param source
	 *            The source image to edit.
	 * @param darkenFactor
	 *            The factor with which the image will be darken.
	 * @param contrast
	 *            The factor with which the image's contrast will be increased.
	 * @return The resulting image after processing.
	 */
	public static BufferedImage darkenImage(BufferedImage source, float darkenFactor, float contrast) {
		int width = source.getWidth();
		int height = source.getHeight();
		BufferedImage dest = ge.createCompatibleImage(width, height);
		// calculate the average colour values of the image
		float sumR = 0, sumG = 0, sumB = 0;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int RGB = source.getRGB(x, y);
				sumR += (RGB >> 16) & 0xFF;
				sumG += (RGB >> 8) & 0xFF;
				sumB += (RGB) & 0xFF;
			}
		}
		int pixels = width * height;
		float avR = sumR / pixels;
		float avG = sumG / pixels;
		float avB = sumB / pixels;

		// change contrast and brightness of each pixel
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int sRGB = source.getRGB(x, y);
				int sR = (sRGB >> 16) & 0xFF;
				int sG = (sRGB >> 8) & 0xFF;
				int sB = (sRGB) & 0xFF;

				int dR = (int) (Math.min(255, Math.max(0, (sR - avR) * contrast + avR)) / darkenFactor);
				int dG = (int) (Math.min(255, Math.max(0, (sG - avG) * contrast + avG)) / darkenFactor);
				int dB = (int) (Math.min(255, Math.max(0, (sB - avB) * contrast + avB)) / darkenFactor);

				int dRGB = dR << 16 | dG << 8 | dB;
				dest.setRGB(x, y, dRGB);
			}
		}
		return dest;
	}

	/**
	 * Change the image's brightness and contrast according to given parameters. 1 means not
	 * changing that property.
	 * 
	 * @param source
	 *            The source image to edit.
	 * @param darkenFactor
	 *            The factor with which the image will be darken.
	 * @param contrast
	 *            The factor with which the image's contrast will be increased.
	 * @return The resulting image after processing.
	 */
	public static BufferedImage lightenImage(BufferedImage source, float lightenFactor) {
		int width = source.getWidth();
		int height = source.getHeight();
		BufferedImage dest = ge.createCompatibleImage(width, height);

		// change contrast and brightness of each pixel
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int sRGB = source.getRGB(x, y);
				int sR = (sRGB >> 16) & 0xFF;
				int sG = (sRGB >> 8) & 0xFF;
				int sB = (sRGB) & 0xFF;

				int dR = (int) (Math.min(255, Math.max(0, (sR+ 255*lightenFactor))));
				int dG = (int) (Math.min(255, Math.max(0, (sG+ 255*lightenFactor))));
				int dB = (int) (Math.min(255, Math.max(0, (sB+ 255*lightenFactor))));

				int dRGB = dR << 16 | dG << 8 | dB;
				dest.setRGB(x, y, dRGB);
			}
		}
		return dest;
	}
	
	/**
	 * Change the image's brightness and contrast according to given parameters. 1 means not
	 * changing that property.
	 * 
	 * @param source
	 *            The source gray-scale image to edit.
	 * @param darkenFactor
	 *            The factor with which the image will be darken.
	 * @param contrast
	 *            The factor with which the image's contrast will be increased.
	 * @return The resulting image after processing.
	 */
	public static BufferedImage replaceDarkPixels(BufferedImage source, int threshold, int newColor) {
		int width = source.getWidth();
		int height = source.getHeight();
		BufferedImage dest = ge.createCompatibleImage(width, height);
		// change contrast and brightness of each pixel
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int sRGB = source.getRGB(x, y);
				int intensity = sRGB & 0xFF;
				
				int dR = (newColor>>16) & 0xFF;
				int dG = (newColor>>8) & 0xFF;
				int dB = (newColor>>0) & 0xFF;
				
				if (intensity<=threshold) {
					dest.setRGB(x, y, newColor);
				}
				else {
					dR = (int) (dR + (255-dR) * ((intensity-threshold) / (255.0 - threshold)));
					dG = (int) (dG + (255-dG) * ((intensity-threshold) / (255.0 - threshold)));
					dB = (int) (dB + (255-dB) * ((intensity-threshold) / (255.0 - threshold)));
					int dRGB = dR << 16 | dG << 8 | dB;
					dest.setRGB(x, y, dRGB);
				}
			}
		}
		return dest;
	}
	
	public static BufferedImage glowImage(BufferedImage source, BufferedImage dest, float radius, float amount) {
		if (dest==null)
			dest = ge.createCompatibleImage(source.getWidth(),source.getHeight());
		GlowFilter gf = new GlowFilter(radius);
		gf.setAmount(amount);
		gf.setEdgeAction(ConvolveFilter.WRAP_EDGES);
		return gf.filter(source, dest);
	}

	public static BufferedImage blurImage(BufferedImage source, BufferedImage dest, float radius) {
		if (dest==null)
			dest = ge.createCompatibleImage(source.getWidth(),source.getHeight());
		GaussianFilter gf = new GaussianFilter(radius);
		gf.setEdgeAction(ConvolveFilter.WRAP_EDGES);
		return gf.filter(source, dest);
	}
	
	public static BufferedImage drawArena(Arena a) {
		BufferedImage source = ge.createCompatibleImage(Renderer.toPixelDefault(a.getWidthMeter()),
				Renderer.toPixelDefault(a.getHeightMeter()));
		Graphics2D g2D = (Graphics2D)source.getGraphics();
		Rectangle2D drawArea = new Rectangle2D.Double(0,0,a.getWidthMeter(),a.getHeightMeter());
		Renderer.renderArenaBGFlat(g2D, a, drawArea);
		Renderer.renderArenaObjects(g2D, a, drawArea);
		Renderer.renderArenaMiscs(g2D, a, drawArea);
		g2D.dispose();
		return source;
	}
	
	public static BufferedImage applyBackgroundEffect(BufferedImage source) {
		BufferedImage d = ge.createCompatibleImage(source.getWidth(),source.getHeight());
		GrayscaleFilter gf = new GrayscaleFilter();
		//return darkenImage(gf.filter(source, d),0.7f,0.5f);
		d = gf.filter(source, d);
		//return replaceDarkPixels(d,0x4f,0x1f1f3f);
		return replaceDarkPixels(d,0x1f,0x1f1f3f);
	}
	
	public static BufferedImage applyMiddlegroundEffect(BufferedImage source) {
		/*
		BufferedImage d = deepCopy(source);
		BufferedImage black = ge.createCompatibleImage(d.getWidth(),d.getHeight(),Transparency.OPAQUE);
		Graphics2D blackG2D = (Graphics2D) black.getGraphics();
		blackG2D.setColor(new Color(0x1f1f1f));
		blackG2D.fillRect(0, 0, d.getWidth(),d.getHeight());
		
		Graphics2D g2D = (Graphics2D)d.getGraphics();
		g2D.setComposite(new SoftHardLightComposite(1f));
		g2D.drawImage(black,0,0,d.getWidth(),d.getHeight(),null);
		g2D.dispose();
		*/
		return new GrayscaleFilter().filter(source, null);
	}
	
	public static BufferedImage applyForegroundEffect(BufferedImage source) {
		return source;//glowImage(source,null,25f,0.2f);
	}
	
	static BufferedImage deepCopy(BufferedImage bi) {
		 ColorModel cm = bi.getColorModel();
		 boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		 WritableRaster raster = bi.copyData(null);
		 return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}
	
	public static BufferedImage blendLightImage(BufferedImage foreground, BufferedImage lightmap) {
		BufferedImage source = deepCopy(foreground);
		Graphics2D g2D = (Graphics2D)source.getGraphics();
		g2D.setComposite(new SoftHardLightComposite(1f));
		g2D.drawImage(lightmap,0,0,null);
		g2D.dispose();
		return source;
	}
	
	public static BufferedImage drawLightImage(Arena a) {
		BufferedImage source;
		if (a.isReal()) {
			source = ge.createCompatibleImage(Renderer.toPixelDefault(a.getWidthMeter()),Renderer.toPixelDefault(a.getHeightMeter()));
			Graphics2D g2D = (Graphics2D)source.getGraphics();
			Renderer.renderHardLight(g2D, a.getLightmap(), new Rectangle2D.Double(0,0,a.getWidthMeter(),a.getHeightMeter()));
			g2D.dispose();
		} else {
			source = ge.createCompatibleImage(Renderer.toPixelDefault(a.getWidthMeter()),Renderer.toPixelDefault(a.getHeightMeter()),Transparency.TRANSLUCENT);
			Graphics2D g2D = (Graphics2D)source.getGraphics();
			Renderer.renderMatrixHardLight(g2D, a.getLightmap(), new Rectangle2D.Double(0,0,a.getWidthMeter(),a.getHeightMeter()));
			g2D.dispose();
		}
		
		return source;
	}
	
	public static BufferedImage drawLightImage(Arena a, FullCharacterData player) {
		BufferedImage source = ge.createCompatibleImage(Renderer.toPixel(a.getWidthMeter()),Renderer.toPixel(a.getHeightMeter()));
		Graphics2D g2D = (Graphics2D)source.getGraphics();
		Visibility v = new Visibility();
		Shape s = v.generateLoS(player, a);
		g2D.setColor(Color.BLACK);
		g2D.fillRect(0,0,source.getWidth(),source.getHeight());
		g2D.setColor(Color.WHITE);
		g2D.fill(s);
		g2D.dispose();
		//BufferedImage dest = ge.createCompatibleImage(Renderer.toPixel(a.getWidthMeter()),Renderer.toPixel(a.getHeightMeter()));
		//return blurImage(source,dest,50f);
		return source;
	}

	public static BufferedImage drawArena(Arena a, int layer) {
		BufferedImage source = ge.createCompatibleImage(
				Renderer.toPixel(a.getWidthMeter()),Renderer.toPixel(a.getHeightMeter()),Transparency.TRANSLUCENT);
		Graphics2D g2D = (Graphics2D)source.getGraphics();
		Renderer.drawArenaLayer(g2D, a, layer, true, true, true);
		
		return source;
	}

	public static BufferedImage createImage(int width, int height, int transparency) {
		return ge.createCompatibleImage(width,height,Transparency.TRANSLUCENT);
	}

	public static BufferedImage applyColor(Color color, BufferedImage source) {
		BufferedImage copy = deepCopy(source);
		Graphics2D gunG = (Graphics2D)copy.getGraphics();
		gunG.setComposite(new OverlayComposite(1.0f));
		gunG.setColor(color);
		gunG.fillRect(0, 0, copy.getWidth(), copy.getHeight());
		gunG.dispose();
		return copy;
	}
}
