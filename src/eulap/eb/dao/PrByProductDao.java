package eulap.eb.dao;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.PrByProduct;

/**
 * Data access object of {@link PrByProduct}

 *
 */
public interface PrByProductDao extends Dao<PrByProduct> {

	/**
	 * Get the By product by refObject Id and Item Id.
	 * @param refObjectId The reObject Id.
	 * @param itemId The id of the item.
	 * @return The By Product Object.
	 */
	PrByProduct getByRefObjectId(Integer refObjectId, Integer itemId);
}
