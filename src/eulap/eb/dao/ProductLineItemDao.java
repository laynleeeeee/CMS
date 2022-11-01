package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.ProductLineItem;

/**
 * Data access object for {@link ProductLineItem}

 *
 */
public interface ProductLineItemDao extends Dao<ProductLineItem>{

	/**
	 * Get all the raw materials of the main product.
	 * @param mainItemId The id of the item.
	 * @return The list of raw materials.
	 */
	List<ProductLineItem> getRawMaterials(int mainItemId);
}
