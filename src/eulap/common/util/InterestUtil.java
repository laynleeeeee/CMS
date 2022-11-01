package eulap.common.util;

import java.util.Date;

/**
 * Utility Class that handles the computation of interest.

 *
 */
public class InterestUtil {
	/**
	 * Get the interest multiplier per day.
	 * @param interestRate The interest rate.
	 * @return The interest multiplier that will be use in computing the interest per day.
	 */
	public static double getInterestMultiplier (double interestRate) {
		return (interestRate / 100) / 365;
	}

	/**
	 * Compute for the interest.
	 * @param startDate Start date of the interest.
	 * @param endDate End date of the interest.
	 * @param amount The principal amount.
	 * @param interestRate Percentage of the principal.
	 * @return The interest of the principal per day.
	 */
	public static double computeInterest (Date startDate, Date endDate, double amount, double interestRate) {
		int dateDiff = DateUtil.getDayDifference(startDate, endDate);
		if (dateDiff <= 0)
			return 0;
		return getInterestMultiplier(interestRate) * dateDiff * amount;
	}
}
