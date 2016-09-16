package graphics;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.Kernel;

/**
 * Provide image processing methods.
 * 
 * @author Anh Pham
 */
public class ImageBlender {
	
	static final GraphicsConfiguration ge = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
	private static final float[] box77 = { 1 / 49f, 1 / 49f, 1 / 49f, 1 / 49f, 1 / 49f, 1 / 49f, 1 / 49f, 1 / 49f, 1 / 49f, 1 / 49f, 1 / 49f,
			1 / 49f, 1 / 49f, 1 / 49f, 1 / 49f, 1 / 49f, 1 / 49f, 1 / 49f, 1 / 49f, 1 / 49f, 1 / 49f, 1 / 49f, 1 / 49f, 1 / 49f, 1 / 49f, 1 / 49f,
			1 / 49f, 1 / 49f, 1 / 49f, 1 / 49f, 1 / 49f, 1 / 49f, 1 / 49f, 1 / 49f, 1 / 49f, 1 / 49f, 1 / 49f, 1 / 49f, 1 / 49f, 1 / 49f, 1 / 49f,
			1 / 49f, 1 / 49f, 1 / 49f, 1 / 49f, 1 / 49f, 1 / 49f, 1 / 49f, 1 / 49f };
	private static final BufferedImageOp blurOp = new ConvolveFilter(new Kernel(7, 7, box77));

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
	 * Blur the image.
	 * 
	 * @param The
	 *            source image to be blurred.
	 * @return The resulting blurred image.
	 */
	public static BufferedImage blurImage(BufferedImage source) {
		return blurOp.filter(source, null);
	}

}
