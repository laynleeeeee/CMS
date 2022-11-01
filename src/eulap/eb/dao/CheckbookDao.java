package eulap.eb.dao;

import java.math.BigDecimal;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.Checkbook;
import eulap.eb.domain.hibernate.User;

/**
 * Data access layer of {@link Checkbook}

 */
public interface CheckbookDao extends Dao<Checkbook>{

	/**
	 * Check if the checkbook is unique
	 * @param checkbook The checkbook to be evaluated.
	 * @return True if unique otherwise, false
	 */
	boolean isUniqueCheckbook(Checkbook checkbook, int bankAccountId);

	/**
	 * Check if check series is valid.
	 * @param checkbook The checkbook to be evaluated.
	 * @return True if valid otherwise, false
	 */
	boolean isValidSeries(Checkbook checkbook);

	/**
	 * Search for checkbooks
	 * @param searchCriteria The search criteria.
	 * @param pageSetting The page setting.
	 * @return The page result.
	 */
	Page<Checkbook> searchCheckbook(User user, String searchCriteria, PageSetting pageSetting);

	/**
	 * Get all the checkbooks under the bank account.
	 * @param bankAccountId The Id of the bank account.
	 * @param checkbookName The checkbook name.
	 * @param limit the limit of items to be showed.
	 * @return The list of checkbooks.
	 */
	List<Checkbook> getCheckbooks (User user, int bankAccountId, String checkbookName, Integer limit);

	/**
	 * The paged list of checkbooks.
	 * @param companyId The company id.
	 * @param bankAccountName The bank account name.
	 * @param name The name of checkbook.
	 * @param checkNo The checkbook number.
	 * @param status Active or inactive.
	 * @param pageSetting The page setting.
	 * @return The paged list of checkbooks.
	 */
	public Page<Checkbook> getCheckbooks (User user, Integer companyId, String bankAccountName, String name,
			BigDecimal checkNo,  int status, PageSetting pageSetting);

	/**
	 * Get all the checkbooks under the bank account.
	 * @param bankAccountId The Id of the bank account.
	 * @return The list of checkbooks.
	 */
	List<Checkbook> getCheckbooks (User user, int bankAccountId);

	/**
	 * Get the checkbook by parameters.
	 * @param user The user current logged.
	 * @param bankAccountId The unique id of the bank account.
	 * @param checkBookName The checkbook name.
	 * @return The checkbook.
	 */
	Checkbook getCheckBook(User user, Integer bankAccountId, String checkBookName);
}