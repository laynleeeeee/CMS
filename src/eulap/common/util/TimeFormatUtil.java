package eulap.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Class that handles the validation of specific time format, e.g 7:22 AM.

 */
public class TimeFormatUtil {

	private static final String TIME12HOURS_PATTERN = "([01]?[0-9]|[1-9]):[0-5][0-9](\\s)?(?i)(am|pm)";
	private static final String TIME24HOURS_PATTERN = "([01]?[0-9]|2[0-3]):[0-5][0-9]";

	/**
	 * Validate the time format.
	 * @param time The to be evaluated.
	 * @return True if valid, otherwise false.
	 */
	public static boolean validate(final String time){
		return validateTime(time, TIME12HOURS_PATTERN);
	}

	/**
	 * Validate time in 24 hours format with regular expression
	 * @param time time address for validation
	 * @return true valid time format, false invalid time format
	 */
	public static boolean isMilitaryTime(final String time){
		return validateTime(time, TIME24HOURS_PATTERN);
	}

	/**
	 * Evaluate the time format.
	 * @param time The time to be evaluated.
	 * @param strPattern The time pattern.
	 * @return True if time matches the pattern, otherwise false.
	 */
	private static boolean validateTime (final String time, final String strPattern) {
		Pattern pattern = Pattern.compile(strPattern);
		Matcher matcher = pattern.matcher(time);
		return matcher.matches();
	}
}
