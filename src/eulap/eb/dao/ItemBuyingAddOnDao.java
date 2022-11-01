package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.ItemBuyingAddOn;


/**
 * Data access object for {@link ItemBuyingAddOn}

 *
 */
public interface ItemBuyingAddOnDao extends Dao<ItemBuyingAddOn> {

	/**
	 * Get the list of buying add ons of the item.
	 * @param itemId The id of the item.
	 * @param companyId The id of the company.
	 * @return The list of {@link ItemBuyingAddOn} that matches the itemId and companyId parameters.
	 */
	List<ItemBuyingAddOn> getBuyingAddOns(Integer itemId, Integer companyId);
}
