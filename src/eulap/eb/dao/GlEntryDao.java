package eulap.eb.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.GlEntry;
import eulap.eb.domain.hibernate.TimePeriod;

/**
 * Data access object of {@link GlEntry}


 */
public interface GlEntryDao extends Dao<GlEntry>{
	/**
	 * Get all gl entries
	 * @param pageSetting The page setting.
	 * @return The paged gl entries.
	 */
	public Page<GlEntry> getGlEntries (PageSetting pageSetting);
	
	/**
	 * Filter general entries for account analysis report that have date before the beginnning date.
	 * @param companyId The company id.
	 * @param accountId The account id.
	 * @param divisions The collection of divisions criteria.
	 * @param dateFrom The first date of the range.
	 * @return The collection of gl entries.
	 */
	public Collection<GlEntry> getGlEntries (Integer companyId, Integer accountId, Collection<Division> divisions, 
			Date dateFrom);
	
	
	/**
	 * Filter general entries for account analysis report.
	 * @param companyId The company id.
	 * @param accountId The account id.
	 * @param divisions The collection of divisions criteria.
	 * @param dateFrom The first date of the range.
	 * @param dateTo The second date of the range.
	 * @return The collection of gl entries.
	 */
	public Collection<GlEntry> getGlEntries (Integer companyId, Integer accountId, Collection<Division> divisions, 
			Date dateFrom, Date dateTo);
	
	/**
	 * Get the total debit amount of an account
	 * @param companyId the selected company id.
	 * @param accountId The unique id of the account.
	 * @return The total debit
	 */
	double getTotalDebit (int companyId, Date asOfDate, int accountId);

	/**
	 * Get the total credit amount of an account.
	 * @param companyId the selected company id.
	 * @param accountId The unique id of the account.
	 * @return The total credit amount.
	 */
	double getTotalCredit (int companyId, Date asOfDate, int accountId);
	
	/**
	 * Get the gl entries based on the account combination id and time period.
	 * @param accountCombinationId The account combination id.
	 * @param timePeriod The time period.
	 * @return The collection of gl entries.
	 */
	public Collection<GlEntry> getGlEntries (int accountCombinationId, TimePeriod timePeriod); 
	
	/**
	 * Get the total debit for gl entries with the same company and account within a time period.
	 * @param companyId The company id.
	 * @param accountId The account id.
	 * @param timePeriod The time period.
	 * @return The total debit.
	 */
	double getTotalDebit(int companyId,  int accountId, TimePeriod timePeriod);
	
	/**
	 * Get the total credit for gl entries with the same company and account within a time period.
	 * @param companyId The company id.
	 * @param accountId The account id.
	 * @param timePeriod The time period.
	 * @return The total credit.
	 */
	double getTotalCredit(int companyId,  int accountId, TimePeriod timePeriod);

	/**
	 * Get the total amount of the GL Entry per account.
	 * @param companyId The company Id.
	 * @param asOfDate The end date of the date range.
	 * @return The total amount.
	 */
	double totalPerAccount(int companyId,  int accountId, Date asOfDate);

	/**
	 * Get the gl entries of general ledger.
	 * @param generalLedgerId The unique id of general ledger.
	 * @return The list of gl entries.
	 */
	List<GlEntry> getGLEntries(int generalLedgerId);

	/**
	 * Get the list of active companies
	 * @param companyName the company name
	 * @param companyNumber the company number
	 * @param isActive True if only the active companies are to be retrieved,
	 * otherwise false.
	 * @param limit The limit of the companies to be displayed
	 * @return the list of the active companies
	 */
	List<Company> getCompanies(String company, boolean isActive, Integer limit);
}
