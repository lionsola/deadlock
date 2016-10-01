package client.graphics;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import client.image.*;
import server.world.Arena;

/**
 * Provide image processing methods.
 * 
 * @author Anh Pham
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
	
	public static BufferedImage glowImage(BufferedImage source, float radius, float amount) {
		BufferedImage d = ge.createCompatibleImage(source.getWidth(),source.getHeight());
		GlowFilter gf = new GlowFilter(radius);
		gf.setAmount(amount);
		gf.setEdgeAction(ConvolveFilter.WRAP_EDGES);
		return gf.filter(source, d);
	}

	public static BufferedImage drawArena(Arena a) {
		BufferedImage source = ge.createCompatibleImage(Renderer.toPixel(a.getWidthMeter()),Renderer.toPixel(a.getHeightMeter()));
		Graphics2D g2D = (Graphics2D)source.getGraphics();
		Renderer.renderDark(g2D, a, new Rectangle2D.Double(0,0,a.getWidthMeter(),a.getHeightMeter()));
		g2D.dispose();
		return source;
	}
	
	public static BufferedImage applyBackgroundEffect(BufferedImage source) {
		BufferedImage d = ge.createCompatibleImage(source.getWidth(),source.getHeight());
		GrayscaleFilter gf = new GrayscaleFilter();
		return darkenImage(gf.filter(source, d),2,1);
	}
	
	public static BufferedImage applyForegroundEffect(BufferedImage source) {
		return lightenImage(glowImage(source,100f,0.4f),0.05f);
	}
}
