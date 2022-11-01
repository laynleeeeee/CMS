package eulap.eb.dao;

import java.util.Collection;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.AccountType;

/**
 * Account type data access object.

 *
 */
public interface AccountTypeDao  extends Dao<AccountType>{

	/**
	 * Get all account types.
	 * @param serviceLeaseKeyId The service lease key id of the logged user.
	 * @return Paged collection of account types.
	 */
	Page<AccountType> getAccountTypes (int serviceLeaseKeyId);

	/**
	 * Search/filter the account types by criteria.
	 * @param accountTypeName
	 * @param normalBalanceId The normal balance id.
	 * @param serviceLeaseKeyId The service lease key id of the logged user.
	 * @param pageSetting The page setting.
	 * @return Paged collection of filtered account types by criteria.
	 */
	Page<AccountType> searchAccountTypes (String accountTypeName, int normalBalanceId, int serviceLeaseKeyId, 
			PageSetting pageSetting);

	/**
	 * Check if the name is unique or not.
	 * @param accountTypeName The account type name.
	 * @param serviceLeaseKeyId The service lease key id of the logged user.
	 * @return True if the account type name is unique otherwise, false.
	 */
	boolean isUniqueAccountType (String accountTypeName, int serviceLeaseKeyId);

	/**
	 * Get all active account types.
	 * @param serviceLeaseKeyId The service lease key id.
	 * @return Collection of active account types.
	 */
	Collection<AccountType> getActiveAccountTypes (int serviceLeaseKeyId);

	/**
	 * Get the account types by account class.
	 * @param accountClassId The account class id.
	 * @param isContraAccount true for contra-account, otherwise false.
	 * @param serviceLeaseKeyId The service lease key id.
	 * @return The list of account types.
	 */
	List<AccountType> getAccountTypes (int accountClassId, boolean isContraAccount, int serviceLeaseKeyId);

	/**
	 * Get the account types by account class.
	 * @param accountClassId The account class id.
	 * @param isContraAccount true for contra-account, otherwise false.
	 * @param serviceLeaseKeyId The service lease key id.
	 * @param companyId The selected company id.
	 * @return The list of account types.
	 */
	List<AccountType> getAccountTypes (int accountClassId, boolean isContraAccount, int serviceLeaseKeyId, int companyId);

	/**
	 * Get the list of all active account types
	 * @return The list of all active account types
	 */
	List<AccountType> getAllActiveAccountTypes();
}
