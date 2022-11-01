package eulap.eb.dao;

import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.RPurchaseOrder;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.BmsTrackerReportDto;
import eulap.eb.web.dto.PoRegisterDto;

/**
 * Data Access Object {@link RPurchaseOrder}

 */
public interface RPurchaseOrderDao extends Dao<RPurchaseOrder> {

	/**
	 * Get the max PO number by company.
	 * @param companyId The company id.
	 * @param divisionId The division id
	 * @return The PO Number.
	 */
	Integer getMaxPONumber (int companyId, Integer divisionId);

	/**
	 * Get the latest Unit Cost of item
	 * @param itemId The item Id
	 * @param supplierAcctId The supplier account Id
	 * @return unit cost
	 */

	Double getLatestUCPerSupplierAndItem(int itemId, int supplierAcctId);

	/**
	 * Search for retail purchase order
	 * @param divisionId The division id
	 * @param criteria The search criteria
	 * @param pageSetting The page setting
	 * @return Paged search results.
	 */

	Page<RPurchaseOrder> searchPOs(Integer divisionId, String criteria, PageSetting pageSetting);

	/**
	 * Get all the Retail - POs by status.
	 * @param searchParam The search parameter.
	 * @return The paged result.
	 */
	Page<RPurchaseOrder> getAllPOsByStatus(ApprovalSearchParam searchParam, List<Integer> formStatusIds, PageSetting pageSetting);

	/**
	 * Get the list of purchase orders.
	 * @param companyId The company id
	 * @param user The current logged user.
	 * @param supplierId The supplier id
	 * @param poNumber The purchase order number
	 * @param dateFrom The start date
	 * @param dateTo The end date
	 * @param status Used, UnUsed and All
	 * @param pageNumber The paged number
	 * @return The page result
	 */
	Page<RPurchaseOrder> getPurchaseOrders(User user, Integer companyId, Integer supplierId,
			String poNumber, Date dateFrom, Date dateTo, Integer status, PageSetting pageSetting);

	/**
	 * Get the list of purchase orders.
	 * @param companyId The company id
	 * @param divisionId The division id.
	 * @param user The current logged user.
	 * @param supplierId The supplier id
	 * @param poNumber The purchase order number.
	 * @param bmsNumber The bms number.
	 * @param dateFrom The start date
	 * @param dateTo The end date
	 * @param status Used, UnUsed and All
	 * @param pageNumber The paged number
	 * @return The page result
	 */
	Page<RPurchaseOrder> getPurchaseOrders(User user, Integer companyId, Integer divisionId, Integer supplierId,
			String poNumber, String bmsNumber, Date dateFrom, Date dateTo, Integer status, PageSetting pageSetting);

	/**
	 * Get the PO Remaining QTY by reference object id.
	 * @param refenceObjectId The reference object id.
	 * @param wsId The id of Withdrawal Slip.
	 * @return The remaining quantity.
	 */
	double getRemainingQty(Integer refenceObjectId, Integer wsId);

	/**
	 * Get the data to generate purchase order register report in page format.
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @param supplierId The supplier id.
	 * @param supplierAccountId The supplier account id.
	 * @param termId The term id.
	 * @param poDateFrom The purchase order start date.
	 * @param poDateTo The purchase order end date.
	 * @param rrDateFrom The receiving report start date.
	 * @param rrDateTo The receiving report end date.
	 * @param statusId The status id.
	 * @param deliveryStatus The delivery status.
	 * @return The purchase order report data
	 */
	List<PoRegisterDto> getPoRegisterData(Integer companyId, Integer divisionId, Integer supplierId,
			Integer supplierAccountId, Integer termId, Date poDateFrom, Date poDateTo, Date rrDateFrom, Date rrDateTo,
			Integer statusId, String deliveryStatus);

	/**
	 * Get the total remaining quantity of the PR reference form
	 * @param refRfObjectId The reference RF item object id
	 * @param itemId The item id
	 * @return The total remaining quantity of the PR reference form
	 */
	double getRemainingPrReferenceQty(Integer refRfObjectId, Integer itemId);

	/**
	 * Get the list of all purchase orders by division for approval
	 * @param typeId The division type id
	 * @param searchParam The search parameter object
	 * @param formStatusIds The list of form status ids
	 * @param pageSetting The page settings
	 * @return The list of all purchase orders by division for approval
	 */
	Page<RPurchaseOrder> getAllPODivsByStatus(int typeId, ApprovalSearchParam searchParam,
			List<Integer> formStatusIds, PageSetting pageSetting);

	/**
	 * Get the data to be used for the BMS Tracker Report.
	 * @param companyId The id of the company.
	 * @param divisionId The id of the division.
	 * @param type The invoice Type, -1 for all invoice type, 0 for PO, 1 for Non PO.
	 * @param bmsNo the number of BMS.
	 * @param poDateFrom Start date of the PO date range.
	 * @param poDateTo End date of the PO date range.
	 * @param invoiceDateFrom Start date of the Invoice date range.
	 * @param invoiceFateTo End date of the Invoice date range.
	 * @param statusId The id of the payment status.
	 * @param pageSetting The page setting.
	 * @return The paged data for the report.
	 */
	Page<BmsTrackerReportDto> getBmsTrackerData(Integer companyId, Integer divisionId, Integer typeId,
			String bmsNo, Date poDateFrom, Date poDateTo, Date invoiceDateFrom, Date invoiceDateTo,
			Integer statusId, PageSetting pageSetting);
}
