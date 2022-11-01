package eulap.eb.dao;

import java.util.Collection;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.User;

/**
 * Account Combination Data-Access-Object.

 */
public interface AccountCombinationDao extends Dao<AccountCombination>{

	/**
	 * Get all account combinations.
	 * @param user The logged user.
	 * @return The page collection of account combinations.
	 */
	Page<AccountCombination> getAccountCombinations(User user);

	/**
	 * Search/filter account combinations based on the parameters.
	 * @param companyNumber The company number.
	 * @param divisionNumber The division number.
	 * @param accountNumber The account number.
	 * @param companyName The company name.
	 * @param divisionName The division name.
	 * @param accountName  The account name.
	 * @param serviceLeaseKeyId The service lease key id.
	 * @param pageSetting The page setting.
	 * @return Paged collection of account combination.
	 */
	Page<AccountCombination> searchAccountCombinations (String companyNumber,  String divisionNumber, 
			 String accountNumber,  String companyName,  String divisionName, String accountName, 
			 int serviceLeaseKeyId, SearchStatus status, PageSetting pageSetting, User user);

	/**
	 * Check if the account combination is unique or not.
	 * @param accountCombination The account combination object to be evaluated.
	 * @return True if the account combination number is unique, otherwise false.
	 */
	boolean isUniqueAccountCombination(AccountCombination accountCombination);

	/**
	 * Get the account combination object based on the company id, division id, and account id.
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @param accountId The account id.
	 * @return The account combination object.
	 */
	AccountCombination getAccountCombination (int companyId, int divisionId, int accountId);

	/**
	 * Get the account combination object based on the company number, division number, and account number.
	 * @param companyNumber The company number.
	 * @param divisionNumber The division number.
	 * @param accountNumber The account number.
	 * @return The account combination object.
	 */
	AccountCombination getAccountCombination (String companyNumber, String divisionNumber, String accountNumber);

	/**
	 * Get the account combinations by company id.
	 * @param companyId The company id.
	 * @return The collection of account combinations.
	 */
	Collection<AccountCombination> getAccountCombinations (int companyId);

	/**
	 * Get account combination by supplier account.
	 * @param supplierAccountId The Id of the supplier account.
	 * @return The account combination object.
	 */
	AccountCombination getAcctCombiBySupplierAcctId (int supplierAccountId);

	/**
	 * Get the Account Combination from the AR Line Setup.
	 * @param accountCombinationId The Id of the Account Combination.
	 * @return The Account Combination object.
	 */
	AccountCombination getACFromArLine (int accountCombinationId);

	/**
	 * Get the Account Combination of AR Customer by customeraccountId.
	 * @param customerAccountId The customer account id.
	 * @return The Account Combination object.
	 */
	AccountCombination getAcctCombiByCustomerAcctId (int customerAccountId);

	/**
	 * Get the list of account combination base on the companyId and divisionId.
	 * @param companyId The company Id.
	 * @param divisionId the division Id.
	 * @param limit the limit of items to be showed
	 * @param accountName The account name.
	 * @return The account combination object.
	 */
	List<AccountCombination> getAccountCombinations(Integer companyId,
			Integer divisionId, String accountName, Integer limit);

	/**
	 * Get the list of companies that are existing in the account combination table.
	 * @param isActiveOnly True if only the active companies are to be retrieved, 
	 * otherwise false.
	 * @param user The logged user.
	 * @return The companies that are configured in the account combination table.
	 */
	Collection<Company> getCompanyByAcctCombination (boolean isActiveOnly, User user);
}
