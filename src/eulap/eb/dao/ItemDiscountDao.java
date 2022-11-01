package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.ItemDiscount;

/**
 * Data access object for {@link ItemDiscount}

 *
 */
public interface ItemDiscountDao extends Dao<ItemDiscount> {
	
	/**
	 * Get the list of item discounts by item.
	 * @param itemId The item id.
	 * @param activeOnly True if to return active discounts only, otherwise false.
	 * @return The list of item discounts.
	 */
	List<ItemDiscount> getItemDiscountsByItem (Integer itemId, boolean activeOnly);

	/**
	 * Get the list of item discounts by stock code and company.
	 * @param itemId The id of the item.
	 * @param companyId The company id.
	 * @return The list of item discounts.
	 */
	List<ItemDiscount> getIDsByItemIdAndCompany (int itemId, Integer companyId);

	/**
	 * Get the list of item discount id with selected inactive discount id
	 * @param itemId The item id
	 * @param companyId The company id
	 * @param itemDiscountId The item discount id
	 * @return The list of item discount id with selected inactive discount id
	 */
	List<ItemDiscount> getDiscountsWithSlctdDiscId(Integer itemId,
			Integer companyId, Integer itemDiscountId);

	/**
	 * Get the item discount by item and name.
	 * @param itemId The item id.
	 * @param discountName The item discount name.
	 * @return {@link ItemDiscount}
	 */
	ItemDiscount getItemDiscountByItemAndName(Integer itemId, String discountName);
}
