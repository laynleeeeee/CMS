package eulap.eb.dao;

import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.StockAdjustment;
import eulap.eb.web.dto.ApprovalSearchParam;

/**
 * Data Access Object {@link StockAdjustment}

 */
public interface StockAdjustmentDao extends Dao<StockAdjustment>{

	/**
	 * Search stock adjustment forms.
	 * @param searchCriteria The search criteria.
	 * @param typeId The type of stock adjustment {1=IN, 2=OUT, 3=IN-IS, 4=OUT-IS}
	 * @param pageSetting The page setting.
	 * @return List of stock adjustments in paged format.
	 */
	Page<StockAdjustment> searchStockAdjustments(String searchCriteria, int typeId, PageSetting pageSetting);

	/**
	 * Get all the Stock Adjustment forms by status.
	 * @param typeId, the type of Stock Adjustment. 1=IN, 2=OUT
	 * @param searchParam The search parameter.
	 * @return List of stock adjustment forms in paged format.
	 */
	Page<StockAdjustment> getAllSAsByStatus(ApprovalSearchParam searchParam, List<Integer> formStatusIds, Integer typeId, PageSetting pageSetting);
	
	/**
	 * Get the withdrawn stock adjustment. 
	 * @param itemId The item id. 
	 * @param warehouseId The selected warehouse.
	 * @param date The transaction date.
	 * @param pageSetting the page setting.
	 */
	Page<StockAdjustment> getWithdrawnStockAdjustments (int itemId, int warehouseId, Date date, PageSetting pageSetting);

	/**
	 * Generate SA Number by company and stock adjustment type.
	 * @param companyId The company id.
	 * @param typeId The type id.
	 * @return
	 */
	int generateSANoByCompAndTypeId(int companyId, int typeId);
}
