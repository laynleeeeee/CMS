package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.CustodianAccount;

/**
 * Data Access Object of {@link CustodianAccount}

 *
 */
public interface CustodianAccountDao extends Dao<CustodianAccount>{

	/**
	 * Check if custodian name is unique
	 * @param custodianAccount The custodian name
	 * @return True or false
	 */
	boolean isUniqueCustodianName(CustodianAccount custodianAccount);

	/**
	 * Check if custodian account name is unique
	 * @param custodianAccount The custodian name
	 * @return True or false
	 */
	boolean isUniqueCustodianAccountName(CustodianAccount custodianAccount);

	/**
	 * Search the customer accounts.
	 * @param custodianName The customer name.
	 * @param custodianAccountName The customer account name.
	 * @param companyId The company id.
	 * @param termId The term id.
	 * @param status The search status.
	 * @param pageSetting The page setting.
	 * @return The page result.
	 */
	Page<CustodianAccount> searchCustodianAccounts(String custodianName, String custodianAccountName, Integer companyId,
			Integer termId, SearchStatus status, PageSetting pageSetting);

	/**
	 * Get the list of custodian accounts based on the company id and name.
	 * @param companyId The selected company id.
	 * @param name The name of the custodian account.
	 * @param isExact True if it must be exact, otherwise false.
	 * @return The list of custodian accounts.
	 */
	List<CustodianAccount> getCustodianAccounts(Integer companyId, String name, Boolean isExact);

	/**
	 * Get the list of custodian accounts
	 * @param companyId The company id
	 * @param divisionId The division id
	 * @param custodianAcctId The custodian account id
	 * @return The list of custodian accounts
	 */
	List<CustodianAccount> getCustodianAccounts(Integer companyId, Integer divisionId, Integer custodianAcctId);
}
