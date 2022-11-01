package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.PrRawMaterialsItem;

/**
 * Data access object of {@link PrRawMaterialsItem}

 *
 */
public interface PrRawMaterialsItemDao extends Dao<PrRawMaterialsItem> {

	/**
	 * Get all the list of processing report - raw material items.
	 * @param processingReportId The processing report id.
	 * @param itemId The item id.
	 * @return The list of processing report - raw material items.
	 */
	List<PrRawMaterialsItem> getAllPrRawMaterialItems(Integer processingReportId, Integer itemId);
}
