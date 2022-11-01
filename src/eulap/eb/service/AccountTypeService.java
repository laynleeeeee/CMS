package eulap.eb.service;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.domain.Audit;
import bp.web.ar.AuditUtil;

import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.AccountClassDao;
import eulap.eb.dao.AccountTypeDao;
import eulap.eb.dao.NormalBalanceDao;
import eulap.eb.domain.hibernate.AccountClass;
import eulap.eb.domain.hibernate.AccountType;
import eulap.eb.domain.hibernate.NormalBalance;
import eulap.eb.domain.hibernate.User;

/**
 * Account type service.

 *
 */
@Service
public class AccountTypeService {
	@Autowired
	private NormalBalanceDao normalBalanceDao;
	@Autowired
	private AccountTypeDao accountTypeDao;
	@Autowired
	private AccountClassDao accountClassDao;

	/**
	 * Get all the normal balances.
	 * @return Collection of normal balances.
	 */
	public Collection<NormalBalance> getNormalBalances () {
		return normalBalanceDao.getAll();
	}

	/**
	 * Get the account type object.
	 * @param accountTypeId The account type id.
	 * @return The account type object.
	 */
	public AccountType getAccountType (int accountTypeId) {
		return accountTypeDao.get(accountTypeId);
	}

	/**
	 * Load all the account types.
	 * @param user The logged user.
	 * @return Paged list of account types.
	 */
	public Page<AccountType> getAccountTypes (User user) {
		return accountTypeDao.getAccountTypes(user.getServiceLeaseKeyId());
	}

	/**
	 * Search/filter the account types by name.
	 * @param accountTypeName
	 * @param normalBalanceId The normal balance id.
	 * @param pageNumber The page number.
	 * @param user The logged user.
	 * @return Paged collection of filtered account types by name.
	 */
	public Page<AccountType> searchAccountTypes (String accountTypeName, int normalBalanceId, int pageNumber, User user) {
		return accountTypeDao.searchAccountTypes(accountTypeName.trim(), normalBalanceId,
				user.getServiceLeaseKeyId(), new PageSetting(pageNumber));
	}

	/**
	 * Save the account type domain object.
	 * @param accountType The account type object to be saved.
	 * @param user The currently logged user.
	 */
	public void saveAccountType (AccountType accountType, User user) {
		boolean isNewRecord = accountType.getId() == 0 ? true : false;
		AuditUtil.addAudit(accountType, new Audit (user.getId(), isNewRecord, new Date ()));
		accountType.setName(accountType.getName().trim());
		accountTypeDao.saveOrUpdate(accountType);
	}

	/**
	 * Evaluate if the account type name is unique or not
	 * @param accountType The account type object.
	 * @return True if the account type name is not existing in the database,
	 * otherwise false.
	 */
	public boolean isUniqueAccountType (AccountType accountType, User user) {
		if (accountType.getId() == 0)
			return accountTypeDao.isUniqueAccountType(accountType.getName().trim(), user.getServiceLeaseKeyId());
		AccountType oldAccountType = accountTypeDao.get(accountType.getId());
		// Account type name is the same.
		if (accountType.getName().trim().equalsIgnoreCase(oldAccountType.getName().trim()))
			return true;
		return accountTypeDao.isUniqueAccountType(accountType.getName().trim(), user.getServiceLeaseKeyId());
	}

	/**
	 * Get the collection of account classes.
	 * @param user The logged user.
	 * @return The collection of account classes.
	 */
	public Collection<AccountClass> getAccountClasses (User user) {
		return accountClassDao.getAccountClasses(user);
	}
}
