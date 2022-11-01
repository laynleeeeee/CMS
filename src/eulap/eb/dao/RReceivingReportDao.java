package eulap.eb.dao;

import java.util.Date;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.RReceivingReport;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.fap.FormPluginParam;
import eulap.eb.web.dto.RrRegisterDto;
/**
 * Data access object of {@link RReceivingReport}

 */
public interface RReceivingReportDao extends Dao<RReceivingReport>{

	/**
	 * Generate the receiving report number based on the company id.
	 * @param companyId The selected company id.
	 * @return The generated receiving report number.
	 */
	public Integer generateMaxRRNumber(Integer companyId);

	/**
	 * Search receiving reports.
	 * @param searchCriteria The search criteria.
	 * @param pageSetting The page setting.
	 * @return The page result.
	 */
	public Page<RReceivingReport> searchReceivingReport (String searchCriteria, int typeId, PageSetting pageSetting);

	/**
	 * Get the list of receiving reports for approval.
	 * @param param The search parameter
	 * @return The paged receiving reports.
	 */
	public Page<RReceivingReport> getReceivingReports(int typeId, FormPluginParam param);

	/**
	 * Get the receiving reports by selected parameter.
	 * @param user The current logged user.
	 * @param companyId The company id.
	 * @param warehouseId The warehouse id.
	 * @param supplierId The supplier id.
	 * @param rrNumber The receiving report number.
	 * @param dateFrom The start date.
	 * @param dateTo The end date.
	 * @param status Used, Unused, All
	 * @param pageSetting The page setting.
	 * @return The paged receiving reports.
	 */
	public Page<RReceivingReport> getReceivingReports(User user, Integer companyId, Integer warehouseId, Integer supplierId,
			String rrNumber, Date dateFrom, Date dateTo, Integer status, PageSetting pageSetting);

	/**
	 * Get the R Receiving Report object.
	 * @param apInvoiceId The AP Invoice id.
	 * @return R Receiving Report object.
	 */
	public RReceivingReport getRrByInvoiceId(int apInvoiceId);

	/**
	 * Get the receiving report register data.
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @param warehouseId The warehouse id.
	 * @param supplierId The supplier id.
	 * @param supplierAcctId The supplier account id. 
	 * @param dateFrom The invoice date start.
	 * @param dateTo The invoice date end.
	 * @param termId The term id.
	 * @param amountFrom Amount start.
	 * @param amountTo Amount end.
	 * @param statusId The invoice status id.
	 * @param paymentStatId The payment status id.
	 * @return The receiving report register data.
	 */
	public Page<RrRegisterDto> getRrRegisterData(int companyId, int divisionId, int warehouseId, int supplierId,
			int supplierAcctId, Date dateFrom, Date dateTo, int termId, Double amountFrom, Double amountTo,
			int statusId, Integer paymentStatId, PageSetting pageSetting);
}
