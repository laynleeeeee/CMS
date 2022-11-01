package eulap.eb.payroll;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Date;

/**
 *  This interface will serve as parser for biometric data.

 *
 */
public interface PayrollParser {

	/**
	 * Get the biometric model Id. this id is the unique identifier
	 * of this biometric.
	 */
	int getBiometricModelId ();

	/**
	 * Initialize this parser.
	 */
	void init();

	/**
	 * Parse the data.
	 * @param employeeTypeId The unique id of employee type.
	 * @param dateFrom The start of date range filter.
	 * @param dateTo The end of date range filter.
	 * @param in the input stream.
	 * @throws IOException
	 * @throws ParseException
	 */
	void parseData(Date dateFrom, Date dateTo, InputStream in, PayrollDataHandler handler) throws IOException, ParseException;
}
