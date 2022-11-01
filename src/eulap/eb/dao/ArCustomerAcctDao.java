package eulap.eb.dao;

import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.ArCustomerAccount;
import eulap.eb.service.report.CustomerAccountHistoryParam;
import eulap.eb.service.report.StatementOfAccountParam;
import eulap.eb.web.dto.CustomerAccountHistoryDto;
import eulap.eb.web.dto.CustomerBalancesSummaryDto;
import eulap.eb.web.dto.StatementOfAccountDto;

/**
 * Data Access Object of {@link ArCustomerAccount}

 *
 */
public interface ArCustomerAcctDao extends Dao<ArCustomerAccount>{

	/**
	 * Evaluate the AR Customer Account name if unique.
	 * @param name The name to be evaluated.
	 * @return True if unique, false if duplicate.
	 */
	public boolean isUniqueName(String name);

	/**
	 * Search the AR Customer Account.
	 * @param searchCriteria The search criteria.
	 * @param pageSetting The page setting.
	 * @return The paged result.
	 */
	Page<ArCustomerAccount> searchAccounts(String searchCriteria, PageSetting pageSetting);

	/**
	 * Get the AR customer accounts by customer and company.
	 * @param arCustomerId The AR customer id.
	 * @param companyId The company id.
	 * @param divisionId The division id
	 * @return The list of AR customer accounts.
 	 */
	List<ArCustomerAccount> getArCustomerAccounts (Integer arCustomerId, Integer companyId, Integer divisionId);

	/**
	 * Get the AR customer accounts by customer and company.
	 * @param arCustomerId The AR customer id.
	 * @param companyId The company id.
	 * @param divisionId The division id
	 * @param activeOnly Active only
	 * @return The list of AR customer accounts.
 	 */
	List<ArCustomerAccount> getArCustomerAccounts (Integer arCustomerId, Integer companyId, Integer divisionId, boolean activeOnly);

	/**
	 * Get the Customer's account history.
	 * @param param The parameter that will be used in generating the report.
	 * @return The list of customer account history.
	 */
	List<CustomerAccountHistoryDto> getCustomerAccountHistory (CustomerAccountHistoryParam param);

	/**
	 * Get the total transactions and total receipts of the AR Customer Account.
	 * @param customerAccountId The unique id of the AR Customer Account.
	 * @return The total transactions and total receipts.
	 */
	CustomerAccountHistoryDto getTotalTransactionsAndReceipts(int customerAccountId);

	/**
	 * Search the customer accounts.
	 * @param customerName The customer name.
	 * @param customerAcctName The customer account name.
	 * @param companyId The company id.
	 * @param termId The term id.
	 * @param status The search status.
	 * @param pageSetting The page setting.
	 * @return The page result.
	 */
	Page<ArCustomerAccount> searchCustomerAccounts(Integer divisionId, String customerName, String customerAcctName, Integer companyId,
			Integer termId, SearchStatus status, PageSetting pageSetting);

	/**
	 * Get the previous transaction balance.
	 * @param param The selected parameter of the user.
	 * @return The previous balance amount
	 */
	Double getPreviousBalanceAmount(StatementOfAccountParam param);

	/**
	 * Get AR customer account.
	 * @param companyId The selected company id.
	 * @param arCustomerId The selected customer id.
	 * @return The object AR customer account.
	 */
	ArCustomerAccount getArCustomerAccount (Integer companyId, Integer arCustomerId);

	/**
	 * Get the customer account history.
	 * @param param The selected parameter of the user.
	 * @param pageSetting The page settings
	 * @return The customer account history.
	 */
	public Page<CustomerAccountHistoryDto> getCustomerAccountHistoryReport(CustomerAccountHistoryParam param, PageSetting pageSetting);

	/**
	 * Get the customer account balances summary.
	 * @param customerAcctId The customer Account ID
	 * @param companyId The company ID
	 *  @param companyId The division ID
	 * @param balanceOption The balance option, set to 0 if to display only 
	 * unpaid and partially paid transactions, otherwise -1 to display all.
	 * @param asOfDate The as of date
	 *  @param companyId The currency ID
	 * @param pageSetting The page setting
	 * @return The customer account balances summary.
	 */
	public Page<CustomerBalancesSummaryDto> getCustomerBalancesSummaryReport(Integer customerAcctId,
			Integer companyId, Integer divisionId, Integer balanceOption, Date asOfDate, Integer currencyId,
			Integer accountId, PageSetting pageSetting);

	/**
	 * Get the transactions for SOA report.
	 * @param param The container that holds the parameter.
	 * @param pageSetting The page setting.
	 * @return The list of transaction in paging.
	 */
	public Page<StatementOfAccountDto> getSoaData(StatementOfAccountParam param, PageSetting pageSetting);

	/**
	 * Get the transactions for SOA report with Account Collection reference sequence number.
	 * @param param The container that holds the parameter.
	 * @param pageSetting The page setting.
	 * @return The list of transaction in paging.
	 */
	public Page<StatementOfAccountDto> getSoaDataWithAcReference(StatementOfAccountParam param, PageSetting pageSetting);

	/**
	 * Get the invoices and transactions for SOA report with Account Collection.
	 * @param param The container that holds the parameter.
	 * @param pageSetting The page setting.
	 * @return The list of transaction in paging.
	 */
	public Page<StatementOfAccountDto> getBillingStatement(StatementOfAccountParam param, PageSetting pageSetting);

	/**
	 * Get the customer balances summary report data within selected date range
	 * @param companyId The company id
	 * @param balanceOption The balance option
	 * @param dateFrom The start date
	 * @param dateTo The end date
	 * @param pageSetting The page setting
	 * @return The customer account balances summary
	 */

	Page<CustomerBalancesSummaryDto> getCustomerBalSummaryFromDateRange (Integer companyId,
			Integer balanceOption, Date dateFrom, Date dateTo, PageSetting pageSetting);

	/**
	 * Get the list of customer accounts
	 * @param companyId the company id
	 * @param projectId the project/customer id
	 * @return The list of customer accounts
	 */
	List<ArCustomerAccount> getArCustomerAccts(Integer companyId, Integer projectId);

	/**
	 * Get the total customer sales order paid advances
	 * @param drReferenceIds The delivery receipt references ids
	 * @return The total paid advances
	 */
	double getPaidAdvances(String drReferenceIds);
}
