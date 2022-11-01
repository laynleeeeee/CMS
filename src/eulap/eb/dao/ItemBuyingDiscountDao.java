package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.ItemBuyingDiscount;


/**
 * Data access object for {@link ItemBuyingDiscoun}

 *
 */
public interface ItemBuyingDiscountDao extends Dao<ItemBuyingDiscount> {

	/**
	 * Get the list of buying discounts of the item.
	 * @param itemId The id of the item.
	 * @param companyId The id of the company.
	 * @return The list of {@link ItemBuyingDiscount}.
	 */
	List<ItemBuyingDiscount> getBuyingDiscounts(Integer itemId, Integer companyId);
}
