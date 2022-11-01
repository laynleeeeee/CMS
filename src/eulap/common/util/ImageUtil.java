package eulap.common.util;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;

/**
 * Utility class for image handling.

 *
 */
public class ImageUtil {

	/**
	 * Convert the base 64 string to buffered image.
	 * @param strBase64 The base 64 string to be converted to image.
	 * @return The converted image
	 * @throws IOException
	 */
	public static BufferedImage convBase64ToBuffImg (String strBase64) throws IOException {
		BufferedImage image = null;
		if (strBase64 !=null && !strBase64.trim().isEmpty()) {
			strBase64 = strBase64.split(",")[1];
			ByteArrayInputStream bis = null; 
			try {
				bis = new ByteArrayInputStream(Base64.decodeBase64(strBase64.getBytes()));
				image = ImageIO.read(bis);
			}  finally {
				bis.close();
			}
		}
		return image;
	}

	/**
	 * Gets a base64 string, converts to image and reduce the size, then
	 * convert it back to base 64 string.
	 * @param strBase64 The base 64 string.
	 * @param width The preferred width (pixel).
	 * @param height The preferred height (pixel).
	 * @return The reduced image converted to base 64
	 * @throws IOException
	 */
	public static String reduceImageSize(String strBase64, int width, int height) throws IOException {
		BufferedImage image = convBase64ToBuffImg(strBase64);
		if (image == null) {
			throw new RuntimeException("Error in converting the base 64 image to buffered image.");
		}
		String prefix = strBase64.split(",")[0] + ",";
		String imgFormat = prefix.split(";")[0].split("/")[1];
		int type = image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : image.getType();
		BufferedImage resizedImage = new BufferedImage(width, height, type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(image, 0, 0, width, height, null);
		g.dispose();
		g.setComposite(AlphaComposite.Src);

		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			ImageIO.write(resizedImage, imgFormat, os);
			return prefix + Base64.encodeBase64String(os.toByteArray());
		} finally {
			os.close();
		}
	}

	/**
	 * Converts base 64 image to input stream.
	 * @param strBase64 The base 64 string.
	 * @return The input stream.
	 */
	public static InputStream convBase64ImgToInputStream(String strBase64) {
		if (strBase64 == null) {
			throw new RuntimeException("Base 64 image must not be null.");
		}
		return new ByteArrayInputStream(Base64.decodeBase64(strBase64.getBytes()));
	}
}