package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.StockAdjustmentType;

/**
 * Data Access Object {@link StockAdjustmentType}

 */
public interface StockAdjustmentTypeDao extends Dao<StockAdjustmentType>{

	/**
	 * Get the list of Stock Adjustment types.
	 * @param companyId The id of the company of the Stock Adjustment Type.
	 * @param divisionId The division id.
	 * @param activeOnly If active only
	 * @return The list of Stock adjustment types.
	 */
	List<StockAdjustmentType> getSATypes(Integer companyId, Integer divisionId, Boolean activeOnly);

	/**
	 * Get all the stock adjustment types.
	 * @param companyId The id of the company.
	 * @param name The name of the adjustment type.
	 * @param status The status of the adjustment type.
	 * @param pageSetting The page setting.
	 * @return The list of stock adjustment types in page format.
	 */
	Page<StockAdjustmentType> getAllSAdjustmentTypes(Integer companyId, String name,
			SearchStatus status, PageSetting pageSetting);

	/**
	 * Validate stock adjustment type name if unique per company.
	 * @param name The name to be validated.
	 * @param companyId The id of the company.
	 * @param adjustmentTypeId The id of the stock adjustment type.
	 * @param divisionId The division id
	 * @return True if unique, otherwise false.
	 */
	boolean isUniqueSATName(String name, Integer companyId, Integer adjustmentTypeId, Integer divisionId);
}
