package eulap.eb.dao;

import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.RReceivingReportItem;
import eulap.eb.web.dto.ItemUnitCostHistoryPerSupplier;
/**
 * Data access object of {@link RReceivingReportItem}

 */
public interface RReceivingReportItemDao extends Dao<RReceivingReportItem>{

	/**
	 * Get the RR items of the receiving report.
	 * @param apInvoiceId The unique id of the AP Invoice.
	 * @return The list of rr items.
	 */
	List<RReceivingReportItem> getRrItems(Integer apInvoiceId);
	
	/**
	 * Generate the Item Unit Cost History Per Supplier report.
	 * @param companyId The company id.
	 * @param itemId The item id.
	 * @param supplierId The supplier id.
	 * @param supplierAccountId The supplier account id.
	 * @param dateFrom The start date.
	 * @param dateTo The end date.
	 * @param pageSetting The page setting.
	 * @return Item Unit Cost History Per Supplier report.
	 */
	Page<ItemUnitCostHistoryPerSupplier> generateItemUCHistPerSupplier(int companyId, int divisionId, int itemId, int supplierId,
			int supplierAccountId, Date dateFrom, Date dateTo, PageSetting pageSetting);

	/**
	 * Get the latest unit cost of the item by supplier account.
	 * @param itemId The unique id of the item.
	 * @param warehouseId The id of the warehouse.
	 * @param supplierAcctId The id of the supplier account.
	 * @param isSerial True if item is serialized item, otherwise false.
	 * @return The latest unit cost of the item.
	 */
	double getLatestUcPerSupplierAcct(int itemId, int warehouseId, int supplierAcctId, boolean isSerial);

	/**
	 * Get the total quantity of the item per purchase order number.
	 * @param itemId The unique id of the item.
	 * @param poNumber The purchase order number of the receiving report.
	 * @return The total quantity of the item per purchase order number.
	 */
	double getTotalItemQtyByPO(Integer itemId, String poNumber);

	/**
	 * Get the latest unit cost of the item by warehouse.
	 * @param itemId The unique id of the item.
	 * @param warehouseId The id of the warehouse.
	 *  @param isSerial True if item is serialized item, otherwise false.
	 * @return The latest unit cost of the item.
	 */
	double getLatestUcPerWarehouse(int itemId, int warehouseId, boolean isSerial);
}
