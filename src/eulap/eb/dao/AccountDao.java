
package eulap.eb.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.Account;
import eulap.eb.domain.hibernate.AccountType;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.AccountAnalysisReport;
import eulap.eb.web.dto.AccountBalancesDto;
import eulap.eb.web.dto.AccountDto;
import eulap.eb.web.dto.ISAccountDto;
import eulap.eb.web.dto.ISBSAccountDto;
import eulap.eb.web.dto.JournalEntriesRegisterDto;

/**
 * Account data access object.

 */
public interface AccountDao extends Dao<Account> {

	/**
	 * Get all accounts
	 * @param user The logged in user.
	 * @return Paged collection of accounts.
	 */
	Page<Account> getAccounts(User user);

	/**
	 * Search/filter the accounts by criteria.
	 * @param accountTypeId The account type id.
	 * @param accountNumber The account number.
	 * @param accountName The account name.
	 * @param user The logged in user.
	 * @param searchStatus The search status.
	 * @param isMainAccountOnly True if to display only the main account.
	 * @param pageSetting The page setting.
	 * @return Paged collection of filtered accounts by criteria.
	 */
	Page<Account> searchAccounts(int accountTypeId, String accountNumber, String accountName, User user,
			SearchStatus status, boolean isMainAccountOnly, PageSetting pageSetting);

	/**
	 * Get non contra accounts.
	 * @param user The logged in user.
	 * @return The collection of non contra accounts.
	 */
	Collection<Account> getNonContraAccounts(int accountId, User user);

	/**
	 * Check if the name is unique or not.
	 * @param accountName The account name.
	 * @param user The logged in user.
	 * @return True if the account name is unique otherwise, false.
	 */
	boolean isUniqueAccountName(String accountName, User user);

	/**
	 * Check if the number is unique or not.
	 * @param accountNumber The account number.
	 * @return True if the account number is unique otherwise, false.
	 */
	boolean isUniqueAccountNumber(String accountNumber);

	/**
	 * Get all active accounts.
	 * @param user The logged in user.
	 * @return Collection of active accounts.
	 */
	Collection<Account> getActiveAccounts(User user);

	/**
	 * Get the list of accounts that are existing in the account combination table.
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @param isOrderByNumber Set to true if the ordering of the list is by Account Number, otherwise Account Name.
	 * @return The accounts that are configured in the account combination table.
	 */
	List<Account> getAccountByAcctCombination(int companyId, int divisionId, boolean isOrderByNumber);

	/**
	 * Get the list of accounts by company that are existing in the account combination table.
	 * @param accountName The name of account.
	 * @param companyId The company id.
	 * @return The accounts that are configured in the account combination table.
	 */
	List<Account> getAccountsByCompany(String accountName, String accountNumber, int companyId);

	/**
	 * Get the list of accounts
	 * @param companyId The company id.
	 * @param accountTypeId The account type id.
	 * @param serviceLeaseKeyId The service lease key id.
	 * @return The list of accounts.
	 */
	List<Account> getAccounts (int companyId, int accountTypeId, int serviceLeaseKeyId);

	/**
	 *  Get the list of accounts
	 * @param companyId The company id.
	 * @param serviceLeaseKeyId The service lease key id.
	 * @return The list of accounts.
	 */
	List<Account> getAccounts (int companyId, int serviceLeaseKeyId );

	/**
	 * Get the contra accounts of an account.
	 * @param accountId The account id.
	 * @return The list of contra accounts.
	 */
	List<Account> getContraAccounts (int accountId);

	/**
	 * Get the account object using its number.
	 * @param accountNumber The account number.
	 * @param serviceLeaseKeyId The service lease Id of the logged user.
	 * @return The account object.
	 */
	Account getAccountByNumber (String accountNumber, int serviceLeaseKeyId);

	/**
	 * Get the account object by company id, account type id, and service lease key.
	 * @param companyId The company id.
	 * @param accountTypeId The account type id.
	 * @param serviceLeaseKeyId The service lease key id.
	 * @return The account object.
	 */
	Account getAccount (int companyId, int accountTypeId, int serviceLeaseKeyId);

	/**
	 * Get contra account using its related account Id.
	 * @param accountId The account Id.
	 * @return The account object.
	 */
	Account getContraAcctByRelatedAcct (int accountId);

	/**
	 * Get the account balances of the company
	 * @param companyId The company id.
	 * @param asOfDate As of date.
	 * @return The account balances of the company
	 */
	Page<AccountBalancesDto> getAccountBalances (int companyId, Date asOfDate, PageSetting pageSetting);

	/**
	 * Get the account balance of the selected account.
	 * @param bankAcctId The id of the selected bank account.
	 * @param companyId The id of the company.
	 * @param accountId The id of the account.
	 * @param asOfDate
	 * @return The account balance of the account.
	 */
	AccountBalancesDto getAcctBalance(int bankAcctId, int companyId, int accountId, Date asOfDate);

	/**
	 * Get the total balance of the account.
	 * @param companyId The selected company
	 * @param account The account object
	 * @param asOfDate As of date of the account's balance
	 * @return The total account balance
	 */
	AccountAnalysisReport getAccountBalance (int companyId, Account account, 
			String divisionNumberFrom, String divisionNumberTo, Date asOfDate, String description);

	/**
	 * Get the account analysis report.
	 * @param companyId The company id
	 * @param account The account object
	 * @param fromDate start date
	 * @param toDate end date
	 * @param description Description of the account.
	 * @return The list of accounts. 
	 */
	Page<AccountAnalysisReport> getAccountAnalysisReport (int companyId, Account account,
			String divisionNumberFrom, String divisionNumberTo, Date fromDate, Date toDate,
			String description, PageSetting pageSetting);

	/**
	 * Get the journal entries register report.
	 * @param companyId The company id.
	 * @param fromDate The start date.
	 * @param toDate The end date.
	 * @param statusId The status id.
	 * @return The list of journal entries register.
	 */
	Page<JournalEntriesRegisterDto> getJournalEntriesRegister (Integer companyId, Date fromDate, Date toDate,
			Integer statusId, String source, String refNo, PageSetting pageSetting);

	/**
	 * Get the list of accounts by company and account type.
	 * @param companyId The company id.
	 * @param accountTypeId The account type id.
	 * @return The list of accounts.
	 */
	List<Account> getAcctsByCompanyAndType (Integer companyId, Integer accountTypeId);

	/**
	 * Get the contra account for income statement.
	 * @param companyId The company id.
	 * @param accountId The account id. 
	 * @param asOfDate The as of date.
	 * @return The as of balance account dto.
	 */
	ISAccountDto getAsOfBalanceAccount(Integer companyId, Integer accountId,  Date asOfDate);

	/**
	 * Get the Account object using its account name.
	 * @param name The account name.
	 * @return The Account object, otherwise null.
	 */
	Account getAccountByName(String name, boolean isActiveOnly);

	/**
	 * Get the accounts for Income Statement or Balance Sheet.
	 * @param companyId The company id.
	 * @param accountId The account id.
	 * @param fromDate The start of date range.
	 * @param toDate The end of date range.
	 * @param isIncomeStatement Get the account for income statement if true, 
	 * otherwise for balance sheet.
	 * @param isContraAccount Get the contra account if true, otherwise normal account.
	 * @return
	 */
	ISAccountDto getISOrBSAccounts ( Integer companyId, Integer accountId, Date fromDate, Date toDate,
			boolean isIncomeStatement, boolean isContraAccount);

	/**
	 * Get the list of accounts
	 * @param accountName the account name
	 * @param companyId the company id
	 * @param divisionId the division id
	 * @param limit the limit of items to be showed
	 * @param isOrderByNumber Set to true if ordering is by number, otherwise by name.
	 * @return the list of accounts
	 */
	List<Account> getAccountsByName(String accountName, Integer companyId, Integer divisionId,
			Integer limit, boolean isOrderByNumber);

	/**
	 * Get the general ledger listing report.
	 * @param companyId The company id.
	 * @param fromDate The start date.
	 * @param toDate The end date.
	 * @param statusId The status id.
	 * @return The list of general ledger listing.
	 */
	Page<JournalEntriesRegisterDto> getGeneralLedgerListing(Integer companyId, Date dateFrom, Date dateTo,
			Integer accountId, PageSetting pageSetting);

	/**
	 * Get all accounts by company id.
	 * @param companyId The company id.
	 * @return List of accounts.
	 */
	List<Account> getAccounts(Integer companyId);

	/**
	 * Get the list of account/s based on either account number or name.
	 * @param numberOrName Parameter that represents either account number or account name.
	 * @param The id of the account to be excluded.
	 * @return The list of accounts.
	 */
	List<Account> getAccounts(String numberOrName,  Integer accountId);

	/**
	 * Search/filter the accounts by criteria.
	 * @param accountTypeId The account type id.
	 * @param accountNumber The account number.
	 * @param accountName The account name.
	 * @param user The logged in user.
	 * @param searchStatus The search status.
	 * @param isMainAccountOnly True if to display only the main account.
	 * @param pageSetting The page setting.
	 * @return Paged collection of filtered accounts by criteria.
	 */
	Page<AccountDto> searchAccountWithSubs(int accountTypeId, String accountNumber,
			String accountName, User user, SearchStatus status, boolean isMainAccountOnly, PageSetting pageSetting);

	/**
	 * Check if the account is last level.
	 * @param accountId The account id.
	 * @return True or false.
	 */
	boolean isLastLevel(int accountId);

	/**
	 * Get all the children account of the specific account given by its id.
	 * @param accountId The id of the account to be checked.
	 * @return The list of children.
	 */
	List<AccountDto> getAllChildren(int accountId);

	/**
	 * Get all last level accounts.
	 * Last level accounts refer to any account that has no child account.
	 * @return List of all last level accounts.
	 */
	List<Account> getLastLevelAccounts();

	/**
	 * Get the list of accounts
	 * @param accountName the account name
	 * @param companyId the company id
	 * @param divisionId the division id
	 * @param accountTypeId The account type id
	 * @param limit the limit of items to be showed
	 * @return the list of accounts
	 */
	List<Account> getAccountsByNameAndType(String accountName, Integer companyId, Integer divisionId, Integer limit,
			Integer accountTypeId);

	/**
	 * Get the Account by param.
	 * @param accountName the account name
	 * @param companyId the company id
	 * @param divisionId the division id
	 * @param accountTypeId The account type id
	 * @return The {@link Account}
	 */
	Account getAccountByNameAndType(String accountName, Integer companyId, Integer divisionId, Integer accountTypeId);

	/**
	 * Get the list of accounts by account combination.
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @param accountTypeId The account type id
	 * @param acctNameOrNumber The account name or number filter
	 * @return List of accounts.
	 */
	List<AccountDto> getByCombinationAndType(Integer companyId, Integer divisionId, Integer accountTypeId, String acctNameOrNumber);

	/**
	 * Check if account id is used as parent reference account
	 * @param accountId The account id
	 * @return True if the account is used as parent reference account, otherwise false
	 */
	boolean isUsedAsParentAccount(Integer accountId);

	/**
	 * Get the account balances of the company with division filter.
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @param dateFrom The before date.
	 * @param dateTo As of date.
	 * @return The account balances of the company
	 */
	Page<AccountBalancesDto> getAccountBalances (int companyId, Integer accountId, int divisionId,  Date dateFrom, Date dateTo, PageSetting pageSetting);

	/**
	 * Check if an account or any of its offspring is in account combination.
	 * @param companyId The company filter.
	 * @param accountId The account filter.
	 * @param divisionId The division filter.
	 * @return True if in account combination, otherwise false.
	 */
	boolean isInCombination(int companyId, int accountId, int divisionId) ;

	/**
	 * Get the list of accounts that are existing in the account combination in two division.
	 * @param companyId The company id.
	 * @param divisionFromId The division id.
	 * @param divisionToId The division id.
	 * @return The accounts that are configured in the account combination table.
	 */
	List<Account> getAccountByDivisions(int companyId, int divisionFromId, int divisionToId);

	/**
	 * Get the list of accounts and exclude specific Id
	 * @param accountName The account name
	 * @param companyId The company id
	 * @param divisionId The division id
	 * @param accountId The to be excluded account id
	 * @param limit The number of items to be shown
	 * @param isOrderByName True if ordering is by name, otherwise by number.
	 * @return the list of accounts
	 */
	List<Account> getAccountsAndExcludeId(String accountName, Integer companyId, Integer divisionId,
			Integer accountId, Integer limit, boolean isOrderByName, boolean isExact);

	/**
	 * Get the list of  {@code ISBSAccountDto}  for the Income Statement and Balance Sheet reports.
	 * @param companyId The company filter.
	 * @param accountId The account filter.
	 * @param divisionId The division filter.
	 * @param dateFrom The start date filter.
	 * @param dateTo The end date filter.
	 * @return The List of {@code ISBSAccountDto} result.
	 */
	List<ISBSAccountDto> getISBSAccounts(int companyId, int accountId, int divisionId, 
			List<AccountType> accountTypes, Date dateFrom, Date dateTo, boolean isIs);

	/**
	 * Get the list of  {@code ISBSAccountDto}  for the Income Statement and Balance Sheet reports.
	 * @param companyId The company filter.
	 * @param accountId The account filter.
	 * @param divisionId The division filter.
	 * @param dateFrom The start date filter.
	 * @param dateTo The end date filter.
	 * @return The List of {@code ISBSAccountDto} result.
	 */
	List<ISBSAccountDto> getISBSAccounts(int companyId, int accountId, int divisionId, 
			List<AccountType> accountTypes, Date dateFrom, Date dateTo, boolean isIs, boolean isByMonth);

	/**
	 * Get all accounts configured in AR Customer Account.
	 * @return List of all active AR Accounts of the company.
	 */
	Collection<String> getArAccounts(int companyId, Integer divisionId);

	/**
	 * Get the list of Supplier Accounts for Classification.
	 * @param companyId The id of the company.
	 * @param divisionId The division id.
	 * @return The list of Supplier Accounts.
	 */
	Collection<String> getSupplierAccountNames(int companyId, Integer divisionId);
}
