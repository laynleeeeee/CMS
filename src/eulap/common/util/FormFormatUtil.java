package eulap.common.util;

import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.Company;

/**
 * A utility class that handles the form related formating.

 *
 */
public class FormFormatUtil {

	/**
	 * Get the formatted form number.
	 * @param clazz The class name of the subclass.
	 * @param company The company name
	 * @param sequenceNumber The sequence number.
	 * @return Company Code + upper case for class name (ex. CashSale = CS) + sequence number
	 */
	public static String formatFormSeqNumber (BaseFormWorkflow form, Company company, int sequenceNumber) {
		String name = form.getClass().getSimpleName();
		String upperCaseChar = name.replaceAll("[a-z]", "");
		String companyCode = null;
		if (company.getCompanyCode() != null) {
			companyCode = company.getCompanyCode();
		} else {
			companyCode = company.getName().substring(0, 1);
		}
		return companyCode + " " + upperCaseChar + " " + sequenceNumber;
	}
}
