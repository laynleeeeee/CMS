package eulap.eb.dao;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.ProductLine;

/**
 * Data access object for {@link ProductLine}

 *
 */
public interface ProductLineDao extends Dao<ProductLine>{

	/**
	 * Check if product line has duplicate entry.
	 * @param productLine The product line.
	 * @return True if the product line has duplicate entry, otherwise false.
	 */
	boolean isDuplicateProductline(ProductLine productLine);

	/**
	 * Get the list of product list.
	 * @param productLine The main item stock code.
	 * @param rawMaterial The raw material stock code.
	 * @param status The search status.
	 * @param pageNumber The page number.
	 * @return The list of product line.
	 */
	Page<ProductLine> getProductList(String productLine, String rawMaterial, SearchStatus searchStatus,
			PageSetting pageSetting);

	/**
	 * Get product line by item id.
	 * @param itemId The item id.
	 * @return The product line.
	 */
	ProductLine getByItem(int itemId);

	/**
	 * Checks if the item has product line configuration.
	 * @param itemId The unique id of the item to be checked.
	 * @return True if there is config, otherwise false.
	 */
	boolean hasConfig(int itemId);
}
