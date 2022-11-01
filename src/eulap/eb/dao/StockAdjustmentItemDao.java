package eulap.eb.dao;

import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.StockAdjustmentItem;

/**
 * Data Access Object {@link StockAdjustmentItem}

 */
public interface StockAdjustmentItemDao extends Dao<StockAdjustmentItem>{

	/**
	 * Get the list of stock adjustment items using the id of the stock adjustment.
	 * @param stockAdjustmentId The unique id of the stock adjustment.
	 * @param itemId The id of the item.
	 * @return List of stock adjustment items.
	 */
	List<StockAdjustmentItem> getSAItems(int stockAdjustmentId, Integer itemId);
	
	/**
	 * Get the list of stock adjustment items using the id of the stock adjustment.
	 * @param stockAdjustmentId The unique id of the stock adjustment.
	 * @return List of stock adjustment items.
	 */
	List<StockAdjustmentItem> getSAItems(int stockAdjustmentId);

	/**
	 * Get the list of Stock Adjustment Items.
	 * @param companyId The id of the company.
	 * @param warehouseId The id of the warehouse.
	 * @param adjustmentTypeId The id of the stock adjustment type.
	 * @param dateFrom Start date of the date range.
	 * @param dateTo End date of the date range.
	 * @param pageSetting The page setting.
	 * @return The list of Stock Adjustment Items in paging format.
	 */
	Page<StockAdjustmentItem> getStockAdjustmentRegisterData(Integer companyId, Integer warehouseId,
			Integer adjustmentTypeId, Date dateFrom, Date dateTo, PageSetting pageSetting);

	/**
	 * Get the saved stock adjustment item average cost
	 * @param stockAdjustmentId The stock adjustment id
	 * @param itemId The item id
	 * @return The item average cost
	 */
	double getItemAverageCost(int stockAdjustmentId, int itemId);
}
