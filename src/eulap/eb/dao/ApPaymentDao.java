package eulap.eb.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.ApPayment;
import eulap.eb.domain.hibernate.Checkbook;
import eulap.eb.domain.hibernate.SupplierAdvancePayment;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.MCFlowSubDetailDto;
import eulap.eb.web.dto.PaymentRegisterDto;

/**
 * Data Access Object of {@link ApPayment}

 */

public interface ApPaymentDao extends Dao<ApPayment> {

	/**
	 * Generate the voucher number for the ap payment per company.
	 * @param companyId The id of the company.
	 * @param paymentTypeId The id of Payment Type [1 = ApPayment, 2 = Direct Payment]
	 * @return Generated voucher number.
	 */
	int generateVoucherNumber(int companyId, Integer paymentTypeId);

	/**
	 * Validate the check number if it is unique.
	 * @param apPaymentId The Id of the AP Payment.
	 * @return True if unique , otherwise false.
	 */
	boolean isUniqueCheckNumber(ApPayment apPayment, int bankAccountId);

	/**
	 * Get the maximum check number saved.
	 * @param checkbook The checkbook object.
	 * @return The maximum checkbook saved.
	 */
	BigDecimal getMaxCheckNumber (Checkbook checkbook);

	/**
	 * Search the Account Payable payments.
	 * @param searchCriteria The criteria that the use inputed, check number, voucher number, amount.
	 * @param pageSetting The page setting.
	 * @return The list of payments
	 */
	Page<ApPayment> searchPayment (String searchCriteria, PageSetting pageSetting, int paymentTypeId);

	/**
	 * Get the payment register report data
	 * @param companyId The Id of the company selected.
	 * @param divisionId The id of the division selected.
	 * @param bankAccountId The Id of the bank account selected.
	 * @param supplierId The Id of the supplier.
	 * @param supplierAccountId The Id of the supplier account.
	 * @param paymentDateFrom The start date of the payment date range.
	 * @param paymentDateTo The end date of the payment date range.
	 * @param checkDateFrom The start date of the check date range.
	 * @param checkDateTo The end date of the check date range.
	 * @param amountFrom The lowest value of the amount range.
	 * @param amountTo The greatest value of the amount range.
	 * @param voucherNoFrom The start of the voucher number range.
	 * @param voucherNoTo The end of the voucher number range.
	 * @param checkNoFrom The start of the check number range.
	 * @param checkNoTo The end of the check number range.
	 * @param paymentStatusId The Id of the payment status
	 * @return The payment register report data
	 */
	List<PaymentRegisterDto> searchPayments(int companyId, int divisionId, int bankAccountId, int supplierId, int supplierAccountId,
			Date paymentDateFrom, Date paymentDateTo, Date checkDateFrom, Date checkDateTo, Double amountFrom,
			Double amountTo, Integer voucherNoFrom, Integer voucherNoTo, BigDecimal checkNoFrom, BigDecimal checkNoTo,
			int paymentStatusId);

	/**
	 * Compute the total amount from AP Payment per account.
	 * @param companyId Id of the company.
	 * @param asOfDate The end date of the date range.
	 * @param accountId The Id of the account.
	 * @return The total amount.
	 */
	double totalPerAccount(int companyId, Date asOfDate, int accountId);

	/**
	 * Get the paged AP Payments.
	 * @param searchParam Search AP Payment by voucher number, check number,
	 * description, company number, date and amount.
	 * @param formStatusIds the statuses of the GL that will be retrieved.
	 * @param pageSetting The page setting.
	 * @return The paged data.
	 */
	Page<ApPayment> getAllAPPayment (ApprovalSearchParam searchParam, List<Integer> formStatusIds, PageSetting pageSetting);

	/**
	 * Get the list of AP Payment that has null Form Workflow Id.
	 * @return The list of AP Payment that has null Form Workflow Id.
	 */
	List<ApPayment> getApPaymentWithNullWF ();

	/**
	 * Checks if the AP_PAYMENT table contains at lease one null Form Workflow Id.
	 * @return True if the AP_PAYMENT table contains at lease one null Form Workflow Id,
	 * otherwise false.
	 */
	boolean hasNullFW();

	/**
	 * Get AP Payment object by workflow id.
	 * @param formWorkflowId The workflow id.
	 * @return The AP Payment.
	 */
	ApPayment getApPaymentByWorkflow (Integer formWorkflowId);

	/**
	 * Get the total payment amount
	 * @param companyId The selected company id
	 * @param supplierAcctId The 
	 * @param asOfDate The check date of the payment.
	 * @return The total payment amount.
	 */
	double getTotalPaymentAmount(Integer companyId, Integer supplierAcctId, Date asOfDate);

	/**
	 * Generate the voucher number for the direct payment per company.
	 * @param companyId The company id.
	 * @param directPaymentTypeId The direct payment type.
	 * @return The generated voucher number.
	 */
	Integer generateDirectPaymentVC(Integer companyId, Integer directPaymentTypeId);

	/**
	 * Get the list of {@link ApPayment} in page format.
	 * @param divisionId The division id.
	 * @param searchParam The {@link ApprovalSearchParam}.
	 * @return The list of {@link ApPayment}.
	 */
	Page<ApPayment> getAllApPaymentForms(final int divisionId, final ApprovalSearchParam searchParam,
			final  List<Integer> formStatusIds, final PageSetting pageSetting);

	/**
	 * Get the list of {@link ApPayment} that used {@link SupplierAdvancePayment} as a reference object.
	 * @param sapId The {@link SupplierAdvancePayment}.
	 * @return The list of {@link ApPayment}.
	 */
	List<ApPayment> getApPaymentBySap(Integer sapId);

	/**
	 * Get the list of ap payments by the parent object id of the ap payment line.
	 * @param objectId The parent object id.
	 * @return The list of {@link ApPayment}.
	 */
	List<ApPayment> getApPaymentsByObjectId(Integer objectId);

	/**
	 * Get the list of ap payment transactions with negative sap ap payment line.
	 * @param sapObjectId The {@link SupplierAdvancePayment} object id.
	 * @param paymentId The {@link ApPayment} id.
	 * @return The list of {@link ApPayment} that used the sap reference transaction with negative paid amount value.
	 */
	List<ApPayment> getApPaymentsWithNegativeSap(Integer sapObjectId, Integer paymentId);

	/**
	 * Get the monthly cash flow report data
	 * @param companyId The company id
	 * @param divisionId The division id
	 * @param dateFrom The start date filter
	 * @param dateTo The end date filter
	 * @return The monthly cash flow report data
	 */
	List<MCFlowSubDetailDto> getMcFlowSubDetailDtos(int companyId, int divisionId, Date dateFrom, Date dateTo);
}