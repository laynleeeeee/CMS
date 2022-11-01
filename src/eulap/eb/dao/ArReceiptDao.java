package eulap.eb.dao;

import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.ArInvoice;
import eulap.eb.domain.hibernate.ArReceipt;
import eulap.eb.domain.hibernate.CustomerAdvancePayment;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.web.dto.ApprovalSearchParam;

/**
 * Data Access Object of {@link ArReceipt}

 *
 */
public interface ArReceiptDao extends Dao<ArReceipt>{

	/**
	 * Search the AR Receipts.
	 * @param searchCriteria The criteria that the use inputed, receipt number, amount.
	 * @param pageSetting The page setting.
	 * @return The list of AR Receipts.
	 */
	Page<ArReceipt> searchArReceipts (String searchCriteria, PageSetting pageSetting);

	/**
	 * Search the AR Receipts.
	 * @param searchCriteria The criteria that the use inputed, receipt number, amount.
	 * @param divisionId The division id.
	 * @param pageSetting The page setting.
	 * @return The list of AR Receipts.
	 */
	Page<ArReceipt> searchArReceipts (String searchCriteria, Integer divisionId, PageSetting pageSetting);

	/**
	 * Get the paged AR Receipts.
	 * @param searchParam Search AR Receipt by receipt number,
	 * sequence number, company number, date and amount.
	 * @param formStatusIds the statuses of the GL that will be retrieved.
	 * @param pageSetting The page setting.
	 * @param divisionId The {@link Division} id.
	 * @return The paged data.
	 */
	Page<ArReceipt> getAllArReceipts (ApprovalSearchParam searchParam, List<Integer> formStatusIds, int divisionId, PageSetting pageSetting);

	/**
	 * Checks if the receipt number is unique.
	 * @param arReceipt The AR Transaction object.
	 * @param companyId The unique id of the company associated with the
	 * AR Customer Account.
	 * @return True the receipt number is unique, otherwise false.
	 */
	public boolean isUniqueReceiptNo (ArReceipt arReceipt, Integer companyId);

	/**
	 * Get the ar receipt object by workflow.
	 * @param formWorkflowId The unique id of the workflow of the ar receipt.
	 * @return ar receipt object.
	 */
	public ArReceipt getArReceiptByWorkflow (Integer formWorkflowId);

	/**
	 * Search the AR Receipts.
	 * @param companyId The Id of the selected company.
	 * @param customerId The id of the customer selected.
	 * @param customerAcctId The id of the customer account selected.
	 * @param receiptDateFrom The start date of the receipt date range.
	 * @param receiptDateTo The end date of the receipt date range.
	 * @param maturityDateFrom The start date of the maturity date range.
	 * @param maturityDatSeTo The end date of the maturity date range.
	 * @param asOfDate The end date of the transaction date; default to current date.
	 * @param pageSetting The page setting.
	 * @return The paged result.
	 */
	Page<ArReceipt> searchArReceipts(int companyId, int customerId, int customerAcctId, Date receiptDateFrom, Date receiptDateTo, 
			Date maturityDateFrom, Date maturityDateTo, Date asOfDate, PageSetting pageSetting);

	/**
	 * Get the AR Receipts.
	 * @param companyId The id of the selected company.
	 * @param receiptTypeId The id of the receipt type.
	 * @param customerId The id of the customer selected.
	 * @param customerAcctId The id of the customer account selected.
	 * @param receiptDateFrom The start date of the receipt date range.
	 * @param receiptDateTo The end date of the receipt date range.
	 * @param maturityDateFrom The start date of the maturity date range.
	 * @param maturityDatSeTo The end date of the maturity date range.
	 * @param amountFrom The starting amount of the amount range.
	 * @param amountTo The ending amount of the amount range.
	 * @param wfStatusId The form workflow status id.
	 * @param pageSetting The page setting.
	 * @return The paged result.
	 */
	Page<ArReceipt> getArReceipts(int companyId, int receiptTypeId, int receipMethodId, int customerId, int customerAcctId, String receiptNumber, Date receiptDateFrom, Date receiptDateTo, 
			Date maturityDateFrom, Date maturityDateTo, Double amountFrom, Double amountTo, int wfStatusId, PageSetting pageSetting);

	/**
	 * Get the total receipt amount of the customer account.
	 * @param customerAcctId The customer account id.
	 * @return The total receipt amount.
	 */
	public Double getCustomerAcctTotalReceipt(Integer customerAcctId);

	/**
	 * Get the total receipt amount of the customer.
	 * @param companyId The company id.
	 * @param customerAcctId The customer account id.
	 * @param asOfDate The maturity date.
	 * @return The total receipt amount.
	 */
	public Double getCustomerAcctTotalReceipt(Integer companyId, Integer customerAcctId, Date asOfDate);

	/**
	 * Get the total receipt amount of the customer.
	 * @param arCustomerId The ar customer id.
	 * @return The total receipt amount.
	 */
	public double getCustomerTotalReceipt(Integer arCustomerId);

	/**
	 * Get the AR Receipt per AR Receipt ID.
	 * @param pId The AR Receipt ID.
	 * @return The AR Receipt
	 */
	List<ArReceipt> getArReceipts(Integer pId);

	/**
	 * Get the list of Account Collections of the Transaction.
	 * @param arTransactionId The unique id of the AR Transaction.
	 * @return The list of Account Collections.
	 */
	List<ArReceipt> getTransactionPayments(int arTransactionId);

	/**
	 * Generate the sequence number of the form
	 * @param companyId The company id
	 * @return The generated sequence number
	 */
	Integer generateSequenceNo(Integer companyId, Integer divisionId);

	/**
	 * Get the list of {@link ArReceipt} by {@link CustomerAdvancePayment}.
	 * @param capId The {@link CustomerAdvancePayment} id.
	 * @return The list of {@link ArReceipt}.
	 */
	List<ArReceipt> getArReceiptsByCapId(Integer capId);

	/**
	 * Get the list of {@link ArReceipt} by {@link ArInvoice}.
	 * @param arInvoiceId The AR Invoice id.
	 * @return The list of {@link ArReceipt}.
	 */
	List<ArReceipt> geArReceipsByArInvoiceId(Integer arInvoiceId);

	/**
	 * Get the list of {@link ArReceipt} by AR transaction id
	 * @param arTransactionId The AR transaction id
	 * @return The list of {@link ArReceipt} objects.
	 */
	List<ArReceipt> getArReceiptsByTrId(Integer arTransactionId);
}
