package eulap.eb.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.ArTransaction;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.fap.FormPluginParam;
import eulap.eb.service.report.ArTransactionRegisterParam;
import eulap.eb.service.report.TransactionAgingParam;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.ArTransactionAgingDto;
import eulap.eb.web.dto.ArTransactionRegisterDto;
import eulap.eb.web.dto.VATDeclarationDto;

/**
 * Data-Access-Object of {@link ArTransaction}.

 */
public interface ArTransactionDao extends Dao<ArTransaction>{

	/**
	 * Get the Transactions with the same customer account.
	 * @param customerAcctId The Id of the customer account.
	 * @return Collection of AR transactions.
	 */
	Collection<ArTransaction> getTransactionsByCustomerAcct (int customerAcctId);
	
	/**
	 * Get the list of AR Transactions.
	 * @param serviceLeaseKeyId The service lease key id of the logged user.
	 * @param arCustomerAccountId The unique id of the AR customer account.
	 * @return The list of AR Transactions.
	 */
	List<ArTransaction> getARTransactions (int serviceLeaseKeyId, Integer arCustomerAccountId);
	
	/**
	 * Get the paged list of AR Transactions based on the search criteria.
	 * @param arTransactionTypeId The transaction type id.
	 * @param searchCriteria The search criteria.
	 * @param pageSetting The page setting.
	 * @return The paged list of AR Transactions.
	 */
	Page<ArTransaction> searchARTransactions (Integer arTransactionTypeId, String searchCriteria,  PageSetting pageSetting);

	/**
	 * Get the paged list of AR Transactions based on the search criteria.
	 * @param arTransactionTypeId The transaction type id.
	 * @param divisionId The transaction division id.
	 * @param searchCriteria The search criteria.
	 * @param pageSetting The page setting.
	 * @return The paged list of AR Transactions.
	 */
	Page<ArTransaction> searchARTransactions (Integer arTransactionTypeId, Integer divisionId, String searchCriteria,  PageSetting pageSetting);

	/**
	 * Get the all of the AR Transaction data.
	 * @param arTransactionTypeId The transaction type id.
	 * @param searchParam Search AR Transaction by sequence number,
	 * description, company number, date and amount.
	 * @param formStatusIds the statuses of the GL that will be retrieved.
	 * @param pageSetting The page setting.
	 * @return The paged data.
	 */
	Page<ArTransaction> getAllArTransaction (int arTransactionTypeId, ApprovalSearchParam searchParam, List<Integer> formStatusIds, PageSetting pageSetting);

	/**
	 * Get the all of the AR Transaction data.
	 * @param arTransactionTypeId The transaction type id.
	 * @param divisionId The transaction division id
	 * @param searchParam Search AR Transaction by sequence number,
	 * description, company number, date and amount.
	 * @param formStatusIds the statuses of the GL that will be retrieved.
	 * @param pageSetting The page setting.
	 * @return The paged data.
	 */
	Page<ArTransaction> getAllArTransaction (int arTransactionTypeId, Integer divisionId, ApprovalSearchParam searchParam, List<Integer> formStatusIds, PageSetting pageSetting);

	/**
	 * Checks if the transaction number is unique.
	 * @param arTransaction The AR Transaction object.
	 * @param companyId The unique id of the company associated with the
	 * AR Customer Account.
	 * @return True the transaction number is unique, otherwise false.
	 */
	public boolean isUniqueTransactionNo (ArTransaction arTransaction, Integer companyId);

	/**
	 * Get the list of AR Transactions.
	 * @param criteria The transaction number.
	 * @param arCustAcctId The unique id of Ar Customer Account.
	 * @param id The unique id of Ar Transaction.
	 * @param transactionNums The string of Ar Transaction numbers to be excluded.
	 * @param noLimit True if display all ar transactions.
	 * @param isExact True if name should be exact.
	 * @param serviceLeaseKeyId The service lease key id of the logged user.
	 * @return The list of AR Transactions.
	 */
	List<ArTransaction> getARTransactions (String criteria, 
			Integer arCustAcctId, List<String> transactionNums, Integer id, 
			Boolean noLimit, Boolean isExact, int serviceLeaseKeyId);

	/**
	 * Get the ar transaction object by workflow.
	 * @param formWorkflowId The unique id of the workflow of the ar transaction.
	 * @return ar transaction object.
	 */
	public ArTransaction getArTransactionByWorkflow (Integer formWorkflowId);

	/**
	 * Checks if the ar transaction is already existing in ar receipt.
	 * @param arTransactionId The id of ar transaction.
	 * @return True if existing, otherwise false.
	 */
	public boolean isExistingInArReceipt (Integer arTransactionId);

	/**
	 * Get the AR Transaction object by transaction number.
	 * @param transactionNumber The transaction number.
	 * @return AR Transaction object.
	 */
	public ArTransaction getTransactionByNumber (String transactionNumber);

	/**
	 * Search the AR Transactions.
	 * @param param The object that holds the search parameters.
	 * @param pageSetting The page setting.
	 * @return The paged result.
	 */
	Page<ArTransactionRegisterDto> searchTransaction(ArTransactionRegisterParam param, PageSetting pageSetting);

	/**
	 * Get the total transaction amount per customer account.
	 * @param arCustomerAcctId The AR customer account id.
	 * @return The total transaction amount.
	 */
	List<ArTransaction> getTransactionsByCustAcct (Integer arCustomerAcctId);

	/**
	 * Get Ar Transaction by number.
	 * @param transactionNumber The transaction number.
	 * @return The Ar Transaction object.
	 */
	ArTransaction getArTransactionByNumber (String transactionNumber);

	/**
	 * Get AR Transaction by number and customer account.
	 * @param arCustomerAcctId The unique id of the AR Customer Account.
	 * @param criteria The criteria.
	 * @return Ar Transaction list.
	 */	
	ArTransaction getArTransaction(Integer arCustomerAcctId, String criteria);

	/**
	 * Get AR Transaction by number and customer account.
	 * @param arCustomerAcctId The unique id of the AR Customer Account.
	 * @param criteria The criteria.
	 * @return Ar Transaction list.
	 */	
	ArTransaction getCompletedTransaction(Integer arCustomerAcctId, String criteria);

	/**
	 * Get the ar transaction object.
	 * @param transactionNumber The transaction number
	 * @param arCustomerAcctId The customer account id.
	 * @return The ar transaction.
	 */
	ArTransaction getArTByCustAcctAndTNum(Integer arCustomerAcctId, String transactionNumber);

	/**
	 * Get all AR Transactions.
	 * @param companyId The id of the selected company.
	 * @param arLineId The id of the ar line.
	 * @param unitOfMeasureId The id of the unit of measurement.
	 * @param transactionDateFrom The start date of the transaction date range.
	 * @param transactionDateTo The end date of the transaction date range.
	 * @param glDateFrom The start date of the gl date range.
	 * @param glDateTo The end date of the gl date range.
	 * @param customerId The id of the customer selected.
	 * @param customerAcctId The id of the customer account selected.
	 * @param pageSetting The page setting.
	 * @return The paged result.
	 */
	Page<ArTransaction> getArTransactions(Integer companyId, Integer arLineId, Integer unitOfMeasureId, Date transactionDateFrom, 
			Date transactionDateTo, Date glDateFrom, Date glDateTo, Integer customerId, Integer customerAcctId, PageSetting pageSetting);

	/**
	 * Get the total transaction amount of the customer account.
	 * @param customerAcctId The customer account id.
	 * @return The total transaction amount.
	 */
	public Double getCustomerAcctTotalTransaction(Integer customerAcctId);

	/**
	 * Get the total transaction amount of the ar customer.
	 * @param arCustomerId The unique id of the AR Customer.
	 * @return The total transaction amount.
	 */
	double getTotalTransactionAmtOfCustomer(int arCustomerId, int arTransactionId);

	/**
	 * Get all Transaction given the parameters selected.
	 * @param companyId The selected company id.
	 * @param customerId The selected customer id.
	 * @param customerAcctId The selected customer account id.
	 * @param transactionDateFrom The start of date range.
	 * @param transactionDateTo The end of date range.
	 * @return The list of 
	 */
	public List<ArTransaction> getTranactions(Integer companyId, Integer CustomerId, Integer customerAcctId,
			Date transactionDateFrom, Date transactionDateTo);

	/**
	 * Get the total transaction of the customer.
	 * @param customerAcctId The customer account id.
	 * @param asOfDate The gl date of the transaction.
	 * @return The total transaction amount.
	 */
	public Double getCustomerAcctTotalTransaction(Integer companyId, Integer customerAcctId, Date asOfDate);

	/**
	 * Get the total account sales of the customer.
	 * @param customerAcctId The customer account id.
	 * @param asOfDate The form date of the transaction.
	 * @return The total account sales amount.
	 */
	public Double getCustAcctTotalSales(Integer companyId, Integer customerAcctId, Date asOfDate);

	/**
	 * Generate sequence no based on the company and transaction type.
	 * @param companyId The company id.
	 * @param arTransactionTypeId The transaction type id.
	 * @return The generated sequence number.
	 */
	public Integer genSequenceNo (Integer companyId, Integer arTransactionTypeId);

	/**
	 * Generate sequence no based on the company and transaction type.
	 * @param companyId The company id.
	 * @param arTransactionTypeId The transaction type id.
	 * @param divisionId The transaction division id.
	 * @return The generated sequence number.
	 */
	public Integer genSequenceNo (Integer companyId, Integer arTransactionTypeId, Integer divisionId);

	/**
	 * Get the paged list of account sales for AS Reference.
	 * @param companyId The company id.
	 * @param arCustomerId The customer id.
	 * @param arCustomerAccountId The customer account id.
	 * @param sequenceNo The sequence number.
	 * @param dateFrom The start of date range.
	 * @param dateTo The end of date range.
	 * @param status The status of the cash sale.
	 * @param transactionTypeId The transaction type id..
	 * @param pageSetting The page setting.
	 * @param user The current user.
	 * @return The paged list of account sales.
	 */
	Page<ArTransaction> getAccountSales (Integer companyId, Integer arCustomerId, Integer arCustomerAccountId, 
			Integer sequenceNo, Date dateFrom, Date dateTo, Integer status, Integer transactionTypeId, PageSetting pageSetting, User user);
	
	/**
	 * Get the account receivable transaction about date. 
	 * @param itemId The item id.
	 * @param warehouseId the warehouse id.
	 * @param date The transaction date. 
	 * @return The list of transaction in page.
	 */
	Page<ArTransaction> getTransactions(int itemId, int warehouseId, Date date, PageSetting pageSetting);

	/**
	 * Get the total amount of completed transactions per customer account.
	 * @param arCustomerAccountId The customer account id.
	 * @return The total amount.
	 */
	double getTotalTransactionAmount (Integer arCustomerAccountId);

	/**
	 * Get the size of account sales that are not cancelled.
	 * @return The size of cash sales.
	 */
	int getASSize();

	/**
	 * Get the account sales.
	 * @param pageSetting The page setting.
	 * @return The paged account sales.
	 */
	Page<ArTransaction> getAccountSales (PageSetting pageSetting);

	/**
	 * Get AR Transaction object.
	 * @param sequenceNo The sequence number of the ar transaction.
	 * @param transactionTypeId The ar transaction type id.
	 * @return The AR Transaction object.
	 */
	ArTransaction getTransactionBySeqNoAndType(Integer sequenceNo, Integer transactionTypeId);

	/**
	 * Get the list of AR Transactions with ids greater than the arTransactionId parameter.
	 * @param arTransactionId The ar transaction id.
	 * @param transactionTypeId The ar transaction type id.
	 * @return The list of AR Transactions.
	 */
	List<ArTransaction> getTransactions(int arTransactionId, int transactionTypeId);

	/**
	 * Checks if the ar transaction type account sale is already existing in ar transaction type account sale return.
	 * @param arTransactionId The id of ar transaction.
	 * @return True if existing, otherwise false.
	 */
	boolean isExistingInAccountSaleReturn(int arTransactionId);

	/**
	 * Generate data for AR Transaction Aging Report.
	 * @param param The transaction aging parameters.
	 * @param pageSetting The Page setting.
	 * @return The page result
	 */
	Page<ArTransactionAgingDto> genareteTransactionAging(TransactionAgingParam param,
			PageSetting pageSetting);

	/**
	 * Get the AR transaction balances.
	 * @param arCustAcctId The customer account ID.
	 * @param transactionNums The transaction number.
	 * @param criteria The transaction number.
	 * @param isShow isShow Check if it is for showing.
	 * @return The transactions customer balance
	 */
	Page<ArTransactionRegisterDto> getArTransactions(Integer arCustAcctId, Integer accountCollectionId,
			String transactionNums, String criteria, Boolean isShow, Boolean isExact, PageSetting pageSetting);

	/**
	 * Get transactions that are used in Account sale returns
	 * @param arTransactionId The AR transaction Id
	 * @return The list of AR transactions
	 */
	List<ArTransaction> getTransactionUsedInReturns(Integer arTransactionId);

	/**
	 * Get the sales orders.
	 * @param param The search parameter.
	 * @return The paged sales order.
	 */
	Page<ArTransaction> getArTransactions(FormPluginParam formPluginParam);

	/**
	 * Get the ar transactions of type sales order.
	 * @param companyId The Company Id.
	 * @param arCustomerId The Ar Customer Id.
	 * @param arCustomerAccountId The Ar Customer Account Id.
	 * @param soNumber The sequence Number.
	 * @param dateFrom The start of date range.
	 * @param dateTo The end of date range.
	 * @param user The current user.
	 * @param pageSetting The Page Setting.
	 * @return The list of Ar Transactions of type Sales Order.
	 */
	Page<ArTransaction> getArTransactions(Integer companyId,
			Integer arCustomerId, Integer arCustomerAccountId,
			Integer soNumber, Date dateFrom, Date dateTo, User user,
			PageSetting pageSetting);

	/**
	 * Computes the available balance of the customer.
	 * @param arCustomerId The id of the customer.
	 * @return The available balance of the customer.
	 */
	double computeAvailableBalance(Integer arCustomerId);

	/**
	 * Get the output VAT declaration details
	 * @param companyId the company id.
	 * @param divisionId the division id.
	 * @param year the current year.
	 * @param quarter the one-fourth of a year.
	 * @param month the current month.
	 * @return The output VAT declaration details
	 */
	List<VATDeclarationDto> getOutputVatDeclaration(Integer companyId, Integer divisionId, Integer year,
			Integer quarter, Integer month);
}
