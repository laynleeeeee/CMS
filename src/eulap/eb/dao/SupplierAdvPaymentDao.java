package eulap.eb.dao;

import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.SupplierAdvancePayment;
import eulap.eb.service.fap.FormPluginParam;
import eulap.eb.service.report.SupplierAdvancePaymentAgingParam;
import eulap.eb.web.dto.PurchaseOrderDto;
import eulap.eb.web.dto.SupplierAdvPaymentRegstrDto;
import eulap.eb.web.dto.SupplierAdvancePaymentAgingDto;

/**
 * Data access object for {@link SupplierAdvancePayment}

 */

public interface SupplierAdvPaymentDao extends Dao<SupplierAdvancePayment> {

	/**
	 * Get the list of purchase order references
	 * @param companyId The company id
	 * @param divisionId The division id
	 * @param supplierId The supplier id
	 * @param supplierAcctId The supplier account id
	 * @param poNumber The purchase order number
	 * @param bmsNumber The BMS number
	 * @param dateFrom The date from filter
	 * @param dateTo The date to filter
	 * @param pageSetting The page setting
	 * @return The list of purchase order references
	 */
	Page<PurchaseOrderDto> getPurchaseOrders(Integer companyId, Integer divisionId, Integer supplierId,
			Integer supplierAcctId, Integer poNumber, String bmsNumber, Date dateFrom, Date dateTo,
			PageSetting pageSetting);

	/**
	 * Get the list of purchase orders applied to supplier advance payment
	 * @param advPaymentId The advance payment id
	 * @param rpurchaseOrderId The purchase order id
	 * @return The list of purchase orders applied to supplier advance payment
	 */
	List<SupplierAdvancePayment> getPoAdvPayments(Integer advPaymentId, Integer rpurchaseOrderId);

	/**
	 * Generate the form sequence number
	 * @param companyId The company id
	 * @param divisionId The division id
	 * @return The generated form sequence number
	 */
	Integer generateSequenceNo(Integer companyId, Integer divisionId);

	/**
	 * Get the list of supplier advance payment for approval form view
	 * @param divisionId The division id
	 * @param param The form plugin parameter
	 * @return The list of supplier advance payment for approval form view
	 */
	Page<SupplierAdvancePayment> getSupplierAdvPayments(int divisionId, FormPluginParam param);

	/**
	 * Get the list of supplier advance payments in general search
	 * @param divisionId The division id
	 * @param searchCriteria The search criteria
	 * @param pageSetting The page setting
	 * @return The list of supplier advance payments in general search
	 */
	Page<SupplierAdvancePayment> searchSupplierAdvancePayments(int divisionId, String searchCriteria, PageSetting pageSetting);

	/**
	 * Get the supplier advance payment object by the child eb object id.
	 * @param childEbObjectId The child eb object id.
	 * @return The {@link SupplierAdvancePayment}.
	 */
	SupplierAdvancePayment getByChildEbObject(int childEbObjectId);

	/**
	 * Get the {@link SupplierAdvancePayment} object that used another {@link SupplierAdvancePayment} as a child transaction.
	 * @param objectId The child {@link SupplierAdvancePayment} object id.
	 * @return The {@link SupplierAdvancePayment}.
	 */
	List<SupplierAdvancePayment> getSapBySapObjectId(Integer objectId);

	/**
	 * Get the the list supplier advance payment register
	 * @param companyId the company id
	 * @param divisionId the division id
	 * @param supplierId the supplier id
	 * @param supplierAccountId the supplier account id
	 * @param bmsNumber the bms number of supplier advance payment
	 * @param dateFrom
	 * @param dateTo
	 * @param status
	 * @param pageSetting
	 * @return the list of supplier advance payment register
	 */
	Page<SupplierAdvPaymentRegstrDto> getSupplierAdvPaymentRgstr(int companyId, int divisionId, int supplierId,
			int supplierAcctId, String bmsNumber, Date dateFrom, Date dateTo, int status, PageSetting pageSetting);

	/**
	 * Generate data for Supplier Advance Payment Report.
	 * @param param The Supplier Advance Payment parameters.
	 * @param pageSetting The Page setting.
	 * @return The page result
	 */
	Page<SupplierAdvancePaymentAgingDto> generateSupplierAdvancePaymentAging(SupplierAdvancePaymentAgingParam param,
			PageSetting pageSetting);

	/**
	 * Check if {@link PurchaseOrder}
	 * @param poId The {@link PurchaseOrder} id
	 * @return If used or not
	 */
	boolean isUsedBySAP(Integer poId);
}