package eulap.eb.dao;

import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.LPLine;

/**
 * DAO layer of {@link LPLine}

 */
public interface LPLineDao extends Dao<LPLine>{

	/**
	 * Get the list of AP Lines.
	 * @param apInvoiceId The unique AP invoice id.
	 * @return The list of AP Lines.
	 */
	List<LPLine> getLPLines (int apInvoiceId);

	/**
	 * Compute the total debit of the AP Line per account.
	 * <br>Summated amount are greater than zero.
	 * @param companyId Id of the company.
	 * @param asOfDate The end date of the date range.
	 * @param accountId The Id of the account.
	 * @return The total debit of AP Line.
	 */
	double getTotalDebit(int companyId, Date asOfDate, int accountId);

	/**
	 * Compute the total credit of the AP Line per account.
	 * <br>Summated amount are lesser than zero.
	 * @param companyId Id of the company.
	 * @param asOfDate The end date of the date range.
	 * @param accountId The Id of the account.
	 * @return The total credit of AP Line.
	 */
	double getTotalCredit(int companyId, Date asOfDate, int accountId);
}
