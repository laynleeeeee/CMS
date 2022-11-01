package eulap.common.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.codec.binary.Base64;
import org.springframework.web.multipart.MultipartFile;

/**
 * A utility class that handles the file related handling.

 *
 */
public class FileUtil {

	/**
	 * Convert the multipart file to InputStream.
	 * @param file The multipart file.
	 * @return The converted multipart file.
	 * @throws IOException
	 */
	public static InputStream convertMpf2InputStream (MultipartFile file) throws IOException {
		byte [] byteArr = file.getBytes();
		return new ByteArrayInputStream(byteArr);
	}

	/**
	 * Encode the multipart file into base 64 string.
	 * @param file The multipart file.
	 * @return The converted multipart file.
	 * @throws IOException
	 */
	public static String encodeFileToBase64Binary(MultipartFile file)
			throws IOException {
		byte[] bytes =  file.getBytes();
		byte[] encoded = Base64.encodeBase64(bytes);
		return new String(encoded);
	}
}
