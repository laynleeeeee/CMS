package eulap.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

import org.apache.commons.codec.binary.Base64;

/**
 * A utility class that handles the attachment related formating.

 */

public class ReferenceDocumentUtil {
	// private static final String FILE_PATH = System.getProperty("user.home") + "/cbsFiles/";
	// TODO : temporary file path for reference document files
	private static final String FILE_PATH = "/home/ref/";

	/**
	 * Generate random 32 digit hex number.
	 */
	public static String getRandomHexString() {
		Random r = new Random();
		StringBuffer sb = new StringBuffer();
		while (sb.length() < 31) {
			sb.append(Integer.toHexString(r.nextInt()));
		}
		return sb.toString().substring(0, 31);
	}

	/**
	 * Write base 64 to file.
	 * @param strBase64 Base 64 string format.
	 */
	public static String writeBase64ToFile(String strBase64, String fileExtension) {
		String filePath = null;
		if (strBase64 !=null && !strBase64.trim().isEmpty()) {
			File file = new File(FILE_PATH);
			file.mkdir();
			strBase64 = strBase64.split(",")[1];
			byte[] data = Base64.decodeBase64(strBase64.getBytes());
			filePath = FILE_PATH + ReferenceDocumentUtil.getRandomHexString() + (fileExtension != null ? "." + fileExtension : "");
			try (OutputStream stream = new FileOutputStream(filePath)) {
				stream.write(data);
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return filePath;
	}

	/**
	 * Encode file to base 64 binary
	 * @param filePath The file path
	 * @return The encoded file
	 */
	public static String encodeFileToBase64Binary(String filePath) throws IOException {
		try {
			File file =  new File(filePath);
			String encodedfile = null;
			FileInputStream fileInputStreamReader = new FileInputStream(file);
			byte[] bytes = new byte[(int) file.length()];
			fileInputStreamReader.read(bytes);
			encodedfile = new String(Base64.encodeBase64(bytes), "UTF-8");
			// Set file data type
			Path path = FileSystems.getDefault().getPath(filePath);
			String dataType = "data:" + Files.probeContentType(path) + ";base64,";
			return dataType + encodedfile;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
