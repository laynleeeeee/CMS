package eulap.common.util;

import eulap.eb.domain.hibernate.TaxType;

/**
 * Utility class for tax type

 */

public class TaxUtil {

	public static final double GOVT_TAX_PERCENTAGE = 0.05;

	/**
	 * Check if the tax type is VAT
	 * @param taxTypeId The tax type id
	 * @return True if the tax type is VAT, otherwise false
	 */
	public static boolean isVatable(Integer taxTypeId) {
		if (taxTypeId != null) {
			return taxTypeId.equals(TaxType.VATABLE) || taxTypeId.equals(TaxType.GOODS) || taxTypeId.equals(TaxType.SERVICES)
					|| taxTypeId.equals(TaxType.CAPITAL) || taxTypeId.equals(TaxType.PRIVATE) || taxTypeId.equals(TaxType.GOVERNMENT)
					|| taxTypeId.equals(TaxType.IMPORTATION);
		}
		return false;
	}

	/**
	 * Compute VAT amount.
	 * @param amount The amount.
	 * @return The computed VAT amount.
	 */
	public static double computeVat(double amount) {
		return (amount / 1.12) * 0.12;
	}
}
