package eulap.eb.dao;

import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.BankAccount;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.BankReconItem;

/**
 *  Data access object of {@link BankAccount}

 */
public interface BankAccountDao extends Dao<BankAccount>{

	/**
	 * Check if the bank account is unique
	 * @param bankAccount The bank account that will be evaluated.
	 * @param isNumber True if evaluate number, otherwise name
	 * @return True if unique otherwise, false.
	 */
	boolean isUniqueBankAccount (BankAccount bankAccount, boolean isNumber);

	/**
	 * Get all the bank account under a service lease key.
	 * @param serviceLeaseKeyId The service lease key.
	 * @return The list of bank accounts.
	 */
	List<BankAccount> getBankAccounts (int serviceLeaseKeyId);

	/**
	 * Get the bank account object.
	 * @param bankAccountId The bank account id.
	 * @return The bank account object.
	 */
	BankAccount getBankAccountById(int bankAccountId);

	/**
	 * Search for bank accounts.
	 * @param searchCriteria  The search criteria.
	 * @param companyId The company id.
	 * @param status Active or inactive.
	 * @param pageSetting The page setting.
	 * @return The paged result.
	 */
	Page<BankAccount> searchBankAccount(String searchCriteria,
			int companyId, int status, PageSetting pageSetting);

	/**
	 * Get all bank accounts(active/inactive).
	 * @param serviceLeaseKeyId The service lease key.
	 * @return The list os all bank accounts.
	 */
	List<BankAccount> getAllBankAccounts(int serviceLeaseKeyId);

	/**
	 * Get bank accounts by company id.
	 * @param companyId The company id.
	 * @return The list of bank accounts.
	 */
	List<BankAccount> getBankAccounts (Integer companyId);

	/**
	 * Get the list of reconciling items to be used in the report.
	 * @param bankAcctId The id of the bank account.
	 * @param asOfDate The end date of the date range.
	 * @return The list of Reconciling Items.
	 */
	List<BankReconItem> getBankReconItems(int bankAcctId, Date asOfDate, Integer divisionId);

	/**
	 * Get the list of bank account by name
	 * @param name the name of the bank account
	 * @param user The current user logged
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @return The list of bank accounts
	 */
	List<BankAccount> getAllBankAccountsByName (String name, Integer limit, User user, Integer companyId, Integer divisionId);

	/**
	 * Get the bank account object by name
	 * @param name the name of the bank account
	 * @return The bank account object
	 */
	BankAccount getBankAccountByName(String name);

	/**
	 * Get the bank account object by name
	 * @param name the name of the bank account
	 * @param companyIOd The company id
	 * @param divisionId The division id
	 * @return The bank account object
	 */
	BankAccount getBankAccountByName(String name, Integer companyId, Integer divisionId);

	/**
	 * Get all bank accounts by user company.
	 * @param companyId The company id.
	 * @param isActiveOnly True if get all active bank accounts, otherwise all
	 * @return The list of bank accounts.
	 */
	List<BankAccount> getBankAccountsByUser(User user, boolean isActiveOnly);
}