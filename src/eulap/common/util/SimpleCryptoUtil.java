package eulap.common.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * A utility class for ctyptology.

 *
 */
public class SimpleCryptoUtil {
	/**
	 * Encrypt the string to SHA algorithm. 
	 * @param str String to be encrypted.
	 * @return the encrypted string.
	 */
	public static String convertToSHA1 (String str) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		md.update(str.getBytes());
		return convToHex(md.digest());
	}

	private static String convToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9))
                    buf.append((char) ('0' + halfbyte));
                else
                        buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while(two_halfs++ < 1);
        }
        return buf.toString();
    }
}
