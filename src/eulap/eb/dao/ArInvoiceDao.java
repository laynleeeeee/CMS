package eulap.eb.dao;

import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.ArInvoice;
import eulap.eb.service.fap.FormPluginParam;
import eulap.eb.web.dto.COCTaxDto;
import eulap.eb.web.dto.CertFinalTaxWithheldMonthlyDto;
import eulap.eb.web.dto.SAWTDto;
import eulap.eb.web.dto.bir.SummaryLSPImportationDto;
import eulap.eb.web.dto.bir.SummaryLSPPurchasesDto;
import eulap.eb.web.dto.bir.SummaryLSPSalesDto;

/**
 * Data access object for {@link ArInvoice}

 */

public interface ArInvoiceDao extends Dao<ArInvoice> {

	/**
	 * Get the paged delivery receipts.
	 * @param param The search parameter.
	 * @param typeId The ar invoice type id.
	 * @return The paged delivery receipts.
	 */
	Page<ArInvoice> getArInvoices(Integer typeId, FormPluginParam param);

	/**
	 * Filter {@link ArInvoice}
	 * @param criteria The searchCriteria
	 * @param pageSetting The Pagesetting.
	 * @return The paged collection of {@link ArInvoice}
	 */
	Page<ArInvoice> searchArInvoices(String criteria, PageSetting pageSetting);

	/**
	 * Generate sequence number.
	 * @param companyId The company id.
	 * @param typeId The AR invoice type id
	 * @return The sequence number.
	 */
	int generateSeqNo(int companyId, int typeId);

	/**
	 * Get the ar invoice by dr reference.
	 * @param ebObjectId The eb object id.
	 * @return The ar invoice.
	 */
	ArInvoice getByDeliveryReceipt(int ebObjectId);

	/**
	 * Get the list of invoices to be paid.
	 * @param arCustomerAcctId The customer acct id.
	 * @return The list of invoices.
	 */
	List<ArInvoice> getArInvoices(int arCustomerAcctId, String criteria, String tNumbers, boolean isShow);

	/**
	 * Get the AR invoice object
	 * @param sequenceNo The sequence number
	 * @param companyId The company id
	 * @return The AR invoice object
	 */
	ArInvoice getArInvoiceBySequenceNo(int sequenceNo, int companyId);

	/**
	 * Search the {@link ArInvoice}
	 * @param searchCriteria The search Criteria
	 * @param typeId The Ar invoice type id.
	 * @return The List of searched Ar Invoices.
	 */
	Page<ArInvoice> searchARInvoice(String criteria, int typeId, PageSetting pageSetting);

	/**
	 * Get the list of ar invoicess by delivery receipt id.
	 * @param drId The delivery receipt id.
	 * @return The list of ARIs.
	 */
	List<ArInvoice> getARIsByDRId(Integer drId);

	/**
	 * Get the Summary Alphalist of Withholding Taxes
	 * @param companyId The company id
	 * @param divisionId The division id
	 * @param birAtcId The bir atc id
	 * @param monthFrom The date from
	 * @param monthTo The date from
	 * @param year The date to
	 * @param processTin If processed tin
	 * @return List of Summary Alphalist of Withholding Taxes
	 */
	List<SAWTDto> getSAWT(Integer companyId, Integer divisionId, Integer birAtcId, Integer monthFrom, Integer monthTo, Integer year, boolean processTin);

	/**
	 * Get the summary list of Sales 
	 * @param companyId The company id
	 * @param divisionId The division id
	 * @param year Year of sales
	 * @param month Month of Sales
	 * @return the list of sales
	 */
	List<SummaryLSPSalesDto> getSLSales (int companyId, int divisionId, int year, int month, boolean isTinFormatted);

	/**
	 * Get the summary list of purchases. 
	 * @param companyId The company id
	 * @param divisionId The division id
	 * @param year Year of sales
	 * @param month Month of Sales
	 * @return the list of purchases.
	 */
	List<SummaryLSPPurchasesDto> getSLPurchases (int companyId, int divisionId, int year, int month, boolean isTinFormatted);

	/**
	 * get the list of final tax withheld monthly
	 * @param companyId the id of the company
	 * @param divisionId the division Id
	 * @param year the final tax withheld year
	 * @param fromMonth the final tax withheld start month
	 * @param toMonth the final tax withheld end month
	 * @param birAtcId the bir atc code
	 * @param pageSetting the page setting of the report
	 * @return The list of Certificate of final tax withheld monthly
	 */
	List<CertFinalTaxWithheldMonthlyDto> getFinalTaxWithheldMonthly(int companyId, int divisionId, int year,
			int fromMonth, int toMonth, int birAtcId, PageSetting pageSetting);

	/**
	 * Get the summary list of importations. 
	 * @param companyId The company id
	 * @param divisionId The division id
	 * @param year Year of sales
	 * @param month Month of Sales
	 * @return the list of imports.
	 */
	List<SummaryLSPImportationDto> getSLImportations(int companyId, int divisionId, int year, int month, boolean isTinFormatted);

	/**
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @param dateFrom The date from.
	 * @param dateTo The date to.
	 * @return The list COC Tax Withheld at Source data.
	 */
	List<COCTaxDto> getCOCTaxData(Integer companyId, Integer divisionId, Date dateFrom, Date dateTo);
}