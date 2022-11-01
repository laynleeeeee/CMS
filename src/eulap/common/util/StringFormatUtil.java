package eulap.common.util;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.text.WordUtils;


/**
 * Utility class for handling string manipulation.

 */
public class StringFormatUtil {

	public static String PERCENT_CHAR = "%";
	public static String UNDERSCORE_CHAR = "_";
	public static final Pattern DIACRITICS_AND_FRIENDS = Pattern.compile("[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}]+");

	/**
	 * Evaluate the string for invalid characters such as:
	 * TILDE("~"), EXCLAMATION_POINT("!"), AT("@"), HASH("#"), DOLLAR("$"), PERCENT("%"),
	 * CARET("^"), AMPERSAND("&"), ASTERISK("*"), LEFT_PARENTHESIS("("), RIGHT_PARENTHESIS(")"),
	 * UNDERSCORE("_"), PLUS("+"), EQUAL("="), LEFT_CURLY_BRACE("{"), RIGHT_CURLY_BRACE("}"),
	 * LEFT_SQURE_BRACKET("["), RIGHT_SQUARE_BRACKET("]"), COLON(":"), SEMI-COLON(";"), DOUBLE_QUOTE('"'),
	 * SINGLE_QUOTE("'"), VERTICAL_BAR("|"), BACK_SLASH("\"), LESS_THAN("<"), GREATER_THAN(">"),
	 * QUESTION("?"), COMMA(","), FORWARD_SLASH("/"), GRAVE_ACCENT("`")
	 * @param str String to be evaluated.
	 * @return True if the string has no invalid characters, otherwise false.
	 */
	public static boolean containsInvalidCharacter (String str) {
		boolean containsSpecialChar = false;
		if (str!=null) {
			Matcher m = Pattern.compile("[^A-Za-z0-9Ã-Ɫ. -]").matcher(str);
			while(m.find()) {
				containsSpecialChar = true;
			}
		}
		return containsSpecialChar;
	}

	/**
	 * Evaluate the string if it contains the unwanted special characters
	 * based from the parameter passed.
	 * @param str String to be evaluated.
	 * @param invalidCharacters The list of unwanted special characters.
	 * @return True if the string has characters "%" and "_", otherwise false.
	 */
	public static boolean containsInvalidChar(String str, String ...invalidCharacters) {
		String appendedInvalidChars = "";
		for (String invalidChar : invalidCharacters)
			appendedInvalidChars += invalidChar;
	
		boolean containsInvalidChar = false;
		Matcher matcher = Pattern.compile("[" + appendedInvalidChars + "]").matcher(str);
		while(matcher.find()) 
			containsInvalidChar = true;
		return containsInvalidChar;
	}

	/**
	 * Validate the string if it is numeric.
	 * Numeric characters ranges from 0 to 9.
	 * @param str String to be validated.
	 * @return True if string is numeric, otherwise false.
	 */
	public static boolean isNumeric (String str) {
		if(str.trim().isEmpty())
			return false;
		str = str.trim();
		for (char c : str.toCharArray()) {
			if (!Character.isDigit(c))
				return false;
		}
		return !str.isEmpty() && true;
	}

	/**
	 * RegEx value and equivalent.
	 * \\d  # numbers from [0-9]
	 * {3}  # length number validation
	 *  -   # contains dash
	 * 
	 * Validate the string if valid TIN format. e.g 123-456-789
	 * @param str The string to be validated.
	 * @return True if valid TIN format, otherwise false.
	 */
	public static boolean isValidTIN(String str){

		boolean isValid = false;
		if (str.isEmpty() || str == null){
    		return true;
    	}else{
    		Matcher m = Pattern.compile("^(\\d{3}-?\\d{3}-?\\d{3})(-\\d{3})?$").matcher(str);
			while(m.find()) {
					isValid = true;			
			}
		}
		return isValid;
	}

	/**
	 * Converts the all the first letters of the word to uppercase.
	 * @param str The string.
	 * @return The converted string.
	 */
	public static String conv2UCFirstLetter (String str) {
		return WordUtils.capitalizeFully(str);
	}

	/**
	 * Checks if all the characters of a string are uppercase.
	 * @param string The string.
	 * @return True if all characters are uppercase, otherwise false.
	 */
	public static boolean isAllUpperCase(String string) {
		if (string != null && !string.isEmpty()) {
			for(char c : string.toCharArray()){
				if(c != ' ' && !Character.isUpperCase(c))
					return false;
			}
		}
		return false;
	}

	/**
	 * Format string to lowercase except for the first character.
	 * <br>Example: STRING formats to: String
	 * @param string The string to be formatted.
	 * @return The formatted string.
	 */
	public static String formatToLowerCase(String string) {
		String formattedString = string.charAt(0) + string.substring(1).toLowerCase();
		return formattedString;
	}

	/**
	 * Remove the extra white spaces.
	 * @param string The string to have the extra white spaces removed.
	 * @param replacer The string to replace the extra white space
	 * @return The string without the extra white spaces.
	 */
	public static String removeExtraWhiteSpaces (String string, String replacer) {
		if (replacer != null) {
			return string.trim().replaceAll("\\s+", replacer);
		}
		return string.trim().replaceAll("\\s+", " ");
	}

	/**
	 * Remove the extra white spaces.
	 * @param string The string to have the extra white spaces removed.
	 * @return The string without the extra white spaces.
	 */
	public static String removeExtraWhiteSpaces (String string) {
		return removeExtraWhiteSpaces(string, null);
	}

	/**
	 * Get the first character of a string.
	 * @param str The string to be evaluated.
	 * @return The fist character of a string.
	 */
	public static String getFirstCharOfString(String str) {
		return conv2UCFirstLetter(String.valueOf(str.charAt(0)));
	}

	/**
	 * Removes the line breaks from a string.
	 * @param str The string to be edited.
	 * @return String without line breaks.
	 */
	public static String removeLineBreaks(String str) {
		return str.replaceAll("\n", " ");
	}

	/**
	 * Formats the sequence number.
	 * @param prefix The prefix for the sequence number.
	 * @param seqNo The sequence number.
	 * @return The formatted sequence number. Example: A 1952
	 */
	public static String getFormattedSeqNo(String prefix, Integer seqNo) {
			return prefix + " " +String.valueOf(seqNo);
	}

	/**
	 * Splits the string to list of Strings, delimiter used is semicolon (;)
	 * @param toBeParsedIds The string to be parsed.
	 * @return The list of strings.
	 */
	public static List<Integer> parseIds(String toBeParsedIds) {
		if(toBeParsedIds == null || toBeParsedIds == "") {
			return null;
		}
		List<Integer> parsedIds = new ArrayList<Integer>();
		String[] splittedIds = toBeParsedIds.split(";");
		String parsedId = null;
		for (String id : splittedIds) {
			//Remove the semicolon
			parsedId = id.replaceAll(";", "");
			parsedIds.add(Integer.valueOf(parsedId));
		}
		return parsedIds;
	}

	/**
	 * Appends wild card before and after the string.
	 * @param str The String to be edited.
	 * @return The String with appended wild card before and after.
	 */
	public static String appendWildCard(String str) {
		str = str != null ? str.trim() : "";
		return "%" + str + "%";
	}

	/**
	 * Convert the list integer into string, separated by comma.
	 * @return The stringed list of integers.
	 */
	public static String convertIntegersToString(List<Integer> integers){
		String integerStr = "";
		int length = integers.size();
		int index = 0;
		for (Integer integer : integers) {
			index++;
			if(index < length){
				integerStr += integer + ", ";
			} else {
				integerStr += integer;
			}
		}
		return integerStr;
	}

	/**
	 * Get the first character per word excluded dash or minus sign(-).
	 * @param string The string.
	 * @return The initials of the string.
	 */
	public static String getFirstCharacterPerWord (String string) {
		String initials = "";
		for (String s : string.split(" ")) {
			if(!s.equals("-")){
				initials+=s.charAt(0);
			}
		}
		return initials;
	}

	/**
	 * Check if the string is purely ascii
	 * @param string The string to be checked.
	 * @return True if purely ascii, otherwise false.
	 */
	public static boolean isPureAscii(String string) {
		byte bytearray []  = string.getBytes();
		CharsetDecoder d = Charset.forName("US-ASCII").newDecoder();
		try {
			CharBuffer r = d.decode(ByteBuffer.wrap(bytearray));
			r.toString();
		}
		catch(CharacterCodingException e) {
			return false;
		}
		return true;
	}

	/**
	 * Convert a string with non latin character to UTF-8.
	 * <br>
	 * When string is purely ascii it automatically returns the original string.
	 * @param string The string to be converted.
	 * @return The converted string.
	 * @throws UnsupportedEncodingException
	 */
	public static String convNonPureAsciiStr2Utf8(String string) throws UnsupportedEncodingException {
		if (isPureAscii(string)) {
			return string;
		}
		String encodedStr = string;
		byte[] bytes = encodedStr.getBytes("ISO-8859-1");
		return new String(bytes, "UTF-8");
	}

	/**
	 * Change the string sentence to title case.
	 * @param string The string to be changed to title case.
	 * @return The title cased string.
	 */
	public static String toTitleCase(String string) {
		if (string != null && !string.trim().isEmpty()) {
			String arr[] = string.split("\\s+");
			int length = arr.length;
			StringBuilder sb = new StringBuilder();
			int ctr = 0;
			for (int i=0; i<length; i++) {
				if (!isBasicRomanNumeral(arr[i])) {
					char chars[] = arr[i].toCharArray();
					for (char c : chars) {
						if (ctr == 0) {
							c = Character.toUpperCase(c);
						} else {
							c = chars[ctr-1] != '/' ? Character.toLowerCase(c) : Character.toUpperCase(c);
						}
						sb.append(c);
						ctr++;
					}
					sb.append(" ");
				} else {
					sb.append(arr[i]);
				}
				ctr = 0;
			}
			return sb.toString();
		}
		return "";
	}

	private static boolean isBasicRomanNumeral(String string) {
		String arr[] = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII", "XIV", "XV"};
		if (string != null && !string.isEmpty()) {
			for (String a : arr) {
				if (string.equals(a)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Process tin number. Append '-' every 3 characters.
	 * @param tinNumber The tin number.
	 * @return The processed tin number
	 */
	public static String processTin(String tinNumber) {
		String tin = "";
		if (tinNumber == null) {
			return tin;
		}
		int interval = 3;
		Integer length = tinNumber.length() - 1;
		int endIndex = interval;
		if(length - 1 >= endIndex) {
			tin = tinNumber.substring(0, endIndex);
			while(endIndex <= length) {
				if(endIndex + interval > length) {
					tin += "-" + tinNumber.substring(endIndex, length + 1);
				} else {
					tin += "-" + tinNumber.substring(endIndex, endIndex + interval);
				}
				endIndex += interval;
			}
		}
		return tin;
	}

	/**
	 * Since we are storing the TIN and branch code  of the supplier/customer/company
	 * in the database. This utility will parse the first 9 digit for the actual 
	 * TIN of the suppler/customer/company.
	 * 
	 * 1234567890123 = 123456789
	 * 123456789 = 123456789
	 * @param tINNumber The TIN number
	 * @return the parsed data
	 */
	public static String parseBIRTIN (String tINNumber) {
		if (tINNumber == null || tINNumber.length() < 9) {
			return tINNumber;
		}
		return tINNumber.substring(0, 9);
	}

	/**
	 * Get the branch code of the TIN.
	 *  1234567890123 = 0123
	 *  123456789 = 0000
	 * @param tINNumber the TIN number
	 * @return the branch code
	 */
	public static String parseBranchCode (String tINNumber) {
		if (tINNumber == null || tINNumber.length() < 9) {
			return "";
		}
		if (tINNumber.length() == 9) {
			return "0000";
		}
		if (tINNumber.length() < 13) {
			return tINNumber.substring(9, tINNumber.length());
		}
		return tINNumber.substring(9,13);
	}

	/**
	 * Removing diacritical marks on alphabet
	 * ñ = n
	 * @param string The string
	 * @return Replaced diacritical marks
	 */
	public static String stripDiacritics(String string) {
		if(!string.isEmpty()) {
			string = Normalizer.normalize(string, Normalizer.Form.NFD);
			string = DIACRITICS_AND_FRIENDS.matcher(string).replaceAll("");
			return string;
		}
		return "";
	}

	/**
	 * Format BIR TIN to 13 digit format: 123-456-789-0000
	 * @param tin The BIR TIN
	 * @return The formatted BIR TIN
	 */
	public static String processBirTinTo13Digits(String tin) {
		String parsedTin = parseBIRTIN(tin);
		String parsedBranchCode = parseBranchCode(tin);
		if (parsedBranchCode != null && !parsedBranchCode.isEmpty()) {
			return processTin(parsedTin) + "-" + parsedBranchCode;
		}
		return processTin(parsedTin);
	}

	/**
	 * Format BIR TIN to 9 digit format: 123-456-789
	 * @param tin The BIR TIN
	 * @return The formatted BIR TIN
	 */
	public static String processBirTinTo9Digits(String tin) {
		String parsedTin = parseBIRTIN(tin);
		return processTin(parsedTin);
	}
}