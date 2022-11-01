package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.ItemAddOn;

/**
 * Data access object for {@link ItemAddOn}

 *
 */
public interface ItemAddOnDao extends Dao<ItemAddOn> {

	/**
	 * Get the list of item add ons by item.
	 * @param itemId The item id.
	 * @param activeOnly True if to return active add ons only, otherwise false.
	 * @return The list of item add ons.
	 */
	List<ItemAddOn> getItemAddOnsByItem (Integer itemId, boolean activeOnly);

	/**
	 * Get the list of item add ons by item and company id.
	 * @param itemId The item id.
	 * @param companyId The id of the company.
	 * @param activeOnly True if to return active add ons only, otherwise false.
	 * @return The list of item add ons.
	 */
	List<ItemAddOn> getItemAddOnsByItem (Integer itemId, Integer companyId, boolean activeOnly);

	/**
	 * Get the list of item add ons with selected inactive item add on id
	 * @param itemId The item id
	 * @param companyId The company id
	 * @param itemAddOnId The add on id
	 * @return The list of item add ons with selected inactive item add on id
	 */
	List<ItemAddOn> getAddOnsWSlctdInactiveAddOnId(Integer itemId, Integer companyId, Integer itemAddOnId);
}
