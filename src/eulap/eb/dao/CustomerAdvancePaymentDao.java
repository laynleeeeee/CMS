package eulap.eb.dao;

import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.CustomerAdvancePayment;
import eulap.eb.domain.hibernate.SalesOrder;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.ArTransactionRegisterDto;

/**
 * Data access object of {@link CustomerAdvancePayment}

 *
 */
public interface CustomerAdvancePaymentDao extends Dao<CustomerAdvancePayment>{

	/**
	 * Generate unique customer advance payment number per company.
	 * @param companyId The company id.
	 * @param typeId The type id of customer advance payment
	 * @return The cash sales number.
	 */
	Integer generateCapNumber (Integer companyId, Integer typeId);

	/**
	 * Search for customer advance payments.
	 * @param criteria The search criteria.
	 * @param typeId The type id of customer advance payment
	 * @param pageSetting The page setting.
	 * @return Paged search results.
	 */
	Page<CustomerAdvancePayment> searchCaps (String criteria, Integer typeId, PageSetting pageSetting);

	/**
	 * Get the customer advance payments.
	 * @param typeId The id of the type of CAP. {1=Retail, 2=Individual Selection}
	 * @param param The search parameter.
	 * @return The page customer advance payments for approval.
	 */
	Page<CustomerAdvancePayment> getCapsForApproval(ApprovalSearchParam searchParam, List<Integer> formStatusIds, Integer typeId, PageSetting pageSetting);

	/**
	 * Get the data to be used for the CAP Register Report.
	 * @param companyId The id of the company.
	 * @param divisionId The id of the division.
	 * @param arCustomerId The id of the customer.
	 * @param arCustomerAccountId The id of the customer account, -1 for all accounts.
	 * @param dateFrom Start date of the date range.
	 * @param dateTo End date of the date range.
	 * @param statusId The id of the status {-1 = ALL, 1 = Fully Served, 2 = Partially Served, 3 = Unserved and 4 = Cancelled}
	 * @param pageSetting The page setting.
	 * @return The paged data for the report.
	 */
	Page<ArTransactionRegisterDto> getCAPRegisterData(int companyId, int divisionId, int arCustomerId, int arCustomerAccountId,
			Date dateFrom, Date dateTo, int statusId, PageSetting pageSetting);

	/**
	 * Get the list of Customer Advance Payments.
	 * @param companyId The id of the company.
	 * @param typeId The inventory type of the CAP.
	 * @param arCustomerId The id of the AR Customer.
	 * @param arCustomerAccountId The id of the Account of the AR Customer.
	 * @param dateFrom Start date of date range.
	 * @param dateTo End date of date range.
	 * @param statusId The id of the status {1=ALL, 2=UNUSED, 3=USED}
	 * @param pageSetting The page setting.
	 * @param user The current user logged.
	 * @return The list of Customer Advance Payments in paging format.
	 */
	Page<CustomerAdvancePayment> getCAPReferences(int companyId, int typeId, int arCustomerId, int arCustomerAccountId,
			int capNo, Date dateFrom, Date dateTo, int statusId, PageSetting pageSetting, User user);

	/**
	 * Get the list of Customer Advance Payments.
	 * @param companyId The id of the company.
	 * @param divisionId The id of the division.
	 * @param typeId The inventory type of the CAP.
	 * @param arCustomerId The id of the AR Customer.
	 * @param arCustomerAccountId The id of the Account of the AR Customer.
	 * @param dateFrom Start date of date range.
	 * @param dateTo End date of date range.
	 * @param statusId The id of the status {1=ALL, 2=UNUSED, 3=USED}
	 * @param pageSetting The page setting.
	 * @param user The current user logged.
	 * @return The list of Customer Advance Payments in paging format.
	 */
	Page<CustomerAdvancePayment> getCAPReferences(int companyId, Integer divisionId, int typeId, int arCustomerId, int arCustomerAccountId,
			int capNo, Date dateFrom, Date dateTo, int statusId, PageSetting pageSetting, User user);

	/**
	 * Get the total advance payments by sales order.
	 * Returns 0 if the main reference is not a CAP form transaction.
	 * @param soId The sales order id.
	 * @param The customer advance payment id.
	 * @return The total advance payments.
	 */
	Double getTotalAdvPaymentsBySO(Integer soId, Integer capId);

	/**
	 * Get the list of customer advance payments by selected customer transaction
	 * @param arTransactionIds The AR transaction ids
	 * @return The list of customer advance payments
	 */
	List<CustomerAdvancePayment> getCustomerAdvancePayments(String arTransactionIds);

	/**
	 * Get the list of customer advance payments
	 * @param companyId The company id
	 * @param customerId The customer id 
	 * @param customerAcctId The customer account id
	 * @param capNumber The CAP number
	 * @return The list of customer advance payments
	 */
	List<CustomerAdvancePayment> getCustomerAdvancePayments(Integer companyId, Integer customerId, Integer customerAcctId,
			Integer capNumber, boolean isExact, String toBeExcludedCapIds, Integer arReceiptId);

	/**
	 * Get the list of customer advance payments by sales order id.
	 * @param soId The sales order id.
	 * @return The list of CAPs.
	 */
	List<CustomerAdvancePayment> getCAPsBySalesOrderId(Integer soId);

	/**
	 * Get the AR invoice sales order/CAP reference number for billing statement/statement of account report
	 * @param drReferenceIds The delivery receipt references ids
	 * @return The AR invoice sales order/CAP reference number
	 */
	String getSalesOrderRefNumber(String drReferenceIds);

	/**
	 * Get the list of {@link CustomerAdvancePayment} by {@link SalesOrder} id.
	 * @param advPaymentId The {@link CustomerAdvancePayment} id.
	 * @param refSoId The reference {@link SalesOrder} id.
	 * @return The list of {@link CustomerAdvancePayment}.
	 */
	List<CustomerAdvancePayment> getSoAdvPayments(Integer advPaymentId, Integer refSoId);

	/**
	 * Get the list of CAP transactions that used the CAP parameter as child transaction.
	 * @param capId The child {@link CustomerAdvancePayment} id.
	 * @return list of CAP transactions.
	 */
	List<CustomerAdvancePayment> getCapsByCapId(Integer capId);

	/**
	 * Get the remaining unused advance payment balance.
	 * @param salesOrderId The sales order id.
	 * @param arInvoiceId The ar invoice id.
	 * @return The remaining balance.
	 */
	double getRemainingCapBalance(int salesOrderId, Integer arInvoiceId, Integer arReceiptId);
}
