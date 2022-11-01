package eulap.eb.dao;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.PrMainProduct;

/**
 * Data access object of {@link PrMainProduct}

 *
 */
public interface PrMainProductDao extends Dao<PrMainProduct> {

	/**
	 * Get the Main product by refObject Id and Item Id.
	 * @param refObjectId The reObject Id.
	 * @param itemId The id of the item.
	 * @return The Main Product Object.
	 */
	PrMainProduct getRefObjectId(Integer refObjectId, Integer itemId);
}
