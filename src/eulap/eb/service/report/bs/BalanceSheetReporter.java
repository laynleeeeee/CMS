package eulap.eb.service.report.bs;

import java.util.Date;
import java.util.List;

import eulap.eb.domain.hibernate.User;

/**
 * An interface class that defines handles the different portion of balance sheet report.  

 *
 */
public interface BalanceSheetReporter {
	
	/**
	 * Generate the balance sheet report per account type.
	 * @param user The current log-in user.   
	 * @param companyId The selected company.
	 * @param asOfDate The cut off date.
	 * @return The generated report
	 */
	List<BalanceSheetRowData> generateReport (User user, int companyId, Date asOfDate);
}
