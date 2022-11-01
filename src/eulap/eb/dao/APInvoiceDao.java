package eulap.eb.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.service.report.ApInvoiceAgingParam;
import eulap.eb.web.dto.AnnualAlphalistPayeesDetailDto;
import eulap.eb.web.dto.AnnualAlphalsitWTEDetailsDto;
import eulap.eb.web.dto.ApInvoiceAgingDto;
import eulap.eb.web.dto.ApInvoiceDto;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.COCTaxDto;
import eulap.eb.web.dto.MonthlyAlphalistPayeesDto;
import eulap.eb.web.dto.SupplierApLineDto;
import eulap.eb.web.dto.VATDeclarationDto;

/**
 * A base class for accessing data in Accounts payable invoices. 

 *
 */
public interface APInvoiceDao extends Dao<APInvoice>{

	/**
	 * Evaluate the invoice number if unique per supplier account.
	 * @param invoiceNumber The invoice number to be evaluated.
	 * @param supplierAccountId The Id of the supplier account.
	 * @return True if unique, otherwise false.
	 */
	boolean isUniqueInvoiceNo (String invoiceNumber, int supplierAccountId);

	/**
	 * Evaluate the reference number if unique per supplier account.
	 * @param invoiceNumber The invoice number to be evaluated.
	 * @param divisionId The Id of the division.
	 * @param invoiceTypeId The invoice type id.
	 * @return True if unique, otherwise false.
	 */
	boolean isUniqueReferenceNo (String invoiceNumber, Integer divisionId, Integer invoiceTypeId);

	/**
	 * Get the Invoices with the same supplier account.
	 * @param supplierAccountId The Id of the supplier account.
	 * @return Collection of AP Invoices.
	 */
	Collection<APInvoice> getInvoicesBySupplierAcct (int supplierAccountId);

	/**
	 * Generate the sequence number.
	 * @return Generated sequence number.
	 */
	int generateSequenceNumber(int invoiceTypeId, Integer companyId);

	/**
	 * Get the all of the ap invoice data.
	 * @param searchParam Search AP Invoice by sequence number, invoice number,
	 * description, company number, date and amount.
	 * @param formStatusIds the statuses of the GL that will be retrieved.
	 * @param pageSetting The page setting.
	 * @param typeId Invoice type id.
	 * @return The paged data.
	 */
	Page<APInvoice> getAllAPInvoice (ApprovalSearchParam searchParam, List<Integer> formStatusIds, PageSetting pageSetting, int typeId);
	/**
	 * Search AP Invoices.
	 * @param searchCriteria The search criteria.
	 * @param pageSetting The page setting.
	 * @return The paged result.
	 */
	Page<APInvoice> searchAPInvoice(String searchCriteria, PageSetting pageSetting);

	/**
	 * Search AP Invoices.
	 * @param searchCriteria The search criteria.
	 * @param pageSetting The page setting.
	 * @return The paged result.
	 */
	Page<APInvoice> searchAPInvoice(String searchCriteria, PageSetting pageSetting, int divisionId);

	/**
	 * Get the AP Invoice using the invoice number and supplier account.
	 * @param supplierAccountId The Id of the supplier account
	 * @param invoiceNumber The invoice number.
	 * @return The AP Invoice.
	 */
	APInvoice getInvoice (int supplierAccountId, String invoiceNumber, int serviceLeaseKeyId);

	/**
	 * Get the AP Invoice using the invoice number and supplier account.
	 * @param supplierAccountId The Id of the supplier account
	 * @param apInvoiceId The id of the selected invoice.
	 * @param invoiceNumber The invoice number.
	 * @return The AP Invoice.
	 */
	APInvoice getInvoice (int supplierAccountId, int apInvoiceId, String invoiceNumber, int serviceLeaseKeyId);

	/**
	 * Compute the total debit of the AP Invoice per account.
	 * <br>Summated amount are lesser than zero.
	 * @param companyId Id of the company.
	 * @param asOfDate The end date of the date range.
	 * @param accountId The Id of the account.
	 * @return The total debit of AP Invoice.
	 */
	double getTotalDebit(int companyId, Date asOfDate, int accountId);

	/**
	 * Get the total credit of the AP Invoice per account.
	 * <br>Summated amount are greater than zero.
	 * @param companyId Id of the company.
	 * @param asOfDate The end date of the date range.
	 * @param accountId The Id of the account.
	 * @return The total credit of AP Invoice.
	 */
	double getTotalCredit(int companyId, Date asOfDate, int accountId);

	/**
	 * Search the AP Invoices.
	 * @param companyId The Id of the company selected.
	 * @param invoiceTypeId The Id of the invoice type selected.
	 * @param supplierId The Id of the supplier.
	 * @param supplierAcctId The Id of the supplier account.
	 * @param termId The Id of the term.
	 * @param invoiceNumber The invoice number.
	 * @param invoiceDateFrom The start date of the invoice date range.
	 * @param invoiceDateTo The end date of the invoice date range.
	 * @param glDateFrom The start date of the gl date range.
	 * @param glDateTo The end date of the gl date range.
	 * @param dueDateFrom The start date of the due date range.
	 * @param dueDateTo The end date of the due date range.
	 * @param fromAmount The lowest amount of the amount range.
	 * @param toAmount The greatest amount of the amount range.
	 * @param fromSequenceNumber The start of the sequence number range.
	 * @param toSequenceNumber The end of the sequence number range.
	 * @param invoiceStatusId The Id of the invoice status.
	 * @param paymentStatusId The payment status are: Fully Paid, Partially Paid and Unpaid.
	 * @return The paged result.
	 */
	Page<APInvoice> searchInvoices (int companyId, int invoiceTypeId, int supplierId, int supplierAcctId,
			int termId, String invoiceNumber, Date invoiceDateFrom, Date invoiceDateTo, Date glDateFrom,
			Date glDateTo, Date dueDateFrom, Date dueDateTo, Double fromAmount, Double toAmount,
			Integer fromSequenceNumber, Integer toSequenceNumber, int invoiceStatusId,
			int paymentStatusId, Date asOfDate, PageSetting pageSetting);

	/**
	 * Search the AP Invoices.
	 * @param companyId The Id of the company selected.
	 * @param invoiceTypeId The Id of the invoice type selected.
	 * @param divisionId The division id.
	 * @param supplierId The Id of the supplier.
	 * @param supplierAcctId The Id of the supplier account.
	 * @param termId The Id of the term.
	 * @param invoiceNumber The invoice number.
	 * @param invoiceDateFrom The start date of the invoice date range.
	 * @param invoiceDateTo The end date of the invoice date range.
	 * @param glDateFrom The start date of the gl date range.
	 * @param glDateTo The end date of the gl date range.
	 * @param dueDateFrom The start date of the due date range.
	 * @param dueDateTo The end date of the due date range.
	 * @param fromAmount The lowest amount of the amount range.
	 * @param toAmount The greatest amount of the amount range.
	 * @param fromSequenceNumber The start of the sequence number range.
	 * @param toSequenceNumber The end of the sequence number range.
	 * @param invoiceStatusId The Id of the invoice status.
	 * @param paymentStatusId The payment status are: Fully Paid, Partially Paid and Unpaid.
	 * @return The paged result.
	 */
	Page<APInvoice> searchInvoices (int companyId, Integer divisionId, int invoiceTypeId, int supplierId, int supplierAcctId,
			int termId, String invoiceNumber, Date invoiceDateFrom, Date invoiceDateTo, Date glDateFrom,
			Date glDateTo, Date dueDateFrom, Date dueDateTo, Double fromAmount, Double toAmount,
			Integer fromSequenceNumber, Integer toSequenceNumber, int invoiceStatusId,
			int paymentStatusId, Date asOfDate, PageSetting pageSetting);
	
	/**
	 * Search the AP Invoices.
	 * @param companyId The Id of the company selected.
	 * @param supplierId The Id of the supplier.
	 * @param supplierAccountId The Id of the supplier account.
	 * @param asOfDate The end date of the date range.
	 * @param pageSetting The page setting
	 * @return The paged result.
	 */
	Page<APInvoice> searchInvoices(int companyId, int supplierId, int supplierAccountId, Date asOfDate, PageSetting pageSetting);

	/**
	 * Generate date for AP Invoice Aging Report.
	 *  @param companyId The Id of the company selected.
	 * @param supplierId The Id of the supplier.
	 * @param supplierAccountId The Id of the supplier account.
	 * @param pageSetting The page setting.
	 * @return The paged result.
	 */
	Page<ApInvoiceAgingDto> searchInvoiceAging(ApInvoiceAgingParam param, PageSetting pageSetting);

	/**
	 * Checks if the invoice has associated payment that is not cancelled.
	 * @param invoiceId The invoice id.
	 * @return True if it has not cancelled payment, otherwise false.
	 */
	public boolean hasAssociatedPayment (int invoiceId);
	
	/**
	 * Get the AP Invoice object by workflow.
	 * @param formWorkflowId The unique id of the workflow of the AP Invoice.
	 * @return AP Invoice object.
	 */
	public APInvoice getAPInvoiceByWorkflow (Integer formWorkflowId);

	/**
	 * Get the total invoice amount.
	 * @param companyId The company id of supplier account.
	 * @param supplierAcctId The supplier account id.
	 * @param AsOfDate The gl date of an invoice.
	 * @return The total invoice amount.
	 */
	double getTotalInvoiceAmount(Integer companyId, Integer supplierAcctId, Date asOfDate, boolean isApproved);

	/**
	 * Get the total invoice amount.
	 * @param companyId The company id of supplier account.
	 * @param supplierAcctId The supplier account id.
	 * @param AsOfDate The date of an invoice.
	 * @param isRtsType True if invoice type is rts, otherwise false.
	 * @return The total invoice amount.
	 */
	double getTotalAmountOfInvoice(Integer companyId, Integer supplierAcctId, Date asOfDate, boolean isRtsType);

	/**
	 * Get the AP Invoice object.
	 * @param refId The reference id.
	 * @param isRrItem set to true if reference is from RR Item, otherwise RTS Item.
	 * @return The AP Invoice, otherwise null.
	 */
	APInvoice getInvoiceByRefId(Integer refId, boolean isRrItem);

	/**
	 * Get the list of unpaid / partially paid completed invoices.
	 * @param supplierAcctId The supplier account id.
	 * @param pageSetting
	 * @return The list of unpaid invoices.
	 */
	Page<APInvoice> getUnpaidInvoices (int supplierAcctId, String invoiceNumber, String invoiceIds, PageSetting pageSetting);

	/**
	 * Get the AP invoice object
	 * @param supplierAccountId The supplier account id
	 * @param invoiceNumber The invoice number
	 * @param invoiceTypeId The invoice type id
	 * @return The AP invoice object
	 */
	APInvoice getAPInvoice(Integer supplierAccountId, String invoiceNumber, Integer invoiceTypeId);

	/**
	 * Get all unsettled receiving report forms
	 * @param supplierAcctId The supplier account id
	 * @param invoiceNumber The invoice number
	 * @param invoiceIds The invoice ids
	 * @param pageSetting The page setting
	 * @return The list of all unsettled receiving report forms
	 */
	Page<APInvoice> getReceivingReports(Integer formId, Integer supplierAcctId,
			String invoiceNumber, String invoiceIds, boolean isExact, PageSetting pageSetting);

	/**
	 * Get the AP invoices by invoice type
	 * @param criteria The search criteria
	 * @param typeId The invoice type id
	 * @param pageSetting The page setting
	 * @return The paged result
	 */
	Page<APInvoice> retrieveApInvoices(String criteria, Integer typeId, PageSetting pageSetting);

	/**
	 * Get the supplier account per AP line
	 * @param companyId the company ID
	 * @param supplierId the supplier ID
	 * @param supplierAcctId the supplier account ID
	 * @param divisionId the division ID
	 * @param acctId the account ID
	 * @param pageSetting the page setting
	 * @param dateFrom the date from
	 * @param dateTo the date to
	 * @return the supplier account per ap line in page format
	 */
	Page<SupplierApLineDto> getSupplierApLine(int companyId, int supplierId, int supplierAcctId, int divisionId,
			int acctId, Date dateFrom, Date dateTo, PageSetting pageSetting);

	/**
	 * Get the list of AP invoice RR form references
	 * @param companyId The company id
	 * @param divisionId The division id
	 * @param supplierId The supplier id
	 * @param rrNumber The RR number
	 * @param bmsNumber The BMS number
	 * @param dateFrom The date from
	 * @param dateTo The date to
	 * @param status The form status
	 * @param pageSetting The page setting
	 * @return The list of AP invoice RR form references
	 */
	Page<ApInvoiceDto> getRrReferences(Integer companyId, Integer divisionId, Integer supplierId, Integer rrNumber,
			String bmsNumber, Date dateFrom, Date dateTo, Integer status, PageSetting pageSetting);

	/**
	 * Get the list of AP invoice goods/services for general search
	 * @param typeId The type id
	 * @param divisionId The division id
	 * @param searchCriteria The search criteria
	 * @param pageSetting The page setting
	 * @return The list of AP invoice goods/services for general search
	 */
	Page<APInvoice> searchInvoiceGoodsAndServices(int typeId, int divisionId, String searchCriteria,
			PageSetting pageSetting);

	/**
	 * Get the ap invoice object by the child eb object id.
	 * @param childEbObjectId The child eb object id.
	 * @return The {@link APInvoice}.
	 */
	APInvoice getByChildEbObject(int childEbObjectId);

	/**
	 * Get the list of Goods and Services by filter/s.
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @param supplierId The supplier id.
	 * @param supplierAcctId The supplier account id.
	 * @param invGsNumber The Good Service References.
	 * @param bmsNumber The BMS number.
	 * @param dateFrom The date filtering will start.
	 * @param dateTo The date filtering will end.
	 * @param status The status (1 = all, 2 = unused, 3 = used.)
	 * @param pageSetting The page settings
	 * @return The list of Goods and Services by filter/s.
	 */
	Page<ApInvoiceDto> getInvGsReferences(Integer companyId, Integer divisionId, Integer supplierId,
			Integer supplierAcctId, Integer invGsNumber, String bmsNumber, Date dateFrom, Date dateTo,
			Integer status, PageSetting pageSetting);

	/**
	 * Get the list of Ap invoice goods and services by receiving report.
	 * @param rrId The receiving report id.
	 * @return The list of ap invoice goods and services.
	 */
	APInvoice getApInvoiceGsByRR(Integer rrId);

	List<APInvoice> getChildInvoicesByParentInvoiceObjectId(Integer objectId);

	/**
	 * Check if invoice number is unique based on the provided supplier id.
	 * @param supplierId The supplier id.
	 * @param invoiceId The invoice id.
	 * @param invoiceNumber The invoice number.
	 * @return True if invoice number is unique, otherwise false.
	 */
	boolean isUniqueInvoiceNoBySupplier(int supplierId, Integer invoiceId, String invoiceNumber);

	/**
	 * Get the all of the ap invoice data.
	 * @param searchParam Search AP Invoice by sequence number, invoice number,
	 * description, company number, date and amount.
	 * @param statuses the statuses of the GL that will be retrieved.
	 * @param pageSetting The page setting.
	 * @param typeId Invoice type id.
	 * @return The paged data.
	 */
	Page<APInvoice> retrieveReplenishments (ApprovalSearchParam searchParam, List<Integer> statuses, PageSetting pageSetting, int typeId);

	/**
	 * Generate the sequence number.
	 * @return Generated sequence number.
	 */
	Integer generateReplenishmentSequenceNo(int invoiceTypeId, Integer companyId, Integer divisionId);

	/**
	 * Get Annual Alpha List of Payees final withholding taxes.
	 * @param comnpanyId The comnpanyId.
	 * @param divisionId The divisionId.
	 * @param year The year for generated data.
	 * @param isTinFormatted If true the tin will be formatted with dash (-) sign
	 * @param schedule Schedule number for generating dat file.
	 * @return The paged data.
	 */
	List<AnnualAlphalistPayeesDetailDto> getAnnualAlphalistWTFinal (Integer companyId, Integer divisionId,
			Integer year, boolean isTinFormatted,  String schedule);

	/**
	 * Get the Monthly Summary of Withholding Taxes in page format.
	 * @param divisionId The company id.
	 * @param divisionId The division id.
	 * @param year The year
	 * @param month The month in number form. 0-the index of January in java, 1-the equivalent value of January in sql
	 * @param wtTaxType 0 = withholding taxes expanded, 1 = final withholding taxes
	 * @return The list of Withholding Taxes Expanded.
	 */
	Page<COCTaxDto> getWithholdingTaxesSummary(Integer companyId, Integer divisionId, Integer year, Integer month, String wtTaxType, PageSetting pageSetting);

	/**
	 * Get the Quarterly Summary of Withholding Taxes Expanded in page format.
	 * @param divisionId The company id.
	 * @param divisionId The division id.
	 * @param year The year
	 * @param quarter The quarter in number form. 0-the index of first quarter in java, 1-the equivalent value of 1st quarter in sql
	 * @return The list of Withholding Taxes Expanded.
	 */
	Page<COCTaxDto> getQuarterlySummaryWT(Integer companyId, Integer divisionId, Integer year, Integer quarter, PageSetting pageSetting);

	/**
	 * Get the Quarterly Value-Added Tax Declaration
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @param selyear The selected year.
	 * @param selquarter The selected quarter.
	 * @param selmonth The selected month.
	 * @param quarter The quarter in number form. 0-the index of first quarter in java, 1-the equivalent value of 1st quarter in sql
	 * @return The list of Withholding Taxes Expanded.
	 */
	List<VATDeclarationDto> getQrtrlyValAddedTaxDeclrtnData(int companyId, int divisionId, int selyear,
			int selquarter, int selmonth);

	/**
	 * @param companyId
	 * @param divisionId
	 * @param year
	 * @param scheduleId
	 * @param pageSetting
	 * @return
	 */
	List<AnnualAlphalsitWTEDetailsDto>getAnnualAlphalistWTExpanded(int companyId, Integer divisionId, Integer monthFrom, Integer monthTo, Integer year, String schedTypeId, boolean isPDF);


	/**
	 * Get all Monthly Alphalist of Payees
	 * @param companyId The Company ID.
	 * @param divisionId The Division ID.
	 * @param yEar the Selected Year.
	 * @param mOnth The Selected Month.
	 * @return List of Monthly Alphalist of Payees.
	 */
	List<MonthlyAlphalistPayeesDto> getMAPayees(int companyId, int divisionId, Integer fromMonth, Integer toMonth, Integer year, String schedTypeId);
}
