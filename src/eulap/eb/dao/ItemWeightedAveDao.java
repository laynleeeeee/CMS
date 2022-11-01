package eulap.eb.dao;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.ItemWeightedAverage;

/**
 * Data access object interface for {@code ItemWeightedAverage}

 */

public interface ItemWeightedAveDao extends Dao<ItemWeightedAverage> {

	/**
	 * Get the item weighted average
	 * @param warehouseId The warehouse id
	 * @param itemId The item id
	 * @return The item weighted average
	 */
	ItemWeightedAverage getItemWeightedAverage(Integer warehouseId, Integer itemId);
}
