package eulap.eb.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.domain.Domain;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.dao.AccountCombinationDao;
import eulap.eb.dao.AccountDao;
import eulap.eb.dao.AccountTypeDao;
import eulap.eb.dao.ArCustomerAcctDao;
import eulap.eb.dao.EBObjectDao;
import eulap.eb.domain.hibernate.Account;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.AccountType;
import eulap.eb.domain.hibernate.ArCustomerAccount;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.AccountDto;
import eulap.eb.web.dto.ChartOfAccountsDto;

/**
 * Account service.

 *
 */
@Service
public class AccountService {
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private AccountTypeDao accountTypeDao;
	@Autowired
	private EBObjectDao ebObjectDao;
	@Autowired
	private ArCustomerAcctDao customerAcctDao;
	@Autowired
	private AccountCombinationDao acctCombiDao;

	/**
	 * Get the collection of active account types.
	 * @return The collection of active account types.
	 */
	public Collection<AccountType> getActiveAccountTypes (User user) {
		return accountTypeDao.getActiveAccountTypes(user.getServiceLeaseKeyId());
	}

	/**
	 * Get the account domain object.
	 * @param accountId The account id.
	 * @return The account object.
	 */
	public Account getAccount (int accountId) {
		return accountDao.get(accountId);
	}

	/**
	 * Get the non contra accounts.
	 * @param acountTypeId The account type id.
	 * @param user The logged in user.
	 * @return The collection of non-contra accounts.
	 */
	public Collection<Account> getRelatedAccounts (int accountTypeId, int accountId, User user) {
		Collection<Account> accounts = new ArrayList<Account>();
		AccountType accountType = accountTypeDao.get(accountTypeId);
		if (accountType.isContraAccount()) 
			accounts = accountDao.getNonContraAccounts(accountId, user);
		return accounts;
	}


	/**
	 * Get all accounts
	 * @param user The logged in user.
	 * @return Paged collection of accounts.
	 */
	public Page<Account> getAccounts (User user) {
		return accountDao.getAccounts(user);
	}

	/**
	 * Search/filter the accounts by criteria.
	 * @param accountTypeId The account type id.
	 * @param accountNumber The account number.
	 * @param accountName The account name.
	 * @param isMainAccountOnly True if to display only the main account.
	 * @param user The logged in user.
	 * @param pageNumber The page number.
	 * @return Paged collection of filtered accounts by criteria.
	 */
	public Page<Account> searchAccounts (int accountTypeId, String accountNumber, String accountName,
			String status, boolean isMainAccountOnly, User user, int pageNumber) {
		SearchStatus searchStatus = SearchStatus.getInstanceOf(status);
		return accountDao.searchAccounts(accountTypeId, accountNumber.trim(), accountName.trim(),
				user, searchStatus, isMainAccountOnly, new PageSetting(pageNumber));
	}

	/**
	 * Save the account domain object.
	 * @param account The account domain object.
	 * @param user The currently logged user.
	 */
	public void saveAccount (Account account, User user) {
		boolean isNewRecord = account.getId() == 0;
		boolean hasNoEbObjectId = false;
		if (!isNewRecord) {
			Account savedAccount = accountDao.get(account.getId());
			if (savedAccount != null && savedAccount.getEbObjectId() == null) {
				hasNoEbObjectId = true;
			}
			if (account.getAccountTypeId().intValue() != savedAccount.getAccountTypeId().intValue()) {
				updateAllChildrenAcctType(account.getId(), account.getAccountTypeId().intValue());
			}
		}
		if(isNewRecord ||  hasNoEbObjectId){
			EBObject eb = new EBObject();
			AuditUtil.addAudit(eb, new Audit(user.getId(), true, new Date()));
			eb.setObjectTypeId(Account.OBJECT_TYPE_ID);
			ebObjectDao.save(eb);
			account.setEbObjectId(eb.getId());
		}
		AuditUtil.addAudit(account, new Audit(user.getId(), isNewRecord, new Date ()));
		if (account.getRelatedAccountId() == 0)
			account.setRelatedAccountId(null);
		account.setAccountName(account.getAccountName().trim());
		account.setNumber(account.getNumber().trim());
		account.setDescription(account.getDescription().trim());
		account.setServiceLeaseKeyId(user.getServiceLeaseKeyId());
		// The sub account must inherit the account type of its parent.
		if (account.getParentAccountId() != null) {
			Account parentAcct = accountDao.get(account.getParentAccountId());
			account.setAccountTypeId(parentAcct.getAccountTypeId());
		}
		accountDao.saveOrUpdate(account);
	}


	private void updateAllChildrenAcctType(int accountId, int accountTypeId) {
		List<AccountDto> acctDtos = getAllChildren(accountId);
		if (!acctDtos.isEmpty()) {
			Set<Integer> acctIds = new HashSet<>();
			populateAcctIds(acctIds, acctDtos);
			Account account = null;
			List<Domain> toBeUpdated = new ArrayList<>();
			for (Integer a : acctIds) {
				account = accountDao.get(a);
				account.setAccountTypeId(accountTypeId);
				toBeUpdated.add(account);
			}
			accountDao.batchSaveOrUpdate(toBeUpdated);
		}
	}

	private void populateAcctIds(Set<Integer> acctIds, List<AccountDto> acctDtos) {
		for (AccountDto a : acctDtos) {
			acctIds.add(a.getId());
			if (a.getChildrenAccount() != null) {
				populateAcctIds(acctIds, a.getChildrenAccount());
			}
		}
	}

	/**
	 * Evaluate if the account name is unique or not
	 * @param accountType The account object.
	 * @param user The logged in user.
	 * @return True if the account name is not existing in the database,
	 * otherwise false.
	 */
	public boolean isUniqueAccountName (Account account, User user) {
		if (account.getId() == 0)
			return accountDao.isUniqueAccountName(account.getAccountName().trim(), user);
		Account oldAccount = accountDao.get(account.getId());
		// Account name is the same.
		if (account.getAccountName().trim().equalsIgnoreCase(oldAccount.getAccountName().trim()))
			return true;
		return accountDao.isUniqueAccountName(account.getAccountName().trim(), user);
	}

	/**
	 * Evaluate if the account name is unique or not
	 * @param accountType The account object.
	 * @return True if the account name is not existing in the database,
	 * otherwise false.
	 */
	public boolean isUniqueAccountNumber (Account account) {
		if (account.getId() == 0)
			return accountDao.isUniqueAccountNumber(account.getNumber().trim());
		Account oldAccount = accountDao.get(account.getId());
		// Account name is the same.
		if (account.getNumber().trim().equalsIgnoreCase(oldAccount.getNumber().trim()))
			return true;
		return accountDao.isUniqueAccountNumber(account.getNumber().trim());
	}

	/**
	 * Get all active accounts.
	 * @param user The logged in user.
	 * @return Collection of active accounts.
	 */
	public Collection<Account> getActiveAccounts (User user, int accountId) {
		Collection<Account> accounts = accountDao.getActiveAccounts(user);
		return appendInactiveAccount(accounts, accountId);
	}

	/**
	 * Get the accounts given the company and division id that was mapped in account combination.
	 * @param companyId The company id
	 * @param divisionId The division id.
	 * @return list of accounts.
	 */
	public List<Account> getAccounts (int companyId, int divisionId) {
		return getAccounts(companyId, divisionId, true);
	}

	/**
	 * Get the list of {@link Account} using the id of company and division.
	 * @param companyId the id of the company.
	 * @param divisionId The id of the division.
	 * @param isOrderByNumber Ordering of the list, set to true if
	 *  order by number, otherwise false, order by name.
	 * @return The list of Accounts.
	 */
	private List<Account> getAccounts(int companyId, int divisionId, boolean isOrderByNumber) {
		return accountDao.getAccountByAcctCombination(companyId,
				divisionId, isOrderByNumber);
	}

	/**
	 * Get the list of accounts by company that are existing in the account
	 * combination table.
	 * @param companyId The company id.
	 * @return The accounts that are configured in the account combination table.
	 */
	public List<Account> getAccounts (String accountName, String accountNumber, int companyId) {
		return accountDao.getAccountsByCompany(accountName, accountNumber, companyId);
	}

	/**
	 * Get the account by its number.
	 * @param accountNumber The account number.
	 * @param user The logged user.
	 * @return The account object.
	 */
	public Account getAccountByNumber (String accountNumber, User user) {
		return accountDao.getAccountByNumber(accountNumber, user.getServiceLeaseKeyId());
	}

	/**
	 * Get the Account object.
	 * @param name The account name.
	 * @param isActiveOnly True if to include only active accounts, otherwise false.
	 * @return The {@link Account}, otherwise null.
	 */
	public Account getAcctByName(String name, boolean isActiveOnly) {
		if(name == null) {
			return null;
		}
		return accountDao.getAccountByName(name.trim(), isActiveOnly);
	}

	/**
	 * Get the list of account by name criteria
	 * @param name The account name criteria
	 * @param companyId The company id
	 * @param divisionId The division id
	 * @param limit The number of items to be shown on the list
	 * @return The list of account by name criteria
	 */
	public List<Account> getAccountsByName(String name, Integer companyId, Integer divisionId, Integer limit) {
		return accountDao.getAccountsByName(name, companyId, divisionId, limit, true);
	}

	/**
	 * Get the collection of active accounts and the selected account if inactive.
	 * @return The collection of accounts filtered by companyId and divisionId.
	 */
	public Collection<Account> getAccounts(int companyId, int divisionId, int accountId) {
		Collection<Account> accounts = getAccounts(companyId, divisionId);
		return appendInactiveAccount(accounts, accountId);
	}

	/**
	 * Method to append the inactive account to the list.
	 * @param accounts The list of accounts.
	 * @param accountId The id of the account.
	 * @return The list of accounts with the selected inactive account.
	 */
	private Collection<Account> appendInactiveAccount(Collection<Account> accounts, int accountId) {
		if(accountId != 0) {
			Collection<Integer> accountIds = new ArrayList<Integer>();
			for (Account account : accounts) {
				accountIds.add(account.getId());
			}
			if(!accountIds.contains(accountId))
				accounts.add(getAccount(accountId));
		}
		return accounts;
	}

	/**
	 * Get the withdrawal Customer Account by customer account.
	 * @param arCustomerAcctId The customer account id.
	 * @return The account.
	 */
	public Account getwithdrawalCustAcct(Integer arCustomerAcctId) {
		ArCustomerAccount arCustomerAccount = customerAcctDao.get(arCustomerAcctId);
		AccountCombination combination = acctCombiDao.get(arCustomerAccount.getDefaultWithdrawalSlipACId());
		Account account = null;
		if(combination != null){
			account = combination.getAccount();
		}
		return account;
	}

	/**
	 * Get the list of account/s based on either account number or name.
	 * @param numberOrName Parameter that represents either account number or account name.
	 * @param The id of the account to be excluded.
	 * @return The list of accounts.
	 */
	public List<Account> getAccounts(String numberOrName, Integer accountId) {
		return accountDao.getAccounts(numberOrName, accountId);
	}

	/**
	 * Search/filter the accounts by criteria.
	 * @param accountTypeId The account type id.
	 * @param accountNumber The account number.
	 * @param accountName The account name.
	 * @param user The logged in user.
	 * @param searchStatus The search status.
	 * @param isMainAccountOnly True if to display only the main account.
	 * @param pageNumber The page number.
	 * @return Paged collection of filtered accounts by criteria.
	 */
	public Page<AccountDto> searchAccountWithSubs(int accountTypeId, String accountNumber,
			String accountName, String status, User user, int pageNumber) {
		SearchStatus searchStatus = SearchStatus.getInstanceOf(status);
		boolean isMainAccountOnly = accountNumber.trim().isEmpty() && accountName.trim().isEmpty();
		return accountDao.searchAccountWithSubs(accountTypeId, accountNumber, accountName, user,
				searchStatus, isMainAccountOnly, new PageSetting(pageNumber, PageSetting.MAX_ADMIN_RECORD));
	}

	/**
	 * Get all the children account of the specific account given by its id.
	 * @param accountId The id of the account to be checked.
	 * @return The list of children.
	 */
	public List<AccountDto> getAllChildren(int accountId) {
		List<AccountDto> children = accountDao.getAllChildren(accountId);
		addChildren(children);
		return children;
	}

	private void addChildren(List<AccountDto> children) {
		List<AccountDto> tmpList = null;
		if (!children.isEmpty()) {
			for (AccountDto l : children) {
				tmpList = accountDao.getAllChildren(l.getId());
				if (!tmpList.isEmpty()) {
					l.setChildrenAccount(tmpList);
					addChildren(tmpList);
				}
			}
		}
	}

	/**
	 * Function to get all accounts for printout.
	 * @param user The logged user.
	 * @param accountTypeId Account type id.
	 * @return The list of accounts for printout.
	 */
	public List<AccountDto> genereateAcctPrintout(User user, Integer accountTypeId) {
		List<AccountDto> accts = (List<AccountDto>) accountDao.searchAccountWithSubs(accountTypeId, "", "", user,  SearchStatus.getInstanceOf("All"), 
				true, new PageSetting(PageSetting.START_PAGE, PageSetting.NO_PAGE_CONSTRAINT)).getData();
		for (AccountDto a : accts) {
			a.setChildrenAccount(getAllChildren(a.getId()));
		}
		return accts;
	}

	/**
	 * Get all last level accounts.
	 * Last level accounts refer to any account that has no child account.
	 * @return List of all last level accounts.
	 */
	public List<Account> getLastLevelAccounts(Integer accountId) {
		List<Account> accounts = accountDao.getLastLevelAccounts();
		return (List<Account>) appendInactiveAccount(accounts, accountId);
	}

	/**
	 * Generate Chart of Accounts details.
	 * @return The list of {@link ChartOfAccountsDto}
	 */
	public List<ChartOfAccountsDto> generateChartOfAccounts(User user) {
		List<AccountType> accountTypes = accountTypeDao.getAllActiveAccountTypes();
		List<ChartOfAccountsDto> currAssets = new ArrayList<ChartOfAccountsDto>();
		List<ChartOfAccountsDto> nonCurrAssets = new ArrayList<ChartOfAccountsDto>();
		List<ChartOfAccountsDto> currLiabilities = new ArrayList<ChartOfAccountsDto>();
		List<ChartOfAccountsDto> nonCurrLiabilities = new ArrayList<ChartOfAccountsDto>();
		List<ChartOfAccountsDto> equities = new ArrayList<ChartOfAccountsDto>();
		List<ChartOfAccountsDto> revenues = new ArrayList<ChartOfAccountsDto>();
		List<ChartOfAccountsDto> others = new ArrayList<ChartOfAccountsDto>();
		List<ChartOfAccountsDto> directLabors = new ArrayList<ChartOfAccountsDto>();
		List<ChartOfAccountsDto> directMaterials = new ArrayList<ChartOfAccountsDto>();
		List<ChartOfAccountsDto> inventoryAdjs = new ArrayList<ChartOfAccountsDto>();
		List<ChartOfAccountsDto> mfgOverheads = new ArrayList<ChartOfAccountsDto>();
		List<ChartOfAccountsDto> mfgOverheadRms = new ArrayList<ChartOfAccountsDto>();
		List<ChartOfAccountsDto> sellings = new ArrayList<ChartOfAccountsDto>();
		List<ChartOfAccountsDto> genAdmins = new ArrayList<ChartOfAccountsDto>();
		List<ChartOfAccountsDto> deprecations = new ArrayList<ChartOfAccountsDto>();
		List<ChartOfAccountsDto> interestIncomes = new ArrayList<ChartOfAccountsDto>();
		List<ChartOfAccountsDto> otherIncomes = new ArrayList<ChartOfAccountsDto>();
		List<ChartOfAccountsDto> otherExpenses = new ArrayList<ChartOfAccountsDto>();
		List<ChartOfAccountsDto> supenseAccts = new ArrayList<ChartOfAccountsDto>();
		List<ChartOfAccountsDto> chartOfAccountsDtos = new ArrayList<ChartOfAccountsDto>();
		ChartOfAccountsDto chartOfAccountsDto = null;
		for (AccountType at : accountTypes) {
			chartOfAccountsDto = new ChartOfAccountsDto();
			Integer accountTypeId = at.getId();
			if (accountTypeId.equals(1)) {
				chartOfAccountsDto.setAccountType(at.getName());
				chartOfAccountsDto.setAccountDtos(genereateAcctPrintout(user, accountTypeId));
				currAssets.add(chartOfAccountsDto);
			} else if (accountTypeId.equals(2) || accountTypeId.equals(10)
					|| accountTypeId.equals(11)) {
				chartOfAccountsDto.setAccountType(at.getName());
				chartOfAccountsDto.setAccountDtos(genereateAcctPrintout(user, accountTypeId));
				nonCurrAssets.add(chartOfAccountsDto);
			} else if (accountTypeId.equals(3)) {
				chartOfAccountsDto.setAccountType(at.getName());
				chartOfAccountsDto.setAccountDtos(genereateAcctPrintout(user, accountTypeId));
				currLiabilities.add(chartOfAccountsDto);
			} else if (accountTypeId.equals(4) || accountTypeId.equals(12)
					|| accountTypeId.equals(13)) {
				chartOfAccountsDto.setAccountType(at.getName());
				chartOfAccountsDto.setAccountDtos(genereateAcctPrintout(user, accountTypeId));
				nonCurrLiabilities.add(chartOfAccountsDto);
			} else if (accountTypeId.equals(8) || accountTypeId.equals(14)
					|| accountTypeId.equals(15)) {
				chartOfAccountsDto.setAccountType(at.getName());
				chartOfAccountsDto.setAccountDtos(genereateAcctPrintout(user, accountTypeId));
				equities.add(chartOfAccountsDto);
			} else if (accountTypeId.equals(5) || accountTypeId.equals(16)) {
				chartOfAccountsDto.setAccountType(at.getName());
				chartOfAccountsDto.setAccountDtos(genereateAcctPrintout(user, accountTypeId));
				revenues.add(chartOfAccountsDto);
			} else if (accountTypeId.equals(18)) {
				chartOfAccountsDto.setAccountType(at.getName());
				chartOfAccountsDto.setAccountDtos(genereateAcctPrintout(user, accountTypeId));
				directLabors.add(chartOfAccountsDto);
			} else if (accountTypeId.equals(17)) {
				chartOfAccountsDto.setAccountType(at.getName());
				chartOfAccountsDto.setAccountDtos(genereateAcctPrintout(user, accountTypeId));
				directMaterials.add(chartOfAccountsDto);
			} else if (accountTypeId.equals(30)) {
				chartOfAccountsDto.setAccountType(at.getName());
				chartOfAccountsDto.setAccountDtos(genereateAcctPrintout(user, accountTypeId));
				inventoryAdjs.add(chartOfAccountsDto);
			} else if (accountTypeId.equals(19)) {
				chartOfAccountsDto.setAccountType(at.getName());
				chartOfAccountsDto.setAccountDtos(genereateAcctPrintout(user, accountTypeId));
				mfgOverheads.add(chartOfAccountsDto);
			} else if (accountTypeId.equals(20)) {
				chartOfAccountsDto.setAccountType(at.getName());
				chartOfAccountsDto.setAccountDtos(genereateAcctPrintout(user, accountTypeId));
				mfgOverheadRms.add(chartOfAccountsDto);
			} else if (accountTypeId.equals(21)) {
				chartOfAccountsDto.setAccountType(at.getName());
				chartOfAccountsDto.setAccountDtos(genereateAcctPrintout(user, accountTypeId));
				sellings.add(chartOfAccountsDto);
			} else if (accountTypeId.equals(22)) {
				chartOfAccountsDto.setAccountType(at.getName());
				chartOfAccountsDto.setAccountDtos(genereateAcctPrintout(user, accountTypeId));
				genAdmins.add(chartOfAccountsDto);
			} else if (accountTypeId.equals(23)) {
				chartOfAccountsDto.setAccountType(at.getName());
				chartOfAccountsDto.setAccountDtos(genereateAcctPrintout(user, accountTypeId));
				deprecations.add(chartOfAccountsDto);
			} else if (accountTypeId.equals(24)) {
				chartOfAccountsDto.setAccountType(at.getName());
				chartOfAccountsDto.setAccountDtos(genereateAcctPrintout(user, accountTypeId));
				interestIncomes.add(chartOfAccountsDto);
			} else if (accountTypeId.equals(25)) {
				chartOfAccountsDto.setAccountType(at.getName());
				chartOfAccountsDto.setAccountDtos(genereateAcctPrintout(user, accountTypeId));
				otherIncomes.add(chartOfAccountsDto);
			} else if (accountTypeId.equals(26)) {
				chartOfAccountsDto.setAccountType(at.getName());
				chartOfAccountsDto.setAccountDtos(genereateAcctPrintout(user, accountTypeId));
				otherExpenses.add(chartOfAccountsDto);
			} else if (accountTypeId.equals(29)) {
				chartOfAccountsDto.setAccountType(at.getName());
				chartOfAccountsDto.setAccountDtos(genereateAcctPrintout(user, accountTypeId));
				supenseAccts.add(chartOfAccountsDto);
			} else {
				chartOfAccountsDto.setAccountType(at.getName());
				chartOfAccountsDto.setAccountDtos(genereateAcctPrintout(user, accountTypeId));
				others.add(chartOfAccountsDto);
			}
		}
		chartOfAccountsDtos.addAll(currAssets);
		chartOfAccountsDtos.addAll(nonCurrAssets);
		chartOfAccountsDtos.addAll(currLiabilities);
		chartOfAccountsDtos.addAll(nonCurrLiabilities);
		chartOfAccountsDtos.addAll(equities);
		chartOfAccountsDtos.addAll(revenues);
		chartOfAccountsDtos.addAll(directLabors);
		chartOfAccountsDtos.addAll(directMaterials);
		chartOfAccountsDtos.addAll(inventoryAdjs);
		chartOfAccountsDtos.addAll(mfgOverheads);
		chartOfAccountsDtos.addAll(mfgOverheadRms);
		chartOfAccountsDtos.addAll(sellings);
		chartOfAccountsDtos.addAll(genAdmins);
		chartOfAccountsDtos.addAll(deprecations);
		chartOfAccountsDtos.addAll(interestIncomes);
		chartOfAccountsDtos.addAll(otherIncomes);
		chartOfAccountsDtos.addAll(otherExpenses);
		chartOfAccountsDtos.addAll(supenseAccts);
		chartOfAccountsDtos.addAll(others);
		return chartOfAccountsDtos;
	}

	/**
	 * Get the account level based on the parent.
	 * @param parentAccountId The parent account id.
	 * @return The account level.
	 */
	public int getAccountLevelByParent(Integer parentAccountId, Integer accountId, boolean isIncludeChildLevel) {
		int level = 1;
		if (parentAccountId == null) {
			return level;
		}
		Account account = accountDao.get(parentAccountId);
		if (account != null) {
			level += getAccountLevelByParent(account.getParentAccountId(), null, isIncludeChildLevel);
			if (isIncludeChildLevel) {
				level += countChildLevels(accountId);
			}
		}
 		return level;
	}

	/**
	 * Counts how many levels below is the lowest level child of the selected account if there are child accounts.
	 * @param accountId The account id.
	 * @return The number of levels below the lowest level child account is from the selected account.
	 */
	public int countChildLevels(Integer accountId) {
		int accntLvl = 1;
		if(accountId != null) {
			List<Account> childAccnts = accountDao.getAllByRefId(Account.FIELD.parentAccountId.name(), accountId);
			if(childAccnts != null && !childAccnts.isEmpty()) {
				for(Account childAccnt : childAccnts) {
					accntLvl += countChildLevels(childAccnt.getId());
					// End loop if there is a max level account found.
					if(accntLvl == Account.MAX_LEVEL) {
						break;
					}
				}
			} else {
				return 0;
			}
		} else {
			return 0;
		}
		return accntLvl;
	}

	/**
	 * Checks if the selected parent account is already a child account of the account selected.
	 * @param accountId The account id.
	 * @param parentId The parent account id.
	 * @return True if the parent account is selected is already a child of the selected account, otherwise false.
	 */
	public boolean isSlctdParentAChild(Integer accountId, Integer parentId) {
		boolean isSlctdParentAChild = false;
		List<Account> childAccnts = accountDao.getAllByRefId(Account.FIELD.parentAccountId.name(), accountId);
		if(childAccnts != null && !childAccnts.isEmpty()) {
			for(Account childAccnt : childAccnts) {
				if(childAccnt.getId() != parentId) {
					isSlctdParentAChild = isSlctdParentAChild(childAccnt.getId(), parentId);
				} else {
					isSlctdParentAChild = true;
				}
				if(isSlctdParentAChild == true) {
					break;
				}
			}
		}
		return isSlctdParentAChild;
	}

	/** Get the list of accounts
	 * @param accountName the account name
	 * @param companyId the company id
	 * @param accountId the account id
	 * @param accountTypeId The account type id
	 * @param limit the limit of items to be showed
	 * @return The list of accounts
	 */
	public List<Account> getAccountsByNameAndType(String accountName, Integer companyId, Integer accountId,
			Integer accountTypeId, Integer limit) {
		return accountDao.getAccountsByNameAndType(accountName, companyId, accountId, limit, accountTypeId);
	}

	/**
	 * Get the Account by param.
	 * @param accountName the account name
	 * @param companyId the company id
	 * @param accountId the account id
	 * @param accountTypeId The account type id
	 * @return The {@link Account}
	 */
	public Account getAccountByNameAndType(String accountName, Integer companyId, Integer accountId,
			Integer accountTypeId) {
		return accountDao.getAccountByNameAndType(accountName, companyId, accountId, accountTypeId);
	}

	/**
	 * Get the list of accounts by account combination and level.
	 * @param companyId The company id.
	 * @return List of accounts.
	 */
	public List<AccountDto> getByCombinationAndType(Integer companyId, Integer divisionId, Integer accountTypeId, String acctNameOrNumber) {
		List<AccountDto> accts = accountDao.getByCombinationAndType(companyId, divisionId, accountTypeId, acctNameOrNumber);
		if (!accts.isEmpty()) {
			for (AccountDto a : accts) {
				a.setInCombination(true);
				addParent(a);
			}
		}
		return accts;
	}

	/**
	 * Add parent object.
	 * @param a The {@code AccountDto} object where the parent object to be added.
	 */
	public void addParent(AccountDto a) {
		if (a.getPaId() != null && a.getPaId().intValue() != 0) {
			a.setParentAccount(domain2Dto(accountDao.get(a.getPaId())));
			a.setLevel(getAccountLevelByParent(a.getPaId(), null, false));
			addParent(a.getParentAccount());
		} else {
			a.setLevel(1);
		}
	}

	/**
	 * Convert the domain division to dto.
	 * @param division The domain division.
	 * @return The division dto.
	 */
	public AccountDto domain2Dto(Account account) {
		return AccountDto.getInstanceOf(account.getId(), account.getNumber(), account.getAccountName(), 
				account.getDescription(), account.getParentAccountId() != null ? account.getParentAccountId() : 0, 
				account.getAccountType().getName(), account.getParentAccount() != null ? account.getParentAccount().getAccountName() : "", 
				account.isActive(), account.getParentAccountId() == null);
	}

	/**
	 * Check if account id is used as parent reference account
	 * @param accountId The account id
	 * @return True if the account is used as parent reference account, otherwise false
	 */
	public boolean isUsedAsParentAccount(Integer accountId) {
		return accountDao.isUsedAsParentAccount(accountId);
	}

	/**
	 * Get the list of accounts by company through account combination and account level.
	 * @param companyId The company filter.
	 * @param level The level filter.
	 * @param isAddChildren True to add children, otherwise false.
	 * @return The list of accounts.
	 */
	public List<AccountDto> getByCompanyAndLevel(Integer companyId, Integer divisionId, Integer accountTypeId, int level, boolean isAddChildren) {
		List<AccountDto> acctsByLevel = new ArrayList<>();
		List<AccountDto> acctsByCompany =  getByCombinationAndType(companyId, divisionId, accountTypeId, null);
		AccountDto byLevel = null;
		for (AccountDto a : acctsByCompany) {
			byLevel = getByLevel(a, level);
			if (byLevel != null) {
				if (isAddChildren) {
					byLevel.setChildrenAccount(getAllChildren(byLevel.getId()));
				}
				acctsByLevel.add(byLevel);
			}
		}
		return acctsByLevel;
	}

	private AccountDto getByLevel(AccountDto a, int level) {
		if (level == a.getLevel()) {
			return a;
		}
		if (a.getParentAccount() == null) {
			return null;
		}
		return getByLevel(a.getParentAccount(), level);
	}

	/**
	 * Check if an account or any of its offspring is in account combination.
	 * @param companyId The company filter.
	 * @param accountId The account filter.
	 * @param divisionId The division filter.
	 * @return True if in account combination, otherwise false.
	 */
	public boolean isInCombination(int companyId, int accountId, int divisionId) {
		boolean isInCombination = accountDao.isInCombination(companyId, accountId, divisionId);
		if (isInCombination) {
			return true;
		}
		List<AccountDto> accounts = accountDao.getAllChildren(accountId);
		if (!accounts.isEmpty()) {
			for (AccountDto a : accounts) {
				return isInCombination(companyId, a.getId(), divisionId);
			}
		}
		return false;
	}

	/**
	 * Get the list of accounts and exclude specific Id
	 * @param accountName The account name
	 * @param companyId The company id
	 * @param divisionId The division id
	 * @param accountId The to be excluded account id
	 * @param limit The number of items to be shown
	 * @return the list of accounts
	 */
	public List<Account> getAccountAndExcludeId(String accountName, Integer companyId, Integer divisionId,
			Integer accountId, Integer limit, boolean isExact) {
		return accountDao.getAccountsAndExcludeId(accountName, companyId, divisionId, accountId, limit, true, isExact);
	}
}
