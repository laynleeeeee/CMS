package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.ItemSrp;

/**
 * Data access object for {@link ItemSrp}

 *
 */
public interface ItemSrpDao extends Dao<ItemSrp>{
	
	/**
	 * Get the list of item srps by item id.
	 * @param itemId The item id.
	 * @return The list of item srps.
	 */
	List<ItemSrp> getItemSrpsByItem (Integer itemId);

	/**
	 * Get the item srp.
	 * @param companyId The company id.
	 * @param itemId The item id.
	 * @param divisionId The division id
	 * @return The item srp object.
	 */
	ItemSrp getLatestItemSrp(Integer companyId, Integer itemId, Integer divisionId);

	/**
	 * Checks if the item has an srp.
	 * @param itemId The id of the item to be checked.
	 * @return True if it has srp, otherwise false.
	 */
	boolean hasItemSrp (int itemId);

}
