package eulap.eb.dao;

import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.RReturnToSupplier;
import eulap.eb.service.fap.FormPluginParam;
import eulap.eb.web.dto.ReturnToSupplierRegisterDto;
/**
 * Data access object of {@link RReturnToSupplier}

 */
public interface RReturnToSupplierDao extends Dao<RReturnToSupplier>{

	/**
	 * Generate the return to supplier number based on the company id.
	 * @param companyId The selected company id.
	 * @return The generated return to supplier number.
	 */
	public Integer generateMaxRtsNumber(Integer companyId);

	/**
	 * Search the return to supplier.
	 * @param searchCriteria The search criteria.
	 * @param invoiceTypeId The invoice type id.
	 * @param pageSetting The page setting.
	 * @return The page result.
	 */
	public Page<RReturnToSupplier> searchReturnToSupplier(int invoiceTypeId, String searchCriteria, PageSetting pageSetting);

	/**
	 * Get the return to suppliers.
	 * @param param The search parameter
	 * @param typeId The invoice type Id.
	 * @return The paged return to suppliers.
	 */
	public Page<RReturnToSupplier> getReturnToSuppliers(int typeId, FormPluginParam param);

	/**
	 * Get the Retail - RTS object using the unique id of AP Invoice.
	 * @param apInvoiceId The id of the ap invoice.
	 * @return The Retail - RTS object.
	 */
	RReturnToSupplier getRtsByInvoiceId(int apInvoiceId);

	/**
	 * Get the RTS report.
	 * @param companyId The company ID.
	 * @param divisionId The division ID.
	 * @param warehouseId The warehouse ID.
	 * @param supplierId The supplier ID.
	 * @param supplierAcctId The supplier account ID.
	 * @param rrDateFrom rr Start date.
	 * @param rrDateTo rr end date.
	 * @param rtsDateFrom rts start date.
	 * @param rtsDateTo rts end date.
	 * @param amountFrom Starting Amount.
	 * @param amountTo Amount ending.
	 * @param statusId The RTS status.
	 * @param paymentStatId The payment ID
	 * @param pageSetting The page settings.
	 * @return Return the RTS Report.
	 */
	public Page<RReturnToSupplier> getRTSReportRegister(int companyId,
			int warehouseId, int supplierId, int supplierAcctId,
			String rrDateFrom, String rrDateTo, Date rtsDateFrom, Date rtsDateTo,
			Double amountFrom, Double amountTo, int statusId,
			Integer paymentStatId, PageSetting pageSetting);

	/**
	 * Get the list of return to supplier.
	 * @param companyId The company ID.
	 *  @param divisionId The division ID.
	 * @param warehouseId The warehouse ID.
	 * @param supplierId The supplier ID.
	 * @param supplierAcctId The supplier account ID.
	 * @param rtsDateFrom rts start date.
	 * @param rtsDateTo rts end date.
	 * @param rrDateFrom rr Start date.
	 * @param rrDateTo rr end date.
	 * @param amountFrom Starting Amount.
	 * @param amountTo Amount ending.
	 * @param statusId The RTS status.
	 * @param paymentStatusId The payment ID
	 * @param pageSetting The page settings.
	 * @return Return the RTS Report.
	 **/
	List<ReturnToSupplierRegisterDto> getReturnToSupplierRegisterData(Integer companyId, Integer divisionId, Integer warehouseId,
			Integer supplierId, Integer supplierAcountId, Date rtsDateFrom, Date rtsDateTo,  Date rrDateFrom, Date rrDateTo,
			double amountFrom, double amountTo,  Integer rtsStatusId, Integer paymentStatId, PageSetting pageSetting);


}
