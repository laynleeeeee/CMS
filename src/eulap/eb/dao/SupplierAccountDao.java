package eulap.eb.dao;

import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.SupplierAccount;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.SupplierAcctHistDto;
import eulap.eb.web.dto.SupplierBalancesSummaryDto;

/**
 * A base class that defines the database transactions of SupplierAccounts

 *
 */
public interface SupplierAccountDao extends Dao<SupplierAccount>{
	/**
	 * Validates if the supplier's name is unique
	 * @param supplierAccount The supplier account that will be evaluated.
	 * @return The if the the account is unique otherwise false.
	 */
	boolean isUniqueSupplierAccount (SupplierAccount supplierAccount);

	/**
	 * Get the list of supplier's account.
	 * @param supplierId The supplier id.
	 * @return The list of supplier's account.
	 */
	List<SupplierAccount> getSupplierAccounts (int supplierId, User user);

	/**
	 * Get the list of supplier's account.
	 * @param supplierId The supplier id.
	 * @param divisionId The division id.
	 * @return The list of supplier's account.
	 */
	List<SupplierAccount> getSupplierAccounts (int supplierId, User user, Integer divisionId);

	/**
	 * Search for supplier accounts.
	 * @param searchCriteria The search criteria.
	 * @param pageSetting The page setting.
	 * @return The page result.
	 */
	Page<SupplierAccount> searchSupplierAccount(String searchCriteria, PageSetting pageSetting);

	/**
	 * Get the list of supplier accounts filtered by supplier, company, and division (if provided)
	 * @param supplierId The Id of the supplier.
	 * @param companyId The Id of the company.
	 * @param divisionId The division id
	 * @return The list of supplier accounts (both active and inactive).
	 */
	List<SupplierAccount> getSupplierAccounts (int supplierId, int companyId, Integer divisionId, boolean activeOnly);

	/**
	 * Get the list of supplier accounts filtered by supplier, company, and division (if provided)
	 * @param supplierId The Id of the supplier.
	 * @param supplierAccountId The id of the supplier account.
	 * @param companyId The Id of the company.
	 * @param divisionId The division id
	 * @return The list of supplier accounts (both active and inactive).
	 */
	List<SupplierAccount> getSupplierAccounts (int supplierId, Integer supplierAccountId, int companyId, Integer divisionId, boolean activeOnly);

	/**
	 * Search the supplier accounts.
	 * @param supplierName The supplier name.
	 * @param supplierAcctName The supplier account name.
	 * @param companyId The company id.
	 * @param termId The term id.
	 * @param status The search status.
	 * @param pageSetting The page setting.
	 * @return The page result.
	 */
	Page<SupplierAccount> searchSupplierAccounts(String supplierName, String supplierAcctName, Integer companyId, Integer termId, SearchStatus status, PageSetting pageSetting);

	/**
	 * Get the data to be used in Supplier Account History report.
	 * @param supplierAcctId The unique id of the supplier account.
	 * @param asOfDate The end date of the date range criteria.
	 * @param divisionId The unique id of the division.
	 * @param currencyId The unique id of the currency.
	 * @param pageSetting The page setting.
	 * @return The data to be presented in the report.
	 */
	Page<SupplierAcctHistDto> getSupplierAcctHistoryData(int supplierAcctId, Date dateFrom, int divisionId, int currencyId, PageSetting pageSetting);

	/**
	 * Get the beginning balance of the supplier account as of the date.
	 * @param supplierAcctId The unique id of the supplier account.
	 * @param asOfDate The end date of the date range.
	 * @param divisionId The unique id of the division.
	 * @param currencyId The unique id of the currency.
	 * @return The beginning balance of the supplier account computed from AP Invoice and AP Payment.
	 */
	double getBeginningBalance(Date asOfDate ,Integer supplierAcctId, Integer divisionId, Integer currencyId);

	/**
	 * Get the supplier account by name and company.
	 * @param companyId The company id.
	 * @param supplierAcctName The supplier account name.
	 * @return {@link SupplierAccount}
	 */
	SupplierAccount getSupplierAcctByNameAndCompany(Integer companyId, String supplierAcctName);

	/**
	 * The supplier account balances report data
	 * @param companyId The company id
	 * @param divisionId The division id
	 * @param creditAccountId The supplier credit account id
	 * @param currencyId The currency id
	 * @param asOfDate The as of date
	 * @param balanceOption 0 if exclude zero and negative balance, otherwise show all
	 * @param pageSetting The page setting
	 * @return
	 */
	Page<SupplierBalancesSummaryDto> getSupplierAcctBalancesData(int companyId, int divisionId, int creditAccountId,
			int currencyId, Date asOfDate, int balanceOption, PageSetting pageSetting);
}